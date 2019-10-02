package com.dal.stopwatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // properties
    private Handler timerHandler;
    private ArrayAdapter<String> itemAdapter;
    private TextView txtTimer;
    private Button btnStartPause, btnLapReset;

    // used to keep track of time
    private long millisecondTime, startTime, pauseTime, updateTime = 0;

    // used to display time
    private int seconds, minutes, milliseconds;

    // used to handle the state of te stopwatch
    private boolean stopWatchStarted, stopWatchPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // only used in one place, so shouldn't be a global variable
        ListView lvLapse;

        // timer handler is bound to a thread
        // used to schedule our Runnable to be executed after particular actions
        timerHandler = new Handler();

        // sets the layout for each item of the list view
        itemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        txtTimer = findViewById(R.id.txt_timer);
        btnStartPause = findViewById(R.id.btn_start_pause);
        btnLapReset = findViewById(R.id.btn_lap_reset);
        lvLapse = findViewById(R.id.lv_laps);
        lvLapse.setAdapter(itemAdapter);
        btnStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!stopWatchStarted || stopWatchPaused){
                    stopWatchStarted = true;
                    stopWatchPaused = false;

                    startTime = SystemClock.uptimeMillis();

                    timerHandler.postDelayed(timeRunnabel,0);

                    btnStartPause.setText(R.string.btnPause);
                    btnLapReset.setText(R.string.btnLap);
                }
                else{
                    pauseTime += millisecondTime;
                    stopWatchPaused = true;


                    timerHandler.removeCallbacks(timeRunnabel);
                    btnStartPause.setText(R.string.btnStart);
                    btnLapReset.setText(R.string.btnReset);
                }
            }
        });

        btnLapReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stopWatchStarted && ! stopWatchPaused){
                    String laptime = minutes + ":"
                            + String.format("%02d", seconds) + ":"
                            + String.format("%03d", milliseconds);

                    itemAdapter.add(laptime);
                }
                else if(stopWatchStarted){
                    stopWatchStarted = false;
                    stopWatchPaused = false;

                    timerHandler.removeCallbacks(timeRunnabel);

                    milliseconds = 0;
                    seconds = 0;
                    minutes = 0;
                    millisecondTime = 0;
                    startTime = 0;
                    pauseTime = 0;
                    updateTime = 0;

                    txtTimer.setText(R.string.lbl_timer);
                    btnLapReset.setText(R.string.btnLap);

                    itemAdapter.clear();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Timer hasn't started yet.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
        This instance is executed by a handler which is associated with a thread, this means a
        Runnable interface Overriding the run() method should be implemented by the class.
     */

    public Runnable timeRunnabel = new Runnable() {
        @Override
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;

            // values used to keep track of where the stopwatch time left off
            updateTime          = pauseTime - millisecondTime;
            milliseconds        = (int) (updateTime % 1000);
            seconds             = (int) (updateTime / 1000);

            // convert values to display
            minutes             = seconds / 60;
            seconds             = seconds % 60;
            String updateTime   = minutes + ":"+ String.format( "%02d",seconds) +
                                                 String.format("%03d",milliseconds);
            txtTimer.setText(updateTime);

            // enqueues the Runnable to be called by the message queue after the specified amount of time elapses.
            timerHandler.postDelayed(this,0);
        }
    };
}
