package letshangllc.allfitness.ClassObjects.bodyweight;

/**
 * Created by cvburnha on 7/1/2016.
 */
public class BodyWeightSet {
    public int setId;
    public double duration;
    public int reps;
    public int minutes;
    public int seconds;
    public int dayId;

    public BodyWeightSet(int setId, double duration, int reps, int minutes, int seconds, int dayId) {
        this.setId = setId;
        this.duration = duration;
        this.reps = reps;
        this.minutes = minutes;
        this.seconds = seconds;
        this.dayId = dayId;
    }
}
