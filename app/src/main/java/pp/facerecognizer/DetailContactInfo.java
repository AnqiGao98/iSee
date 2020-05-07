package pp.facerecognizer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import static pp.facerecognizer.env.FileUtils.ROOT;

public class DetailContactInfo extends AppCompatActivity implements EditContacts.EditDialogListener{

    private static final String TAG = DetailContactInfo.class.getName();
    private String contact_name;
    private String contact_annotation;
    private String image_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_contact_info);

        Bundle bundle = getIntent().getExtras();
        contact_name = bundle.getString("contact_name");
        contact_annotation = bundle.getString("contact_annotation");
        image_path = bundle.getString("image_path");
        System.out.println(image_path);
        setText();
        //set image using image path
        File imgFile = new File(image_path);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = (ImageView) findViewById(R.id.imageView_contact);
            myImage.setImageBitmap(myBitmap);
        }

        Button name_button = findViewById(R.id.edit_button);

        name_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                openDialog(contact_name,contact_annotation);
            }
        });
    }

    public void openDialog(String nameClick, String annotationClick){
        EditContacts editContactsDialog = new EditContacts();
        editContactsDialog.originName = nameClick;
        editContactsDialog.originAnnotation = annotationClick;
        editContactsDialog.show(getSupportFragmentManager(), "Contact Management Dialog");
    }

    @Override
    public void applyTexts(String newName, String newAnnotation){
        Log.d(TAG, newName + " "+ newAnnotation);
        replaceLines("label", contact_name,newName);
        replaceLines("annotations",contact_annotation,newAnnotation);

        contact_name = newName;
        contact_annotation = newAnnotation;
        setText();
    }
    private void setText(){
        TextView nameView = findViewById(R.id.textView_name);
        TextView annView = findViewById(R.id.textView_anno);

        nameView.setText("Name: "+ contact_name);
        annView.setText("Annotations: "+ contact_annotation);
    }

    public static void replaceLines(String filePath, String originText, String newText) {

        // input the (modified) file content to the StringBuffer "input"
        final File fileToBeModified = new File(ROOT+File.separator + filePath);

        BufferedReader reader = null;
        StringBuffer inputBuffer = new StringBuffer();

        try {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line; //= reader.readLine();
            while ((line = reader.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append("\n");
            }

            String inputStr = inputBuffer.toString();
            System.out.println(inputStr); // display the original file for debugging
            inputStr = inputStr.replace(originText, newText);

            FileOutputStream fileOut = new FileOutputStream(fileToBeModified);
            fileOut.write(inputStr.getBytes());
            fileOut.close();

            // display the new file for debugging
            System.out.println("----------------------------------\n" + inputStr);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
