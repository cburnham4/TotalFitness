package letshangllc.allfitness.MockData;

import java.util.ArrayList;

import letshangllc.allfitness.ClassObjects.MuscleGroup;

/**
 * Created by cvburnha on 4/23/2016.
 */
public final class MockedGroups {

    public static MuscleGroup back = new MuscleGroup(0, "Back");
    public static MuscleGroup legs = new MuscleGroup(1, "Legs");
    public static MuscleGroup chest = new MuscleGroup(2, "Chest");

    public static MuscleGroup[] groups = {back, legs, chest};
}
