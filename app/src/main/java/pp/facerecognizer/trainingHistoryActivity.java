package pp.facerecognizer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.emergency.EmergencyNumber;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;

import pp.facerecognizer.env.FileUtils;

import static pp.facerecognizer.env.FileUtils.ROOT;


public class trainingHistoryActivity extends AppCompatActivity {

    LineChart mpLineChart;
    private static final String TAG = "trainingHistoryActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_history);
        //accuracy
        mpLineChart = (LineChart)findViewById(R.id.line_chart_acc);
        LineDataSet lineDataSetAcc = new LineDataSet(readHistory(FileUtils.ACCURACY_FILE), "Accuracy History");
        ArrayList<ILineDataSet> dataSets_acc = new ArrayList<>();
        dataSets_acc.add(lineDataSetAcc);

        LineData acc_data = new LineData(dataSets_acc);
        mpLineChart.setData(acc_data);
        mpLineChart.invalidate();

        //reaction time
        mpLineChart = (LineChart)findViewById(R.id.line_chart_time);
        LineDataSet lineDataSetTime = new LineDataSet(readHistory(FileUtils.REACTION_FILE), "Reaction Time History");
        ArrayList<ILineDataSet> dataSets_time = new ArrayList<>();
        dataSets_time.add(lineDataSetTime);

        LineData time_data = new LineData(dataSets_time);
        mpLineChart.setData(time_data);
        mpLineChart.invalidate();
    }

//    private ArrayList<Entry> timeValues(){
//        ArrayList<Entry> dataVals = new ArrayList<>();
//        dataVals.add(new Entry(0, 20));
//        dataVals.add(new Entry(1, 24));
//        dataVals.add(new Entry(2, 4));
//        dataVals.add(new Entry(3, 10));
//        dataVals.add(new Entry(4, 15));
//
//        return dataVals;
//    }

    public static ArrayList<Entry> readHistory(String filename){
        Scanner s = null;
        ArrayList<Entry> list = new ArrayList<>();
        try {
            s = new Scanner(new File(ROOT + File.separator + filename));
            int i = 0;
            while (s.hasNextLine()){
                list.add(new Entry(i, Float.parseFloat(s.nextLine())));
                i++;
            }
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

}
