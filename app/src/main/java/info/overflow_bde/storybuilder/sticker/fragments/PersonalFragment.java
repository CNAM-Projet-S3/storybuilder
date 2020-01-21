package info.overflow_bde.storybuilder.sticker.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import java.util.Objects;

import info.overflow_bde.storybuilder.MenuFragment;
import info.overflow_bde.storybuilder.R;

public class PersonalFragment extends MovableFragment {

	private ScaleGestureDetector scaleDetector;

	private Bitmap       icon;
	//private ViewGroup    mainLayout;
	//private MenuFragment menuFragment;

	public PersonalFragment(Bitmap icon) {
		this.icon = icon;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View      view = inflater.inflate(R.layout.layer_fragment, container, false);
		ImageView iv   = view.findViewById(R.id.layer_icon);
		iv.setImageBitmap(this.icon);
		view.setOnTouchListener(onTouchListener(container, view));
		return view;
	}
}
