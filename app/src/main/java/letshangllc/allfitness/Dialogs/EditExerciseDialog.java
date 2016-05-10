package letshangllc.allfitness.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import letshangllc.allfitness.ClassObjects.ExerciseType;
import letshangllc.allfitness.ClassObjects.MuscleGroup;
import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.R;

/**
 * Created by cvburnha on 5/10/2016.
 */
public class EditExerciseDialog extends DialogFragment {


    public interface Listener {
        public void onDialogPositiveClick(String name, String type, MuscleGroup muscleGroup);
    }


    public void setCallback(Listener mListener) {
        this.mListener = mListener;
    }

    Listener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_add_exercise, null);

        final EditText et_item_name = (EditText) view.findViewById(R.id.et_newExercise);
        final Spinner spin_type = (Spinner) view.findViewById(R.id.spin_exerciseType);
        final Spinner spin_muscle = (Spinner) view.findViewById(R.id.spin_muscleGroup);


        ArrayList<String> exerciseTypes = new ArrayList<>();
        for(ExerciseType exerciseType: ExerciseType.values()){
            exerciseTypes.add(exerciseType.getExerciseTypeName());
        }
        // Create an ArrayAdapter using a string array and a default spinner layout
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, exerciseTypes);
        // Specify the layout to use when the list of choices appears
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spin_type.setAdapter(adapterType);

        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ArrayList<MuscleGroup> muscleGroups = new ArrayList<>();
        String[] projection = {TableConstants.MuscleID, TableConstants.MuscleName};
        Cursor c = db.query(TableConstants.MuscleTableName, projection, null,
                null, null, null, null);
        c.moveToFirst();

        while (c.isAfterLast() == false){
            muscleGroups.add(new MuscleGroup(c.getInt(0), c.getString(1)));
            c.moveToNext();
        }
        if(muscleGroups.size() == 0){
            muscleGroups.add(new MuscleGroup(getResources().getInteger(R.integer.fake_muscle_group_id),
                    getString(R.string.no_muscle_groups)));
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<MuscleGroup> adapterMuscle = new ArrayAdapter<MuscleGroup>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, muscleGroups);

        adapterMuscle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_muscle.setAdapter(adapterMuscle);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /* Get Dialog items and send back to Exercise Fragment */
                        String name = et_item_name.getText().toString();
                        String type = spin_type.getSelectedItem().toString();
                        MuscleGroup muscleGroup = (MuscleGroup) spin_muscle.getSelectedItem();

                        mListener.onDialogPositiveClick(name, type, muscleGroup);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddExerciseDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}