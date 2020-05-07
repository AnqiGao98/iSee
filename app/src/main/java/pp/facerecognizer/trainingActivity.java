package pp.facerecognizer;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import pp.facerecognizer.env.FileUtils;

import static pp.facerecognizer.MainActivity.EXTRA_MESSAGE;
import static pp.facerecognizer.env.FileUtils.ROOT;
import pp.facerecognizer.env.FileUtils;


public class trainingActivity extends AppCompatActivity {


    private static int RESULT_LOAD_IMAGE = 1;
    private ArrayList<String> images;
    private int training_cycle;
    ArrayList<String> listOfAllImages = new ArrayList<String>();
    String training_accuracy_message;
    private float training_accuracy;
    private String this_round_correct_image;
    private String this_round_correct_image_path;
    private int correct_picks;
    private long time_started;
    private long time_elapsed;

    private static final String TAG = trainingActivity.class.getName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        training_cycle = 0;
        correct_picks = 0;
        time_started = SystemClock.elapsedRealtime();

        loadAppImages();

        Log.d(TAG, "im here");
        Log.d(TAG, "Arraylistsize: "+ listOfAllImages.size());
        for(int i =0; i < listOfAllImages.size(); i++){
            Log.d(TAG, "image path: "+ listOfAllImages.get(i));
        }

        refreshPage();

    }
    private void loadAppImages(){
        String path = ROOT; //+ File.separator + FileUtils.ImageDir;//+"/Pictures";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            //Log.d("Files", "FileName:" + files[i].getName());
            if(isImage(files[i])){
                if(!files[i].toString().equals("/storage/emulated/0/facerecognizer/face_album") && !files[i].toString().equals("/storage/emulated/0/facerecognizer/preview.png")){
                    listOfAllImages.add(files[i].toString());
                }
            }

        }
        //        //try to get all images from album
//        Uri uri = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
//        String[] filePathColumn = { MediaStore.Images.Media.DATA };
//        //String[] filePathColumn = { ROOT };
//        String absolutePathOfImage = null;
//        ArrayList<String> ten_uri_string = new ArrayList<>();
//
//
//        Cursor cursor = getApplicationContext().getContentResolver().query(
//                MediaStore.Images.Media.INTERNAL_CONTENT_URI, filePathColumn, null, null, null);
//
//        while (cursor.moveToNext()) {
//            absolutePathOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
//
//            listOfAllImages.add(absolutePathOfImage);
//        }
    }
    public static boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }

    public void loadTrainingHistory(){

    }
    private void imageShowInGrid(ArrayList<String> ten_uri_string){
        //add random pick 10 image to the grid view
        GridView gallery = (GridView) findViewById(R.id.galleryGridView);

        gallery.setAdapter(new ImageAdapter(this, ten_uri_string));
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (null != images && !images.isEmpty()) {
                    //get image name that user chose
                    String selected_image = getImageName(images.get(position));
                    if (this_round_correct_image.equals(selected_image)) {
                        correct_picks++;
                    }
                    //pick wrong, show the correct image
                    else {
                        showUpCorrectImg(this_round_correct_image_path);
                    }

                    training_cycle++;
                    if (training_cycle < 15) {
                        refreshPage();
                    } else {
                        //show training result
                        Intent intent = new Intent(trainingActivity.this, resultAnalysis.class);
                        training_accuracy = (float) correct_picks * 100 / 15;
                        time_elapsed = SystemClock.elapsedRealtime() - time_started;
                        float time_elapsed_float = (float) time_elapsed / 1000;
                        String acc_message = "training accuracy is " + training_accuracy + "%" + '\n'
                                + "Time: " + time_elapsed_float + " seconds";
                        intent.putExtra(EXTRA_MESSAGE, acc_message);
                        //add to accuracy file and reaction file for future analysis
                        FileUtils.appendText(String.valueOf(training_accuracy), FileUtils.ACCURACY_FILE);
                        FileUtils.appendText(String.valueOf(time_elapsed_float), FileUtils.REACTION_FILE);

                        startActivity(intent);
                    }
                }
            }
        });

    }

    private void showUpCorrectImg(String img_path){
        Log.d(TAG, "correct image path is "+ img_path);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        Log.v("width", width+"");

        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //
            }
        });

        ImageView imageView = new ImageView(this);
        Bitmap myBitmap = BitmapFactory.decodeFile(img_path);
        imageView.setImageBitmap(myBitmap);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        builder.getWindow().setLayout((6*width)/7,(height)/2);
        builder.show();
    }

    private String getImageName(String picturePath){
        File f = new File(picturePath);
        String imageName = f.getName();
        return imageName;
    }

    private void refreshPage(){
        ArrayList<String> ten_uri_string = new ArrayList<>();

        Set<Integer> number_set = generateRandomNumer(listOfAllImages.size());
        Log.d(TAG, "number_set size is " + number_set.size());

        for(Integer i: number_set){
            Log.d(TAG, "random number is :" + i);
            String new_path = listOfAllImages.get(i);
            ten_uri_string.add(new_path);
            Log.d(TAG, "path: "+ new_path);
        }

        //randomly choose one in this 10 images
        Random rand = new Random();
        int rand_int = rand.nextInt(10);

        this_round_correct_image_path = ten_uri_string.get(rand_int);
        this_round_correct_image = getImageName(this_round_correct_image_path);

        TextView textView = findViewById(R.id.displayName);
        textView.setText(this_round_correct_image);

        imageShowInGrid(ten_uri_string);
    }
    protected Set<Integer> generateRandomNumer(int array_size){
        Random rand = new Random();
        Set<Integer> hash_set = new HashSet<Integer>();
        while(hash_set.size() < 10){
            int rand_int = rand.nextInt(array_size);
            hash_set.add(rand_int);
        }
        return hash_set;

    }



    //    /**
//     * The Class ImageAdapter.
//     */
    private class ImageAdapter extends BaseAdapter {

        /** The context. */
        private Activity context;

        /**
         * Instantiates a new image adapter.
         *
         * @param localContext
         *            the local context
         */
        public ImageAdapter(Activity localContext, ArrayList<String> paths) {
            context = localContext;
            images = paths;
        }

        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView
                        .setLayoutParams(new GridView.LayoutParams(270, 270));

            } else {
                picturesView = (ImageView) convertView;
            }

            Glide.with(context).load(images.get(position))
                    .placeholder(new ColorDrawable(Color.BLACK)).centerCrop()
                    .into(picturesView);

//            Matrix matrix = new Matrix();
//            picturesView.setScaleType(ImageView.ScaleType.MATRIX);   //required
//            //matrix.postRotate((float) angle, pivotX, pivotY);
//            matrix.postRotate( 180f, picturesView.getDrawable().getBounds().width()/2, picturesView.getDrawable().getBounds().height()/2);
//            picturesView.setImageMatrix(matrix);

            return picturesView;
        }
    }
}
