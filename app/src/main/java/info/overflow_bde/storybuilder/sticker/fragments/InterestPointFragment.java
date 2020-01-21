package info.overflow_bde.storybuilder.sticker.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Objects;

import info.overflow_bde.storybuilder.MenuFragment;
import info.overflow_bde.storybuilder.R;

public class InterestPointFragment extends MovableFragment {

    private Bitmap icon;
    private String title;

    public InterestPointFragment(Bitmap icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.layer_fragment, container, false);
        ImageView iv = view.findViewById(R.id.layer_icon);
        TextView tv = view.findViewById(R.id.layer_title);
        iv.setImageBitmap(this.icon);
        tv.setText(this.title);

        view.setOnTouchListener(onTouchListener(container, view));
        return view;
    }

}
