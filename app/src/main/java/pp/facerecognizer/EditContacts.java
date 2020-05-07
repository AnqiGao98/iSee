package pp.facerecognizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDialogFragment;

import pp.facerecognizer.R;

public class EditContacts extends AppCompatDialogFragment {
    private EditText editName;
    private EditText editAnnotation;
    private EditDialogListener listener;
    public String originName;
    public String originAnnotation;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_contact, null);

        editName = view.findViewById(R.id.contactName);
        editAnnotation = view.findViewById(R.id.annotation);
        editName.setText(originName);
        editAnnotation.setText(originAnnotation);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String newName = editName.getText().toString();
                        String newAnnotation = editAnnotation.getText().toString();
                        listener.applyTexts(newName, newAnnotation);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //LoginDialogFragment.this.getDialog().cancel();
                    }
                });
//        editName = view.findViewById(R.id.contactName);
//        editAnnotation = view.findViewById(R.id.annotation);
        return builder.create();
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            listener = (EditDialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement editDialogListener");
        }
    }

    public interface EditDialogListener{
        void applyTexts(String newName, String newAnnotation);
    }
}