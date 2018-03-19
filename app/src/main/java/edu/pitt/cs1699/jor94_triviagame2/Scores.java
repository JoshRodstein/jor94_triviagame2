package edu.pitt.cs1699.jor94_triviagame2;


public class Scores {
    private String Timestamp;
    private String Score;


    public Scores() {

    }

    public Scores(String t, String s) {
        this.Timestamp = t;
        this.Score = s;
    }

    public String getTimestamp() {
        return this.Timestamp;
    }

    public String getScore() {
        return this.Score;
    }
}
