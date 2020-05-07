package pp.facerecognizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import pp.facerecognizer.env.FileUtils;

import static pp.facerecognizer.MainActivity.EXTRA_MESSAGE;
import static pp.facerecognizer.env.FileUtils.ROOT;

public class ContactManagment extends AppCompatActivity{

    private ListView lv;
    ArrayAdapter<String> arrayAdapter;
//    private RecyclerView recyclerView;
//    private RecyclerView.Adapter mAdapter;
//    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<String> contacts = new ArrayList<>();
    private ArrayList<String> annotations = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private static final String TAG = trainingActivity.class.getName();
    private String originName;
    private String originAnn;

    private int refreshPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_managment);
        lv = (ListView) findViewById(R.id.listView);

        //ArrayList<String> contacts = new ArrayList<>();
        try {
            Log.d(TAG, "file path: "+ ROOT);
            contacts = FileUtils.readLabel(FileUtils.LABEL_FILE);
            annotations = FileUtils.readLabel(FileUtils.ANNOTATION_FILE);
            images = FileUtils.readLabel(FileUtils.IMAGE_FILE);

            System.out.println(contacts);
            System.out.println(annotations);
            System.out.println(images);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //refreshListView();
        //Log.d(TAG, "contact list size is"+contacts.size());

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                contacts);

        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                if (null != contacts && !contacts.isEmpty()) {

                    //get image name that user chose
                    Log.d(TAG, "position detail is "+ position + ", name is "+ contacts.get(position));
                    originName = contacts.get(position);
                    originAnn = annotations.get(position);
                    refreshPosition = position;

                    //show detailed information
                    Bundle bundle = new Bundle();
                    bundle.putString("contact_name", originName);
                    bundle.putString("contact_annotation", originAnn);
                    bundle.putString("image_path", images.get(position));
                    Intent intent = new Intent(ContactManagment.this, DetailContactInfo.class);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    //openDialog(originName,originAnn);

                }
            }
            public void onNothingSelected(AdapterView parentView) {

            }
        });

    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    private void refreshListView(){
        try {
            contacts = FileUtils.readLabel(FileUtils.LABEL_FILE);
            annotations = FileUtils.readLabel(FileUtils.ANNOTATION_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        arrayAdapter.notifyDataSetChanged();
    }
}
