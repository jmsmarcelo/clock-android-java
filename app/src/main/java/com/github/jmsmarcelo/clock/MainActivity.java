package com.github.jmsmarcelo.clock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.LocalDateTime;

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
    private SharedPreferences.Editor prefsEditor;
    private String clockDefault;
    int update = 999;
    boolean dotVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(MY_PREFS_CONFIGS, MODE_PRIVATE);
        clockDefault = prefs.getString("clock", "analog");
        prefsEditor = prefs.edit();

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
        btnSetClock.setText("Get " + clockDefault + " Clock");

        if(clockDefault.equals("analog")) {
            analogClock.setVisibility(View.VISIBLE);
            digitalClock.setVisibility(View.GONE);
        } else {
            analogClock.setVisibility(View.GONE);
            digitalClock.setVisibility(View.VISIBLE);
        }

        btnSetClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clockDefault.equals("analog")) {
                    btnSetClock.setText("Get Digital Clock");
                    clockDefault = "digital";

                    analogClock.setVisibility(View.GONE);
                    digitalClock.setVisibility(View.VISIBLE);

                    update = 499;

                    prefsEditor.putString("clock", clockDefault);
                    prefsEditor.commit();
                } else {
                    btnSetClock.setText("Get Analog Clock");
                    clockDefault = "analog";

                    analogClock.setVisibility(View.VISIBLE);
                    digitalClock.setVisibility(View.GONE);

                    update = 999;

                    prefsEditor.putString("clock", clockDefault);
                    prefsEditor.commit();
                }
            }
        });

        startClockUpdate();
    }
    private void startClockUpdate() {
        handler.postDelayed(updateClickRunnable, 0);
    }
    private Runnable updateClickRunnable = new Runnable() {
        @Override
        public void run() {
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

                update = 999;
            } else {
                setDigital(ivDigitalHH, ivDigitalH, hh);
                setDigital(ivDigitalMM, ivDigitalM, mm);
                setDigital(ivDigitalSS, ivDigitalS, ss);
                if(dotVisible)
                    tvDigitalDot.setVisibility(View.INVISIBLE);
                else
                    tvDigitalDot.setVisibility(View.VISIBLE);

                update = 499;
            }

            handler.postDelayed(this, update);
        }
    };
    private void setDigital(ImageView ivFirst, ImageView ivSecond, int time) {
        if(time < 10) {
            setNumber(0, ivFirst);
            setNumber(time, ivSecond);
        } else if(time < 20) {
            setNumber(1, ivFirst);
            setNumber(time - 10,ivSecond);
        } else if(time < 30) {
            setNumber(2, ivFirst);
            setNumber(time - 20, ivSecond);
        } else if(time < 40) {
            setNumber(3, ivFirst);
            setNumber(time - 30, ivSecond);
        } else if(time < 50) {
            setNumber(4, ivFirst);
            setNumber(time - 40, ivSecond);
        } else {
            setNumber(5, ivFirst);
            setNumber(time - 50, ivSecond);
        }
    }
    private void setNumber(int time, ImageView ivNumber) {
        switch(time) {
            case 0:
                ivNumber.setImageResource(R.drawable.digital_zero);
                break;
            case 1:
                ivNumber.setImageResource(R.drawable.digital_one);
                break;
            case 2:
                ivNumber.setImageResource(R.drawable.digital_two);
                break;
            case 3:
                ivNumber.setImageResource(R.drawable.digital_three);
                break;
            case 4:
                ivNumber.setImageResource(R.drawable.digital_four);
                break;
            case 5:
                ivNumber.setImageResource(R.drawable.digital_five);
                break;
            case 6:
                ivNumber.setImageResource(R.drawable.digital_six);
                break;
            case 7:
                ivNumber.setImageResource(R.drawable.digital_seven);
                break;
            case 8:
                ivNumber.setImageResource(R.drawable.digital_eight);
                break;
            case 9:
                ivNumber.setImageResource(R.drawable.digital_nine);
                break;
        }
    }
    @Override
    protected void onDestroy() {
        handler.removeCallbacks(updateClickRunnable);
        super.onDestroy();
    }
}