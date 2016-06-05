package letshangllc.allfitness.ClassObjects;

/**
 * Created by cvburnha on 6/5/2016.
 */
public class CardioSet {
    public double elapsedTime;
    public double distance;
    public int dayId;
    public int setId;

    public CardioSet(double elapsedTime, double distance, int dayId, int setId) {
        this.elapsedTime = elapsedTime;
        this.distance = distance;
        this.dayId = dayId;
        this.setId = setId;
    }
}
