package letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import letshangllc.allfitness.ClassObjects.cardio.CardioSet;
import letshangllc.allfitness.ClassObjects.cardio.PastCardioItem;
import letshangllc.allfitness.adapters.cardio.CardioHistoryAdapter;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastCardioFragment extends Fragment {
    private String TAG = PastCardioFragment.class.getSimpleName();

    /* Passed in lift variables */
    private int exerciseId;

    /* Array of past days */
    private ArrayList<PastCardioItem> pastCardioItems;

    /* Recycle view variables */
    private RecyclerView rvPastCardio;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /* Database Helper */
    DatabaseHelper databaseHelper;

    public PastCardioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        pastCardioItems = new ArrayList<>();

        exerciseId = args.getInt(getString(R.string.exercise_id), 0);

        databaseHelper = new DatabaseHelper(this.getContext());

        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);

        getExistingData();


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_past_sets, container, false);
        rvPastCardio = (RecyclerView) view.findViewById(R.id.rv_past_sets);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        rvPastCardio.setLayoutManager(mLayoutManager);

        mAdapter = new CardioHistoryAdapter(pastCardioItems);
        rvPastCardio.setAdapter(mAdapter);


        return view;
    }

    /* todo Change to one query */
    public void getExistingData() {
        /* Get readable db */
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        /* Get day ids and dates */
        String[] projection = {TableConstants.DayId, TableConstants.DayDate};

        /* Create an arraylist of the dates and Dayid*/
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Integer> dayIds = new ArrayList<>();

        /* Query the exercise table based on the exercise id to get all the associated exercises */
        Cursor c = db.query(TableConstants.DayTableName, projection,
                TableConstants.ExerciseId + " = " + exerciseId, null, null, null, null);

        c.moveToFirst();

        /* Insert the dayId and dates into their arrays */
        while (!c.isAfterLast()) {
            dayIds.add(c.getInt(0));
            dates.add(c.getString(1));
            c.moveToNext();
        }


        Log.e(TAG, "Daysize = " + dayIds.size());

        db = databaseHelper.getReadableDatabase();

        /* Get the sets for each day */
        int i = 0;
        for (Integer dayId : dayIds) {
            /* Query the sets table based on dayId */
            String[] projection2 = {TableConstants.CARDIO_SETS_ID, TableConstants.CARDIO_SET_DISTANCE,
                    TableConstants.CARDIO_SET_TIME, TableConstants.CARDIO_SET_HOURS, TableConstants.CARDIO_SET_MINUTES,
                    TableConstants.CARDIO_SET_SECONDS};

            c = db.query(TableConstants.CARDIO_SETS_TABLE_NAME, projection2, TableConstants.DayId + " = "
                    + dayId, null, null, null, null);

            ArrayList<CardioSet> cardioSets = new ArrayList<>();

            /* Add all sets for the day into a new array */
            c.moveToFirst();
            while (!c.isAfterLast()) {
                cardioSets.add(new CardioSet(c.getDouble(2), c.getInt(3), c.getInt(4), c.getInt(5),
                        c.getDouble(1), dayId, c.getInt(0)));
                c.moveToNext();
            }

            /* Add the list and date to past sets */
            pastCardioItems.add(0, new PastCardioItem(cardioSets, dates.get(i++)));

            c.close();
        }
        db.close();
    }

    /* Add new liftset to most current when set it added */
    public void addCardioSet(CardioSet cardioSet) {
        /* If the set set is not empty add on the new item */
        if(pastCardioItems.size()!=0){
            /* Add the inserted item to the first recycleview item */
            PastCardioItem pastCardioItem = pastCardioItems.get(0);
            pastCardioItem.cardioSets.add(cardioSet);
            mAdapter.notifyItemChanged(0);
        }
    }

    public void deleteCardioSet(CardioSet cardioSet){
        /* If the set set is not empty add on the new item */
        if(pastCardioItems.size()!=0){
            /* Remove the liftset from the first recycleview item */
            PastCardioItem pastCardioItem = pastCardioItems.get(0);
            /* Find the liftset to be deleted and remove it */
            for(CardioSet item: pastCardioItem.cardioSets){

                /* Delete the item that matches */
                if(item.setId == cardioSet.setId){
                    pastCardioItem.cardioSets.remove(item);
                    mAdapter.notifyItemChanged(0);
                    break;
                }
            }



        }
    }

    public void editCardioSet(CardioSet cardioSet)


    {
        /* If the set set is not empty add on the new item */
        if(pastCardioItems.size()!=0){
            /* Remove the liftset from the first recycleview item */
            PastCardioItem pastCardioItem = pastCardioItems.get(0);
            /* Find the liftset to be deleted and remove it */
            for(CardioSet item: pastCardioItem.cardioSets){

                if(item.setId == cardioSet.setId){
                    item.distance = cardioSet.distance;
                    item.elapsedTime = cardioSet.elapsedTime;
                    item.hours = cardioSet.hours;
                    item.minutes = cardioSet.minutes;
                    item.seconds = cardioSet.seconds;

                    mAdapter.notifyItemChanged(0);
                    break;
                }
            }
        }
    }
}
