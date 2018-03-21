package edu.pitt.cs1699.jor94_triviagame2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by josh on 3/21/18.
 */

public class ChildEventListener extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        initChannels(this);
        Log.w("INIT_LIST:", "IN THE SERVICE");
        initListeners();
        return START_STICKY;
    }

    public void onCreate() {
        Log.w("INIT_LIST:", "IN THE SERVICE");
    }

    public void initChannels(Context context) {
        Log.w("INIT-CHAN", "INitiated CHannls");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = getString(R.string.channel_name);
            NotificationChannel channel = new NotificationChannel("channel_1", name,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Top Scores Changed");
            // Register the channel with the system
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void initListeners(){
        Log.w("INIT_LIST", "ininit");
        FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference DbHsRef = fbdb.getReference("HighScores");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "channel_1")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Score Alert!")
                .setContentText("Top 10 has changed!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


        DbHsRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                notificationManager.notify(1, mBuilder.build());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                notificationManager.notify(1, mBuilder.build());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                notificationManager.notify(1, mBuilder.build());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }



}
