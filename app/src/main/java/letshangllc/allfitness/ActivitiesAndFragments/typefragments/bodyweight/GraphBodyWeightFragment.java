package letshangllc.allfitness.ActivitiesAndFragments.typefragments.bodyweight;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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

import letshangllc.allfitness.ClassObjects.bodyweight.BodyWeightSet;
import letshangllc.allfitness.ClassObjects.bodyweight.PastBodyWeightItem;
import letshangllc.allfitness.ClassObjects.cardio.CardioSet;
import letshangllc.allfitness.ClassObjects.cardio.PastCardioItem;
import letshangllc.allfitness.MockData.MockedDataPoints;
import letshangllc.allfitness.R;
import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class GraphBodyWeightFragment extends Fragment {
    private static final String TAG= GraphBodyWeightFragment.class.getSimpleName();

    /* Views */
    private GraphView graph;
    private RelativeLayout rel_graph;
    private TextView tvNoData;
    private TextView[] tvDateSelections;

    /* Array of past days */
    private ArrayList<PastBodyWeightItem> pastBodyWeightItems;

    /* DataPoints and series */
    private LineGraphSeries<DataPoint> lineGraphSeries;
    private ArrayList<DataPoint> dataPointsReps;
    private ArrayList<DataPoint> dataPointsTime;
    private ArrayList<DataPoint> presentedDataPoints;

    /* Passed in lift variables */
    private int exerciseId;

    /* Database Helper */
    private DatabaseHelper databaseHelper;


    public GraphBodyWeightFragment() {
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
        View view = inflater.inflate(R.layout.fragment_graph_spinner, container, false);

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

//        try {
//            dataPointsReps = MockedDataPoints.getMockDataPoints();
//            presentedDataPoints = dataPointsReps;
//            lineGraphSeries.resetData(presentedDataPoints.toArray(new DataPoint[presentedDataPoints.size()]));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        /* createGraph */
        createGraph();
        return view;
    }

    public void setupAdditionalViews(View view){
        graph = (GraphView) view.findViewById(R.id.graph);
        rel_graph = (RelativeLayout) view.findViewById(R.id.relGraphOptions);
        tvNoData = (TextView) view.findViewById(R.id.tvNoData);

        Spinner spinner = (Spinner) view.findViewById(R.id.spinGraph);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.bodyweight_graph_types, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cardioType = (String) parent.getItemAtPosition(position);
                switch (cardioType){
                    case "Time (min)":
                        presentedDataPoints = dataPointsTime;
                        break;
                    case "Reps":
                        presentedDataPoints = dataPointsReps;
                        break;
                }
                if(presentedDataPoints.size() == 0){
                    graph.setVisibility(View.GONE);
                    tvNoData.setVisibility(View.VISIBLE);
                }else{
                    graph.setVisibility(View.VISIBLE);
                    tvNoData.setVisibility(View.GONE);
                    updateGraphWithDataPoints(presentedDataPoints, 4, true);
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


        /* Milliseconds in a day */
        double timeInDay = 24 * 60 * 60 * 1000;
        final double timeInMonth = timeInDay * 30.5;
        final double timeIn3Month = timeInMonth *3;
        final double timeIn6Month = timeIn3Month * 2;
        final double timeInYear = timeIn6Month * 2;

        /* Set the range change listeners */
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
            updateGraphWithDataPoints(dataPointsLocal, tvIndex, false);

        }
    }

    private void updateGraphWithDataPoints(ArrayList<DataPoint> dataPoints, int tvIndex, boolean dataSetChanged){
        if(dataPoints.size() == 1 && dataSetChanged){
            graph.removeAllSeries();
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
        }else if(dataPoints.size() > 1){
            graph.removeAllSeries();
            DataPoint[] dataPoints1 = dataPoints.toArray(new DataPoint[dataPoints.size()]);
            lineGraphSeries.resetData(dataPoints1);
            Viewport viewport = graph.getViewport();
            viewport.setMinX(lineGraphSeries.getLowestValueX()-5*24*60*60*1000);
            viewport.setMaxX(lineGraphSeries.getHighestValueX()+5*24*60*60*1000);
            viewport.setMinY(lineGraphSeries.getLowestValueY()-5);
            viewport.setMaxY(lineGraphSeries.getHighestValueY()+5);
            graph.addSeries(lineGraphSeries);
        }

        for (TextView tv: tvDateSelections){
            tv.setBackgroundColor(0);
        }
        /* set background for selected item */
        tvDateSelections[tvIndex].setBackgroundColor(getResources().getColor(R.color.divider));

    }

    private void createGraph(){
        graph.setTitle("Body Weight Exercises");
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.getContext()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getGridLabelRenderer().setPadding(16);
        if(!lineGraphSeries.isEmpty()){
            Log.i(TAG, "Creating Graph");

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
        dataPointsTime = new ArrayList<>();
        dataPointsReps = new ArrayList<>();
        lineGraphSeries = new LineGraphSeries<>();

        /* Get date formatter */
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);

        /* Fill the data series with the appropriate data */
        for (PastBodyWeightItem pastBodyWeightItem: pastBodyWeightItems){
            Date date = dateFormat.parse(pastBodyWeightItem.date);
            if(pastBodyWeightItem.getMaxReps() != 0){
                DataPoint dataPoint = new DataPoint(date, pastBodyWeightItem.getMaxReps());
                dataPointsReps.add(dataPoint);
                lineGraphSeries.appendData(dataPoint, true, pastBodyWeightItems.size());
            }
            if(pastBodyWeightItem.getMaxTime() != 0){
                DataPoint datapoint = new DataPoint(date, (pastBodyWeightItem.getMaxTime()));
                dataPointsTime.add(datapoint);
            }


        }

        presentedDataPoints = dataPointsReps;
        Log.e(TAG, "# Datapoints Reps = " + dataPointsReps.size());
        Log.e(TAG, "# Datapoints Time = " + dataPointsTime.size());


        if(presentedDataPoints.size() == 0){
            graph.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }else{
            graph.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
        }
    }

    public void getExistingData() {
        pastBodyWeightItems = new ArrayList<>();
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
                    null, null, null, TableConstants.DayId + " ASC");
            c.moveToFirst();

            ArrayList<BodyWeightSet> bodyWeightSets = new ArrayList<>();

            while (!c.isAfterLast()){
                bodyWeightSets.add(new BodyWeightSet(c.getInt(0), c.getDouble(1), c.getInt(2),
                        c.getInt(3), c.getInt(4), dayId));
                c.moveToNext();
            }

            /* Add the list and date to past sets */
            pastBodyWeightItems.add(new PastBodyWeightItem(bodyWeightSets, dates.get(i++)));

            c.close();
        }
        db.close();
    }

    public void updateCardioSet() throws ParseException {
        getExistingData();
        setupDatapoints();
        createGraph();
    }



    //* SQL CODE FOR FUTURE */
//    Log.i(TAG, "Start SQL Query ");
//
//    String sql = "SELECT * FROM " +
//            TableConstants.DayTableName + " INNER JOIN " + TableConstants.BODY_WEIGHT_TABLE_NAME +
//            " ON " + TableConstants.DayTableName +"." + TableConstants.DayId + " = " +
//            TableConstants.BODY_WEIGHT_TABLE_NAME +"." + TableConstants.DayId +
//            " WHERE " + TableConstants.DayTableName +"." + TableConstants.ExerciseId + " = " +exerciseId;
//
//    /* Run the query */
//    Cursor c = db.rawQuery(sql, null);
//    c.moveToFirst();
//
//        /* Find out the column indexes */
//    Log.i(TAG, "End SQL Query");
//        /* Insert the data into an list */
//    while(!c.isAfterLast()){
//        String date = c.getString(c.getColumnIndex(TableConstants.DayDate));
//        int dayId = c.getInt(c.getColumnIndex(TableConstants.DayId));
//        int bodyWeightId = c.getInt(c.getColumnIndex(TableConstants.BODY_WEIGHT_SET_ID));
//        double bodyWeightTime = c.getDouble(c.getColumnIndex(TableConstants.BODY_WEIGHT_TIME));
//        int reps = c.getInt(c.getColumnIndex(TableConstants.BODY_WEIGHT_REPS));
//        int minutes = c.getInt(c.getColumnIndex(TableConstants.BODY_WEIGHT_MINUTES));
//        int seconds = c.getInt(c.getColumnIndex(TableConstants.BODY_WEIGHT_SECONDS));
//    }
//
//    Log.i(TAG, "End SQL Query");

}
