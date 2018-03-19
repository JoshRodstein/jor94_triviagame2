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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class ScoreHistoryActivity extends AppCompatActivity {

    ListView score_list;
    TextView high_score;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_history);
        score_list = findViewById(R.id.scoreList);
        high_score = findViewById(R.id.textView4);

        loadScores();

    }

    protected void back_button(View view){
        finish();
    }

    /*
    * loadScors method accepts string of score history and parses into a string
    * for the ArrayAdapter.
    * */
    protected void loadScores(){

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference DBScoresRef = fbdb.getReference("Scores");
        DatabaseReference userNode = DBScoresRef.child(mAuth.getCurrentUser().getUid());

        userNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> scores = new ArrayList<>();
                for(DataSnapshot child:dataSnapshot.getChildren()) {

                    Scores sc = new Scores(child.getKey(), child.getValue().toString());
                    scores.add(sc.getTimestamp() + "    :    " + sc.getScore() + "%");


                }
                String[] strAry = new String[scores.size()];
                strAry = scores.toArray(strAry);

                ListAdapter adapter =
                        new ArrayAdapter<String>( ScoreHistoryActivity.this,
                                android.R.layout.simple_list_item_1, strAry);
                score_list.setAdapter(adapter);
            }@Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }
}
