package edu.pitt.cs1699.jor94_triviagame2;


public class TermAndDef {
    private String term, def;


    public TermAndDef() {

    }

    public TermAndDef(String t, String d) {
        this.term = t;
        this.def = d;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getTerm() {
        return term;
    }

    public String getDef() {
        return def;
    }
}
