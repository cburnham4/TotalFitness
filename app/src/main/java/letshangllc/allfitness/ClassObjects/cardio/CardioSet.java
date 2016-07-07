package letshangllc.allfitness.ClassObjects.cardio;

import android.util.Log;

/**
 * Created by cvburnha on 6/5/2016.
 */
public class CardioSet {
    public double elapsedTime;
    public int hours;
    public int minutes;
    public int seconds;
    public double distance;
    public int dayId;
    public int setId;

    public CardioSet(double elapsedTime, int hours, int minutes, int seconds, double distance, int dayId, int setId) {
        this.elapsedTime = elapsedTime;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.distance = distance;
        this.dayId = dayId;
        this.setId = setId;
    }

    public double getMPH(){
        if(distance == 0 || elapsedTime ==0){
            return 0;
        }
        double hours  = elapsedTime/3600.0;
        double mph = distance/hours;
        Log.i("CARDIOSET: " , "MPH: " +mph );
        return mph;
    }

    public double getMileTime(){
        if(distance == 0 || elapsedTime ==0){
            return 0;
        }
        double minutes  = elapsedTime/60.0;
        Log.e("CARDIO", "MINUTES: " + minutes);
        double mileTime = minutes/distance;
        Log.e("CARDIO", "Mile Time: " + mileTime);
        return mileTime;
    }
}
