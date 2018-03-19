/*
* By: Joshua Rodstein
* Assignment1 - CS1699
* PItt: jor94@pitt.edu
* ID: 4021607
*
* */


package com.example.josh.triviagame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class ScoreHistoryActivity extends AppCompatActivity {

    ListView score_list;
    TextView high_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_history);
        score_list = findViewById(R.id.scoreList);
        high_score = findViewById(R.id.textView4);
        byte[] bytes = new byte[1024];

        try {
            FileInputStream is2 = openFileInput("score_history.txt");
            is2.read(bytes);
            is2.close();
            String scoreString = new String(bytes);
            loadScores(scoreString);
        } catch (IOException io){}

    }

    protected void back_button(View view){
        finish();
    }

    /*
    * loadScors method accepts string of score history and parses into a string
    * for the ArrayAdapter.
    * */
    protected void loadScores(String stamps){
        ArrayList<String> split_string = new ArrayList<>(Arrays.asList(stamps.split("\\n")));
        split_string.remove(split_string.size()-1);
        String[] sa = stamps.split("\\t|\\n");
        ArrayList<Integer> ia = new ArrayList<Integer>();
        for(int i = 0; i < sa.length-2;i+=2){
            ia.add(Integer.parseInt(sa[i+1]));
        }
        int high = Collections.max(ia);
        high_score.setText(high + "%");
        for(int i = 0; i < split_string.size();i++){
            split_string.set(i, split_string.get(i) + "%");
        }
        if(split_string != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, split_string);
            score_list.setAdapter(adapter);
        }
    }
}
