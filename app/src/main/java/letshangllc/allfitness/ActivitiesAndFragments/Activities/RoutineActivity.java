package letshangllc.allfitness.ActivitiesAndFragments.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

import letshangllc.allfitness.ClassObjects.ExerciseItem;
import letshangllc.allfitness.ClassObjects.ExerciseType;

import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.Dialogs.AddExerciseToGroupDialog;
import letshangllc.allfitness.ListViewAdapters.ExerciseListAdapter;
import letshangllc.allfitness.R;

/* todo allow for edit of listview poistions */
public class RoutineActivity extends AppCompatActivity {
    /* Layout Views */
    private Toolbar toolbar;
    private ListView lv_routineExercises;
    private FloatingActionButton fab_addExercise;

    /* arraylist to contain routines */
    private ArrayList<ExerciseItem> exerciseItems;

    /* id for current routine */
    private int routineId;

    /* Database Helper */
    private DatabaseHelper databaseHelper;

    /* Listview Adapter */
    ExerciseListAdapter exerciseListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routines);

        this.findViews();
        /* Retrieve values from caller intent */
        routineId = getIntent().getIntExtra(getString(R.string.routine_id), 0);
        String routineName = getIntent().getStringExtra(getString(R.string.intent_value_name));

        setTitle(routineName);
        /* Initialize DB Helper */
        databaseHelper = new DatabaseHelper(this);

        exerciseItems = new ArrayList<>();
        /* Get Extisting Data */
        getExistingData();

        /* setup list view */
        exerciseListAdapter = new ExerciseListAdapter(this, exerciseItems);
        lv_routineExercises.setAdapter(exerciseListAdapter);
        registerForContextMenu(lv_routineExercises);

        /* Create click listener for list view to go to exercise activity */
        lv_routineExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Pass exercise data to exercise activity */
                Intent intent = new Intent(RoutineActivity.this, ExerciseActivity.class);
                ExerciseItem exerciseItem = exerciseListAdapter.getItem(position);

                /* Put extras in the intent */
                intent.putExtra(getString(R.string.exercise_id), exerciseItem.getExerciseID());
                intent.putExtra(getString(R.string.intent_value_name), exerciseItem.getExerciseName());
                intent.putExtra(getString(R.string.type_id), exerciseItem.getExerciseType().getExerciseTypeID());

                startActivity(intent);
            }
        });
    }

    /* Get Existing Data */
    public void getExistingData(){
        /* Select the exercises that are in the current routine */
        String SQL = "SELECT " + TableConstants.ExerciseTableName+ "."+TableConstants.ExerciseId
                + ", " + TableConstants.ExerciseName + ", "
                + TableConstants.ExerciseType +" , " + TableConstants.MuscleID
                + " FROM " + TableConstants.ExerciseTableName
                + " INNER JOIN " + TableConstants.RoutinesTableName + " ON "
                + TableConstants.ExerciseTableName+ "."+TableConstants.ExerciseId + " = "
                + TableConstants.RoutinesTableName + "." + TableConstants.ExerciseId
                + " WHERE " + TableConstants.RoutineId + " = " + routineId +"";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        /* Run the query */
        Cursor c = db.rawQuery(SQL, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            ExerciseType exerciseType = ExerciseType.getType(c.getInt(2));
            exerciseItems.add(new ExerciseItem(c.getInt(0), c.getString(1), exerciseType, c.getInt(3)));
            c.moveToNext();
        }
        c.close();
        db.close();

    }

    /* find and initialize views with listeners */
    private void findViews(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Go back to previous state of activity */
        if(toolbar != null){getSupportActionBar().setDisplayHomeAsUpEnabled(true);}
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        lv_routineExercises = (ListView) findViewById(R.id.lv_routine_exercises);
        fab_addExercise = (FloatingActionButton) findViewById(R.id.fab_add_exercise_routine);

        fab_addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDialog();
            }
        });
    }

    /* todo change to listview item select */
    private void openAddDialog(){
        AddExerciseToGroupDialog addExerciseToGroupDialog = new AddExerciseToGroupDialog();
        addExerciseToGroupDialog.setCallback(new AddExerciseToGroupDialog.Listener() {
            @Override
            public void onDialogPositiveClick(ExerciseItem exerciseItem) {
                /* If the exercise is already a part of the routine then do not add it */
                if(routineContainsExercise(exerciseItem)){
                    Toast.makeText(RoutineActivity.this, exerciseItem.getExerciseName()
                            + " already exist in this routine.", Toast.LENGTH_SHORT).show();
                    return;
                }
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                /* Create values to put into database */
                ContentValues values =  new ContentValues();
                values.put(TableConstants.RoutineId, routineId);
                values.put(TableConstants.ExerciseId, exerciseItem.getExerciseID());

                /* Insert values into db */
                db.insert(TableConstants.RoutinesTableName, null, values);
                db.close();

                exerciseItems.add(getExerciseFromId(exerciseItem.getExerciseID()));
                exerciseListAdapter.notifyDataSetChanged();
            }
        });
        addExerciseToGroupDialog.show(getSupportFragmentManager(), "ADD_EXERCISE_ROUTINE");
    }

    /* Get Exercise via its id */
    private ExerciseItem getExerciseFromId(int exerciseID){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {TableConstants.ExerciseId, TableConstants.ExerciseName, TableConstants.ExerciseType,
                TableConstants.MuscleID};

        /* Query the data to get the exercise based on its id */
        Cursor c = db.query(TableConstants.ExerciseTableName, projection, TableConstants.ExerciseId
                + " = " + exerciseID, null, null, null, null);
        c.moveToFirst();

        /* Create a new exercise based on the data */
        ExerciseType exerciseType = ExerciseType.getType(c.getInt(2));
        ExerciseItem exerciseItem= new ExerciseItem(c.getInt(0), c.getString(1), exerciseType, c.getInt(3));

        c.close();
        db.close();
        /* Return item */
        return exerciseItem;
    }

    /* Create context menu upon holding down listview row */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context_routine_exercise, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.remove:
                removeFromRoutine(exerciseItems.get(info.position));
                break;
        }
        return true;
    }

    /* Remove the exercise passed in from the current routine */
    public void removeFromRoutine(ExerciseItem exerciseItem){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int exersiceID = exerciseItem.getExerciseID();

        /* Delete from Exercise table and routines table by using exercise id */
        db.delete(TableConstants.RoutinesTableName, TableConstants.ExerciseId + " = " +exersiceID
                + "  AND " + TableConstants.RoutineId + " = " + routineId, null);
        db.close();

        /* Remove item from list and update list view */
        exerciseItems.remove(exerciseItem);
        exerciseListAdapter.notifyDataSetChanged();
    }

    /* Return true if the exercise already exist for this routine */
    public boolean routineContainsExercise(ExerciseItem exerciseItem){
        /* Iterate through each item in the exerciseItems list */
        for(ExerciseItem item: exerciseItems){
            if(item.getExerciseID() == exerciseItem.getExerciseID()){
                return true;
            }
        }
        return false;
    }/* Note: ArrayList.contains does not work for this */
}
