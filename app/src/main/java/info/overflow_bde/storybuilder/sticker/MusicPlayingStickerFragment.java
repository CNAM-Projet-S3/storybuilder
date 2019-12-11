package info.overflow_bde.storybuilder.sticker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import info.overflow_bde.storybuilder.R;

public class MusicPlayingStickerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.music_playing_sticker_fragment, container, false);
    }
}
