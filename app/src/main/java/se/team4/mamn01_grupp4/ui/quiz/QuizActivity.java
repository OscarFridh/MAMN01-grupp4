package se.team4.mamn01_grupp4.ui.quiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    private TextView timeValue;
    private Poi poi;
    TextView questionView;
    TextView locationView;
    Logger LOGGER;

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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        playButton = findViewById(R.id.playButton);
        timeValue = findViewById(R.id.TimeValue);
        locationView = findViewById(R.id.locationName);
        questionView = findViewById(R.id.question);

        locationView.setText(poi.name);
        questionView.setText(poi.question);


        playButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(player != null){
                    if(player.isPlaying()){
                        player.stop();
                        playButton.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                    } else{
                        player.start();
                        playButton.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    }
                } else{
                    LOGGER.e("Sound file not found");
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

}
