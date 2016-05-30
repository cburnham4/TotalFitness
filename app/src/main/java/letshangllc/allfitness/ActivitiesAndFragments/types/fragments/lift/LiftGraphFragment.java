package letshangllc.allfitness.ActivitiesAndFragments.types.fragments.lift;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.List;
import java.util.Locale;

import letshangllc.allfitness.ClassObjects.LiftSet;
import letshangllc.allfitness.ClassObjects.PastLiftSet;
import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.MockData.MockedDataPoints;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiftGraphFragment extends Fragment {
    private static final String TAG= LiftGraphFragment.class.getSimpleName();

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

    public LiftGraphFragment() {
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

        Date currentDate = new Date();

        final double currentTime = currentDate.getTime();

        /* Milliseconds in a day */
        double timeInDay = 24 * 60 * 60 * 1000;
        final double timeInMonth = timeInDay * 30.5;
        final double timeIn3Month = timeInMonth *3;
        final double timeIn6Month = timeIn3Month * 2;
        final double timeInYear = timeIn6Month * 2;

        /* todo check if you can break if not true */
        tv_1m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DataPoint> dataPointsLocal = new ArrayList<DataPoint>();
                /* Iterate though the global datapoints to get the ones that fall in a 1 month range */
                for (DataPoint dataPoint: dataPoints){
                    if(currentTime-dataPoint.getX()<= timeInMonth){
                        dataPointsLocal.add(dataPoint);
                    }
                }

                DataPoint[] dataPoints1 = dataPointsLocal.toArray(new DataPoint[dataPointsLocal.size()]);
                lineGraphSeries.resetData(dataPoints1);
                Viewport viewport = graph.getViewport();
                viewport.setMinX(lineGraphSeries.getLowestValueX()-5*24*60*60*1000);
                viewport.setMaxX(lineGraphSeries.getHighestValueX()+5*24*60*60*1000);

                tv_1m.setBackgroundColor(getResources().getColor(R.color.divider));
                tv_3m.setBackgroundColor(0);
                tv_6m.setBackgroundColor(0);
                tv_1y.setBackgroundColor(0);
                tv_all.setBackgroundColor(0);
            }
        });
        tv_3m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DataPoint> dataPointsLocal = new ArrayList<DataPoint>();
                /* Iterate though the global datapoints to get the ones that fall in a 1 month range */
                for (DataPoint dataPoint: dataPoints){
                    if(currentTime-dataPoint.getX()<= timeIn3Month){
                        dataPointsLocal.add(dataPoint);
                    }
                }

                DataPoint[] dataPoints1 = dataPointsLocal.toArray(new DataPoint[dataPointsLocal.size()]);
                lineGraphSeries.resetData(dataPoints1);
               // graph.onDataChanged(false, false);
                Viewport viewport = graph.getViewport();
                viewport.setMinX(lineGraphSeries.getLowestValueX()-5*24*60*60*1000);
                viewport.setMaxX(lineGraphSeries.getHighestValueX()+5*24*60*60*1000);
                tv_1m.setBackgroundColor(0);
                tv_3m.setBackgroundColor(getResources().getColor(R.color.divider));
                tv_6m.setBackgroundColor(0);
                tv_1y.setBackgroundColor(0);
                tv_all.setBackgroundColor(0);
            }
        });
        tv_6m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DataPoint> dataPointsLocal = new ArrayList<DataPoint>();
                /* Iterate though the global datapoints to get the ones that fall in a 1 month range */
                for (DataPoint dataPoint: dataPoints){
                    if(currentTime-dataPoint.getX()<= timeIn6Month){
                        dataPointsLocal.add(dataPoint);
                    }
                }

                DataPoint[] dataPoints1 = dataPointsLocal.toArray(new DataPoint[dataPointsLocal.size()]);
                lineGraphSeries.resetData(dataPoints1);
                Viewport viewport = graph.getViewport();
                viewport.setMinX(lineGraphSeries.getLowestValueX()-5*24*60*60*1000);
                viewport.setMaxX(lineGraphSeries.getHighestValueX()+5*24*60*60*1000);
                tv_1m.setBackgroundColor(0);
                tv_3m.setBackgroundColor(0);
                tv_6m.setBackgroundColor(getResources().getColor(R.color.divider));
                tv_1y.setBackgroundColor(0);
                tv_all.setBackgroundColor(0);
            }
        });
        tv_1y.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DataPoint> dataPointsLocal = new ArrayList<DataPoint>();
                /* Iterate though the global datapoints to get the ones that fall in a 1 month range */
                for (DataPoint dataPoint: dataPoints){
                    if(currentTime-dataPoint.getX()<= timeInYear){
                        dataPointsLocal.add(dataPoint);
                    }
                }

                DataPoint[] dataPoints1 = dataPointsLocal.toArray(new DataPoint[dataPointsLocal.size()]);
                lineGraphSeries.resetData(dataPoints1);
                Viewport viewport = graph.getViewport();
                viewport.setMinX(lineGraphSeries.getLowestValueX()-5*24*60*60*1000);
                viewport.setMaxX(lineGraphSeries.getHighestValueX()+5*24*60*60*1000);
                tv_1m.setBackgroundColor(0);
                tv_3m.setBackgroundColor(0);
                tv_6m.setBackgroundColor(0);
                tv_1y.setBackgroundColor(getResources().getColor(R.color.divider));
                tv_all.setBackgroundColor(0);
            }
        });
        tv_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<DataPoint> dataPointsLocal = new ArrayList<DataPoint>();
                /* Iterate though the global datapoints to get the ones that fall in a 1 month range */
                for (DataPoint dataPoint: dataPoints) {
                    dataPointsLocal.add(dataPoint);
                }

                DataPoint[] dataPoints1 = dataPointsLocal.toArray(new DataPoint[dataPointsLocal.size()]);
                lineGraphSeries.resetData(dataPoints1);
                Viewport viewport = graph.getViewport();
                viewport.setMinX(lineGraphSeries.getLowestValueX()-5*24*60*60*1000);
                viewport.setMaxX(lineGraphSeries.getHighestValueX()+5*24*60*60*1000);
                tv_1m.setBackgroundColor(0);
                tv_3m.setBackgroundColor(0);
                tv_6m.setBackgroundColor(0);
                tv_1y.setBackgroundColor(0);
                tv_all.setBackgroundColor(getResources().getColor(R.color.divider));
            }
        });
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
                viewport.setYAxisBoundsManual(true);
                viewport.setMinY(dataPoint.getY()-10);
                viewport.setMaxY(dataPoint.getY() + 10);

            }else{
                graph.getViewport().setMinX(lineGraphSeries.getLowestValueX()-.25*24*60*60*1000);
                graph.getViewport().setMaxX(lineGraphSeries.getHighestValueX()+.25*24*60*60*1000);

                lineGraphSeries.setDrawDataPoints(true);
                lineGraphSeries.setDataPointsRadius(10);
                lineGraphSeries.setThickness(4);

                graph.getViewport().setScalable(true);
                graph.getViewport().setScrollable(true);
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
                ", " + TableConstants.DayDateLifted + " " +
                "FROM "+ TableConstants.DayTableName +" INNER JOIN " +TableConstants.MaxTableName +
                " ON " + TableConstants.DayTableName +"." +TableConstants.DayId +" = " +
                TableConstants.MaxTableName + "." +TableConstants.DayId +
                " WHERE " +TableConstants.MaxTableName + "." +TableConstants.ExerciseId
                + " = " + exerciseId +
                " GROUP BY " + TableConstants.DayDateLifted, null);
        c.moveToFirst();

        /* Get date formatter */
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);

        Log.e(TAG, "Max Count: " + c.getCount());

        /* Add the maxes to the line series */
        while(!c.isAfterLast()){
            Date date = dateFormat.parse(c.getString(1));

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
