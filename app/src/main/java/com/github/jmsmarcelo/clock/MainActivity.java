package com.github.jmsmarcelo.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    FrameLayout analogClock;
    LinearLayout digitalClock;
    ImageView ivHour, ivMinute, ivSecond,
            ivDigitalHH, ivDigitalH, ivDigitalMM, ivDigitalM, ivDigitalSS, ivDigitalS;
    TextView tvDigitalDot;
    Button btnSetClock;
    LocalDateTime dateTime;
    int hh, mm, ss, oldSs;
    public static final String MY_PREFS_CONFIGS = "com.github.jmsmarcelo.clock.Configs";
    private SharedPreferences prefs;
    private String clockDefault;
    int update;
    boolean dotVisible = true;
    List<Integer> digitalNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        digitalNumbers = new ArrayList<>();
        digitalNumbers.add(R.drawable.digital_zero);
        digitalNumbers.add(R.drawable.digital_one);
        digitalNumbers.add(R.drawable.digital_two);
        digitalNumbers.add(R.drawable.digital_three);
        digitalNumbers.add(R.drawable.digital_four);
        digitalNumbers.add(R.drawable.digital_five);
        digitalNumbers.add(R.drawable.digital_six);
        digitalNumbers.add(R.drawable.digital_seven);
        digitalNumbers.add(R.drawable.digital_eight);
        digitalNumbers.add(R.drawable.digital_nine);

        prefs = getSharedPreferences(MY_PREFS_CONFIGS, MODE_PRIVATE);
        clockDefault = prefs.getString("clock", "analog");

        analogClock = findViewById(R.id.analogClock);
        digitalClock = findViewById(R.id.digitalClock);

        ivHour = findViewById(R.id.ivHour);
        ivMinute = findViewById(R.id.ivMinute);
        ivSecond = findViewById(R.id.ivSecond);

        ivDigitalHH = findViewById(R.id.ivDigitalHH);
        ivDigitalH = findViewById(R.id.ivDigitalH);
        ivDigitalMM = findViewById(R.id.ivDigitalMM);
        ivDigitalM = findViewById(R.id.ivDigitalM);
        ivDigitalSS = findViewById(R.id.ivDigitalSS);
        ivDigitalS = findViewById(R.id.ivDigitalS);
        tvDigitalDot = findViewById(R.id.tvDigitalDot);

        btnSetClock = findViewById(R.id.btnSetClock);
        if(clockDefault.equals("analog"))
            btnSetClock.setText("Get Digital Clock");
        else
            btnSetClock.setText("Get Analog Clock");

        if(clockDefault.equals("analog")) {
            analogClock.setVisibility(View.VISIBLE);
            digitalClock.setVisibility(View.GONE);
        } else {
            analogClock.setVisibility(View.GONE);
            digitalClock.setVisibility(View.VISIBLE);
        }

        btnSetClock.setOnClickListener(v -> {
            if(clockDefault.equals("analog")) {
                btnSetClock.setText("Get Analog Clock");
                clockDefault = "digital";

                analogClock.setVisibility(View.GONE);
                digitalClock.setVisibility(View.VISIBLE);

                update = 500;
            } else {
                btnSetClock.setText("Get Digital Clock");
                clockDefault = "analog";

                analogClock.setVisibility(View.VISIBLE);
                digitalClock.setVisibility(View.GONE);

                update = 1000;
            }
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putString("clock", clockDefault);
            prefsEditor.commit();
        });

        if(clockDefault.equals("analog"))
            update = 1000;
        else
            update = 500;

        startClockUpdate();
    }
    private void startClockUpdate() {
        handler.postDelayed(updateClickRunnable, 0);
    }
    private Runnable updateClickRunnable = new Runnable() {
        @Override
        public void run() {
            long realTime = SystemClock.elapsedRealtime();
            dateTime = LocalDateTime.now();
            hh = dateTime.getHour();
            mm = dateTime.getMinute();
            ss = dateTime.getSecond();

            dotVisible = ss == oldSs;
            oldSs = ss;

            if(clockDefault.equals("analog")) {
                ivHour.setRotation((hh * 30) + (mm * 0.5F) + (ss * 0.0083333333333333F));
                ivMinute.setRotation((mm * 6) + (ss *  0.1F));
                ivSecond.setRotation(ss * 6);
            } else {
                setDigital(ivDigitalHH, ivDigitalH, hh);
                setDigital(ivDigitalMM, ivDigitalM, mm);
                setDigital(ivDigitalSS, ivDigitalS, ss);
                if(dotVisible)
                    tvDigitalDot.setVisibility(View.INVISIBLE);
                else
                    tvDigitalDot.setVisibility(View.VISIBLE);
            }

            realTime = SystemClock.elapsedRealtime() - realTime;
            handler.postDelayed(this, update - realTime);
        }
    };
    private void setDigital(ImageView ivFirst, ImageView ivSecond, int time) {
        ivFirst.setImageResource(digitalNumbers.get(time / 10));
        ivSecond.setImageResource(digitalNumbers.get(time % 10));
    }
    @Override
    protected void onDestroy() {
        handler.removeCallbacks(updateClickRunnable);
        super.onDestroy();
    }
}