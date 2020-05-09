package se.team4.mamn01_grupp4.ui.quiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
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

public class QuizActivity extends AppCompatActivity {

    MediaPlayer player;
    private ImageView playButton;
    private TextView bonusText;
    private TextView bonusValue;
    private TextView timeValue;
    private Poi poi;
    private int bonusScore;
    FrameLayout container;
    TextView questionView;
    TextView locationView;
    Logger LOGGER;
    private int score = 1;

    CountDownTimer scoreCounter = new CountDownTimer(20000, 2000) {

        public void onTick(long millisUntilFinished) {
            bonusValue.setText(String.valueOf(millisUntilFinished / 2000));
            bonusScore = (int) (millisUntilFinished / 1000);
        }

        public void onFinish() {
            bonusText.setText("");
            bonusValue.setText("");
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        LOGGER = new Logger();

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


        container = findViewById(R.id.quiz_window);
        playButton = findViewById(R.id.playButton);
        bonusText = findViewById(R.id.BonusText);
        bonusValue = findViewById(R.id.BonusValue);
        timeValue = findViewById(R.id.TimeValue);
        locationView = findViewById(R.id.locationName);
        questionView = findViewById(R.id.question);

        locationView.setText(poi.name);
        questionView.setText(poi.question);


        playButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null){
                    player.start();
                    playButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    scoreCounter.start();
                    new CountDownTimer(60000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            timeValue.setText(String.valueOf(millisUntilFinished / 1000));
                        }

                        public void onFinish() {
                            questionView.setText("You got 0 points!");
                            container.setBackgroundResource(R.drawable.gradient_red_background);
                            player.stop();
                        }

                    }.start();
                } else{
                    LOGGER.e("Sound file not found");
                }
            }
        });

        Intent intent = getIntent();
        intent.putExtra("result", score);
        setResult(RESULT_OK, intent);

    }

    private void evaluateResult(boolean ans, int score){
        if(poi.ans == ans){
            container.setBackgroundResource(R.drawable.gradient_green_background);
            questionView.setText("You got " + (score+5) + "points!");
        } else {
            questionView.setText("You got 0 points!");
            container.setBackgroundResource(R.drawable.gradient_red_background);
        }
        player.stop();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop(){
        super.onStop();
        player.stop();
    }

}
