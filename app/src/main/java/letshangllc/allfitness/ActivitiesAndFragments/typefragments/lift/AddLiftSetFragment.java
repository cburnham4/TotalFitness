package letshangllc.allfitness.ActivitiesAndFragments.typefragments.lift;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import letshangllc.allfitness.ActivitiesAndFragments.typefragments.CommonFunctions;
import letshangllc.allfitness.ClassObjects.lift.LiftSet;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;
import letshangllc.allfitness.adapters.lift.LiftSetAdapter;
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
    private String currentDate;

    /* Database Helper */
    private DatabaseHelper databaseHelper;

    /* Day Id */
    private int dayId;

    /* ArrayList of sets */
    private ArrayList<LiftSet> liftSets;

    /* Views */
    private EditText repCount;
    private EditText weightCount;
    private ListView lv_setList;
    private Button addSet;

    /* ListView Adapter */
    LiftSetAdapter liftSetAdapter;

    /* Add set listeners */
    AddLiftSetListener addLiftSetListener;
    DeleteLiftSetListner deleteLiftSetListner;
    EditLiftSetListner editLiftSetListner;

    /* Boolean  and id for if the liftSet is being editted */
    boolean editing;
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

        dayId = CommonFunctions.addDateToDB(databaseHelper, currentDate, exerciseId, TAG);

        getExistingData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_lift_set, container, false);

        this.setupViews(view);

        /* Set listview Adapter */
        liftSetAdapter =  new LiftSetAdapter(this.getContext(), liftSets);
        lv_setList.setAdapter(liftSetAdapter);

        registerForContextMenu(lv_setList);
        return view;
    }

    /* find view and set their listeners */
    private void setupViews(View view){
        /* Find Views */
        addSet =(Button) view.findViewById(R.id.btn_addSet);
        Button addRep = (Button) view.findViewById(R.id.btn_addRep);
        Button subRep = (Button) view.findViewById(R.id.btn_subRep);
        Button subWeight = (Button) view.findViewById(R.id.btn_subWeight);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_clearValues);
        Button addWeight = (Button) view.findViewById(R.id.btn_addWeight);
        repCount = (EditText) view.findViewById(R.id.et_reps);
        weightCount = (EditText) view.findViewById(R.id.et_weight);

        lv_setList = (ListView) view.findViewById(R.id.lv_setsList);

        /* Set up listeners */
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repCount.setText("0");
                weightCount.setText("0");
                if (editing){
                    editing = false;
                    addSet.setText(getString(R.string.add));
                    Toast.makeText(getContext(), "You are no longer in edit mode", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addRep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rep = Integer.parseInt(repCount.getText().toString().trim());
                repCount.setText(rep+1 +"");
            }
        });
        subRep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int rep = Integer.parseInt(repCount.getText().toString().trim());
                if(rep !=0){
                    repCount.setText(rep - 1 + "");
                }
            }
        });
        addWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double weight = Double.parseDouble(weightCount.getText().toString().trim());
                weightCount.setText(String.format(Locale.US, "%.1f", weight+5));
            }
        });
        subWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double weight = Double.parseDouble(weightCount.getText().toString().trim());
                if(weight >= 5){
                    weightCount.setText(String.format(Locale.US, "%.1f", weight-5));
                }

            }
        });

        addSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!weightCount.getText().toString().isEmpty() && !repCount.getText().toString().isEmpty()){

                    double weight = Double.parseDouble(weightCount.getText().toString().trim());
                    int reps = Integer.parseInt(repCount.getText().toString().trim());
                    if(weight == 0 || reps == 0){
                        Toast.makeText(getContext(), "Atleast one attribute is 0", Toast.LENGTH_SHORT).show();
                        if (editing){
                            editing = false;
                            addSet.setText(getString(R.string.add));
                            Toast.makeText(getContext(), "You are no longer in edit mode", Toast.LENGTH_SHORT).show();
                        }
                    }else if (!editing){
                        /* if not editing than add to db (*/
                        addToDB(weight, reps);
                    }else{ /* If editing then add to db */
                        updateLiftSet(weight, reps);
                        editing = false;
                        addSet.setText(getString(R.string.add));
                    }

                }
            }
        });
    }

    public void getExistingData(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
         /* Query the db to get the muscle data */
        String[] projection = {TableConstants.LiftSetsId, TableConstants.LiftSetReps,  TableConstants.LiftSetWeight};
        Cursor c = db.query(TableConstants.LiftSetsTableName, projection, TableConstants.DayId +" = "+ dayId,
                null, null, null, null);
        c.moveToFirst();

        while (!c.isAfterLast()){
            liftSets.add(new LiftSet(dayId, c.getInt(0), c.getInt(1), c.getDouble(2)));
            c.moveToNext();
        }
        c.close();
        db.close();
    }

    /* Add the current set to the db and update listview */
    public void addToDB(double weight, int reps){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //PUT SET INTO SETS
        ContentValues values = new ContentValues();
        values.put(TableConstants.DayId, dayId);
        values.put(TableConstants.LiftSetReps, reps);
        values.put(TableConstants.LiftSetWeight, weight);

        int sid = (int) db.insert(TableConstants.LiftSetsTableName, null,values);

        LiftSet liftSet = new LiftSet(dayId, sid, reps, weight);
        liftSets.add(liftSet);

        liftSetAdapter.notifyDataSetChanged();

        /* Call the callback with the new listSet */
        addLiftSetListener.addNewLiftSet(liftSet);

        /* Add recent lift to Maxes */
        inputCalculatedMAX(weight, reps, sid);
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
        values.put(TableConstants.LiftSetsId, sid);
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
                    confirmDelete(liftSets.get(info.position));
                    break;
                case R.id.edit:
                    editItem(liftSets.get(info.position));
                    break;

            }
            return true;
        }

        return false;
    }

    public void confirmDelete(final LiftSet liftSet){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.confirm_delete));

        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(liftSet);
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

    public void deleteItem(LiftSet liftSet){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        /* Delete from Exercise table and routines table by using exercise id */
        db.delete(TableConstants.LiftSetsTableName, TableConstants.LiftSetsId + " = " + liftSet.getSetId(), null);
        db.delete(TableConstants.MaxTableName, TableConstants.LiftSetsId + " = " + liftSet.getSetId(), null);

        db.close();

        /* Remove item from list and update list view */
        liftSets.remove(liftSet);
        liftSetAdapter.notifyDataSetChanged();

        deleteLiftSetListner.deleteNewLiftSet(liftSet);
    }

    public void editItem(LiftSet liftSet){
        editing = true;
        repCount.setText(String.format(Locale.US,"%d",liftSet.getReps()));
        weightCount.setText(String.format(Locale.US, "%.1f", liftSet.getWeight()));
        addSet.setText(getString(R.string.edit));
        editLiftSet = liftSet;
    }

    public void updateLiftSet(double weight, int reps){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        /* Put in the new values */
        values.put(TableConstants.LiftSetReps, reps);
        values.put(TableConstants.LiftSetWeight, weight);

        /* Update database on set id */
        db.update(TableConstants.LiftSetsTableName, values,
                TableConstants.LiftSetsId + " = " + editLiftSet.getSetId(), null);


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
                TableConstants.LiftSetsId + " = " + editLiftSet.getSetId(), null);

        editLiftSetListner.editNewLiftSet(editLiftSet);

        /* Update List view with new information */
        liftSetAdapter.notifyDataSetChanged();
    }

}
