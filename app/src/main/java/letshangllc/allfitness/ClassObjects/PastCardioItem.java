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

    public double getMaxTime(){
        double maxTime = 0;
        for(CardioSet cardioSet: cardioSets){
            if(maxTime < cardioSet.elapsedTime){
                maxTime = cardioSet.elapsedTime;
            }
        }
        return maxTime/60.0;
    }

    /* Return speed in mph */
    public double getMaxSpeed(){
        double maxMPH = 0;
        for(CardioSet cardioSet: cardioSets){
            if(maxMPH < cardioSet.getMPH()){
                maxMPH = cardioSet.getMPH();
            }
        }
        return maxMPH;
    }

    /* Return max distance */
    public double getMaxDistance(){
        double maxDistance = 0;
        for(CardioSet cardioSet: cardioSets){
            if(maxDistance < cardioSet.distance){
                maxDistance = cardioSet.distance;
            }
        }
        return maxDistance;
    }
}
