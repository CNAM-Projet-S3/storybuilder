package info.overflow_bde.storybuilder.sticker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import info.overflow_bde.storybuilder.MainActivity;
import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.StickersListFragment;
import info.overflow_bde.storybuilder.adapter.MusicAdapter;
import info.overflow_bde.storybuilder.entity.InterestPointEntity;
import info.overflow_bde.storybuilder.entity.MusicEntity;
import info.overflow_bde.storybuilder.sticker.fragments.StickerMusicFragment;
import info.overflow_bde.storybuilder.utils.HTTPUtils;
import info.overflow_bde.storybuilder.utils.SimpleCallback;

public class MusicStickerFragment extends Fragment {

    private View view;
    private ProgressBar circleProgress;
    private ListView musicList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.music_sticker_fragment, container, false);

        this.circleProgress = view.findViewById(R.id.music_progress);
        this.musicList      = view.findViewById(R.id.list_music);

        IntentFilter iF = new IntentFilter();

        /* Listening for broadcast about song changes for android default player, or compatible ones */
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");

        /* Listening for broadcast about song changes for Spotify */
        iF.addAction("com.spotify.music.metadatachanged");

        getActivity().registerReceiver(mReceiver, iF);

        // When clicking the item, pop it up on the frame
        this.musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicEntity me = (MusicEntity) musicList.getItemAtPosition(position);
                StickersListFragment stickersListFragment = (StickersListFragment) Objects.requireNonNull(getFragmentManager()).findFragmentByTag("stickers");
                stickersListFragment.hide();
                ((MainActivity) getActivity()).addFragment(new StickerMusicFragment(me), R.id.editor_content, me.title);
            }
        });

        AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        if (am.isMusicActive()) {

        }

        return view;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.d("STORYBUILDER", "BROADCAST RECEIVED: " + intent.getAction() + " - " + intent.toString());
            String action = intent.getAction();
            String artist = intent.getStringExtra("artist");
            String track = intent.getStringExtra("track");

            fetchInfos(new MusicEntity(track, artist, Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)));
        }
    };

    private void setMusicEntities(final ArrayList<MusicEntity> musics) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MusicAdapter adapter = new MusicAdapter(view.getContext(), musics);
                MusicStickerFragment.this.musicList.setAdapter(adapter);
                circleProgress.setVisibility(View.GONE);
            }
        });
    }

    private void fetchInfos(MusicEntity me) {

        Map<String, String> map = new HashMap<>();
        map.put("Artist", me.artist);
        map.put("Title", me.title);

        HTTPUtils.executeHttpRequest("https://story.overflow-bde.info/spotify", map, new SimpleCallback() {
            @Override
            public void callback(String resp) throws JSONException {
                ArrayList<MusicEntity> mes = new ArrayList<>();
                JSONObject obj = new JSONObject(resp);
                Bitmap mp = HTTPUtils.getBitmapFromURL(obj.getString("Cover"));
                MusicEntity me = new MusicEntity(obj.getString("Title"), obj.getString("Artist"), mp);
                mes.add(me);

                MusicStickerFragment.this.setMusicEntities(mes);
            }
        });


    }
}

