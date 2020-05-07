package pp.facerecognizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;

import pp.facerecognizer.env.FileUtils;

//import static pp.facerecognizer.MainActivity.LOGGER;

public class LauncherPage extends AppCompatActivity {

    private static final String TAG = LauncherPage.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_page);

//        File dir = new File(FileUtils.ROOT);
//        File faceDir = new File(FileUtils.ROOT + File.separator + FileUtils.IMAGE_DIR);
//
//        AssetManager mgr = getAssets();
//        if (!dir.isDirectory()) {
//            if (dir.exists()) dir.delete();
//            dir.mkdirs();
//            faceDir.mkdir();
//
//            FileUtils.copyAsset(mgr, FileUtils.DATA_FILE);
//            FileUtils.copyAsset(mgr, FileUtils.MODEL_FILE);
//            FileUtils.copyAsset(mgr, FileUtils.LABEL_FILE);
//            FileUtils.copyAsset(mgr, FileUtils.IMAGE_FILE);
//
//
//            FileUtils.copyAsset(mgr, FileUtils.REACTION_FILE);
//            FileUtils.copyAsset(mgr, FileUtils.ACCURACY_FILE);
//            FileUtils.copyAsset(mgr, FileUtils.ANNOTATION_FILE);
//
//        }
//        try {
//            if (!dir.exists()) faceDir.mkdir();
//            FileUtils.copyAsset(mgr, FileUtils.UNKNWON_DATA_FILE);
//            FileUtils.copyAsset(mgr, FileUtils.UNKNOWN_MODEL_FILE);
//            FileUtils.copyAsset(mgr, FileUtils.UNKNOWN_LABEL_FILE);
//        } catch (Exception e) {
//            Log.d(TAG, "Exception setting up initial files!");
//            finish();
//        }
    }

    /** Called when the user taps the Real-Time Recognition Mode button */
    public void enterRealTimeRecognitionMode(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    public void startTraining(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, trainingStarterActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the contact button */
    public void manageContacts(View view) {
        Intent intent = new Intent(this, ContactManagment.class);
        startActivity(intent);
    }
}
