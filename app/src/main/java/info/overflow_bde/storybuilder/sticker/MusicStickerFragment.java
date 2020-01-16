package info.overflow_bde.storybuilder.sticker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

import info.overflow_bde.storybuilder.MainActivity;
import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.StickersListFragment;
import info.overflow_bde.storybuilder.adapter.MusicAdapter;
import info.overflow_bde.storybuilder.entity.MusicEntity;
import info.overflow_bde.storybuilder.sticker.fragments.StickerMusicFragment;

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

        //click on interest point item, hidden menu stickers and display selected interest point
        this.musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicEntity me = (MusicEntity) musicList.getItemAtPosition(position);
                StickersListFragment stickersListFragment = (StickersListFragment) Objects.requireNonNull(getFragmentManager()).findFragmentByTag("stickers");
                stickersListFragment.hide();
                ((MainActivity) getActivity()).addFragment(new StickerMusicFragment(me), R.id.editor_content, me.title);
            }
        });

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

            ArrayList<MusicEntity> al = new ArrayList<>();
            al.add(new MusicEntity(track, artist, Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)));

            setMusicEntities(al);
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
}
