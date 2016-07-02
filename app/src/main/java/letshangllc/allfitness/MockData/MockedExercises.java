package letshangllc.allfitness.MockData;

import java.util.ArrayList;

import letshangllc.allfitness.ClassObjects.ExerciseType;
import letshangllc.allfitness.ClassObjects.ExerciseItem;

/**
 * Created by cvburnha on 3/17/2016.
 */
public final class MockedExercises {
    private static ArrayList<ExerciseItem> exerciseItems = new ArrayList<>();

    private static ExerciseItem exerciseItem1 = new ExerciseItem(1,"Bench", ExerciseType.LIFT);
    private static ExerciseItem exerciseItem2 = new ExerciseItem(1, "Jogging", ExerciseType.CARDIO);
    private static ExerciseItem exerciseItem3 = new ExerciseItem(1, "Situps", ExerciseType.BODYWEIGHT);
    //private static ExerciseItem exerciseItem4 = new ExerciseItem(1, "plank", ExerciseType.TIMED);
    private static ExerciseItem exerciseItem5 = new ExerciseItem(5,"PullUps", ExerciseType.BODYWEIGHT);
    private static ExerciseItem exerciseItem6 = new ExerciseItem(6, "Decline", ExerciseType.LIFT);

    public static ArrayList<ExerciseItem> getExerciseItems(){
        exerciseItems.add(exerciseItem1);
        exerciseItems.add(exerciseItem2);
        exerciseItems.add(exerciseItem3);
        //exerciseItems.add(exerciseItem4);
        exerciseItems.add(exerciseItem5);
        exerciseItems.add(exerciseItem6);
        return exerciseItems;
    }
}
