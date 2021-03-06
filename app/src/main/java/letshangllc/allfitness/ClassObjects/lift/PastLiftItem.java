package letshangllc.allfitness.ClassObjects.lift;

import java.util.ArrayList;

/**
 * Created by cvburnha on 5/15/2016.
 */
public class PastLiftItem {
    private ArrayList<LiftSet> liftSets;
    private String date;

    public PastLiftItem(ArrayList<LiftSet> liftSets, String date) {
        this.liftSets = liftSets;
        this.date = date;
    }

    public ArrayList<LiftSet> getLiftSets() {
        return liftSets;
    }

    public void setLiftSets(ArrayList<LiftSet> liftSets) {
        this.liftSets = liftSets;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
