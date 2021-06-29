package com.abhishek.playgaana;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {

    ArrayList<File> songs;
    TextView textView;
    ImageView imageView, playPause, next, previous;
    SeekBar seekBar;
    String textName;
    int position;
    MediaPlayer mediaPlayer;
    Thread updateSeekBar;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeekBar.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        playPause = findViewById(R.id.playPause);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textName = intent.getStringExtra("currentSong");
        textView.setText(textName);
        textView.setSelected(true);
        position = intent.getIntExtra("position", 0);

        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeekBar = new Thread() {

            @Override
            public void run() {
                int currentPosition = 0;
                try {
                    while (currentPosition < mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateSeekBar.start();


        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               mediaPlayer.stop();
               mediaPlayer.release();
               if(position!=0){
                   position=position-1;
               }
               else{
                   position=songs.size()-1;
               }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                playPause.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textName=songs.get(position).getName().toString();
                textView.setText(textName);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position!=songs.size()-1){
                    position=position+1;
                }
                else{
                    position=0;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                playPause.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textName=songs.get(position).getName().toString();
                textView.setText(textName);
            }
        });
        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    playPause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{

                    playPause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
    }
}