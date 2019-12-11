package info.overflow_bde.storybuilder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.sticker.InterestPointStickerFragment;
import info.overflow_bde.storybuilder.sticker.MovieStickerFragment;
import info.overflow_bde.storybuilder.sticker.MusicPlayingStickerFragment;
import info.overflow_bde.storybuilder.sticker.PersonalStickerFragment;
import info.overflow_bde.storybuilder.sticker.StickerAdapter;
import info.overflow_bde.storybuilder.sticker.WeatherStickerFragment;

public class StickersFragment extends Fragment {

    private BottomSheetBehavior behavior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.stickers_fragment, container, false);

        View bottomSheetBehavior = view.findViewById(R.id.behavior_editor_sticker);
        this.behavior = BottomSheetBehavior.from(bottomSheetBehavior);
        this.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        this.configTabs(view);

        return view;
    }

    private void configTabs(View view) {
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabs      = (TabLayout) view.findViewById(R.id.tabs_stickers);

        StickerAdapter stickerAdapter = new StickerAdapter(getFragmentManager());
        stickerAdapter.addFragment(new PersonalStickerFragment(), "Perso");
        stickerAdapter.addFragment(new InterestPointStickerFragment(), "Points intérets");
        stickerAdapter.addFragment(new WeatherStickerFragment(), "Météo");
        stickerAdapter.addFragment(new MusicPlayingStickerFragment(), "Music en cours");
        stickerAdapter.addFragment(new MovieStickerFragment(), "Films");

        viewPager.setAdapter(stickerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        tabs.setupWithViewPager(viewPager);
    }

    public void show() {
        if (this.behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            this.behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void hidden() {
        if (this.behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            this.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}
