package se.team4.mamn01_grupp4.ui.home;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import se.team4.mamn01_grupp4.R;
import se.team4.mamn01_grupp4.ui.map.MapFragment;


public class HomeFragment extends Fragment {

    private SensorManager sm;
    private float acelVal; //Current acceleration value and gravity
    private float acelLast; //Last acc value and gravity
    private float shake; // acc value differ from gravity

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);

        sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sm.registerListener(sensorListner, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        acelVal = SensorManager.GRAVITY_EARTH;
        acelLast = SensorManager.GRAVITY_EARTH;
        shake = 0.00f;

        return root;

    }

    private final SensorEventListener sensorListner = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);

        acelLast = acelVal;
        acelVal = (float) Math.sqrt((double)  x*x + y*y + z*z );
        float delta = acelVal - acelLast;
        shake = shake * 0.9f + delta;



            if(shake > 12){
                navController.navigate(R.id.navigation_map);
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onPause(){
        super.onPause();
        sm.unregisterListener(sensorListner);
    }

    @Override
    public void onStop(){
        super.onStop();
        sm.unregisterListener(sensorListner);
    }

    @Override
    public void onResume() {
        super.onResume();
        sm.registerListener(sensorListner, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

}
