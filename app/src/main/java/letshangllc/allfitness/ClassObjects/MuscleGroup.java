package letshangllc.allfitness.ClassObjects;

/**
 * Created by cvburnha on 4/23/2016.
 */
public class MuscleGroup {
    private int muscleGroupId;
    private String muscleGroupName;

    public MuscleGroup(int muscleGroupId, String muscleGroupName) {
        this.muscleGroupId = muscleGroupId;
        this.muscleGroupName = muscleGroupName;
    }

    public int getMuscleGroupId() {
        return muscleGroupId;
    }

    public void setMuscleGroupId(int muscleGroupId) {
        this.muscleGroupId = muscleGroupId;
    }

    public String getMuscleGroupName() {
        return muscleGroupName;
    }

    public void setMuscleGroupName(String muscleGroupName) {
        this.muscleGroupName = muscleGroupName;
    }

    public String toString(){
        return muscleGroupName;
    }
}
