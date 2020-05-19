package se.team4.mamn01_grupp4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.HashMap;

import se.team4.mamn01_grupp4.env.Logger;

public class MainActivity extends AppCompatActivity {

    private int score = 0;
    private int bonusScore = 0;
    private int answeredQuestions = 0;
    private Logger LOGGER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PoiDb poiDb = new PoiDb(getAssets());
        LOGGER = new Logger();
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_scan)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LOGGER.e("Result aquired");
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode  == RESULT_OK) {

                int result = data.getIntExtra("result", 0);
                if(result > 0){
                    LOGGER.e("Result is: %s", result);
                    score += 5;
                    bonusScore += result-5;
                }
                answeredQuestions ++;
                LOGGER.i("Current score is : %s", score);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public int getScore(){
        return score;
    }

    public int getBonusScore(){
        return bonusScore;
    }

    public int getAnsweredQuestions(){
        return answeredQuestions;
    }


}