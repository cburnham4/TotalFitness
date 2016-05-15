package letshangllc.allfitness.ActivitiesAndFragments.types.fragments.lift;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
    /* Passed in lift variables */
    private int exerciseId;
    private int typeId;
    private String exerciseName;

    /* Current Date variable */
    String currentDate;

    /* Database Helper */
    DatabaseHelper databaseHelper;

    /* Day Id */
    int dayId;

    /* ArrayList of sets */
    ArrayList<LiftSet> liftSets;

    /* Listview */
    ListView lv_setList;

    /* ListView Adapter */
    SetListAdapter setListAdapter;

    public AddLiftSetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

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
        return view;
    }

    private void setupViews(View view){
        /* Find Views */
        Button addSet =(Button) view.findViewById(R.id.btn_addSet);
        Button addRep = (Button) view.findViewById(R.id.btn_addRep);
        Button subRep = (Button) view.findViewById(R.id.btn_subRep);
        Button subWeight = (Button) view.findViewById(R.id.btn_subWeight);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_clearValues);
        final Button addWeight = (Button) view.findViewById(R.id.btn_addWeight);
        final EditText repCount = (EditText) view.findViewById(R.id.et_reps);
        final EditText weightCount = (EditText) view.findViewById(R.id.et_weight);

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
                int weight = Integer.parseInt(weightCount.getText().toString());
                weightCount.setText(weight+5 +"");
            }
        });
        subWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int weight = Integer.parseInt(weightCount.getText().toString());
                if(weight > 4){
                    weightCount.setText(weight - 5 + "");
                }

            }
        });

        addSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(weightCount.getText().toString()!= "" && repCount.getText().toString()!= ""){
                    double weight = Double.parseDouble(weightCount.getText().toString());
                    int reps = Integer.parseInt(repCount.getText().toString());
                    addToDB(weight, reps);
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
        liftSets.add(new LiftSet(dayId, sid, reps, weight));

        setListAdapter.notifyDataSetChanged();
    }

    /* Add date to db id it does not already exist */
    public int addDateToDB(){
        /* First check if the db row has already been created */
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {TableConstants.DayId};

        /* Query the exercise table based on the muscle id to get all the associated exercises */
        Cursor c = db.query(TableConstants.DayTableName, projection, TableConstants.DayDateLifted
                + " = " + currentDate +" AND " + TableConstants.ExerciseId +" = "+ exerciseId, null, null, null, null);
        c.moveToFirst();
        /* If there already exists a dayId for today then return it */
        if(!c.isAfterLast()){
            c.close();
            return c.getInt(0);
        }

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

}
