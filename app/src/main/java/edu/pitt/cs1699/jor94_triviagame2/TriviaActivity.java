package edu.pitt.cs1699.jor94_triviagame2;

/*
* By: Joshua Rodstein
* Assignment1 - CS1699
* PItt: jor94@pitt.edu
* ID: 4021607
*
* */


import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.*;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class TriviaActivity extends AppCompatActivity {
    private Switch tts;
    private TextView term;
    private String ans;
    private ListView list_triv;
    private ProgressBar bar;
    private DatabaseHelper db;
    private int qCounter = 0, correct= 0, barProg = 0;
    private FirebaseAuth mAuth;
    private FirebaseDatabase fbdb;
    private boolean ttsReady;
    private TextToSpeech TTSpeak;
    private ArrayList<TermAndDef> tdList;
    private String[] tdAry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ttsReady = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        fbdb = FirebaseDatabase.getInstance();
        list_triv = findViewById(R.id.triv_list);
        term = findViewById(R.id.term_text);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        bar.setMax(5);
        tdAry = new String[5];


        // create DB instance for terms and defs
        db = new DatabaseHelper(this);
        generateQuiz();

        if (getIntent().getExtras().getBoolean("ttsIsOn") == true){
            initTTS();
        }

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

    protected void initTTS(){
        TTSpeak = new TextToSpeech(this,
                new TextToSpeech.OnInitListener() {
                    public void onInit(int status) {
                        ttsReady = true;
                        quizSpeak();
                    }
                });

    }


    /*
    * Save score by creating file in Android internal storage
    * */
    protected void saveScore(int correct){

        int score = correct * 20;
        String strScore = String.valueOf(score);
        Scores s = new Scores(DateFormat.getDateTimeInstance().format(new Date()).toString(), strScore);
        db.addScore(mAuth.getCurrentUser(), s);

        ScoreHelper.updateHighList(s);

    }

    private void quizSpeak(){
        if(ttsReady == true){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                TTSpeak.speak(term.getText().toString() + " is defined as ",
                        TextToSpeech.QUEUE_ADD,null,null);

                for (int i = 0; i < 5; i++){
                    TTSpeak.speak(tdAry[i],
                            TextToSpeech.QUEUE_ADD,null, null);
                }

            } else {

                TTSpeak.speak(term.getText().toString() + " is defined as ",
                        TextToSpeech.QUEUE_ADD,null);

                for (int i = 0; i < 5; i++){
                    TTSpeak.speak(tdAry[i],
                            TextToSpeech.QUEUE_ADD,null);
                }

            }
        }
    }

    /*
    * Load, shuffle and read from existing glossary files.
    * */
    protected void generateQuiz(){
        if( ttsReady == true && TTSpeak.isSpeaking()){
            TTSpeak.stop();
        }

        Random rand = new Random();
        int t = rand.nextInt(5);
        tdList = new ArrayList<>(db.getAllTermsAndDefs());

        Collections.shuffle(tdList);
        term.setText(tdList.get(t).getTerm());
        ans = tdList.get(t).getDef();
        for (int i = 0; i < 5; i ++) {
            tdAry[i]= tdList.get(i).getDef();
        }
        quizSpeak();
        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                tdAry);

        list_triv.setAdapter(arrayAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (TTSpeak.isSpeaking()){
            TTSpeak.shutdown();
        }
    }



}
