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

import letshangllc.allfitness.ActivitiesAndFragments.Activities.RoutineActivity;
import letshangllc.allfitness.ClassObjects.Routine;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;
import letshangllc.allfitness.Dialogs.AddRoutineDialog;
import letshangllc.allfitness.Dialogs.EditItemNameDialog;
import letshangllc.allfitness.adapters.RoutineListAdapter;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoutinesFragment extends Fragment {
    private String TAG = RoutinesFragment.class.getSimpleName();

    /* Listview Adapter */
    RoutineListAdapter routineListAdapter;

    /* Views */
    private ListView lv_routines;
    private FloatingActionButton fab_addRoutine;

    /* Arraylist data */
    private ArrayList<Routine> routines;

    /* Database Helper */
    private DatabaseHelper databaseHelper;

    public RoutinesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Initialize DB Helper */
        databaseHelper =  new DatabaseHelper(getContext());

        /* Create initial Array list and get data from DB */
        routines = new ArrayList<>();
        getExistingData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_routine, container, false);

        findViews(view);

        /* initialize adapter */
        routineListAdapter = new RoutineListAdapter(getContext(), routines);
        lv_routines.setAdapter(routineListAdapter);

        /* Create click listener for list view to go to routine activity */
        lv_routines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* Pass routine activity the id for the selected routine */
                Intent intent = new Intent(RoutinesFragment.this.getActivity(), RoutineActivity.class);
                Routine routine = routineListAdapter.getItem(position);
                int routineId= routine.getRoutineId();
                intent.putExtra(getString(R.string.routine_id), routineId);
                intent.putExtra(getString(R.string.intent_value_name), routine.getRoutineName());
                startActivity(intent);
            }
        });

        registerForContextMenu(lv_routines);
        return view;
    }

    /* Find Views and add listeners */
    private void findViews(View view){
        lv_routines = (ListView) view.findViewById(R.id.lv_routines);
        fab_addRoutine = (FloatingActionButton) view.findViewById(R.id.fab_routine);

        fab_addRoutine.setOnClickListener(new View.OnClickListener() {
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
        String[] projection = {TableConstants.RoutineId, TableConstants.RoutineName};
        Cursor c = db.query(TableConstants.RoutineTableName, projection, null,
                null, null, null, null);
        c.moveToFirst();

        while (!c.isAfterLast()){
            routines.add(new Routine(c.getInt(0), c.getString(1)));
            c.moveToNext();
        }
        c.close();
        db.close();
    }

    private void openAddDialog(){
        final AddRoutineDialog addRoutineDialog = new AddRoutineDialog();
        addRoutineDialog.setCallback(new AddRoutineDialog.Listener() {
             @Override
             public void onDialogPositiveClick(String name) {
                 if(name.isEmpty())return;
                  /* Create an instance of the Writable DB */
                 SQLiteDatabase db = databaseHelper.getWritableDatabase();

                  /* Create the values to be inserted into the Database */
                 ContentValues contentValues =  new ContentValues();
                 contentValues.put(TableConstants.RoutineName, name);

                  /* Insert into and close the Database */
                 db.insert(TableConstants.RoutineTableName, null, contentValues);
                 db.close();

                 /* Add exercise to arraylist and update listview */
                 routines.add(new Routine(getMaxRotuineId(), name));
                 routineListAdapter.notifyDataSetChanged();
             }
         }
        );
        addRoutineDialog.show(getFragmentManager(), "Add_Muscle_Group");
    }

    /* Get the id of the last inputted muscle */
    private int getMaxRotuineId(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT Max("+ TableConstants.RoutineId+") FROM "+ TableConstants.RoutineTableName;
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
        /* Check if the current fragment is visible. If so then show its context menu */
        if (getUserVisibleHint()) {
            // context menu logic
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            switch(item.getItemId()){
                case R.id.delete:
                    confirmDelete(routines.get(info.position));
                    break;
                case R.id.edit:
                    editItem(routines.get(info.position));
                    break;

            }
            return true;
        }

        return false;
    }

    public void confirmDelete(final Routine routine){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.confirm_delete));

        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteFromDatabase(routine);
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

    /* Delete routine Item from DB */
    private void deleteFromDatabase(Routine routine){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int routineId = routine.getRoutineId();

        /* Delete from Exercise table and routines table by using exercise id */
        db.delete(TableConstants.RoutineTableName, TableConstants.RoutineId + " = " + routineId, null);
        db.delete(TableConstants.RoutinesTableName, TableConstants.RoutineId + " = " + routineId, null);

        /* Update the exercises that were in this muscle group  */
        ContentValues values = new ContentValues();

        db.close();

        /* Remove item from list and update list view */
        routines.remove(routine);
        routineListAdapter.notifyDataSetChanged();
    }

    /* Open EditDialog and edit the selected routine Item */
    private void editItem(final Routine routine){
        final EditItemNameDialog editItemNameDialog = new EditItemNameDialog();
        editItemNameDialog.setDialogTitle(getString(R.string.routine_dialog_edit_title));
        editItemNameDialog.setItemName(routine.getRoutineName());
        editItemNameDialog.setCallback(new EditItemNameDialog.Listener() {
            @Override
            public void onDialogPositiveClick(String name) {
                if(name.isEmpty())return;
                /* Get db*/
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                /* Put in the new values */
                values.put(TableConstants.RoutineName, name);

                /* Update database on routine id */
                db.update(TableConstants.RoutineTableName, values,
                        TableConstants.RoutineId + " = " +  routine.getRoutineId(), null);
                db.close();

                /* update item in fragment context*/
                routine.setRoutineName(name);

                /* Update List view with new information */
                routineListAdapter.notifyDataSetChanged();
            }
        });

        editItemNameDialog.show(getFragmentManager(), "Edit_MuscleGroup");
    }

}
