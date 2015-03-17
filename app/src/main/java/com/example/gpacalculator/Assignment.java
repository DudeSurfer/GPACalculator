package com.example.gpacalculator;

/**
 * Created by Vignesh Ravi on 19/2/2015.
 */
public class Assignment {
    private String subjName;
    private String assmName;
    private float scoreReceived;
    private float scoreMax;
    private float weightage;

    public Assignment(){}

    public Assignment(String subjName, String assmName, float scoreReceived, float scoreMax, float weightage) {
        super();
        this.subjName = subjName;
        this.assmName = assmName;
        this.scoreReceived = scoreReceived;
        this.scoreMax = scoreMax;
        this.weightage = weightage;
        
    }

    public String getSubjName() {
        return subjName;
    }

    public void setSubjName(String subjName) {
        this.subjName = subjName;
    }

    public String getAssmName(){
        return assmName;
    }

    public float getScoreReceived(){
        return scoreReceived;
    }

    public float getScoreMax(){
        return scoreMax;
    }

    public float getWeightage(){
        return weightage;
    }

    public void setAssmName(String assmName) {
        this.assmName = assmName;
    }

    public void setScoreReceived(float scoreReceived) {
        this.scoreReceived = scoreReceived;
    }

    public void setScoreMax(float scoreMax) {
        this.scoreMax = scoreMax;
    }

    public void setWeightage(float weightage) {
        this.weightage = weightage;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "subjName='" + subjName + '\'' +
                ", assmName='" + assmName + '\'' +
                ", scoreReceived=" + scoreReceived +
                ", scoreMax=" + scoreMax +
                ", weightage=" + weightage +
                '}';
    }

}
