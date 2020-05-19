package se.team4.mamn01_grupp4.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import se.team4.mamn01_grupp4.MainActivity;
import se.team4.mamn01_grupp4.R;

public class ResultActivity extends AppCompatActivity {

    private TextView scoreText;
    private TextView bonusText;
    private TextView maxScoreText;
    private TextView totalScoreText;
    private Button restartButton;
    private Button exitButton;
    private Vibrator vibrator;
    private int score;
    private int bonusScore;
    private int maxScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        scoreText = findViewById(R.id.score_value);
        bonusText = findViewById(R.id.bonus_value);
        maxScoreText = findViewById(R.id.max_value);
        totalScoreText = findViewById(R.id.total_score_value);
        restartButton = findViewById(R.id.button_restart);
        exitButton = findViewById(R.id.button_exit);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        score = getIntent().getIntExtra("score", 0);
        maxScore = getIntent().getIntExtra("answered", 0) * 5;
        bonusScore = getIntent().getIntExtra("bonus", 0);

        if(score == maxScore){
            findViewById(R.id.result_layout).setBackgroundResource(R.drawable.gradient_green_background);
        }

        scoreText.setText(String.valueOf(score));
        bonusText.setText(String.valueOf(bonusScore));
        maxScoreText.setText(String.valueOf(maxScore));
        totalScoreText.setText(String.valueOf(score + bonusScore));

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                Intent intent = new Intent(ResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Are you sure you want to exit the application?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ResultActivity.super.onBackPressed();
                    }
                });
        alertDialog.show();
    }
}
