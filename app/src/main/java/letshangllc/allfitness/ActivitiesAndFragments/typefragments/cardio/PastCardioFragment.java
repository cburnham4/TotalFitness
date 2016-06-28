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

import letshangllc.allfitness.ClassObjects.LiftSet;
import letshangllc.allfitness.ClassObjects.PastLiftSet;
import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.adapters.PastCardViewAdapter;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastCardioFragment extends Fragment {
    private String TAG = PastCardioFragment.class.getSimpleName();

    /* Passed in lift variables */
    private int exerciseId;

    /* Array of past days */
    private ArrayList<PastLiftSet> pastLiftSets;

    /* Recycle view variables */
    private RecyclerView rv_pastdates;
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

        pastLiftSets = new ArrayList<>();

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
        rv_pastdates = (RecyclerView) view.findViewById(R.id.rv_past_sets);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this.getContext());
        rv_pastdates.setLayoutManager(mLayoutManager);

        mAdapter = new PastCardViewAdapter(pastLiftSets);
        rv_pastdates.setAdapter(mAdapter);


        return view;
    }

    /* todo change to sql for 1 query */
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
            pastLiftSets.add(0, new PastLiftSet(liftSets, dates.get(i++)));

            c.close();
        }
        db.close();
    }

    /* Add new liftset to most current when set it added */
    public void updateNewLiftSet(LiftSet liftSet) {
        /* If the set set is not empty add on the new item */
        if(pastLiftSets.size()!=0){
                    /* Add the inserted item to the first recycleview item */
            PastLiftSet pastLiftSet = pastLiftSets.get(0);
            pastLiftSet.getLiftSets().add(liftSet);
            mAdapter.notifyItemChanged(0);
        }
    }

    public void deleteLiftSet(LiftSet liftSet){
        Log.i(TAG, "Deleting Liftset");
        /* If the set set is not empty add on the new item */
        if(pastLiftSets.size()!=0){
            /* Remove the liftset from the first recycleview item */
            PastLiftSet pastLiftSet = pastLiftSets.get(0);
            /* Find the liftset to be deleted and remove it */
            Log.i(TAG, "Liftset Id: " + liftSet.getSetId());
            for(LiftSet item: pastLiftSet.getLiftSets()){
                Log.i(TAG, "Item Id: " + item.getSetId());
                if(item.getSetId() == liftSet.getSetId()){
                    pastLiftSet.getLiftSets().remove(item);
                    mAdapter.notifyItemChanged(0);
                    break;
                }
            }



        }
    }


    public void editLiftSet(LiftSet liftSet){
        Log.i(TAG, "Edit Liftset");        /* If the set set is not empty add on the new item */
        if(pastLiftSets.size()!=0){
            /* Remove the liftset from the first recycleview item */
            PastLiftSet pastLiftSet = pastLiftSets.get(0);
            /* Find the liftset to be deleted and remove it */
            for(LiftSet item: pastLiftSet.getLiftSets()){
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
