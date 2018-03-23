package edu.pitt.cs1699.jor94_triviagame2;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import android.view.*;
import android.media.MediaPlayer;
import android.graphics.BitmapFactory;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;


public class StartActivity extends AppCompatActivity {
    private FirebaseAuth mAuth = com.google.firebase.auth
            .FirebaseAuth.getInstance();
    ImageView profileImage;

    private final int top10Id = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        loadProfileImage();

        Log.w("START - ON_CREATE", "");

        final Switch music_switch = (Switch) findViewById(R.id.music_switch);
        final MediaPlayer mp3 = MediaPlayer.create(this, R.raw.jeopardy);
        music_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mp3.isPlaying() == true) {
                    mp3.pause();
                } else {
                    mp3.start();
                    mp3.setLooping(true);
                }
            }
        });


    }

    private void loadProfileImage() {
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbref = fbdb.getReference().child("users")
                .child(mAuth.getCurrentUser().getUid()).child("photo");

        dbref.addListenerForSingleValueEvent( new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String photoURL = dataSnapshot.getValue().toString();
                Log.w("PHOTO URL", photoURL);
                profileImage = (ImageView) findViewById(R.id.ImageView);

                try {
                    URL url = new URL(photoURL);
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Log.w("PHOTO URL", String.valueOf(bmp.getByteCount()));
                    profileImage.setImageBitmap(bmp);
                }catch (Exception e){
                    Log.w("PHOTO URL", e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    protected  void onUpdate(){

    }

    protected void onStart(){
        super.onStart();


    }

    public void playTrivia(View view) {
        Intent intent = new Intent(this, TriviaActivity.class);
        startActivity(intent);
    }

    public void add_word_onClick(View view){
        Intent intent = new Intent(this, addtermActivity.class);
        startActivity(intent);
    }

    public void score_history(View view){
        Intent intent = new Intent(this, ScoreHistoryActivity.class);
        startActivity(intent);
    }


}



