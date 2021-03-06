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

import java.lang.reflect.Array;
import java.util.ArrayList;

import letshangllc.allfitness.ClassObjects.ExerciseType;
import letshangllc.allfitness.ClassObjects.MuscleGroup;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;
import letshangllc.allfitness.R;

/**
 * Created by cvburnha on 3/18/2016.
 * Dialog to add exercise
 */
public class AddExerciseDialog  extends DialogFragment {


    public interface AddExerciseListener {
        public void onDialogPositiveClick(String name, String type, MuscleGroup muscleGroup);
    }


    public void setCallback(AddExerciseListener mListener) {
        this.mListener = mListener;
    }

    AddExerciseListener mListener;

    /* todo move to one dialog class */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_add_exercise, null);

        /* Find Views */
        final EditText et_item_name = (EditText) view.findViewById(R.id.et_newExercise);
        final Spinner spin_type = (Spinner) view.findViewById(R.id.spin_exerciseType);
        final Spinner spin_muscle = (Spinner) view.findViewById(R.id.spin_muscleGroup);

        /* Set the spin adapters */
        spin_type.setAdapter(getExerciseArrayAdapter());
        spin_muscle.setAdapter(getMuscleGroupArrayAdapter());

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /* Get Dialog items and send back to Exercise Fragment */
                        String name = et_item_name.getText().toString().trim();
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

    /* Get the exercise types and put them into an array adapter */
    private ArrayAdapter<String> getExerciseArrayAdapter(){
        /* Get an array of the exercise types */
        ArrayList<String> exerciseTypes = new ArrayList<>();
        for(ExerciseType exerciseType: ExerciseType.values()){
            exerciseTypes.add(exerciseType.getExerciseTypeName());
        }

        /* Create an array adapter for the exercise types */
        ArrayAdapter<String> adapterType = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, exerciseTypes);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapterType;
    }

    /* Pull the Muscle groups from DB and insert them into array adapter */
    private ArrayAdapter<MuscleGroup> getMuscleGroupArrayAdapter(){
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ArrayList<MuscleGroup> muscleGroups = new ArrayList<>();
        String[] projection = {TableConstants.MuscleID, TableConstants.MuscleName};
        Cursor c = db.query(TableConstants.MuscleTableName, projection, null,
                null, null, null, null);
        c.moveToFirst();

        muscleGroups.add(new MuscleGroup(getResources().getInteger(R.integer.fake_muscle_group_id),
                getString(R.string.no_muscle_group)));

        while (c.isAfterLast() == false){
            muscleGroups.add(new MuscleGroup(c.getInt(0), c.getString(1)));
            c.moveToNext();
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<MuscleGroup> adapterMuscle = new ArrayAdapter<MuscleGroup>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, muscleGroups);

        adapterMuscle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapterMuscle;
    }
}