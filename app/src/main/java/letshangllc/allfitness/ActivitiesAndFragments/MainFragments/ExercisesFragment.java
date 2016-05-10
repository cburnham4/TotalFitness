package letshangllc.allfitness.ActivitiesAndFragments.MainFragments;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import letshangllc.allfitness.ClassObjects.ExerciseType;
import letshangllc.allfitness.ClassObjects.MuscleGroup;
import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.Dialogs.AddExerciseDialog;
import letshangllc.allfitness.ListViewAdapters.ExerciseListAdapter;
import letshangllc.allfitness.ClassObjects.ExerciseItem;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExercisesFragment extends Fragment {
    private static String TAG = ExercisesFragment.class.getSimpleName();

    /* List of all the user's exercises */
    private ArrayList<ExerciseItem> listOfExercises;

    /* Listview adaoter to display the exercises */
    private ExerciseListAdapter exerciseListAdapter;

    /* Views */
    private ListView lv_exercises;
    private EditText et_searchExercises;
    private FloatingActionButton fab_addExercise;

    /*Database variables */
    private  DatabaseHelper databaseHelper;

    public ExercisesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Initialize DB Helper */
        databaseHelper =  new DatabaseHelper(getContext());

        /* Create initial Array list and get data from DB */
        listOfExercises = new ArrayList<>();
        getExistingData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exercises, container, false);
        /* Find the views */
        findViews(view);

        /*Instantiate array Adapter */
        exerciseListAdapter = new ExerciseListAdapter(getContext(), listOfExercises);
        /* set the adapter for the listview */
        lv_exercises.setAdapter(exerciseListAdapter);

        return view;
    }

    private void findViews(View view){
        lv_exercises = (ListView) view.findViewById(R.id.lv_exercises);
        et_searchExercises = (EditText) view.findViewById(R.id.et_searchExercise);
        fab_addExercise = (FloatingActionButton) view.findViewById(R.id.fab_exercises);
        setListeners();
    }

    /* Read in the existing exercised from the database and insert into the arraylist */
    private void getExistingData(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {TableConstants.LiftId, TableConstants.LiftName, TableConstants.LiftType,
            TableConstants.MuscleID};
        Cursor c = db.query(TableConstants.LiftTableName, projection, null, null, null, null, null);
        c.moveToFirst();

        while (c!= null && !c.isAfterLast()) {
            ExerciseType exerciseType = ExerciseType.getType(c.getInt(2));
            listOfExercises.add(new ExerciseItem(c.getInt(0), c.getString(1), exerciseType, c.getInt(3)));
            c.moveToNext();
        }
        c.close();
        db.close();
    }

    /* Display the dialog to add an additional exercise */
    private void openAddDialog(){
        final AddExerciseDialog addExerciseDialog = new AddExerciseDialog();
        addExerciseDialog.setCallback(new AddExerciseDialog.AddExerciseListener() {
                  @Override
                  public void onDialogPositiveClick(String name, String type, MuscleGroup muscleGroup) {
                      if(name.isEmpty())return;
                      /* Create an instance of the Writable DB */
                      SQLiteDatabase db = databaseHelper.getWritableDatabase();

                      /* Get the type */
                      ExerciseType exerciseType = ExerciseType.valueOf(type.toUpperCase());

                      /* Create the values to be inserted into the Database */
                      ContentValues contentValues =  new ContentValues();
                      contentValues.put(TableConstants.LiftName, name);
                      contentValues.put(TableConstants.LiftType, exerciseType.getExerciseTypeID());
                      contentValues.put(TableConstants.MuscleID, muscleGroup.getMuscleGroupId());

                      /* Insert into and close the Database */
                      db.insert(TableConstants.LiftTableName, null, contentValues);
                      db.close();

                      /* Add exercise to arraylist and update listview */
                      listOfExercises.add(new ExerciseItem(getMaxExerciseId(),
                              name, exerciseType, muscleGroup.getMuscleGroupId()));
                      exerciseListAdapter.notifyDataSetChanged();
                  }
              }
        );
        addExerciseDialog.show(getFragmentManager(), "Add_Muscle");
    }

    /* Get the id of the last inputted exercise */
    private int getMaxExerciseId(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT Max("+ TableConstants.LiftId +") FROM "+ TableConstants.LiftTableName;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int max = c.getInt(0);
        c.close();
        db.close();
        return max;
    }

    private void setListeners(){
        fab_addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Fab clicked");
                openAddDialog();
            }
        });

        et_searchExercises.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                exerciseListAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }


}
