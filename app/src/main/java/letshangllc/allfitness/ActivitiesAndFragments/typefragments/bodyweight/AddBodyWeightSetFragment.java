package letshangllc.allfitness.ActivitiesAndFragments.typefragments.bodyweight;


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

import letshangllc.allfitness.ClassObjects.bodyweight.BodyWeightSet;
import letshangllc.allfitness.ClassObjects.cardio.CardioSet;
import letshangllc.allfitness.R;
import letshangllc.allfitness.adapters.bodyweight.BodyWeightSetAdapter;
import letshangllc.allfitness.adapters.cardio.CardioSetAdapter;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddBodyWeightSetFragment extends Fragment {
    /* Instance variables */
    private static final String TAG = AddBodyWeightSetFragment.class.getSimpleName();

    /* Passed in lift variables */
    private int exerciseId;

    /* Current Date variable */
    private String currentDate;
    private int dayId;

    /* Database Helper */
    private DatabaseHelper databaseHelper;

    /* Views */
    private Button btnCancel, btnAddSet, btnSubRep, btnAddRep;
    private EditText etMinute, etSeconds, etReps;

    /* Data variables */
    private ArrayList<BodyWeightSet> bodyWeightSets;
    private ListView lvBodyWeightSets;
    private BodyWeightSetAdapter bodyWeightSetAdapter;

    /* Boolean  and cardioSet that is being editted */
    private boolean editing;
    private BodyWeightSet editBodyWeightSet;

    public AddBodyWeightSetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        /* Set editing to false to start the fragment */
        editing = false;

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
        View view = inflater.inflate(R.layout.fragment_add_body_weight, container, false);

        this.findViews(view);
        this.setupViews();

        return view;
    }

    /* find view and set their listeners */
    private void findViews(View view){
        /* Find Views */
        btnAddSet =(Button) view.findViewById(R.id.btnAddCardioSet);
        btnCancel = (Button) view.findViewById(R.id.btnClearCardioValues);
        btnAddRep = (Button) view.findViewById(R.id.btnAddBwRep);
        btnSubRep = (Button) view.findViewById(R.id.btnSubBwRep);

        etMinute = (EditText) view.findViewById(R.id.etMiutes);
        etSeconds = (EditText) view.findViewById(R.id.etSeconds);
        etReps = (EditText) view.findViewById(R.id.etBwReps);

        lvBodyWeightSets = (ListView) view.findViewById(R.id.lvBodyWeightSets);
    }

    /* Setup view listeners and populate listview */
    private void setupViews(){
        bodyWeightSetAdapter = new BodyWeightSetAdapter(this.getContext(), bodyWeightSets);
        lvBodyWeightSets.setAdapter(bodyWeightSetAdapter);

        registerForContextMenu(lvBodyWeightSets);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMinute.setText("");
                etSeconds.setText("");
                if (editing){
                    editing = false;
                    btnAddSet.setText(getString(R.string.add));
                    Toast.makeText(getContext(), "You are no longer in edit mode", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnAddSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String minuteString = etMinute.getText().toString().trim();
                String secondsString = etSeconds.getText().toString().trim();
                String repsString = etReps.getText().toString().trim();
                int minutes =0, seconds = 0, reps =0;
                double miles, totalTime;

                if(!minuteString.isEmpty()){
                    minutes = Integer.parseInt(minuteString);
                }
                if(!secondsString.isEmpty()){
                    seconds = Integer.parseInt(secondsString);
                }
                if(! repsString.isEmpty()) {
                    reps = Integer.parseInt(repsString);
                }
                totalTime = (minutes * 60) + seconds;
                if(minutes == 0 && seconds == 0 && reps ==0 ) {
                    Toast.makeText(getContext(), getString(R.string.please_enter_one_value),
                            Toast.LENGTH_SHORT).show();
                } else if(!editing){
                     saveData(minutes, seconds, totalTime, reps);

                } else { /* If editing the item then update the cardioSet */
                    editing = false;
                    btnAddSet.setText(getString(R.string.add));
                    updateCardioSet(minutes, seconds, totalTime, reps);
                }

            }
        });
    }

    /* Save the set to the DB and update listview */
    public void saveData(int minutes, int seconds, double totalTime, int reps){
        /* Add to DB */
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableConstants.DayId, dayId);
        values.put(TableConstants.BODY_WEIGHT_TIME, totalTime);
        values.put(TableConstants.BODY_WEIGHT_REPS, reps);
        values.put(TableConstants.BODY_WEIGHT_MINUTES, minutes);
        values.put(TableConstants.BODY_WEIGHT_SECONDS, seconds);

        db.insert(TableConstants.BODY_WEIGHT_TABLE_NAME, null,values);

        /* Add new set into the listview */
        int sid = getMaxSetId();

        /* Add to List */
        BodyWeightSet bodyWeightSet = new BodyWeightSet(sid, totalTime, reps, minutes, seconds, dayId);
        bodyWeightSets.add(bodyWeightSet);
        bodyWeightSetAdapter.notifyDataSetChanged();

        addBwSetListener.addBwSet(bodyWeightSet);
    }

    /* Get existing data from the DB */
    public void getExistingData(){
        bodyWeightSets = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
         /* Query the db to get the muscle data */
        String[] projection = {TableConstants.BODY_WEIGHT_SET_ID, TableConstants.BODY_WEIGHT_TIME,
            TableConstants.BODY_WEIGHT_REPS, TableConstants.BODY_WEIGHT_MINUTES,
            TableConstants.BODY_WEIGHT_SECONDS};

        Cursor c = db.query(TableConstants.BODY_WEIGHT_TABLE_NAME, projection, TableConstants.DayId +" = "+ dayId,
                null, null, null, null);
        c.moveToFirst();

        while (!c.isAfterLast()){
            bodyWeightSets.add(new BodyWeightSet(c.getInt(0), c.getDouble(1), c.getInt(2),
                    c.getInt(3), c.getInt(4), dayId));
            c.moveToNext();
        }
        c.close();
        db.close();
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
                    confirmDelete(bodyWeightSets.get(info.position));
                    break;
                case R.id.edit:
                    editItem(bodyWeightSets.get(info.position));
                    break;
            }
            return true;
        }

        return false;
    }

    /* todo refactor to common class */
    /* Add date to db id it does not already exist */
    public int addDateToDB(){
        /* First check if the db row has already been created */
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        String[] projection = {TableConstants.DayId};

        /* Query the exercise table based on the muscle id to get all the associated exercises */
        Cursor c = db.query(TableConstants.DayTableName, projection, TableConstants.DayDate
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
        values.put(TableConstants.DayDate, currentDate);

         /* Insert values into db */
        db.insert(TableConstants.DayTableName, null, values);
        db.close();

        /* Return the max Day id which will be the most recently inserted dayId */
        return getMaxDayId();

    }

    /* todo move to common */
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
        String sql = "SELECT Max("+ TableConstants.CARDIO_SETS_ID +") FROM "+ TableConstants.CARDIO_SETS_TABLE_NAME;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int max = c.getInt(0);
        c.close();
        db.close();
        return max;
    }



    public void confirmDelete(final BodyWeightSet bodyWeightSet){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.confirm_delete));

        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(bodyWeightSet);
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

    public void deleteItem(BodyWeightSet bodyWeightSet){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        /* Delete from Exercise table and routines table by using exercise id */
        db.delete(TableConstants.BODY_WEIGHT_TABLE_NAME, TableConstants.BODY_WEIGHT_SET_ID + " = " +
                bodyWeightSet.setId, null);

        db.close();

        /* Remove item from list and update list view */
        bodyWeightSets.remove(bodyWeightSet);
        bodyWeightSetAdapter.notifyDataSetChanged();

        deleteBwSetListner.deleteBwSet(bodyWeightSet);
    }

    public void editItem(BodyWeightSet bodyWeightSet){
        editing = true;

        etMinute.setText(String.format(Locale.US,"%2d", bodyWeightSet.minutes));
        etSeconds.setText(String.format(Locale.US,"%2d", bodyWeightSet.seconds));

        btnAddSet.setText(getString(R.string.edit));
        editBodyWeightSet = bodyWeightSet;
    }

    public void updateCardioSet(int minutes, int seconds, double totalTime, int reps){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        /* Put in the new values */
        values.put(TableConstants.DayId, dayId);
        values.put(TableConstants.BODY_WEIGHT_TIME, totalTime);
        values.put(TableConstants.BODY_WEIGHT_REPS, reps);
        values.put(TableConstants.BODY_WEIGHT_MINUTES, minutes);
        values.put(TableConstants.BODY_WEIGHT_SECONDS, seconds);

        /* Update database on set id */
        db.update(TableConstants.CARDIO_SETS_TABLE_NAME, values,
                TableConstants.CARDIO_SETS_ID + " = " + editBodyWeightSet.setId, null);

        /* update item in fragment context*/
        editBodyWeightSet.reps = reps;
        editBodyWeightSet.duration = totalTime;

        editBodyWeightSet.minutes = minutes;
        editBodyWeightSet.seconds = seconds;

        /* Update List view with new information */
        bodyWeightSetAdapter.notifyDataSetChanged();

        editBwSetListner.editBwSet(editBodyWeightSet);
    }

    /* BodyWeight Set Change Listeners */
    private AddBwSetListener addBwSetListener;
    private DeleteBwSetListner deleteBwSetListner;
    private EditBwSetListner editBwSetListner;

    public interface AddBwSetListener{
        void addBwSet(BodyWeightSet bodyWeightSet);
    }

    public interface DeleteBwSetListner{
        void deleteBwSet(BodyWeightSet bodyWeightSet);
    }

    public interface EditBwSetListner{
        void editBwSet(BodyWeightSet bodyWeightSet);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            addBwSetListener = (AddBwSetListener) activity;
            deleteBwSetListner = (DeleteBwSetListner) activity;
            editBwSetListner = (EditBwSetListner) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

}
