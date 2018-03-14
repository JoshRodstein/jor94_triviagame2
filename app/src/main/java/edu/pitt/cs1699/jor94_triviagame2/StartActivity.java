/*
* By: Joshua Rodstein
* Assignment1 - CS1699
* PItt: jor94@pitt.edu
* ID: 4021607
*
* */

package edu.pitt.cs1699.jor94_triviagame2;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;


public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Switch music_switch = (Switch)  findViewById(R.id.music_switch);
        final MediaPlayer mp3 = MediaPlayer.create(this, edu.pitt.cs1699.jor94_triviagame2.R.raw.);
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
/*
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
*/


}
