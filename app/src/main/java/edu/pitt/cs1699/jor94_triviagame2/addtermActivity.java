/*
* By: Joshua Rodstein
* Assignment1 - CS1699
* PItt: jor94@pitt.edu
* ID: 4021607
*
* */


package edu.pitt.cs1699.jor94_triviagame2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.FileOutputStream;
import java.io.IOException;

public class addtermActivity extends AppCompatActivity {
    DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addterm);
        db = new DatabaseHelper(this);
    }


    public void ok_onClick(View view){
        EditText term = (EditText) findViewById(R.id.addterm_edittext);
        EditText def = (EditText) findViewById(R.id.adddef_edittext);

        String termString = term.getText().toString();
        String defString = def.getText().toString();

        if(!termString.equals("") && !defString.equals("")){
           db.addTerm(new TermAndDef(termString, defString));
        }

        finish();
    }

    public void addToGlossary(CharSequence term, CharSequence def){
        String t = (String)term;
        String d = (String)def;
        try {
            FileOutputStream fOut = openFileOutput("new_gloss.txt",MODE_APPEND);
            fOut.write((t+System.getProperty("line.separator")).getBytes());
            fOut.write((d+System.getProperty("line.separator")).getBytes());
            fOut.close();
        } catch(IOException io) {
            Log.e("ERROR", io.toString());
        }

    }

}
