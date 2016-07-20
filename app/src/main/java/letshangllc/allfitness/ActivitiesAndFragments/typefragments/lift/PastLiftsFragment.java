package letshangllc.allfitness.ActivitiesAndFragments.typefragments.lift;


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

import java.util.ArrayList;

import letshangllc.allfitness.ClassObjects.lift.LiftSet;
import letshangllc.allfitness.ClassObjects.lift.PastLiftItem;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;
import letshangllc.allfitness.adapters.lift.LiftHistoryAdapter;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */

/* TODO: remove empty days */
public class PastLiftsFragment extends Fragment {
    private String TAG = PastLiftsFragment.class.getSimpleName();

    /* Passed in lift variables */
    private int exerciseId;

    /* Array of past days */
    private ArrayList<PastLiftItem> pastLiftItems;

    /* Recycle view variables */
    private RecyclerView rvPastLifts;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    /* Database Helper */
    DatabaseHelper databaseHelper;

    public PastLiftsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        pastLiftItems = new ArrayList<>();

        exerciseId = args.getInt(getString(R.string.exercise_id), 0);

        databaseHelper = new DatabaseHelper(this.getContext());

        getExistingData();


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_past_sets, container, false);
        rvPastLifts = (RecyclerView) view.findViewById(R.id.rv_past_sets);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        rvPastLifts.setLayoutManager(mLayoutManager);

        mAdapter = new LiftHistoryAdapter(pastLiftItems);
        rvPastLifts.setAdapter(mAdapter);


        return view;
    }

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
            String[] projection2 = {TableConstants.LiftSetsId, TableConstants.LiftSetReps, TableConstants.LiftSetWeight};
            c = db.query(TableConstants.LiftSetsTableName, projection2, TableConstants.DayId + " = "
                    + dayId, null, null, null, null);


            ArrayList<LiftSet> liftSets = new ArrayList<>();

            /* Add all lifts for the day into a new array */
            c.moveToFirst();
            while (!c.isAfterLast()) {
                liftSets.add(new LiftSet(1, c.getInt(0), c.getInt(1), c.getDouble(2)));
                Log.e(TAG, "HERE");
                c.moveToNext();
            }

            /* Add the lift set and date to pastLiftSets */
            pastLiftItems.add(0, new PastLiftItem(liftSets, dates.get(i++)));

            c.close();
        }
        db.close();
    }

    /* Add new liftset to most current when set it added */
    public void updateNewLiftSet(LiftSet liftSet) {
        /* If the set set is not empty add on the new item */
        if(pastLiftItems.size()!=0){
                    /* Add the inserted item to the first recycleview item */
            PastLiftItem pastLiftItem = pastLiftItems.get(0);
            pastLiftItem.getLiftSets().add(liftSet);
            mAdapter.notifyItemChanged(0);
        }
    }

    public void deleteLiftSet(LiftSet liftSet){
        Log.i(TAG, "Deleting Liftset");
        /* If the set set is not empty add on the new item */
        if(pastLiftItems.size()!=0){
            /* Remove the liftset from the first recycleview item */
            PastLiftItem pastLiftItem = pastLiftItems.get(0);
            /* Find the liftset to be deleted and remove it */
            Log.i(TAG, "Liftset Id: " + liftSet.getSetId());
            for(LiftSet item: pastLiftItem.getLiftSets()){
                Log.i(TAG, "Item Id: " + item.getSetId());
                if(item.getSetId() == liftSet.getSetId()){
                    pastLiftItem.getLiftSets().remove(item);
                    mAdapter.notifyItemChanged(0);
                    break;
                }
            }



        }
    }


    public void editLiftSet(LiftSet liftSet){
        Log.i(TAG, "Edit Liftset");        /* If the set set is not empty add on the new item */
        if(pastLiftItems.size()!=0){
            /* Remove the liftset from the first recycleview item */
            PastLiftItem pastLiftItem = pastLiftItems.get(0);
            /* Find the liftset to be deleted and remove it */
            for(LiftSet item: pastLiftItem.getLiftSets()){
                Log.i(TAG, "Item Id: " + item.getSetId());
                if(item.getSetId() == liftSet.getSetId()){
                    item.setWeight(liftSet.getWeight());
                    item.setReps(liftSet.getReps());

                    mAdapter.notifyItemChanged(0);
                    break;
                }
            }



        }
    }
}
