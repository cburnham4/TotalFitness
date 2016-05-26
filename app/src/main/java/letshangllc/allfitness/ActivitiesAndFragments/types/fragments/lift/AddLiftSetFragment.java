package letshangllc.allfitness.ActivitiesAndFragments.types.fragments.lift;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import letshangllc.allfitness.ClassObjects.LiftSet;
import letshangllc.allfitness.ClassObjects.Routine;
import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.ListViewAdapters.SetListAdapter;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddLiftSetFragment extends Fragment {
    /* Instance variables */
    private static final String TAG = AddLiftSetFragment.class.getSimpleName();
    /* Passed in lift variables */
    private int exerciseId;

    /* Current Date variable */
    String currentDate;

    /* Database Helper */
    DatabaseHelper databaseHelper;

    /* Day Id */
    int dayId;

    /* ArrayList of sets */
    ArrayList<LiftSet> liftSets;

    /* Views */
    EditText repCount;
    EditText weightCount;
    ListView lv_setList;

    /* ListView Adapter */
    SetListAdapter setListAdapter;

    /* Add set listeners */
    AddLiftSetListener addLiftSetListener;
    DeleteLiftSetListner deleteLiftSetListner;
    EditLiftSetListner editLiftSetListner;

    /* Boolean  and id for if the liftSet is being editted */
    boolean editing;
    int editId;
    LiftSet editLiftSet;

    public AddLiftSetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        editing = false;

        liftSets = new ArrayList<>();

        exerciseId = args.getInt(getString(R.string.exercise_id), 0);

        databaseHelper = new DatabaseHelper(this.getContext());

        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);
        Date date = new Date();
        currentDate = dateFormat.format(date);

        dayId = addDateToDB();

        getExistingData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_lift_set, container, false);

        this.setupViews(view);

        /* Set listview Adapter */
        setListAdapter =  new SetListAdapter(this.getContext(), liftSets);
        lv_setList.setAdapter(setListAdapter);

        registerForContextMenu(lv_setList);
        return view;
    }

    /* find view and set their listeners */
    private void setupViews(View view){
        /* Find Views */
        Button addSet =(Button) view.findViewById(R.id.btn_addSet);
        Button addRep = (Button) view.findViewById(R.id.btn_addRep);
        Button subRep = (Button) view.findViewById(R.id.btn_subRep);
        Button subWeight = (Button) view.findViewById(R.id.btn_subWeight);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_clearValues);
        final Button addWeight = (Button) view.findViewById(R.id.btn_addWeight);
        repCount = (EditText) view.findViewById(R.id.et_reps);
        weightCount = (EditText) view.findViewById(R.id.et_weight);

        lv_setList = (ListView) view.findViewById(R.id.lv_setsList);

        /* Set up listeners */
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repCount.setText("0");
                weightCount.setText("0");
            }
        });

        addRep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rep = Integer.parseInt(repCount.getText().toString());
                repCount.setText(rep+1 +"");
            }
        });
        subRep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rep = Integer.parseInt(repCount.getText().toString());
                if(rep !=0){
                    repCount.setText(rep - 1 + "");
                }
            }
        });
        addWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double weight = Double.parseDouble(weightCount.getText().toString());
                weightCount.setText(String.format(Locale.US, "%.1f", weight+5));
            }
        });
        subWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double weight = Double.parseDouble(weightCount.getText().toString());
                if(weight >= 5){
                    weightCount.setText(String.format(Locale.US, "%.1f", weight-5));
                }

            }
        });

        addSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!weightCount.getText().toString().isEmpty() && !repCount.getText().toString().isEmpty()){

                    double weight = Double.parseDouble(weightCount.getText().toString());
                    int reps = Integer.parseInt(repCount.getText().toString());
                    if(weight == 0 || reps == 0){
                        Toast.makeText(getContext(), "Atleast one attribute is 0", Toast.LENGTH_SHORT).show();
                        if (editing){
                            editing = false;
                            Toast.makeText(getContext(), "You are no longer in edit mode", Toast.LENGTH_SHORT).show();
                        }
                    }else if (!editing){
                        /* if not editing than add to db (*/
                        addToDB(weight, reps);
                    }else{ /* If editing then add to db */
                        updateLiftSet(weight, reps);
                        editing = false;
                    }

                }
            }
        });
    }

    public void getExistingData(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
         /* Query the db to get the muscle data */
        String[] projection = {TableConstants.SetsId, TableConstants.SetReps,  TableConstants.SetWeight};
        Cursor c = db.query(TableConstants.SetsTableName, projection, TableConstants.DayId +" = "+ dayId,
                null, null, null, null);
        c.moveToFirst();

        while (!c.isAfterLast()){
            liftSets.add(new LiftSet(dayId, c.getInt(0), c.getInt(1), c.getDouble(2)));
            c.moveToNext();
        }
        c.close();
        db.close();
    }

    /* Add the current set to the db */
    public void addToDB(double weight, int reps){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //PUT SET INTO SETS
        ContentValues values = new ContentValues();
        values.put(TableConstants.DayId, dayId);
        values.put(TableConstants.SetReps, reps);
        values.put(TableConstants.SetWeight, weight);

        db.insert(TableConstants.SetsTableName, null,values);

        /* Add new set into the listview */
        int sid = getMaxSetId();
        Log.e(TAG, "Insert DAYID= "+dayId);

        LiftSet liftSet = new LiftSet(dayId, sid, reps, weight);
        liftSets.add(liftSet);

        setListAdapter.notifyDataSetChanged();

        /* Call the callback with the new listSet */
        addLiftSetListener.addNewLiftSet(liftSet);

        /* Add recent lift to Maxes */
        inputCalculatedMAX(weight, reps, sid);
    }

    /* Add date to db id it does not already exist */
    public int addDateToDB(){
        /* First check if the db row has already been created */
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {TableConstants.DayId};

        /* Query the exercise table based on the muscle id to get all the associated exercises */
        Cursor c = db.query(TableConstants.DayTableName, projection, TableConstants.DayDateLifted
                + " = '" + currentDate + "' AND " + TableConstants.ExerciseId +" = "+ exerciseId, null, null, null, null);

        c.moveToFirst();
        /* If there already exists a dayId for today then return it */
        if(!c.isAfterLast()){
            Log.e(TAG, "Day exists");
            int dayId = c.getInt(0);
            c.close();
            return dayId;
        }

        Log.e(TAG, "Day does not exist");

         /* Else insert in a new day */
        ContentValues values = new ContentValues();
        values.put(TableConstants.ExerciseId, exerciseId);
        values.put(TableConstants.DayDateLifted, currentDate);

         /* Insert values into db */
        db.insert(TableConstants.DayTableName, null, values);
        db.close();

        /* Return the max Day id which will be the most recently inserted dayId */
        return getMaxDayId();

    }

    /* Get the id of the last Day Id */
    private int getMaxDayId(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT Max("+ TableConstants.DayId +") FROM "+ TableConstants.DayTableName;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int max = c.getInt(0);
        c.close();
        db.close();
        return max;
    }

    /* Get the id of the last Day Id */
    private int getMaxSetId(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String sql = "SELECT Max("+ TableConstants.SetsId +") FROM "+ TableConstants.SetsTableName;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int max = c.getInt(0);
        c.close();
        db.close();
        return max;
    }

    private void inputCalculatedMAX(double weight, int reps, int sid){
        /*
            max calculations taken from http://www.weightrainer.net/training/coefficients.html
         */
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        double max;

        /* set the max baed on the number of reps */
        switch(reps){
            case 1:
                max = weight;
                break;
            case 2:
                max = weight*1.042;
                break;
            case 3:
                max = weight*1.072;
                break;
            case 4:
                max = weight*1.104;
                break;
            case 5:
                max = weight*1.137;
                break;
            case 6:
                max = weight *1.173;
                break;
            case 7:
                max = weight * 1.211;
                break;
            case 8:
                max = weight * 1.251;
                break;
            case 9:
                max = weight * 1.294;
                break;
            default:
                max = weight*1.341;
        }

        /* Prepare the values to be inserted */
        ContentValues values = new ContentValues();

        values.put(TableConstants.DayId, dayId);
        values.put(TableConstants.ExerciseId, exerciseId);
        values.put(TableConstants.SetsId, sid);
        values.put(TableConstants.MaxWeight, max);

        /* Insert the values into the DB */
        db.insert(TableConstants.MaxTableName ,null , values);
    }

    public interface AddLiftSetListener{
        void addNewLiftSet(LiftSet liftSet);
    }

    public interface DeleteLiftSetListner{
        void deleteNewLiftSet(LiftSet liftSet);
    }

    public interface EditLiftSetListner{
        void editNewLiftSet(LiftSet liftSet);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            addLiftSetListener = (AddLiftSetListener) activity;
            deleteLiftSetListner = (DeleteLiftSetListner) activity;
            editLiftSetListner = (EditLiftSetListner) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
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
                    deleteItem(liftSets.get(info.position));
                    break;
                case R.id.edit:
                    editItem(liftSets.get(info.position));
                    break;

            }
            return true;
        }

        return false;
    }

    public void deleteItem(LiftSet liftSet){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        /* Delete from Exercise table and routines table by using exercise id */
        db.delete(TableConstants.SetsTableName, TableConstants.SetsId + " = " + liftSet.getSetId(), null);
        db.delete(TableConstants.MaxTableName, TableConstants.SetsId + " = " + liftSet.getSetId(), null);

        db.close();

        /* Remove item from list and update list view */
        liftSets.remove(liftSet);
        setListAdapter.notifyDataSetChanged();
        /* Todo add callback */

        deleteLiftSetListner.deleteNewLiftSet(liftSet);
    }

    public void editItem(LiftSet liftSet){
        editing = true;
        repCount.setText(""+liftSet.getReps());
        weightCount.setText(String.format(Locale.US, "%.1f", liftSet.getWeight()));
        editLiftSet = liftSet;
    }

    public void updateLiftSet(double weight, int reps){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        /* Put in the new values */
        values.put(TableConstants.SetReps, reps);
        values.put(TableConstants.SetWeight, weight);

        /* Update database on set id */
        db.update(TableConstants.SetsTableName, values,
                TableConstants.SetsId + " = " + editLiftSet.getSetId(), null);


        /* update item in fragment context*/
        editLiftSet.setReps(reps);
        editLiftSet.setWeight(weight);

        double max;

        /* set the max baed on the number of reps */
        switch(reps){
            case 1:
                max = weight;
                break;
            case 2:
                max = weight*1.042;
                break;
            case 3:
                max = weight*1.072;
                break;
            case 4:
                max = weight*1.104;
                break;
            case 5:
                max = weight*1.137;
                break;
            case 6:
                max = weight *1.173;
                break;
            case 7:
                max = weight * 1.211;
                break;
            case 8:
                max = weight * 1.251;
                break;
            case 9:
                max = weight * 1.294;
                break;
            default:
                max = weight*1.341;
        }

        /* Prepare the values to be inserted */
        ContentValues updateValues =  new ContentValues();
        updateValues.put(TableConstants.MaxWeight, max);

        /* Update database on set id */
        db.update(TableConstants.MaxTableName, updateValues,
                TableConstants.SetsId + " = " + editLiftSet.getSetId(), null);

        editLiftSetListner.editNewLiftSet(editLiftSet);

        /* Update List view with new information */
        setListAdapter.notifyDataSetChanged();
    }

}
