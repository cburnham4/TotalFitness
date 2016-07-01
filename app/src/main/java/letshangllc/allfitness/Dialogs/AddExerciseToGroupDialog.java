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
import android.widget.Spinner;

import java.util.ArrayList;

import letshangllc.allfitness.ClassObjects.ExerciseItem;
import letshangllc.allfitness.ClassObjects.ExerciseType;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;
import letshangllc.allfitness.R;

/**
 * Created by cvburnha on 5/10/2016.
 */
public class AddExerciseToGroupDialog extends DialogFragment {


    public interface Listener {
        public void onDialogPositiveClick(ExerciseItem exerciseItem);
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
        View view = inflater.inflate(R.layout.dialog_add_exercise_to_group, null);

        final Spinner spin_exercises = (Spinner) view.findViewById(R.id.spin_exercises);


        ArrayList<ExerciseItem> exerciseItems = getExercises();

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<ExerciseItem> adapterExercises = new ArrayAdapter<ExerciseItem>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, exerciseItems);

        adapterExercises.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_exercises.setAdapter(adapterExercises);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        /* Get Dialog items and send back to Routine Activity */
                        ExerciseItem exerciseItem = (ExerciseItem) spin_exercises.getSelectedItem();

                        mListener.onDialogPositiveClick(exerciseItem);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddExerciseToGroupDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    /* Read in the existing exercised from the database and return as arraylist */
    private ArrayList<ExerciseItem> getExercises(){
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {TableConstants.ExerciseId, TableConstants.ExerciseName, TableConstants.ExerciseType,
                TableConstants.MuscleID};
        Cursor c = db.query(TableConstants.ExerciseTableName, projection, null, null, null, null, null);
        c.moveToFirst();

        ArrayList<ExerciseItem> exerciseItems = new ArrayList<>();
        while (!c.isAfterLast()) {
            ExerciseType exerciseType = ExerciseType.getType(c.getInt(2));
            exerciseItems.add(new ExerciseItem(c.getInt(0), c.getString(1), exerciseType, c.getInt(3)));
            c.moveToNext();
        }
        c.close();
        db.close();

        return exerciseItems;
    }
}
