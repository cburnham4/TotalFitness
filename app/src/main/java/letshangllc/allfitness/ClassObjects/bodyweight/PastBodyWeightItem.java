package letshangllc.allfitness.ClassObjects.bodyweight;

import java.util.ArrayList;

/**
 * Created by cvburnha on 7/1/2016.
 */
public class PastBodyWeightItem {
    public ArrayList<BodyWeightSet> bodyWeightSets;
    public String date;

    public PastBodyWeightItem(ArrayList<BodyWeightSet> bodyWeightSets, String date) {
        this.bodyWeightSets = bodyWeightSets;
        this.date = date;
    }
}
