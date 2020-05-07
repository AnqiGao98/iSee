/* Copyright 2015 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package pp.facerecognizer;

import android.content.ContentResolver;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;

import java.io.FileDescriptor;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import pp.facerecognizer.env.FileUtils;
import pp.facerecognizer.env.Logger;
import pp.facerecognizer.ml.BlazeFace;
import pp.facerecognizer.ml.FaceNet;
import pp.facerecognizer.ml.LibSVM;

/**
 * Generic interface for interacting with different recognition engines.
 */
public class Recognizer {
    /**
     * An immutable result returned by a Classifier describing what was recognized.
     */
    public class Recognition {
        /**
         * A unique identifier for what has been recognized. Specific to the class, not the instance of
         * the object.
         */
        private final String id;

        /**
         * Display name for the recognition.
         */
        private final String title;

        /**
         * A sortable score for how good the recognition is relative to others. Higher should be better.
         */
        private final Float confidence;

        /** Optional location within the source image for the location of the recognized object. */
        private RectF location;

        Recognition(
                final String id, final String title, final Float confidence, final RectF location) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.location = location;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        public RectF getLocation() {
            return new RectF(location);
        }

        @Override
        public String toString() {
            String resultString = "";
            if (id != null) {
                resultString += "[" + id + "] ";
            }

            if (title != null) {
                resultString += title + " ";
            }

            if (confidence != null) {
                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            }

            if (location != null) {
                resultString += location + " ";
            }

            return resultString.trim();
        }
    }

    private static Recognizer recognizer;

    private BlazeFace blazeFace;
    private FaceNet faceNet;
    private LibSVM svm;

    private List<String> classNames;

    private Recognizer() {}

    /* Parameters to handle unknown faces */
    private LibSVM unknown_svm;
    private int unknown_id = 5;     // start from 5 because initial file has 5 people already

    // text to speech
    public Boolean speechFlag = false;
    public String speechName = "";

    private static final Logger LOGGER = new Logger();

    static Recognizer getInstance (AssetManager assetManager) throws Exception {
        if (recognizer != null) return recognizer;

        recognizer = new Recognizer();
        recognizer.blazeFace = BlazeFace.create(assetManager);
        recognizer.faceNet = FaceNet.create(assetManager);
        recognizer.svm = LibSVM.getInstance(false);
        recognizer.classNames = FileUtils.readLabel(FileUtils.LABEL_FILE);
        recognizer.unknown_svm = LibSVM.getInstance(true);  // true to unknown_flag

        return recognizer;
    }

    CharSequence[] getClassNames() {
        CharSequence[] cs = new CharSequence[classNames.size() + 1];
        int idx = 1;

        cs[0] = "+ add new person";
        for (String name : classNames) {
            cs[idx++] = name;
        }

        return cs;
    }

    List<Recognition> recognizeImage(Bitmap bitmap, Matrix matrix) {
        synchronized (this) {
            List<RectF> faces = blazeFace.detect(bitmap);
            final List<Recognition> mappedRecognitions = new LinkedList<>();

            for (RectF rectF : faces) {
                Rect rect = new Rect();
                rectF.round(rect);

                FloatBuffer buffer = faceNet.getEmbeddings(bitmap, rect);
                LibSVM.Prediction prediction = svm.predict(buffer);

                matrix.mapRect(rectF);
                int index = prediction.getIndex();

                String name = classNames.get(index);
                double rthreshold = recognizeThreshold();
                if(prediction.getProb() > rthreshold) {
                    Recognition result =
                            new Recognition("" + index, name, prediction.getProb(), rectF);
                    mappedRecognitions.add(result);
                    speechFlag = true;
                    speechName = name;
                }
                // Handle the case of unknown
                else
                {
                    speechFlag = false;
                    speechName = "";
                    // first check if it is a "known" unknown
                    LibSVM.Prediction unknown_prediction = unknown_svm.predict(buffer);


                    Recognition result =
                            new Recognition("unknown", "unknown", (float)unknown_prediction.getProb(), rectF);
                    mappedRecognitions.add(result);

                    // only save to new cache if they are not a known unknown
                    if(unknown_prediction.getProb() < recognizeUnknownThreshold())
                    {
                        // Save bitmap to album and add this person as unknown id
                        FileUtils.saveBitmap(bitmap, "unknown " + unknown_id + ".png");

                        addUnknownToSVM(buffer);
                    }
                }
            }

            return mappedRecognitions;
        }

    }

    Boolean getSpeechFlag()
    {
        return speechFlag;
    }

    String getSpeechName()
    {
        return speechName;
    }

    double recognizeThreshold()
    {
        return (1.0 /(double) classNames.size()) * 3;
    }

    double recognizeUnknownThreshold()
    {
        return (1.0 /(double) (unknown_id + 1)) * 2;
    }

    void addUnknownToSVM(final FloatBuffer buffer)
    {
        ArrayList<float[]> list = new ArrayList<>();

        float[] emb_array = new float[FaceNet.EMBEDDING_SIZE];
        buffer.get(emb_array);
        list.add(emb_array);

        unknown_svm.train(unknown_id, list);
        addUnknownPerson();
    }

    void updateData(int label, ContentResolver contentResolver, ArrayList<Uri> uris) throws Exception {
        synchronized (this) {
            ArrayList<float[]> list = new ArrayList<>();
            String imgPath = "";
            int count = 0;
            LOGGER.i("Melissa: updateData");
            for (Uri uri : uris) {
                LOGGER.i("Melissa: uri path = " + uri.getPath() );
                // get face image
                Bitmap bitmap = getBitmapFromUri(contentResolver, uri);
                List<RectF> faces = blazeFace.detect(bitmap);

                Rect rect = new Rect();
                if (!faces.isEmpty()) {
                    faces.get(0).round(rect);
                }

                float[] emb_array = new float[FaceNet.EMBEDDING_SIZE];
                faceNet.getEmbeddings(bitmap, rect).get(emb_array);
                list.add(emb_array);

                // save face image
                imgPath += FileUtils.saveBitmap(bitmap, classNames.get(label) + count + ".png", FileUtils.IMAGE_DIR) + ";";
                count++;

                // save face image path
//                imgPath += FileUtils.saveUnknownImage(classNames.get(label), getRealPathFromURI(contentResolver,uri)) + ";";
            }


//            LOGGER.i("Melissa: ClassName size = " + classNames.size() + " , label = " + label);
//            FileUtils.updateImagePathRecord((classNames.size() - 1) == label, imgPath, label);
            svm.train(label, list);

        }
    }

    int addPerson(String name) {
        FileUtils.appendText(name, FileUtils.LABEL_FILE);
        classNames.add(name);
        //also add a new line in annoation file
        FileUtils.appendText(name, FileUtils.ANNOTATION_FILE);

        return classNames.size();
    }

    int addUnknownPerson() {
        FileUtils.appendText("unknown " + unknown_id, FileUtils.UNKNOWN_LABEL_FILE);
        unknown_id++;

        return unknown_id;
    }

    private Bitmap getBitmapFromUri(ContentResolver contentResolver, Uri uri) throws Exception {
        ParcelFileDescriptor parcelFileDescriptor =
                contentResolver.openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        return bitmap;
    }

    void enableStatLogging(final boolean debug){
    }

    void close() {
        blazeFace.close();
        faceNet.close();
    }


    public String getRealPathFromURI(ContentResolver contentResolver, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = contentResolver.query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
