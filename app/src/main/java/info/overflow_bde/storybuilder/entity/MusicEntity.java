package info.overflow_bde.storybuilder.entity;

import android.graphics.Bitmap;

public class MusicEntity {

    public String title;
    public String artist;
    public Bitmap icon;

    public Bitmap finale;

    public MusicEntity(String title, String artist, Bitmap icon) {
        this.title = title;
        this.artist = artist;
        this.icon = icon;
    }

}
