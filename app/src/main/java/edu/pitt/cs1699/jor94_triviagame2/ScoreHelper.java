package edu.pitt.cs1699.jor94_triviagame2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.provider.Settings.Global.getString;

/**
 * Created by josh on 3/19/18.
 */



public class ScoreHelper {
    private FirebaseAuth mAuth;
    FirebaseDatabase fbdb;

    public ScoreHelper (){

    }


    public static void updateHighList(Scores sc){
        FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference DbHsRef = fbdb.getReference("HighScores");

        DbHsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()) {
                       if (dataSnapshot.hasChildren()) {
                            ArrayList<Scores> scList = new ArrayList<>();
                            String t, s, u;
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                t = String.valueOf(child.child("timestamp").getValue());
                                s = String.valueOf(child.child("score").getValue());
                                u = String.valueOf(child.child("user").getValue());
                                scList.add(new Scores(t, s, u));
                            }
                            scList.add(new Scores(sc.getTimestamp(),
                                    sc.getScore(),
                                    mAuth.getCurrentUser().getUid()));
                            Log.w("GRAB HIGH SCORES", "listSIze = : " + scList.size());
                            Collections.sort(scList, Comparator.comparing(Scores::getIntScore).reversed());
                            Log.w("GRAB HIGH SCORES", "High Score : " + scList.get(0).getScore());
                            //** child name to FINAL VAR
                            for (int i = 0; i < scList.size(); i++) {
                                DbHsRef.child("score" + i).child("timestamp").setValue(scList.get(i).getTimestamp());
                                DbHsRef.child("score" + i).child("score").setValue(scList.get(i).getScore());
                                DbHsRef.child("score" + i).child("user").setValue(scList.get(i).getUid());
                                if(i == 9){break;}
                            }
                        }
                    } else {
                        DbHsRef.child("score" + 0).child("timestamp").setValue(sc.getTimestamp());
                        DbHsRef.child("score" + 0).child("score").setValue(sc.getScore());
                        DbHsRef.child("score" + 0).child("user").setValue(mAuth.getCurrentUser().getUid());
                    }



            }@Override
            public void onCancelled(DatabaseError firebaseError) {}
        });

    }





}








