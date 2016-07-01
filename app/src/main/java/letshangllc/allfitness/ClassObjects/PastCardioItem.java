package letshangllc.allfitness.ClassObjects;

import java.util.ArrayList;

/**
 * Created by cvburnha on 7/1/2016.
 */
public class PastCardioItem {
    public ArrayList<CardioSet> cardioSets;
    public String date;

    public PastCardioItem(ArrayList<CardioSet> cardioSets, String date) {
        this.cardioSets = cardioSets;
        this.date = date;
    }
}
