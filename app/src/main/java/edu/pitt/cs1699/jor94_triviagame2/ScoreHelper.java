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




}
