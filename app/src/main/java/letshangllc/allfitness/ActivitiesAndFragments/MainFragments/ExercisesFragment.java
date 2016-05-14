package letshangllc.allfitness.ActivitiesAndFragments.MainFragments;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import letshangllc.allfitness.ActivitiesAndFragments.Activities.ExerciseActivity;
import letshangllc.allfitness.ActivitiesAndFragments.Activities.MuscleGroupActivity;
import letshangllc.allfitness.ClassObjects.ExerciseType;
import letshangllc.allfitness.ClassObjects.MuscleGroup;
import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.Dialogs.AddExerciseDialog;
import letshangllc.allfitness.Dialogs.EditExerciseDialog;
import letshangllc.allfitness.ListViewAdapters.ExerciseListAdapter;
import letshangllc.allfitness.ClassObjects.ExerciseItem;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExercisesFragment extends Fragment {
    private static String TAG = ExercisesFragment.class.getSimpleName();

    /* List of all the user's exercises */
    private ArrayList<ExerciseItem> exerciseItems;

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
        exerciseItems = new ArrayList<>();
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
        exerciseListAdapter = new ExerciseListAdapter(getContext(), exerciseItems);
        /* set the adapter for the listview */
        lv_exercises.setAdapter(exerciseListAdapter);

        /* Create click listener for list view to go to exercise activity */
        lv_exercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Pass routine activity the id for the selected routine */
                Intent intent = new Intent(ExercisesFragment.this.getActivity(), ExerciseActivity.class);
                ExerciseItem exerciseItem = exerciseListAdapter.getItem(position);

                /* Put extras in the intent */
                intent.putExtra(getString(R.string.exercise_id), exerciseItem.getExerciseID());
                intent.putExtra(getString(R.string.intent_value_name), exerciseItem.getExerciseName());
                intent.putExtra(getString(R.string.type_id), exerciseItem.getExerciseType().getExerciseTypeID());

                startActivity(intent);
            }
        });


        /* Attach context menu to listview */
        registerForContextMenu(lv_exercises);
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
        String[] projection = {TableConstants.ExerciseId, TableConstants.ExerciseName, TableConstants.ExerciseType,
            TableConstants.MuscleID};
        Cursor c = db.query(TableConstants.ExerciseTableName, projection, null, null, null, null, null);
        c.moveToFirst();

        while (c!= null && !c.isAfterLast()) {
            ExerciseType exerciseType = ExerciseType.getType(c.getInt(2));
            exerciseItems.add(new ExerciseItem(c.getInt(0), c.getString(1), exerciseType, c.getInt(3)));
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
                      contentValues.put(TableConstants.ExerciseName, name);
                      contentValues.put(TableConstants.ExerciseType, exerciseType.getExerciseTypeID());
                      contentValues.put(TableConstants.MuscleID, muscleGroup.getMuscleGroupId());

                      /* Insert into and close the Database */
                      db.insert(TableConstants.ExerciseTableName, null, contentValues);
                      db.close();

                      /* Add exercise to arraylist and update listview */
                      exerciseItems.add(new ExerciseItem(getMaxExerciseId(),
                              name, exerciseType, muscleGroup.getMuscleGroupId()));
                      exerciseListAdapter.notifyDataSetChanged();
                  }
              }
        );
        addExerciseDialog.show(getFragmentManager(), "Add_Exercise");
    }

    /* Get the id of the last inputted exercise */
    private int getMaxExerciseId(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT Max("+ TableConstants.ExerciseId +") FROM "+ TableConstants.ExerciseTableName;
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
                /* Set search text to "" or else it will cause a bug */
                et_searchExercises.setText("");
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

    /* Delete exercise Item from DB */
    private void deleteFromDatabase(ExerciseItem exerciseItem){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int exersiceID = exerciseItem.getExerciseID();

        /* Delete from Exercise table and routines table by using exercise id */
        db.delete(TableConstants.ExerciseTableName, TableConstants.ExerciseId + " = " +exersiceID, null);
        db.delete(TableConstants.RoutinesTableName, TableConstants.ExerciseId + " = " +exersiceID, null);

        db.close();

        /* Remove item from list and update list view */
        exerciseItems.remove(exerciseItem);
        exerciseListAdapter.notifyDataSetChanged();
    }

    /* Open EditDialog and edit the selected exercise Item */
    private void editItem(final ExerciseItem exerciseItem){
        final EditExerciseDialog editExerciseDialog = new EditExerciseDialog();
        editExerciseDialog.setExercise(exerciseItem);
        editExerciseDialog.setCallback(new EditExerciseDialog.Listener() {
            @Override
            public void onDialogPositiveClick(String name, ExerciseType exerciseType, MuscleGroup muscleGroup) {
                if(name.isEmpty())return;
                /* Get db*/
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                /* Get the type */
                //ExerciseType exerciseType = ExerciseType.valueOf(type.toUpperCase());

                /* Put in the new values */
                values.put(TableConstants.ExerciseName, name);
                values.put(TableConstants.ExerciseType, exerciseType.getExerciseTypeID());
                values.put(TableConstants.MuscleID, muscleGroup.getMuscleGroupId());

                /* Update database on exercise id */
                db.update(TableConstants.ExerciseTableName, values,
                        TableConstants.ExerciseId + " = " + exerciseItem.getExerciseID(), null);
                db.close();

                /* update item in fragment context*/
                exerciseItem.setExerciseName(name);
                exerciseItem.setExerciseType(exerciseType);
                exerciseItem.setMuscleId(muscleGroup.getMuscleGroupId());

                /* Update List view with new information */
                exerciseListAdapter.notifyDataSetChanged();
            }
        });

        editExerciseDialog.show(getFragmentManager(), "Edit_Exercise");
    }

    /* Create context menu upon holding down listview row */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.menu_context_edit_delete, menu);

    }

    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            // context menu logic
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            switch(item.getItemId()){
                case R.id.delete:
                /* todo confirm that the user wants to delete item */
                    deleteFromDatabase(exerciseItems.get(info.position));
                    break;
                case R.id.edit:
                    editItem(exerciseItems.get(info.position));
                    break;

            }
            return true;
        }

        return false;
    }

}
