package letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import letshangllc.allfitness.ClassObjects.CardioSet;
import letshangllc.allfitness.ClassObjects.PastCardioItem;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphCardioFragment extends Fragment {
    private static final String TAG= GraphCardioFragment.class.getSimpleName();

    /* Views */
    private GraphView graph;
    private RelativeLayout rel_graph;
    private TextView tvNoData;

    /* Array of past days */
    private ArrayList<PastCardioItem> pastCardioItems;

    /* DataPoints and series */
    private LineGraphSeries<DataPoint> lineGraphSeries;
    private ArrayList<DataPoint> dataPointsDistance;
    private ArrayList<DataPoint> dataPointsTime;
    private ArrayList<DataPoint> dataPointsSpeed;
    private ArrayList<DataPoint> presentedDataPoints;

    /* Passed in lift variables */
    private int exerciseId;

    /* Database Helper */
    DatabaseHelper databaseHelper;

    private TextView[] tvDateSelections;

    public GraphCardioFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "Create Graph");

        Bundle args = getArguments();
        exerciseId = args.getInt(getString(R.string.exercise_id), 0);

        databaseHelper = new DatabaseHelper(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph_cardio, container, false);

        Log.e(TAG, "Create Graph View ");

        setupDateSelections(view);
        setupAdditionalViews(view);

        /* Attempt to get the existing data */
        try {
            getExistingData();
            setupDatapoints();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* createGraph */
        createGraph();
        return view;
    }

    public void setupAdditionalViews(View view){
        graph = (GraphView) view.findViewById(R.id.graph);
        rel_graph = (RelativeLayout) view.findViewById(R.id.relGraphOptions);
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinCardioGraph);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.cardio_graph_types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cardioType = (String) parent.getItemAtPosition(position);
                switch (cardioType){
                    case "Distance (mi)":
                        presentedDataPoints = dataPointsDistance;
                        break;
                    case "Time (min)":
                        presentedDataPoints = dataPointsTime;
                        break;
                    case "Speed (mph)":
                        presentedDataPoints = dataPointsSpeed;
                        break;
                }
                if(presentedDataPoints.size() == 0){
                    graph.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                }else{
                    graph.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                    updateGraphWithDataPoints(presentedDataPoints, 4);
                    //createGraph();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setupDateSelections(View view){
        final TextView tv_1m = (TextView) view.findViewById(R.id.tv_1m);
        final TextView tv_3m = (TextView) view.findViewById(R.id.tv_3m);
        final TextView tv_6m = (TextView) view.findViewById(R.id.tv_6m);
        final TextView tv_1y = (TextView) view.findViewById(R.id.tv_1y);
        final TextView tv_all = (TextView) view.findViewById(R.id.tv_all);

        tvDateSelections = new TextView[]{tv_1m, tv_3m, tv_6m, tv_1y, tv_all};

        Date currentDate = new Date();

        final double currentTime = currentDate.getTime();

        /* Milliseconds in a day */
        double timeInDay = 24 * 60 * 60 * 1000;
        final double timeInMonth = timeInDay * 30.5;
        final double timeIn3Month = timeInMonth *3;
        final double timeIn6Month = timeIn3Month * 2;
        final double timeInYear = timeIn6Month * 2;

        /* todo check if you can break if not true */
        tv_1m.setOnClickListener(new OnDateRangeSelection(timeInMonth, 0));
        tv_3m.setOnClickListener(new OnDateRangeSelection(timeIn3Month, 1));
        tv_6m.setOnClickListener(new OnDateRangeSelection(timeIn6Month, 2));
        tv_1y.setOnClickListener(new OnDateRangeSelection(timeInYear, 3));
        tv_all.setOnClickListener(new OnDateRangeSelection(Double.MAX_VALUE, 4));

    }

    public class OnDateRangeSelection implements View.OnClickListener{
        private double timeLimit;
        private int tvIndex;
        public OnDateRangeSelection(double timeLimit, int tvIndex) {
            this.timeLimit = timeLimit;
            this.tvIndex = tvIndex;
        }

        @Override
        public void onClick(View v) {
            Date currentDate = new Date();
            final double currentTime = currentDate.getTime();

            ArrayList<DataPoint> dataPointsLocal = new ArrayList<DataPoint>();
                /* Iterate though the global datapoints to get the ones that fall in a 1 month range */
            for (DataPoint dataPoint: presentedDataPoints){
                if(currentTime-dataPoint.getX()<= timeLimit){
                    dataPointsLocal.add(dataPoint);
                }
            }
            updateGraphWithDataPoints(dataPointsLocal, tvIndex);

        }
    }

    private void updateGraphWithDataPoints(ArrayList<DataPoint> dataPoints, int tvIndex){
        if(dataPoints.size() == 1){
            DataPoint dataPoint = dataPoints.get(0);
            PointsGraphSeries<DataPoint> seriesSingle = new PointsGraphSeries<DataPoint>(new DataPoint[] {
                    dataPoint
            });

            graph.addSeries(seriesSingle);
            seriesSingle.setShape(PointsGraphSeries.Shape.TRIANGLE);

            Viewport viewport = graph.getViewport();
            viewport.setMinX(dataPoint.getX()-5*24*60*60*1000);
            viewport.setMaxX(dataPoint.getX()+5*24*60*60*1000);

            viewport.setMinY(dataPoint.getY()-10);
            viewport.setMaxY(dataPoint.getY() + 10);
        }else{
            DataPoint[] dataPoints1 = dataPoints.toArray(new DataPoint[dataPoints.size()]);
            lineGraphSeries.resetData(dataPoints1);
            Viewport viewport = graph.getViewport();
            viewport.setMinX(lineGraphSeries.getLowestValueX()-5*24*60*60*1000);
            viewport.setMaxX(lineGraphSeries.getHighestValueX()+5*24*60*60*1000);
            viewport.setMinY(lineGraphSeries.getLowestValueY()-5);
            viewport.setMaxY(lineGraphSeries.getHighestValueY()+5);
        /* Set all points textviews to null bg */
            for (TextView tv: tvDateSelections){
                tv.setBackgroundColor(0);
            }
        /* set background for selected item */
            tvDateSelections[tvIndex].setBackgroundColor(getResources().getColor(R.color.divider));
        }

    }


    /* todo just change the x min and x max for times */
    private void createGraph(){
        graph.setTitle("Cardio Results");
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.getContext()));
        Log.i(TAG, "Create Graph");
        if(!lineGraphSeries.isEmpty()){
            Log.i(TAG, "Creating Graph");
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setYAxisBoundsManual(true);
            if(presentedDataPoints.size() == 1){
                DataPoint dataPoint = presentedDataPoints.get(0);
                PointsGraphSeries<DataPoint> seriesSingle = new PointsGraphSeries<DataPoint>(new DataPoint[] {
                        dataPoint
                });

                graph.addSeries(seriesSingle);
                seriesSingle.setShape(PointsGraphSeries.Shape.TRIANGLE);

                Viewport viewport = graph.getViewport();
                viewport.setMinX(dataPoint.getX()-5*24*60*60*1000);
                viewport.setMaxX(dataPoint.getX()+5*24*60*60*1000);

                viewport.setMinY(dataPoint.getY()-10);
                viewport.setMaxY(dataPoint.getY() + 10);

            }else{
                Viewport viewport = graph.getViewport();
                viewport.setMinX(lineGraphSeries.getLowestValueX()-5*24*60*60*1000);
                viewport.setMaxX(lineGraphSeries.getHighestValueX()+5*24*60*60*1000);
                viewport.setMinY(lineGraphSeries.getLowestValueY()-5);
                viewport.setMaxY(lineGraphSeries.getHighestValueY()+5);

                lineGraphSeries.setDrawDataPoints(true);
                lineGraphSeries.setDataPointsRadius(10);
                lineGraphSeries.setThickness(4);

                //graph.getViewport().setScalable(true);
                //graph.getViewport().setScrollable(true);
                graph.addSeries(lineGraphSeries);
            }


        }else{
            Log.i(TAG, "Line Graph Series is empty");
        }

    }

    /* Parse datapoints from the data obtained from getExistingData */
    public void setupDatapoints() throws ParseException {
        dataPointsDistance = new ArrayList<>();
        dataPointsTime = new ArrayList<>();
        dataPointsSpeed = new ArrayList<>();
        lineGraphSeries = new LineGraphSeries<>();

        /* Get date formatter */
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);

        /* Fill the data series with the appropriate data */
        for (PastCardioItem pastCardioItem: pastCardioItems){
            Date date = dateFormat.parse(pastCardioItem.date);
            if(pastCardioItem.getMaxDistance() != 0){
                DataPoint datapoint = new DataPoint(date, pastCardioItem.getMaxDistance());
                dataPointsDistance.add(datapoint);
                lineGraphSeries.appendData(datapoint, true, pastCardioItems.size());
            }

            if(pastCardioItem.getMaxTime() != 0){
                dataPointsTime.add(new DataPoint(date, pastCardioItem.getMaxTime()));
            }
            if(pastCardioItem.getMaxSpeed() != 0){
                dataPointsSpeed.add(new DataPoint(date, pastCardioItem.getMaxSpeed()));
            }
        }

        presentedDataPoints = dataPointsDistance;
        Log.e(TAG, "# Distance Datapoints = " + dataPointsDistance.size());
        Log.e(TAG, "# Datapoints Time = " + dataPointsTime.size());
        Log.e(TAG, "# Datapoints Speed = " + dataPointsSpeed.size());

        if(presentedDataPoints.size() == 0){
            graph.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }else{
            graph.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }
    }

    /* todo Change to one query */
    public void getExistingData() {
        pastCardioItems = new ArrayList<>();

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
            String[] projection2 = {TableConstants.CardioSetsId, TableConstants.CardioSetDistance,
                    TableConstants.CardioSetTime, TableConstants.CardioSetHours, TableConstants.CardioSetMinutes,
                    TableConstants.CardioSetSeconds};

            c = db.query(TableConstants.CardioSetsTableName, projection2, TableConstants.DayId + " = "
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

    public void updateCardioSet() throws ParseException {
        getExistingData();
        setupDatapoints();
        createGraph();
    }

}
