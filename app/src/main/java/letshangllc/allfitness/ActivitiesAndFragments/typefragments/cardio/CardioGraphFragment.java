package letshangllc.allfitness.ActivitiesAndFragments.typefragments.cardio;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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

import letshangllc.allfitness.database.DatabaseHelper;
import letshangllc.allfitness.database.TableConstants;
import letshangllc.allfitness.MockData.MockedDataPoints;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardioGraphFragment extends Fragment {
    private static final String TAG= CardioGraphFragment.class.getSimpleName();

    /* Views */
    private GraphView graph;
    private RelativeLayout rel_graph;
    private TextView tvNoData;

    /* DataPoints and series */
    private LineGraphSeries<DataPoint> lineGraphSeries;
    private ArrayList<DataPoint> dataPoints;

    /* Passed in lift variables */
    private int exerciseId;

    /* Database Helper */
    DatabaseHelper databaseHelper;

    TextView[] tvDateSelections;

    public CardioGraphFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lift_graph, container, false);

        setupDateSelections(view);
        graph = (GraphView) view.findViewById(R.id.graph);
        rel_graph = (RelativeLayout) view.findViewById(R.id.rel_graph);
        tvNoData = (TextView) view.findViewById(R.id.tv_noData);


        /* Attempt to get the existing data */
        try {
            getExistingData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {

            dataPoints = MockedDataPoints.getMockDataPoints();
            lineGraphSeries.resetData(dataPoints.toArray(new DataPoint[dataPoints.size()]));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /* createGraph */
        createGraph();
        return view;
    }

    /* todo clean up code */
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
            for (DataPoint dataPoint: dataPoints){
                if(currentTime-dataPoint.getX()<= timeLimit){
                    dataPointsLocal.add(dataPoint);
                }
            }

            DataPoint[] dataPoints1 = dataPointsLocal.toArray(new DataPoint[dataPointsLocal.size()]);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        exerciseId = args.getInt(getString(R.string.exercise_id), 0);

        databaseHelper = new DatabaseHelper(this.getContext());

    }

    /* todo just change the x min and x max for times */
    private void createGraph(){
        graph.setTitle("Max Weight Over Time");
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.getContext()));
        if(!lineGraphSeries.isEmpty()){
            graph.getGridLabelRenderer().setNumHorizontalLabels(3);
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getViewport().setYAxisBoundsManual(true);
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


        }

    }

    public void getExistingData() throws ParseException {
        dataPoints = new ArrayList<>();
        lineGraphSeries = new LineGraphSeries<>();
        /* Get db and query data */
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT MAX("+ TableConstants.MaxWeight +") " +
                ", " + TableConstants.DayDate + " " +
                "FROM "+ TableConstants.DayTableName +" INNER JOIN " +TableConstants.MaxTableName +
                " ON " + TableConstants.DayTableName +"." +TableConstants.DayId +" = " +
                TableConstants.MaxTableName + "." +TableConstants.DayId +
                " WHERE " +TableConstants.MaxTableName + "." +TableConstants.ExerciseId
                + " = " + exerciseId +
                " GROUP BY " + TableConstants.DayDate +
                " ORDER BY " + TableConstants.MaxTableName + "." +TableConstants.DayId +" ASC", null);
        c.moveToFirst();

        /* Get date formatter */
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);

        Log.e(TAG, "Max Count: " + c.getCount());

        /* Add the maxes to the line series */
        while(!c.isAfterLast()){
            Date date = dateFormat.parse(c.getString(1));
            Log.i(TAG, c.getString(1));
            DataPoint dataPoint =  new DataPoint(date, c.getDouble(0));
            dataPoints.add(dataPoint);
            lineGraphSeries.appendData(dataPoint, true, c.getCount());
            c.moveToNext();
        }



        if(dataPoints.size() == 0){
            tvNoData.setVisibility(View.VISIBLE);
            rel_graph.setVisibility(View.GONE);
        }else{
            tvNoData.setVisibility(View.GONE);
            rel_graph.setVisibility(View.VISIBLE);
        }

        /* close cursor and db */
        c.close();
        db.close();
    }

    public void updateNewLiftSet() throws ParseException {
        getExistingData();
        createGraph();
    }

}
