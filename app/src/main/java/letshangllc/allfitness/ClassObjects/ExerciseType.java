package letshangllc.allfitness.ClassObjects;

/**
 * Created by cvburnha on 3/17/2016.
 * Enum with the 4 types of exercises
 */
public enum ExerciseType {
    BODYWEIGHT ("Bodyweight", 0),
    CARDIO ("Cardio", 1),
    LIFT ("Lift", 2),
    TIMED ("Timed", 3);

    public String getExerciseTypeName() {
        return exerciseTypeName;
    }

    public int getExerciseTypeID() {
        return exerciseTypeID;
    }

    private final String exerciseTypeName;
    private final int exerciseTypeID;

    ExerciseType(String exerciseTypeName, int exerciseTypeID){
        this.exerciseTypeName = exerciseTypeName;
        this.exerciseTypeID = exerciseTypeID;
    }


    public static ExerciseType getType(int index){
        switch (index){
            case 0:
                return BODYWEIGHT;
            case 1:
                return CARDIO;
            case 2:
                return LIFT;
            case 3:
                return TIMED;
        }
        return TIMED;
    }
}
