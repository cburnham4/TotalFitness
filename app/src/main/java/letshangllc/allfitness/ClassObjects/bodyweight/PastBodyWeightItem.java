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

    public int getMaxReps(){
        int maxReps = 0;
        for (BodyWeightSet bodyWeightSet: bodyWeightSets){
            if(bodyWeightSet.reps >maxReps){
                maxReps = bodyWeightSet.reps;
            }
        }
        return maxReps;
    }
    public double getMaxTime(){
        double maxTime = 0;
        for (BodyWeightSet bodyWeightSet: bodyWeightSets){
            if(maxTime < bodyWeightSet.duration){
                maxTime = bodyWeightSet.duration;
            }
        }
        return maxTime/60.0;
    }
}
