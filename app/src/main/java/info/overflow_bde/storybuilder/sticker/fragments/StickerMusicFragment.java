package info.overflow_bde.storybuilder.sticker.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.entity.MusicEntity;

public class StickerMusicFragment extends MovableFragment {

    private MusicEntity musicEntity;

    public StickerMusicFragment(MusicEntity me) {
        this.musicEntity = me;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.list_item_music, container, false);
        ((ImageView)view.findViewById(R.id.item_music_icon)).setImageBitmap(this.musicEntity.icon);
        ((TextView)view.findViewById(R.id.item_music_title)).setText(this.musicEntity.title);
        ((TextView)view.findViewById(R.id.item_music_artist)).setText(this.musicEntity.artist);

        //menuFragment = (MenuFragment) Objects.requireNonNull(this.getFragmentManager()).findFragmentByTag("menu");

        view.setOnTouchListener(onTouchListener(container, view));
        return view;
    }
}
