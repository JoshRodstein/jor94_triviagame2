package edu.pitt.cs1699.jor94_triviagame2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
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
import com.google.firebase.database.ValueEventListener;

/**
 * Created by josh on 3/21/18.
 */

public class ChildEventListener extends Service {
    DatabaseHelper db;
    boolean notifyReadyTerms, notifyReadyScores;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        notifyReadyTerms = false;
        notifyReadyScores = false;
        initChannels(this);
        db = new DatabaseHelper(this);
        Log.w("INIT_LIST:", "IN THE SERVICE");
        initListeners();
        return START_STICKY;
    }

    public void onCreate() {


    }

    public void initChannels(Context context) {
        Log.w("INIT-CHAN", "INitiated CHannls");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            {
                CharSequence name = getString(R.string.channel_name);
                NotificationChannel channel = new NotificationChannel("channel_1", name,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("Top Scores Changed");
                // Register the channel with the system
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(context.NOTIFICATION_SERVICE);
                notificationManager.createNotificationChannel(channel);

                name = "channel_2";
                NotificationChannel channel2 = new NotificationChannel("channel_2", name,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("A user has added a term!");
                notificationManager.createNotificationChannel(channel2);
            }


        }
    }

    public void initListeners(){

        Log.w("INIT_LIST", "ininit");
        FirebaseAuth mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference DbHsRef = fbdb.getReference("HighScores");
        DatabaseReference DbTermsRef = fbdb.getReference("TermsAndDefs");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "channel_1")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Score Alert!")
                .setContentText("Top 10 has changed!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationCompat.Builder mBuilder2 = new NotificationCompat.Builder(getApplicationContext(), "channel_1")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Alert!")
                .setContentText("A user has added a term!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


        DbHsRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (notifyReadyScores) {
                    notificationManager.notify(1, mBuilder.build());
                }
            }


            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (notifyReadyScores) {
                    notificationManager.notify(1, mBuilder.build());
                }
            }


            public void onChildRemoved(DataSnapshot dataSnapshot) {}


            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                if (notifyReadyScores) {
                    notificationManager.notify(1, mBuilder.build());
                }
            }


            public void onCancelled(DatabaseError databaseError) {

            }

        });

        DbTermsRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {

            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (notifyReadyTerms) {
                    Log.w("TermsUpdate", "IN THE SERVICE");
                    new Thread(new Runnable() {
                        public void run() {
                            db.onUpgrade(db.getDB());
                            db.close();

                        }
                    }).start();
                    db.close();
                    BroadcastReceiver bRec = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            notificationManager.notify(2, mBuilder2.build());
                        }
                    };
                    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                    filter.addAction(Intent.ACTION_DEFAULT);
                    registerReceiver(bRec, filter);
                }
            }
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        DbHsRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(notifyReadyScores == false){
                    notifyReadyScores = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DbTermsRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(notifyReadyTerms == false){
                    notifyReadyTerms = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }



}
