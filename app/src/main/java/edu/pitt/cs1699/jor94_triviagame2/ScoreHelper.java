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

    private void sortHigh(){
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

                updateHighList(scoreList);

            }@Override
            public void onCancelled(DatabaseError firebaseError) {}
        });


    }

    private void updateHighList(ArrayList<Scores> sc){
        DatabaseReference DbHsRef = fbdb.getReference("HighScores");

        DbHsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot child:dataSnapshot.getChildren()) {

                    }
                } else {
                    for(int i = 0; i < 10 && i < sc.size(); i++) {
                        DbHsRef.child("HighScores").child(sc.get(i).getTimestamp())
                                .child(mAuth.getCurrentUser().getUid())
                                .setValue(sc.get(i).getScore());
                    }
                }


            }@Override
            public void onCancelled(DatabaseError firebaseError) {}
        });



    }




}
