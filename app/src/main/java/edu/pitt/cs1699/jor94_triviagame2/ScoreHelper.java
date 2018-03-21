package edu.pitt.cs1699.jor94_triviagame2;

import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by josh on 3/19/18.
 */



public class ScoreHelper {
    private FirebaseAuth mAuth;
    FirebaseDatabase fbdb;

    public ScoreHelper (){
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        fbdb = FirebaseDatabase.getInstance();
    }

    private void sortHigh(int score){
        FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();;
        DatabaseReference DBScoresRef = fbdb.getReference("Scores");
        DatabaseReference userNode = DBScoresRef.child(mAuth.getCurrentUser().getUid());

        userNode.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Scores> scoreList = new ArrayList<>();
                for(DataSnapshot child:dataSnapshot.getChildren()) {

                    scoreList.add(new Scores(child.getKey(), child.getValue().toString()));
                };

                Collections.sort(scoreList, Comparator.comparing(Scores::getScore));
                Collections.sort(scoreList, Comparator.comparing(Scores::getTimestamp));

            }@Override
            public void onCancelled(DatabaseError firebaseError) {}
        });


    }

    public static void updateHighList(Scores score){
        FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference DbHsRef = fbdb.getReference("HighScores");

        DbHsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    int i = 0;
                    boolean replace = false;
                    String prevChildUser = null, tempUser;
                    int prevChildScore = -1, tempScore;
                    String prevChildTime = null, tempTime;
                    for(DataSnapshot child:dataSnapshot.getChildren()) {
                        if(Integer.parseInt(score.getScore()) >
                                (Integer) child.child("score").getValue() && replace == false) {

                            prevChildUser = child.child("user").getValue().toString();
                            DbHsRef.child("score" + i).child("user").setValue(mAuth.getCurrentUser().getUid());
                            prevChildTime = child.child("timestamp").getValue().toString();
                            DbHsRef.child("score" + i).child("timestamp").setValue(score.getTimestamp());
                            prevChildScore = (Integer) child.child("score").getValue();
                            DbHsRef.child("score" + i).child("score").setValue(score.getScore());
                            replace = true;
                        } else if (replace == true){
                            tempUser = child.child("user").getValue().toString();
                            DbHsRef.child("score" + i).child("user").setValue(prevChildUser);
                            prevChildUser = tempUser;
                            tempTime = child.child("timestamp").getValue().toString();
                            DbHsRef.child("score" + i).child("timestamp").setValue(prevChildTime);
                            prevChildTime = tempTime;
                            tempScore = (Integer) child.child("score").getValue();
                            DbHsRef.child("score" + i).child("score").setValue(prevChildScore);
                            prevChildScore = tempScore;

                        }
                        i++;
                    }
                } else {
                    DbHsRef.child("HighScores").child("score" + 0)
                                .child("user").setValue(mAuth.getCurrentUser().getUid());
                    DbHsRef.child("HighScores").child("score" + 0)
                            .child("timestamp").setValue(score.getTimestamp());
                    DbHsRef.child("HighScores").child("score" + 0)
                            .child("score").setValue(score.getScore());
                    }

            }@Override
            public void onCancelled(DatabaseError firebaseError) {}
        });



    }




}
