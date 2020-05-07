package se.team4.mamn01_grupp4;

import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;

import com.google.android.gms.maps.model.LatLng;

public class Poi{
    public LatLng location;
    public String name;
    public Drawable image;
    public AssetFileDescriptor sound;
    public String question;
    public boolean ans;
    public String description;

    public Poi(String name, String description, double v, double v1, String question, boolean ans, Drawable image, AssetFileDescriptor sound){
        this.location = new LatLng(v, v1);
        this.name = name;
        this.description = description;
        this.image= image;
        this.sound = sound;
        this.question = question;
        this.ans = ans;
    }
}