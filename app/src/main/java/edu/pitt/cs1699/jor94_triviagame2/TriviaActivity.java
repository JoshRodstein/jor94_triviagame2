package edu.pitt.cs1699.jor94_triviagame2;

/*
* By: Joshua Rodstein
* Assignment1 - CS1699
* PItt: jor94@pitt.edu
* ID: 4021607
*
* */


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class TriviaActivity extends AppCompatActivity {

    TextView term;
    String ans;
    ListView list_triv;
    ProgressBar bar;
    DatabaseHelper db;
    ArrayList gList;
    int qCounter = 0, correct= 0, barProg = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);
        byte[] bytes = new byte[1024];
        list_triv = findViewById(R.id.triv_list);
        term = findViewById(R.id.term_text);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        bar.setMax(5);


        // create DB instance for terms and defs
        db = new DatabaseHelper(this);



        list_triv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPosition = position;
                String  itemValue = (String) list_triv.getItemAtPosition(position);
                // Show Alert

                if(itemValue.contentEquals(ans)){
                    correct++;
                    Toast.makeText(getApplicationContext(), "Correct Answer: Your score is "
                            + correct, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong Answer!",
                            Toast.LENGTH_SHORT).show();
                }

                qCounter++;
                barProg++;
                bar.setProgress(barProg);
                if(qCounter == 5){
                    saveScore(correct);
                    finish();
                } else {
                    generateQuiz();
                }
            }
        });
    }

    /*
    * Save score by creating file in Android internal storage
    * */
    protected void saveScore(int correct){
        int score = correct * 20;
        try {
            FileOutputStream fOut = openFileOutput("score_history.txt",MODE_APPEND);
            String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
            fOut.write((currentDateTimeString + "\t" + score + "\n").getBytes());
            fOut.close();
        } catch(IOException io) {
            Log.e("ERROR", io.toString());
        }
    }

    /*
    * Load, shuffle and read from existing glossary files.
    * */
    protected void generateQuiz(){
        Random rand = new Random();
        if(db != null){
            int t = rand.nextInt(5);
            ArrayList<TermAndDef> tdList = db.getAllTermsAndDefs().;
            Collections.shuffle(tdList);
            term.setText(tdList.get(t).getDef());
            ans = tdList.get(t).getTerm();
            String[] tdAry = {
                    tdList.get(0).getDef(),
                    tdList.get(1).getDef(),
                    tdList.get(2).getDef(),
                    tdList.get(3).getDef(),
                    tdList.get(4).getDef()
            };

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    tdAry);

            list_triv.setAdapter(arrayAdapter);

        }
    }
}
