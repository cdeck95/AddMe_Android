package com.tc2.linkup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {
    TextView textView;
    private Timer timer;
    private ProgressBar progressBar;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        textView = findViewById(R.id.textView);
        textView.setText("");


        final long period = 60;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //this repeats every 100 ms
                if (i < 60) {
                    final float currentI = i * 1.67f;
                    final Integer currentIntI = Math.round(currentI);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(String.valueOf(currentIntI + "%"));
                        }
                    });
                    progressBar.setProgress(Math.round(currentIntI));
                    i++;
                } else {
                    //closing the timer
                    timer.cancel();
                    Intent intent = new Intent(SplashScreen.this, AuthenticatorActivity.class);
                    startActivity(intent);
                    // close this activity
                    // finish();
                }
            }
        }, 0, period);
    }

}

