package se.team4.mamn01_grupp4.ui.quiz;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import se.team4.mamn01_grupp4.Poi;
import se.team4.mamn01_grupp4.PoiDb;
import se.team4.mamn01_grupp4.R;
import se.team4.mamn01_grupp4.env.Logger;

public class QuizActivity extends AppCompatActivity implements SensorEventListener {

    MediaPlayer player;
    private ImageView playButton;
    private TextView bonusText;
    private TextView bonusValue;
    private Vibrator vibrator;
    private Animation shake;
    private ImageView phoneIcon;
    private ImageView greyWave;
    private ClipDrawable blueWaveClip;
    private boolean shouldAnimate = false;
    Handler myHandler;
    private Poi poi;
    private int bonusScore;
    FrameLayout container;
    TextView questionView;
    TextView locationView;
    Logger LOGGER;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    CountDownTimer scoreCounter = new CountDownTimer(20000, 2000) {

        public void onTick(long millisUntilFinished) {
            bonusValue.setText(String.valueOf(millisUntilFinished / 2000));
            bonusScore = (int) (millisUntilFinished / 2000);
        }

        public void onFinish() {
            bonusScore = 0;
            bonusText.setText("");
            bonusValue.setText("");
        }

    };
    private CountDownTimer musicTimer = new CountDownTimer(60000, 100) {

        public void onTick(long millisUntilFinished) {
            blueWaveClip.setLevel((int)((60000 - millisUntilFinished)/6));
        }

        public void onFinish() {
            blueWaveClip.setLevel(0);
            evaluateResult(!poi.ans, 0);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_quiz);

        //Creates the sensor manager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Creates sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        LOGGER = new Logger();


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        String arg = getIntent().getStringExtra("poi");
        if(arg!=null){
            LOGGER.e("Found %s in passed args to quizActivity", arg);
            poi = PoiDb.getDb().get(arg);

            player = new MediaPlayer();
            try {
                player.setDataSource(poi.sound.getFileDescriptor(), poi.sound.getStartOffset(), poi.sound.getLength());
                player.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        myHandler = new Handler();

        container = findViewById(R.id.quiz_window);
        playButton = findViewById(R.id.playButton);
        bonusText = findViewById(R.id.BonusText);
        bonusValue = findViewById(R.id.BonusValue);
        locationView = findViewById(R.id.locationName);
        questionView = findViewById(R.id.question);
        greyWave = findViewById(R.id.grey_wave);
        ImageView blueWave = findViewById(R.id.blue_wave);
        blueWaveClip = (ClipDrawable) blueWave.getDrawable();

        blueWaveClip.setLevel(0);
        locationView.setText(poi.name);
        questionView.setText(poi.question);

        playButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null){
                    player.start();
                    scoreCounter.start();
                    musicTimer.start();

                    //Create sensor listener
                    mSensorManager.registerListener(QuizActivity.this, mAccelerometer,mSensorManager.SENSOR_DELAY_NORMAL);

                    shouldAnimate = true;
                    phoneIcon = findViewById(R.id.phoneIcon);
                    shake = AnimationUtils.loadAnimation(QuizActivity.this, R.anim.shake);
                    shake.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            if(shouldAnimate) {
                                shake = AnimationUtils.loadAnimation(QuizActivity.this, R.anim.shake);
                                shake.setAnimationListener(this);
                                phoneIcon.startAnimation(shake);
                            }
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    phoneIcon.startAnimation(shake);

                } else{
                    LOGGER.e("Sound file not found");
                }
                playButton.setVisibility(View.INVISIBLE);
                greyWave.setVisibility(View.VISIBLE);

                playButton.setOnClickListener(null);
            }
        });

    }

    private void evaluateResult(boolean ans, int bonus){
        shouldAnimate = false;
        player.stop();
        musicTimer.cancel();
        int finalScore = 0;
        if(poi.ans == ans){
            player = MediaPlayer.create(this, R.raw.win);
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,100,100,100}, -1));
            container.setBackgroundResource(R.drawable.gradient_green_background);
            questionView.setText("You got " + (bonus+5) + "points!");
            finalScore = bonus+5;
        } else {
            player = MediaPlayer.create(this, R.raw.loose);
            vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0,400,200,400}, -1));
            questionView.setText("You got 0 points!");
            container.setBackgroundResource(R.drawable.gradient_red_background);
        }

        Intent intent = getIntent();
        intent.putExtra("result", finalScore);
        setResult(RESULT_OK, intent);

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                player.start();
            }
        }, 1000);

        myHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }

    @Override
    protected void onPause() {
        shouldAnimate = false;
        super.onPause();

    }

    @Override
    protected void onStop(){
        super.onStop();
        player.stop();
    }

    @Override
    public void onBackPressed () {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.values[0] > 5) {
            evaluateResult(false, bonusScore);
            mSensorManager.unregisterListener(this);
        } else if (event.values[0] < -5) {
            evaluateResult(true, bonusScore);
            mSensorManager.unregisterListener(this);
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
