package se.team4.mamn01_grupp4.ui.quiz;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;

import se.team4.mamn01_grupp4.MainActivity;
import se.team4.mamn01_grupp4.Poi;
import se.team4.mamn01_grupp4.PoiDb;
import se.team4.mamn01_grupp4.R;
import se.team4.mamn01_grupp4.env.Logger;
import se.team4.mamn01_grupp4.ui.scan.CameraFragment;

public class QuizWindow extends AppCompatActivity {

    MediaPlayer player;
    private ImageView playButton;
    private TextView timeValue;
    String question;
    boolean ans;
    private View popupView;
    Logger LOGGER;
    Fragment fragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        popupView = inflater.inflate(R.layout.window_quiz, container, false);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        playButton = popupView.findViewById(R.id.playButton);
        timeValue = popupView.findViewById(R.id.TimeValue);

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);



        //Set the location of the window on the screen
        //popupWindow.showAtLocation(container, Gravity.CENTER, 0, 0);


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

        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });

        return popupView;
    }

    public QuizWindow(Fragment fragment, Poi poi){
        this.question = poi.question;
        this.ans = poi.ans;
        LOGGER = new Logger();
        this.fragment = fragment;

        player = new MediaPlayer();
        try {
            player.setDataSource(poi.sound.getFileDescriptor(), poi.sound.getStartOffset(), poi.sound.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}