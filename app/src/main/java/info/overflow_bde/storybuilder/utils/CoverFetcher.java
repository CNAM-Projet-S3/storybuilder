package info.overflow_bde.storybuilder.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class CoverFetcher extends AsyncTask<Void, Void, Bitmap> {

    private String id;
    private String title, artist, album;

    /**
     * If we are on spotify, directly fetch the music ID
     */
    public CoverFetcher(String id) {
        this.id = id;
        this.execute();
    }

    public CoverFetcher(String title, String artist, String album) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.execute();
    }

    @Override
    protected void onPostExecute(Bitmap bmp) {
       // ?
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        if (isCancelled()) return null;

        // Fetch the infos from spotify


        return null;
    }
}
