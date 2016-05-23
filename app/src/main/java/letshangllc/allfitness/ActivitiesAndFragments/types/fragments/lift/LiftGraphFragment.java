package letshangllc.allfitness.ActivitiesAndFragments.types.fragments.lift;


import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiftGraphFragment extends Fragment {
    private GraphView graph;
    private LineGraphSeries<DataPoint> lineGraphSeries;

    /* Passed in lift variables */
    private int exerciseId;

    /* Database Helper */
    DatabaseHelper databaseHelper;

    public LiftGraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lift_graph, container, false);

        graph = (GraphView) view.findViewById(R.id.graph);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        exerciseId = args.getInt(getString(R.string.exercise_id), 0);

        databaseHelper = new DatabaseHelper(this.getContext());

        getExistingData();
    }

    public void getExistingData(){
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
    }
}
