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
import letshangllc.allfitness.Dialogs.AddExerciseToRoutineDialog;
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

        /* Retrieve routineId from caller intent */
        routineId = getIntent().getIntExtra(getString(R.string.routine_id), 0);

        /* Initialize DB Helper */
        databaseHelper = new DatabaseHelper(this);

        exerciseItems = new ArrayList<>();
        /* Get Extisting Data */
        getExistingData();

        this.findViews();

        exerciseListAdapter = new ExerciseListAdapter(this, exerciseItems);
        lv_routineExercises.setAdapter(exerciseListAdapter);
    }

    /* Get Existing Data */
    public void getExistingData(){
        /* Select the exercises that are in the current routine */
        String SQL = "SELECT " + TableConstants.ExerciseId + //", " + TableConstants.ExerciseName + ", "
                //+ TableConstants.ExerciseType +" , " + TableConstants.MuscleID +
                " FROM " /*+ TableConstants.ExerciseTableName +" , " */+ TableConstants.RoutinesTableName
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        AddExerciseToRoutineDialog addExerciseToRoutineDialog = new AddExerciseToRoutineDialog();
        addExerciseToRoutineDialog.setCallback(new AddExerciseToRoutineDialog.Listener() {
            @Override
            public void onDialogPositiveClick(ExerciseItem exerciseItem) {
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
        addExerciseToRoutineDialog.show(getSupportFragmentManager(), "ADD_EXERCISE_ROUTINE");
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
}
