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
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    private FirebaseAuth mAuth;
    FirebaseDatabase fbdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia);
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        fbdb = FirebaseDatabase.getInstance();
        byte[] bytes = new byte[1024];
        list_triv = findViewById(R.id.triv_list);
        term = findViewById(R.id.term_text);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        bar.setMax(5);


        // create DB instance for terms and defs
        db = new DatabaseHelper(this);
        generateQuiz();



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
        String strScore = String.valueOf(score);
        Scores s = new Scores(DateFormat.getDateTimeInstance().format(new Date()).toString(), strScore);
        db.addScore(mAuth.getCurrentUser(), s);

        DatabaseReference DBScoresRef = fbdb.getReference("Scores");
        DBScoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Scores> scoreList = new ArrayList<>();
                for(DataSnapshot child:dataSnapshot.getChildren()) {
                    scoreList.add(new Scores(child.getKey(), child.getValue().toString()));
                };

                Collections.sort(scoreList, Comparator.comparing(Scores::getScore));
                Collections.sort(scoreList, Comparator.comparing(Scores::getTimestamp));

                ScoreHelper.updateHighList(scoreList.get(0));

            }@Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    /*
    * Load, shuffle and read from existing glossary files.
    * */
    protected void generateQuiz(){
        Random rand = new Random();
        int t = rand.nextInt(5);
        ArrayList<TermAndDef> tdList = new ArrayList<>(db.getAllTermsAndDefs());
        Collections.shuffle(tdList);
        term.setText(tdList.get(t).getTerm());
        ans = tdList.get(t).getDef();
        String[] tdAry = {
                tdList.get(0).getDef(),
                tdList.get(1).getDef(),
                tdList.get(2).getDef(),
                tdList.get(3).getDef(),
                tdList.get(4).getDef()
        };

        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                tdAry);

        list_triv.setAdapter(arrayAdapter);

    }

}
