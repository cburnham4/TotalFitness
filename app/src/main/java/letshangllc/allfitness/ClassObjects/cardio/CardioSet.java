package letshangllc.allfitness.ClassObjects.cardio;

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
        double hours  = elapsedTime/3600.0;
        double mph = distance/hours;
        return mph;
    }
}
