package letshangllc.allfitness.ClassObjects;

/**
 * Created by cvburnha on 3/17/2016.
 */
public class ExerciseItem {

    private int exerciseID;
    private String exerciseName;
    private int muscleId;
    private ExerciseType exerciseType;

    public ExerciseItem(int exerciseID, String exerciseName, ExerciseType exerciseType) {
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.exerciseType = exerciseType;
    }

    public ExerciseItem(int exerciseID, String exerciseName, ExerciseType exerciseType, int muscleId) {
        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.muscleId = muscleId;
        this.exerciseType = exerciseType;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }

    public int getExerciseID() {
        return exerciseID;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public String toString(){
        return exerciseName;
    }
}
