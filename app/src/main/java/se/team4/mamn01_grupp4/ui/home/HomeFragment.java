package se.team4.mamn01_grupp4.ui.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.Objects;

import se.team4.mamn01_grupp4.MainActivity;
import se.team4.mamn01_grupp4.R;
import se.team4.mamn01_grupp4.env.Logger;
import se.team4.mamn01_grupp4.ui.map.MapFragment;
import se.team4.mamn01_grupp4.ui.quiz.QuizActivity;


public class HomeFragment extends Fragment {

    private SensorManager sm;
    private float acelVal; //Current acceleration value and gravity
    private float acelLast; //Last acc value and gravity
    private float shake; // acc value differ from gravity
    private TextView homeHeader;
    private TextView instructions;
    private TextView shakeText;
    private Logger LOGGER;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        LOGGER = new Logger();

        homeHeader = root.findViewById(R.id.home_header);
        instructions = root.findViewById(R.id.instruction_text);
        shakeText = root.findViewById(R.id.shake_text);

        if(((MainActivity)getActivity()).getAnsweredQuestions() > 0){
            instructions.setVisibility(View.INVISIBLE);
            homeHeader.setVisibility(View.INVISIBLE);
            shakeText.setVisibility(View.VISIBLE);
            sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            sm.registerListener(sensorListner, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

            acelVal = SensorManager.GRAVITY_EARTH;
            acelLast = SensorManager.GRAVITY_EARTH;
            shake = 0.00f;
        }

        return root;

    }

    private final SensorEventListener sensorListner = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        acelLast = acelVal;
        acelVal = (float) Math.sqrt((double)  x*x + y*y + z*z );
        float delta = acelVal - acelLast;
        shake = shake * 0.9f + delta;



            if(shake > 12){
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Warning");
                alertDialog.setMessage("Are you sure you want to grade the quiz? \nYou will have to start from the beginning afterwards.");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getActivity(), ResultActivity.class);
                                Bundle b = new Bundle();
                                b.putInt("score", ((MainActivity)getActivity()).getScore());
                                b.putInt("bonus", ((MainActivity)getActivity()).getBonusScore());
                                b.putInt("answered", ((MainActivity)getActivity()).getAnsweredQuestions());
                                intent.putExtras(b);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        });
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        sm.registerListener(sensorListner, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
                    }
                });
                alertDialog.show();
                sm.unregisterListener(this);

            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onPause(){
        super.onPause();
        if(sm != null) {
            sm.unregisterListener(sensorListner);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        if(sm != null) {
            sm.unregisterListener(sensorListner);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(sm != null) {
            sm.registerListener(sensorListner, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

}
