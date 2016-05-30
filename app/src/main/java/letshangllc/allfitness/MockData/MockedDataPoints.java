package letshangllc.allfitness.MockData;

import android.util.Log;

import com.jjoe64.graphview.series.DataPoint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by cvburnha on 5/30/2016.
 */
public final class MockedDataPoints {

    public static ArrayList<DataPoint> getMockDataPoints() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        ArrayList<DataPoint> dataPoints = new ArrayList<>();

        dataPoints.add(new DataPoint(dateFormat.parse("24-07-2015"), 145));
        dataPoints.add(new DataPoint(dateFormat.parse("28-08-2015"), 140));
        dataPoints.add(new DataPoint(dateFormat.parse("24-09-2015"), 175));
        dataPoints.add(new DataPoint(dateFormat.parse("28-11-2015"), 180));
        dataPoints.add(new DataPoint(dateFormat.parse("24-01-2016"), 155));
        dataPoints.add(new DataPoint(dateFormat.parse("28-01-2016"), 150));
        dataPoints.add(new DataPoint(dateFormat.parse("24-03-2016"), 168));
        dataPoints.add(new DataPoint(dateFormat.parse("02-04-2016"), 167));
        dataPoints.add(new DataPoint(dateFormat.parse("15-04-2016"), 165));
        dataPoints.add(new DataPoint(dateFormat.parse("00-05-2016"), 172));
        dataPoints.add(new DataPoint(dateFormat.parse("09-05-2016"), 170));
        dataPoints.add(new DataPoint(dateFormat.parse("14-05-2016"), 170));
        dataPoints.add(new DataPoint(dateFormat.parse("24-05-2016"), 175));
        dataPoints.add(new DataPoint(dateFormat.parse("28-05-2016"), 180));
        dataPoints.add(new DataPoint(new Date(), 175));

        Log.e("DATAPOINT TIME: ", ""+ dataPoints.get(0).getX());
        Log.e("DATAPOINT TIME: ", ""+ dataPoints.get(1).getX());
        Log.e("DATAPOINT TIME: ", ""+ dataPoints.get(2).getX());
        Log.e("DATAPOINT TIME: ", ""+ dataPoints.get(3).getX());
        Log.e("DATAPOINT TIME: ", ""+ dataPoints.get(4).getX());
        return  dataPoints;
    }


}
