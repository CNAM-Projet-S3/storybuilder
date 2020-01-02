package info.overflow_bde.storybuilder.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class StickerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList      = new ArrayList<>();
    private final List<String>   mFragmentTitleList = new ArrayList<>();

    public StickerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }


    @Override
    public Fragment getItem(int position) {
        return this.mFragmentList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        this.mFragmentList.add(fragment);
        this.mFragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return this.mFragmentTitleList.get(position);
    }

    @Override
    public int getCount() {
        return this.mFragmentList.size();
    }
}