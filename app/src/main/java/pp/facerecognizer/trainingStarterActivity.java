package pp.facerecognizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class trainingStarterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_starter);
    }

    public void startTraining(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, trainingActivity.class);
        startActivity(intent);
    }

    public void showTrainingHistory(View view) {
        // Do something in response to button
        Intent intent = new Intent(this, trainingHistoryActivity.class);
        startActivity(intent);
    }
}
