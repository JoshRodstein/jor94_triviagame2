package edu.pitt.cs1699.jor94_triviagame2;


public class Scores {
    private String Timestamp;
    private String Score;
    private String uid;


    public Scores() {

    }

    public Scores(String t, String s) {
        this.Timestamp = t;
        this.Score = s;
        this.uid = null;
    }

    public Scores(String t, String s, String u) {
        this.Timestamp = t;
        this.Score = s;
        this.uid = u;
    }

    public String getTimestamp() {
        return this.Timestamp;
    }

    public String getScore() {
        return this.Score;
    }

    public String getUid() { return uid; }

    public void setUid(String id){
        this.uid = id;
    }
}
