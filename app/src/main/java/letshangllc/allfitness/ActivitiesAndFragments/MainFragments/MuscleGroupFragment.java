package letshangllc.allfitness.ActivitiesAndFragments.MainFragments;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import letshangllc.allfitness.ActivitiesAndFragments.Activities.MuscleGroupActivity;
import letshangllc.allfitness.ActivitiesAndFragments.Activities.RoutineActivity;
import letshangllc.allfitness.ClassObjects.ExerciseItem;
import letshangllc.allfitness.ClassObjects.ExerciseType;
import letshangllc.allfitness.ClassObjects.MuscleGroup;
import letshangllc.allfitness.ClassObjects.Routine;
import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.Dialogs.AddExerciseDialog;
import letshangllc.allfitness.Dialogs.AddMuscleGroupDialog;
import letshangllc.allfitness.ListViewAdapters.MuscleGroupListAdapter;
import letshangllc.allfitness.MockData.MockedGroups;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MuscleGroupFragment extends Fragment {

    /* Arrayslist containing routine */
    private ArrayList<MuscleGroup> muscleGroups;

    /* Listview Adapter */
    private MuscleGroupListAdapter muscleGroupListAdapter;

    /* Views */
    private ListView lv_groups;
    private FloatingActionButton fab_addGroup;

    /* Database Helper */
    private DatabaseHelper databaseHelper;

    public MuscleGroupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Initialize DB Helper */
        databaseHelper =  new DatabaseHelper(getContext());

        /* Create initial Array list and get data from DB */
        muscleGroups = new ArrayList<>();
        getExistingData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_muscle_group, container, false);

        findViews(view);

        /* Initialize Adapter */
        muscleGroupListAdapter = new MuscleGroupListAdapter(this.getContext(), muscleGroups);

        /* Set adapter */
        lv_groups.setAdapter(muscleGroupListAdapter);

                /* Create click listener for list view to go to routine activity */
        lv_groups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Pass routine activity the id for the selected routine */
                Intent intent = new Intent(MuscleGroupFragment.this.getActivity(), MuscleGroupActivity.class);
                MuscleGroup muscleGroup = muscleGroupListAdapter.getItem(position);
                int muscleGroupId = muscleGroup.getMuscleGroupId();
                intent.putExtra(getString(R.string.muscle_group_id), muscleGroupId);
                intent.putExtra(getString(R.string.intent_value_name), muscleGroup.getMuscleGroupName());
                startActivity(intent);
            }
        });
        return view;
    }

    /* Find Views */
    public void findViews(View view){
        lv_groups = (ListView) view.findViewById(R.id.lv_muscleGroups);
        fab_addGroup = (FloatingActionButton) view.findViewById(R.id.fab_muscleGroup);
        fab_addGroup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                openAddDialog();
            }
        });
    }


    /* Read in the existing muscle groups from the database and insert into the arraylist */
    private void getExistingData(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
         /* Query the db to get the muscle data */
        String[] projection = {TableConstants.MuscleID, TableConstants.MuscleName};
        Cursor c = db.query(TableConstants.MuscleTableName, projection, null,
                null, null, null, null);
        c.moveToFirst();

        while (!c.isAfterLast()){
            muscleGroups.add(new MuscleGroup(c.getInt(0), c.getString(1)));
            c.moveToNext();
        }
        c.close();
        db.close();
    }

    private void openAddDialog(){
        final AddMuscleGroupDialog addMuscleGroupDialog = new AddMuscleGroupDialog();
        addMuscleGroupDialog.setCallback(new AddMuscleGroupDialog.Listener() {
                 @Override
                 public void onDialogPositiveClick(String name) {
                 if(name.isEmpty())return;
                  /* Create an instance of the Writable DB */
                 SQLiteDatabase db = databaseHelper.getWritableDatabase();

                  /* Create the values to be inserted into the Database */
                 ContentValues contentValues =  new ContentValues();
                 contentValues.put(TableConstants.MuscleName, name);

                  /* Insert into and close the Database */
                 db.insert(TableConstants.MuscleTableName, null, contentValues);
                 db.close();

                 /* Add exercise to arraylist and update listview */
                 muscleGroups.add(new MuscleGroup(getMaxMuscleId(), name));
                 muscleGroupListAdapter.notifyDataSetChanged();
                 }
             }
        );
        addMuscleGroupDialog.show(getFragmentManager(), "Add_Muscle_Group");
    }

    /* Get the id of the last inputted muscle */
    private int getMaxMuscleId(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT Max("+ TableConstants.MuscleID +") FROM "+ TableConstants.MuscleTableName;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int max = c.getInt(0);
        c.close();
        db.close();
        return max;
    }


}
