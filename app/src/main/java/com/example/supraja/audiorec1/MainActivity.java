package com.example.supraja.audiorec1;

import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button play, stop;
    private ImageView mic;
    private MediaRecorder myaudiorecorder;
    private String outputFile;
    private int flag = 0;
    private TextView timerValue;
    private long startTime = 0L;

    private Handler customHandler = new Handler();

    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timerValue = (TextView) findViewById(R.id.timerValue);

        play = (Button) findViewById(R.id.play);
        //stop = (Button)findViewById(R.id.stop);
        mic = (ImageView) findViewById(R.id.mic);
        //stop.setEnabled(false);
        play.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis()+ "recording.3gp";

        myaudiorecorder = new MediaRecorder();
        myaudiorecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myaudiorecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myaudiorecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        myaudiorecorder.setOutputFile(outputFile);

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flag == 0) {
                    try {
                        myaudiorecorder.prepare();
                        myaudiorecorder.start();
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 0);


                    } catch (IllegalStateException ise) {
                        //catch an exception
                    } catch (IOException ioe) {
                        //catch an exception
                    }
                    //mic.setEnabled(false);
                    play.setEnabled(false);
                    mic.setEnabled(true);
                    // stop.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
                    flag = 1;
                } else {
                    try {

                        timeSwapBuff=0;
                        timeSwapBuff += timeInMilliseconds;
                        customHandler.removeCallbacks(updateTimerThread);

                        myaudiorecorder.stop();
                        myaudiorecorder.release();
                        myaudiorecorder = null;

                        // stop.setEnabled(false);
                        play.setEnabled(true);
                        mic.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Recording stopped", Toast.LENGTH_LONG).show();
                        flag = 0;
                    } catch (IllegalStateException ise) {
                        //catch an exception
                    }
                }
            }

        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mymediaplayer = new MediaPlayer();
                try {
                    mymediaplayer.setDataSource(outputFile);
                    mymediaplayer.prepare();
                    mymediaplayer.start();
                    //stop.setEnabled(false);
                    mic.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Playing recorded file", Toast.LENGTH_LONG).show();

                } catch (IllegalStateException ise) {
                    //catch an exception
                } catch (IOException ioe) {
                    //catch an exception
                }

            }
        });
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText("" + mins + ":"

                    + String.format("%02d", secs) + ":"

                    + String.format("%03d", milliseconds));

            customHandler.postDelayed(this, 0);

        }


    };
}
