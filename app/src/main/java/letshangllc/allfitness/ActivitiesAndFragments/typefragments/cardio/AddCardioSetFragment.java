package letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio;


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

import letshangllc.allfitness.ClassObjects.cardio.CardioSet;
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

    /* Boolean  and cardioSet that is being editted */
    private boolean editing;
    private CardioSet editCardioSet;

    public AddCardioSetFragment() {
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

    /* Setup view listeners and populate listview */
    private void setupViews(){
        cardioSetAdapter = new CardioSetAdapter(this.getContext(), cardioSets);
        lvCardioSets.setAdapter(cardioSetAdapter);

        registerForContextMenu(lvCardioSets);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 etHour.setText("");
                etMinute.setText("");
                etSeconds.setText("");
                etMiles.setText("");
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
                String hourString = etHour.getText().toString().trim();
                String minuteString = etMinute.getText().toString().trim();
                String secondsString = etSeconds.getText().toString().trim();
                String milesString = etMiles.getText().toString().trim();
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
                    miles = 0;
                }else{
                    miles = Double.parseDouble(milesString);
                }
                totalTime = (hours * 3600) + (minutes * 60) + seconds;
                if(!editing){
                    saveData(hours, minutes, seconds, totalTime, miles);
                } else { /* If editing the item then update the cardioSet */
                    editing = false;
                    btnAddSet.setText(getString(R.string.add));
                    updateCardioSet(hours, minutes, seconds, totalTime, miles);
                }

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

        addCardioSetListener.addCardioSet(cardioSet);
    }

    /* Get existing data from the DB */
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
                    confirmDelete(cardioSets.get(info.position));
                    break;
                case R.id.edit:
                    editItem(cardioSets.get(info.position));
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



    public void confirmDelete(final CardioSet cardioSet){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.confirm_delete));

        builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(cardioSet);
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

    public void deleteItem(CardioSet cardioSet){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        /* Delete from Exercise table and routines table by using exercise id */
        db.delete(TableConstants.CardioSetsTableName, TableConstants.CardioSetsId + " = " +
                cardioSet.setId, null);

        db.close();

        /* Remove item from list and update list view */
        cardioSets.remove(cardioSet);
        cardioSetAdapter.notifyDataSetChanged();

        deleteCardioSetListner.deleteCardioSet(cardioSet);
    }

    public void editItem(CardioSet cardioSet){
        editing = true;

        etHour.setText(String.format(Locale.US,"%2d", cardioSet.hours));
        etMinute.setText(String.format(Locale.US,"%2d", cardioSet.minutes));
        etSeconds.setText(String.format(Locale.US,"%2d", cardioSet.seconds));
        etMiles.setText(String.format(Locale.US,"%.2f", cardioSet.distance));
        btnAddSet.setText(getString(R.string.edit));
        editCardioSet = cardioSet;
    }

    public void updateCardioSet(int hours, int minutes, int seconds, double totalTime, double miles){
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        /* Put in the new values */
        values.put(TableConstants.DayId, dayId);
        values.put(TableConstants.CardioSetDistance, miles);
        values.put(TableConstants.CardioSetTime, totalTime);
        values.put(TableConstants.CardioSetHours, hours);
        values.put(TableConstants.CardioSetMinutes, minutes);
        values.put(TableConstants.CardioSetSeconds, seconds);

        /* Update database on set id */
        db.update(TableConstants.CardioSetsTableName, values,
                TableConstants.CardioSetsId + " = " + editCardioSet.setId, null);

        /* update item in fragment context*/
        editCardioSet.distance = miles;
        editCardioSet.elapsedTime = totalTime;
        editCardioSet.hours = hours;
        editCardioSet.minutes = minutes;
        editCardioSet.seconds = seconds;

        /* Update List view with new information */
        cardioSetAdapter.notifyDataSetChanged();

        editCardioSetListner.editCardioSet(editCardioSet);
    }

    /* CardioSet Change Listeners */
    private AddCardioSetListener addCardioSetListener;
    private DeleteCardioSetListner deleteCardioSetListner;
    private EditCardioSetListner editCardioSetListner;

    public interface AddCardioSetListener{
        void addCardioSet(CardioSet cardioSet);
    }

    public interface DeleteCardioSetListner{
        void deleteCardioSet(CardioSet cardioSet);
    }

    public interface EditCardioSetListner{
        void editCardioSet(CardioSet cardioSet);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            addCardioSetListener = (AddCardioSetListener) activity;
            deleteCardioSetListner = (DeleteCardioSetListner) activity;
            editCardioSetListner = (EditCardioSetListner) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

}
