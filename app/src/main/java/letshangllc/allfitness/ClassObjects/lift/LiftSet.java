package letshangllc.allfitness.ClassObjects.lift;

/**
 * Created by cvburnha on 5/15/2016.
 */
public class LiftSet {
    private int dayId;
    private int setId;
    private int reps;
    private double weight;

    public LiftSet(int dayId, int setId, int reps, double weight) {
        this.dayId = dayId;
        this.setId = setId;
        this.reps = reps;
        this.weight = weight;
    }

    public int getDayId() {
        return dayId;
    }

    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
