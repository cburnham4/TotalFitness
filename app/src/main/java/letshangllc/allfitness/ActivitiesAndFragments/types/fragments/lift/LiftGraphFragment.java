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
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
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
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiftGraphFragment extends Fragment {
    private static final String TAG= LiftGraphFragment.class.getSimpleName();

    private GraphView graph;

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

        graph = (GraphView) view.findViewById(R.id.graph);
        tvNoData = (TextView) view.findViewById(R.id.tv_noData);

        /* Attempt to get the existing data */
        try {
            getExistingData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /* createGraph */
        createGraph();
        return view;
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

                graph.getViewport().setMinX(dataPoint.getX()-5*24*60*60*1000);
                graph.getViewport().setMaxX(dataPoint.getX()+5*24*60*60*1000);
                graph.getViewport().setYAxisBoundsManual(true);
                graph.getViewport().setMinY(dataPoint.getY()-10);
                graph.getViewport().setMaxY(dataPoint.getY() + 10);
                graph.setTitle("Max Weight Over Time");
            }else{
                graph.getViewport().setMinX(lineGraphSeries.getLowestValueX()-.25*24*60*60*1000);
                graph.getViewport().setMaxX(lineGraphSeries.getHighestValueX()+.25*24*60*60*1000);
                graph.setTitle("Max Weight Over Time");

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
            graph.setVisibility(View.GONE);
        }else{
            tvNoData.setVisibility(View.GONE);
            graph.setVisibility(View.VISIBLE);
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
