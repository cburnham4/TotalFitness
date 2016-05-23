package letshangllc.allfitness.ActivitiesAndFragments.types.fragments.lift;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import letshangllc.allfitness.Database.DatabaseHelper;
import letshangllc.allfitness.Database.TableConstants;
import letshangllc.allfitness.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiftGraphFragment extends Fragment {
    private GraphView graph;

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

        /* createGraph */
        createGraph(lineGraphSeries, 0);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        exerciseId = args.getInt(getString(R.string.exercise_id), 0);

        databaseHelper = new DatabaseHelper(this.getContext());

        /* Attempt to get the existing data */
        try {
            getExistingData();
        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

    /* todo just change the x min and x max for times */
    private void createGraph(LineGraphSeries<DataPoint> series, int maxIfOne){
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this.getContext()));
        if(!series.isEmpty()){
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
                graph.getViewport().setMinX(series.getLowestValueX()-.5*24*60*60*1000);
                graph.getViewport().setMaxX(series.getHighestValueX());
                graph.setTitle("Max Weight Over Time");

                series.setDrawDataPoints(true);
                series.setDataPointsRadius(10);
                series.setThickness(4);

                graph.getViewport().setScalable(true);
                graph.getViewport().setScrollable(true);
                graph.addSeries(series);
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
                + " = " + exerciseId , null);
        c.moveToFirst();

        /* Get date formatter */
        DateFormat dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.US);

        /* Add the maxes to the line series */
        while(!c.isAfterLast()){
            Date date = dateFormat.parse(c.getString(1));

            DataPoint dataPoint =  new DataPoint(date, c.getDouble(0));
            dataPoints.add(dataPoint);
            lineGraphSeries.appendData(dataPoint, true, c.getCount());
            c.moveToNext();
        }


        /* close cursor and db */
                c.close();
        db.close();
    }
}
