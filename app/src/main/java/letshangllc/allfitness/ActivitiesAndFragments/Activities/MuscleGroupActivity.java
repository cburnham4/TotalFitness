package letshangllc.allfitness.ActivitiesAndFragments.Activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import letshangllc.allfitness.ClassObjects.ExerciseItem;
import letshangllc.allfitness.ClassObjects.ExerciseType;
import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.Dialogs.AddExerciseToGroupDialog;
import letshangllc.allfitness.ListViewAdapters.ExerciseListAdapter;
import letshangllc.allfitness.R;

public class MuscleGroupActivity extends AppCompatActivity {
    /* Layout Views */
    private Toolbar toolbar;
    private ListView lv_muscleGroupExercises;
    private FloatingActionButton fab_addExercise;

    /* arraylist to contain exercise items */
    private ArrayList<ExerciseItem> exerciseItems;

    /* id for current muscleGroup */
    private int muscleGroupId;

    /* Database Helper */
    private DatabaseHelper databaseHelper;

    /* Listview Adapter */
    ExerciseListAdapter exerciseListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muscle_group);

        this.findViews();
        /* Retrieve values from caller intent */
        muscleGroupId = getIntent().getIntExtra(getString(R.string.muscle_group_id), 0);
        String routineName = getIntent().getStringExtra(getString(R.string.intent_value_name));

        setTitle(routineName);
        /* Initialize DB Helper */
        databaseHelper = new DatabaseHelper(this);

        exerciseItems = new ArrayList<>();
        /* Get Extisting Data */
        getExistingData();

        /* setup list view */
        exerciseListAdapter = new ExerciseListAdapter(this, exerciseItems);
        lv_muscleGroupExercises.setAdapter(exerciseListAdapter);
        registerForContextMenu(lv_muscleGroupExercises);
    }

    /* find and initialize views with listeners */
    private void findViews(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Go back to previous state of activity */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        lv_muscleGroupExercises = (ListView) findViewById(R.id.lv_muscle_exercises);
        fab_addExercise = (FloatingActionButton) findViewById(R.id.fab_add_exercise_muscle_group);

        fab_addExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddDialog();
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
                + " INNER JOIN " + TableConstants.MuscleGroupsTableName + " ON "
                + TableConstants.ExerciseTableName+ "."+TableConstants.ExerciseId + " = "
                + TableConstants.MuscleGroupsTableName + "." + TableConstants.ExerciseId
                + " WHERE " + TableConstants.RoutineId + " = " + muscleGroupId +"";

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        /* Run the query */
        Cursor c = db.rawQuery(SQL, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            ExerciseType exerciseType = ExerciseType.getType(c.getInt(2));
            exerciseItems.add(new ExerciseItem(c.getInt(0), c.getString(1), exerciseType));//, c.getInt(3)
            c.moveToNext();
        }
        c.close();
        db.close();

    }

    /* todo change to listview item select */
    private void openAddDialog(){
        AddExerciseToGroupDialog addExerciseToGroupDialog = new AddExerciseToGroupDialog();
        addExerciseToGroupDialog.setCallback(new AddExerciseToGroupDialog.Listener() {
            @Override
            public void onDialogPositiveClick(ExerciseItem exerciseItem) {
                SQLiteDatabase db = databaseHelper.getWritableDatabase();

                /* Create values to put into database */
                ContentValues values =  new ContentValues();
                values.put(TableConstants.MuscleID, muscleGroupId);
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
        ExerciseItem exerciseItem= new ExerciseItem(c.getInt(0), c.getString(1), exerciseType);//, c.getInt(3)

        c.close();
        db.close();
        /* Return item */
        return exerciseItem;
    }
}
