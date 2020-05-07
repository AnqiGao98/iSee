package pp.facerecognizer.ml;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import pp.facerecognizer.env.FileUtils;

/**
 * Created by yctung on 9/26/17.
 * This is a java wrapper of LibSVM
 */

public class LibSVM {
    private String LOG_TAG = "LibSVM";
    private boolean UNKNOWN_FLAG;
    private String DATA_PATH;
    private String MODEL_PATH;

    private int index;
    private double prob;

    public class Prediction {
        private int index;
        private float prob;

        Prediction(int index, float prob) {
            this.index = index;
            this.prob = prob;
        }
        public int getIndex() {
            return index;
        }
        public float getProb() {
            return prob;
        }
    }

    static {
        System.loadLibrary("jnilibsvm");
    }

    // connect the native functions
    private native void testLog(String log);
    private native void jniSvmTrain(String cmd);
    private native void jniSvmPredict(String cmd, FloatBuffer buf, int len);
    private native void jniSvmScale(String cmd, String fileOutPath);

    // public interfaces
    private void train(String cmd) {
        jniSvmTrain(cmd);
    }
    private void predict(String cmd, FloatBuffer buf, int len) {
        jniSvmPredict(cmd, buf, len);
    }
    private void scale(String cmd, String fileOutPath) {
        jniSvmScale(cmd, fileOutPath);
    }

    public void train(int label, ArrayList<float[]> list) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            float[] array = list.get(i);
            builder.append(label);
            for (int j = 0; j < array.length; j++) {
                builder.append(" ").append(j).append(":").append(array[j]);
            }
            if (i < list.size() - 1) builder.append(System.lineSeparator());
        }

        if (UNKNOWN_FLAG)
        {
            FileUtils.appendText(builder.toString(), FileUtils.UNKNWON_DATA_FILE);
        }
        else
        {
            FileUtils.appendText(builder.toString(), FileUtils.DATA_FILE);
        }


        train();
    }

    public void train() {
        String options = "-t 0 -b 1";
        String cmd = TextUtils.join(" ", Arrays.asList(options, DATA_PATH, MODEL_PATH));
        train(cmd);
    }

    public Prediction predict(FloatBuffer buffer) {
        String options = "-b 1";
        String cmd = TextUtils.join(" ", Arrays.asList(options, MODEL_PATH));

        predict(cmd, buffer, FaceNet.EMBEDDING_SIZE);
        return new Prediction(index, (float) prob);
    }

    // singleton for the easy access
//    private static LibSVM svm;
//    private static LibSVM unknown_svm;

    public static LibSVM getInstance(boolean unknown_flag) {
        return new LibSVM(unknown_flag);
//        if (!unknown_flag)
//        {
//            if (svm == null) {
//                svm = new LibSVM(unknown_flag);
//            }
//            return svm;
//        }
//        else
//        {
//            if (unknown_svm == null) {
//                unknown_svm = new LibSVM(unknown_flag);
//            }
//            return unknown_svm;
//        }
    }

    private LibSVM(boolean unknown_flag) {
        Log.d(LOG_TAG, "LibSVM init");
        UNKNOWN_FLAG = unknown_flag;
        DATA_PATH  = (UNKNOWN_FLAG)? FileUtils.ROOT + File.separator + FileUtils.UNKNWON_DATA_FILE : FileUtils.ROOT + File.separator + FileUtils.DATA_FILE;
        MODEL_PATH = (UNKNOWN_FLAG)? FileUtils.ROOT + File.separator + FileUtils.UNKNOWN_MODEL_FILE : FileUtils.ROOT + File.separator + FileUtils.MODEL_FILE;

    }
}
