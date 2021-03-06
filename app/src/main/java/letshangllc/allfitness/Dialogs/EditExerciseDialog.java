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

import letshangllc.allfitness.ClassObjects.ExerciseItem;
import letshangllc.allfitness.ClassObjects.ExerciseType;
import letshangllc.allfitness.ClassObjects.MuscleGroup;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;
import letshangllc.allfitness.R;

/**
 * Created by cvburnha on 5/10/2016.
 */
public class EditExerciseDialog extends DialogFragment {
    private Listener mListener;
    private ExerciseItem exerciseItem;

    public interface Listener {
        public void onDialogPositiveClick(String name, ExerciseType exerciseType, MuscleGroup muscleGroup);
    }

    public void setExercise(ExerciseItem exerciseItem){
        this.exerciseItem = exerciseItem;
    }

    public void setCallback(Listener mListener) {
        this.mListener = mListener;
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_edit_exercise, null);

        /* find Views */
        final EditText et_item_name = (EditText) view.findViewById(R.id.et_newExercise);
        final Spinner spin_type = (Spinner) view.findViewById(R.id.spin_exerciseType);
        final Spinner spin_muscle = (Spinner) view.findViewById(R.id.spin_muscleGroup);

        /* Set Name */
        et_item_name.setText(exerciseItem.getExerciseName());

        /* Create a list of the exercise type names */
        ArrayList<ExerciseType> exerciseTypes = new ArrayList<>();
        for(ExerciseType exerciseType: ExerciseType.values()){
            exerciseTypes.add(exerciseType);
        }
        // Create an ArrayAdapter using a string array and a default spinner layout
        ArrayAdapter<ExerciseType> adapterType = new ArrayAdapter<ExerciseType>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, exerciseTypes);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spin_type.setAdapter(adapterType);

        /* Set selected to that exercise's type */
        spin_type.setSelection(exerciseTypes.indexOf(exerciseItem.getExerciseType()));


        MuscleGroupAdapterSelection muscleGroupAdapterSelection = getMuscleGroupArrayAdapter();
        spin_muscle.setAdapter(muscleGroupAdapterSelection.muscleAdapter);

        if(muscleGroupAdapterSelection.currentMuscleIndex != -1){
            spin_muscle.setSelection(muscleGroupAdapterSelection.currentMuscleIndex );
        }

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /* Get Dialog items and send back to Exercise Fragment */
                        String name = et_item_name.getText().toString().trim();
                        ExerciseType type = (ExerciseType) spin_type.getSelectedItem();
                        MuscleGroup muscleGroup = (MuscleGroup) spin_muscle.getSelectedItem();

                        mListener.onDialogPositiveClick(name, type, muscleGroup);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditExerciseDialog.this.getDialog().cancel();
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
    private MuscleGroupAdapterSelection getMuscleGroupArrayAdapter(){
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        ArrayList<MuscleGroup> muscleGroups = new ArrayList<>();
        String[] projection = {TableConstants.MuscleID, TableConstants.MuscleName};
        Cursor c = db.query(TableConstants.MuscleTableName, projection, null,
                null, null, null, null);
        c.moveToFirst();

        /* Keep and index of the current muscle to setSection for the correct muscleGroup */
        int currentMuscleIndex = -1;


        muscleGroups.add(new MuscleGroup(getResources().getInteger(R.integer.fake_muscle_group_id),
                getString(R.string.no_muscle_group)));

        while (!c.isAfterLast()){

            muscleGroups.add(new MuscleGroup(c.getInt(0), c.getString(1)));

            /* if that muscle group is the current one then grab its index */
            if(exerciseItem.getMuscleId() == c.getInt(0)){
                currentMuscleIndex = muscleGroups.size()-1;
            }
            c.moveToNext();
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<MuscleGroup> adapterMuscle = new ArrayAdapter<MuscleGroup>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, muscleGroups);

        adapterMuscle.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        /* If the exercise has a muscle group then set the spinner to that muscle */
        c.close();
        db.close();

        return new MuscleGroupAdapterSelection(adapterMuscle, currentMuscleIndex);
    }

    private class MuscleGroupAdapterSelection{
        public ArrayAdapter<MuscleGroup> muscleAdapter;
        public int currentMuscleIndex;

        public MuscleGroupAdapterSelection(ArrayAdapter<MuscleGroup> muscleAdapter, int currentMuscleIndex) {
            this.muscleAdapter = muscleAdapter;
            this.currentMuscleIndex = currentMuscleIndex;
        }
    }
}