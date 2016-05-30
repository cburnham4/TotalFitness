package letshangllc.allfitness.ActivitiesAndFragments.MainFragments;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import letshangllc.allfitness.ActivitiesAndFragments.Activities.MuscleGroupActivity;
import letshangllc.allfitness.ClassObjects.LiftSet;
import letshangllc.allfitness.ClassObjects.MuscleGroup;
import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.Dialogs.AddMuscleGroupDialog;
import letshangllc.allfitness.Dialogs.EditItemNameDialog;
import letshangllc.allfitness.ListViewAdapters.MuscleGroupListAdapter;
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

        /* Register listview to allow for context menu */
        registerForContextMenu(lv_groups);

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

        registerForContextMenu(lv_groups);
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

    /* Create context menu upon holding down listview row */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(getUserVisibleHint()){
            getActivity().getMenuInflater().inflate(R.menu.menu_context_edit_delete, menu);
        }
    }

    /* Perform instructions based on the clicked item */
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            // context menu logic
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            switch(item.getItemId()){
                case R.id.delete:
                    confirmDelete(muscleGroups.get(info.position));
                    break;
                case R.id.edit:
                    editItem(muscleGroups.get(info.position));
                    break;

            }
            return true;
        }

        return false;
    }

    public void confirmDelete(final MuscleGroup muscleGroup){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.confirm_delete));

        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFromDatabase(muscleGroup);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /* Delete musclegroup Item from DB */
    private void deleteFromDatabase(MuscleGroup muscleGroup){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int muscleID = muscleGroup.getMuscleGroupId();

        /* Delete from Exercise table and routines table by using exercise id */
        db.delete(TableConstants.MuscleTableName, TableConstants.MuscleID + " = " + muscleID, null);

        /* Update the exercises that were in this muscle group  */
        ContentValues values = new ContentValues();

       /* Put the fake muscle id for the group for the exercise that now has no musclegroup */
        values.put(TableConstants.MuscleID, getResources().getInteger(R.integer.fake_muscle_group_id));

        /* Update database on muscle id */
        db.update(TableConstants.ExerciseTableName, values,
                TableConstants.MuscleID + " = " + muscleID, null);
        db.close();

        /* Remove item from list and update list view */
        muscleGroups.remove(muscleGroup);
        muscleGroupListAdapter.notifyDataSetChanged();
    }

    /* Open EditDialog and edit the selected muscleGroup Item */
    private void editItem(final MuscleGroup muscleGroup){
        final EditItemNameDialog editItemNameDialog = new EditItemNameDialog();
        editItemNameDialog.setDialogTitle(getString(R.string.muscle_group_edit_title));
        editItemNameDialog.setItemName(muscleGroup.getMuscleGroupName());
        editItemNameDialog.setCallback(new EditItemNameDialog.Listener() {
            @Override
            public void onDialogPositiveClick(String name) {
                if(name.isEmpty())return;
                /* Get db*/
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                /* Put in the new values */
                values.put(TableConstants.MuscleName, name);

                /* Update database on exercise id */
                db.update(TableConstants.MuscleTableName, values,
                        TableConstants.MuscleID+ " = " + muscleGroup.getMuscleGroupId(), null);
                db.close();

                /* update item in fragment context*/
                muscleGroup.setMuscleGroupName(name);

                /* Update List view with new information */
                muscleGroupListAdapter.notifyDataSetChanged();
            }
        });

        editItemNameDialog.show(getFragmentManager(), "Edit_MuscleGroup");
    }


}
