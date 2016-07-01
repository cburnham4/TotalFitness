package letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio;


import android.content.ContentValues;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import letshangllc.allfitness.ClassObjects.CardioSet;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;
import letshangllc.allfitness.R;
import letshangllc.allfitness.adapters.CardioSetAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCardioSetFragment extends Fragment {
    /* Instance variables */
    private static final String TAG = AddCardioSetFragment.class.getSimpleName();
    /* Passed in lift variables */
    private int exerciseId;

    /* Current Date variable */
    String currentDate;

    /* Database Helper */
    DatabaseHelper databaseHelper;

    /* Day Id */
    int dayId;

    /* Views */
    private Button btnCancel, btnAddSet;
    private EditText etHour, etMinute, etSeconds, etMiles;

    /* Data variables */
    private ArrayList<CardioSet> cardioSets;
    private ListView lvCardioSets;
    private CardioSetAdapter cardioSetAdapter;



    public AddCardioSetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

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
        View view = inflater.inflate(R.layout.fragment_add_cardio_set, container, false);

        this.findViews(view);
        this.setupViews();


        return view;
    }

    /* find view and set their listeners */
    private void findViews(View view){
        /* Find Views */
        btnAddSet =(Button) view.findViewById(R.id.btnAddCardioSet);
        btnCancel = (Button) view.findViewById(R.id.btnClearCardioValues);

        etHour = (EditText) view.findViewById(R.id.etHours);
        etMinute = (EditText) view.findViewById(R.id.etMiutes);
        etSeconds = (EditText) view.findViewById(R.id.etSeconds);
        etMiles = (EditText) view.findViewById(R.id.etMiles);

        lvCardioSets = (ListView) view.findViewById(R.id.lvCardioSets);

    }

    private void setupViews(){
        cardioSetAdapter = new CardioSetAdapter(this.getContext(), cardioSets);
        lvCardioSets.setAdapter(cardioSetAdapter);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etHour.setText("");
                etMinute.setText("");
                etSeconds.setText("");
                etMiles.setText("");
            }
        });
        btnAddSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hourString = etHour.getText().toString();
                String minuteString = etMinute.getText().toString();
                String secondsString = etSeconds.getText().toString();
                String milesString = etMiles.getText().toString();
                int hours, minutes, seconds;
                double miles, totalTime;
                if(hourString.isEmpty()){
                    hours = 0;
                }else{
                    hours = Integer.parseInt(hourString);
                }
                if(minuteString.isEmpty()){
                    minutes = 0;
                }else{
                    minutes = Integer.parseInt(minuteString);
                }
                if(secondsString.isEmpty()){
                    seconds = 0;
                }else{
                    seconds = Integer.parseInt(secondsString);
                }
                if(milesString.isEmpty()){
                    totalTime = (hours * 3600) + (minutes * 60) + seconds;
                    miles = 0;
                }else{
                    totalTime = 0;
                    miles = Double.parseDouble(milesString);
                }
                saveData(hours, minutes, seconds, totalTime, miles);
            }
        });
    }

    /* Save the set to the DB and update listview */
    public void saveData(int hours, int minutes, int seconds, double totalTime, double miles){
        /* Add to DB */
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableConstants.DayId, dayId);
        values.put(TableConstants.CardioSetDistance, miles);
        values.put(TableConstants.CardioSetTime, totalTime);
        values.put(TableConstants.CardioSetHours, hours);
        values.put(TableConstants.CardioSetMinutes, minutes);
        values.put(TableConstants.CardioSetSeconds, seconds);

        db.insert(TableConstants.CardioSetsTableName, null,values);

        /* Add new set into the listview */
        int sid = getMaxSetId();

        /* Add to List */
        CardioSet cardioSet = new CardioSet(totalTime, hours, minutes, seconds, miles, dayId, sid);
        cardioSets.add(cardioSet);
        cardioSetAdapter.notifyDataSetChanged();
    }

    public void getExistingData(){
        cardioSets = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
         /* Query the db to get the muscle data */
        String[] projection = {TableConstants.CardioSetsId, TableConstants.CardioSetDistance,
            TableConstants.CardioSetTime, TableConstants.CardioSetHours, TableConstants.CardioSetMinutes,
            TableConstants.CardioSetSeconds};

        Cursor c = db.query(TableConstants.CardioSetsTableName, projection, TableConstants.DayId +" = "+ dayId,
                null, null, null, null);
        c.moveToFirst();

        while (!c.isAfterLast()){
            cardioSets.add(new CardioSet(c.getDouble(2), c.getInt(3), c.getInt(4), c.getInt(5),
                    c.getDouble(1), dayId, c.getInt(0)));
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

                    break;
                case R.id.edit:

                    break;

            }
            return true;
        }

        return false;
    }

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
        String sql = "SELECT Max("+ TableConstants.CardioSetsId+") FROM "+ TableConstants.CardioSetsTableName;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        int max = c.getInt(0);
        c.close();
        db.close();
        return max;
    }





}
