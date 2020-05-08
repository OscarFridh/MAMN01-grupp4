package se.team4.mamn01_grupp4;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PoiDb{

    private static Map<String, Poi> db = new HashMap<>();

    public PoiDb(AssetManager am){
        createDb(am);
    }

    public static Map<String, Poi> getDb(){
        return db;
    }

    public void createDb(AssetManager am){
        Scanner myReader = null;
        try {
            myReader = new Scanner(am.open("database"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        myReader.nextLine();
        myReader.nextLine();
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            String[] values = data.split(";");
            try {
                boolean ans;
                if(values[4] == "ja"){
                    ans = true;
                } else {
                    ans = false;
                }
                db.put(values[0], new Poi(values[0], values[1], Double.parseDouble(values[2].split(",")[0]), Double.parseDouble(values[2].split(",")[1]), values[3], ans, Drawable.createFromStream(am.open(values[0] + ".jpg"),null), am.openFd(values[0] + ".mp3")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        myReader.close();
    }

}