package letshangllc.allfitness.ActivitiesAndFragments.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import letshangllc.allfitness.ClassObjects.ExerciseItem;
import letshangllc.allfitness.ClassObjects.ExerciseType;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;
import letshangllc.allfitness.adapters.ExerciseListAdapter;
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
        String muscleGroupName = getIntent().getStringExtra(getString(R.string.intent_value_name));

        setTitle(muscleGroupName);
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

        /* Create click listener for list view to go to exercise activity */
        lv_muscleGroupExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Pass exercise data to exercise activity */
                Intent intent = new Intent(MuscleGroupActivity.this, ExerciseActivity.class);
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
        /* Select the exercises that are in the current muscle group */
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {TableConstants.ExerciseId, TableConstants.ExerciseName, TableConstants.ExerciseType,
                TableConstants.MuscleID};

        /* Query the exercise table based on the muscle id to get all the associated exercises */
        Cursor c = db.query(TableConstants.ExerciseTableName, projection, TableConstants.MuscleID
                + " = " + muscleGroupId, null, null, null, null);
        c.moveToFirst();

        /* Put all the exercises in an arraylist */
        while (!c.isAfterLast()) {
            ExerciseType exerciseType = ExerciseType.getType(c.getInt(2));
            exerciseItems.add(new ExerciseItem(c.getInt(0), c.getString(1), exerciseType));//, c.getInt(3)
            c.moveToNext();
        }
        c.close();
        db.close();

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
