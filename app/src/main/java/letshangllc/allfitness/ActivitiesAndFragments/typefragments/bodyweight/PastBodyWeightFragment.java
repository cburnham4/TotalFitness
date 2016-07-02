package letshangllc.allfitness.ActivitiesAndFragments.typefragments.bodyweight;


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

import letshangllc.allfitness.ClassObjects.bodyweight.BodyWeightSet;
import letshangllc.allfitness.ClassObjects.bodyweight.PastBodyWeightItem;
import letshangllc.allfitness.ClassObjects.cardio.CardioSet;
import letshangllc.allfitness.ClassObjects.cardio.PastCardioItem;
import letshangllc.allfitness.R;
import letshangllc.allfitness.adapters.bodyweight.BodyWeightHistoryAdapter;
import letshangllc.allfitness.adapters.cardio.CardioHistoryAdapter;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastBodyWeightFragment extends Fragment {
    private String TAG = PastBodyWeightFragment.class.getSimpleName();

    /* Passed in lift variables */
    private int exerciseId;

    /* Array of past days */
    private ArrayList<PastBodyWeightItem> pastBodyWeightItems;

    /* Recycle view variables */
    private RecyclerView rvPastBodyWeight;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /* Database Helper */
    DatabaseHelper databaseHelper;

    public PastBodyWeightFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        pastBodyWeightItems = new ArrayList<>();

        exerciseId = args.getInt(getString(R.string.exercise_id), 0);

        databaseHelper = new DatabaseHelper(this.getContext());

        getExistingData();


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_past_sets, container, false);
        rvPastBodyWeight = (RecyclerView) view.findViewById(R.id.rv_past_sets);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        rvPastBodyWeight.setLayoutManager(mLayoutManager);

        mAdapter = new BodyWeightHistoryAdapter(pastBodyWeightItems);
        rvPastBodyWeight.setAdapter(mAdapter);


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
             /* Query the db to get the muscle data */
            String[] projection2 = {TableConstants.BODY_WEIGHT_SET_ID, TableConstants.BODY_WEIGHT_TIME,
                    TableConstants.BODY_WEIGHT_REPS, TableConstants.BODY_WEIGHT_MINUTES,
                    TableConstants.BODY_WEIGHT_SECONDS};

            c = db.query(TableConstants.BODY_WEIGHT_TABLE_NAME, projection2, TableConstants.DayId +" = "+ dayId,
                    null, null, null, null);
            c.moveToFirst();

            ArrayList<BodyWeightSet> bodyWeightSets = new ArrayList<>();

            while (!c.isAfterLast()){
                bodyWeightSets.add(new BodyWeightSet(c.getInt(0), c.getDouble(1), c.getInt(2),
                        c.getInt(3), c.getInt(4), dayId));
                c.moveToNext();
            }

            /* Add the list and date to past sets */
            pastBodyWeightItems.add(0, new PastBodyWeightItem(bodyWeightSets, dates.get(i++)));

            c.close();
        }
        db.close();
    }

    /* Add new liftset to most current when set it added */
    public void addBodyWeightSet(BodyWeightSet bodyWeightSet) {
        /* If the set set is not empty add on the new item */
        if(pastBodyWeightItems.size()!=0){
            /* Add the inserted item to the first recycleview item */
            PastBodyWeightItem pastBodyWeightItem = pastBodyWeightItems.get(0);
            pastBodyWeightItem.bodyWeightSets.add(bodyWeightSet);
            mAdapter.notifyItemChanged(0);
        }
    }

    public void deleteBodyWeightSet(BodyWeightSet bodyWeightSet){
        /* If the set set is not empty add on the new item */
        if(pastBodyWeightItems.size()!=0){
            /* Remove the liftset from the first recycleview item */
            PastBodyWeightItem pastBodyWeightItem= pastBodyWeightItems.get(0);
            /* Find the liftset to be deleted and remove it */
            for(BodyWeightSet item: pastBodyWeightItem.bodyWeightSets){

                /* Delete the item that matches */
                if(item.setId == bodyWeightSet.setId){
                    pastBodyWeightItem.bodyWeightSets.remove(item);
                    mAdapter.notifyItemChanged(0);
                    break;
                }
            }
        }
    }

    public void editBodyWeightSet(BodyWeightSet bodyWeightSet){
        /* If the set set is not empty add on the new item */
        if(pastBodyWeightItems.size()!=0){
            /* Remove the liftset from the first recycleview item */
            PastBodyWeightItem pastBodyWeightItem= pastBodyWeightItems.get(0);
            /* Find the liftset to be deleted and remove it */
            for(BodyWeightSet item: pastBodyWeightItem.bodyWeightSets){
                if(item.setId == bodyWeightSet.setId){
                    item.minutes = bodyWeightSet.minutes;
                    item.seconds = bodyWeightSet.seconds;
                    item.reps = bodyWeightSet.reps;
                    item.duration = bodyWeightSet.duration;

                    mAdapter.notifyItemChanged(0);
                    break;
                }
            }
        }
    }
}
