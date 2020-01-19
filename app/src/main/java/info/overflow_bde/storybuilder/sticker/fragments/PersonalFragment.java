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

public class PersonalFragment extends Fragment implements ScaleGestureDetector.OnScaleGestureListener {


	private enum Mode {
		NONE,
		DRAG,
		ZOOM
	}

	private static final String TAG      = "ZoomLayout";
	private static final float  MIN_ZOOM = 1.0f;
	private static final float  MAX_ZOOM = 15.0f;

	private Mode  mode            = Mode.NONE;
	private float scale           = 1.0f;
	private float lastScaleFactor = 0f;

	// Where the finger first  touches the screen
	private float startX = 0f;
	private float startY = 0f;

	// How much to translate the canvas
	private float dx     = 0f;
	private float dy     = 0f;
	private float prevDx = 0f;
	private float prevDy = 0f;

	private ScaleGestureDetector scaleDetector;

	private Bitmap       icon;
	private ViewGroup    mainLayout;
	private MenuFragment menuFragment;

	private int xDelta;
	private int yDelta;

	public PersonalFragment(Bitmap icon) {
		this.icon = icon;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View      view = inflater.inflate(R.layout.layer_fragment, container, false);
		ImageView iv   = view.findViewById(R.id.layer_icon);
		iv.setImageBitmap(this.icon);
		mainLayout = (RelativeLayout) container.findViewById(R.id.editor_content);
		menuFragment = (MenuFragment) Objects.requireNonNull(this.getFragmentManager()).findFragmentByTag("menu");
		view.setOnTouchListener(onTouchListener());
		scaleDetector = new ScaleGestureDetector(view.getContext(), this);

		return view;
	}


	private View.OnTouchListener onTouchListener() {
		return new View.OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View view, MotionEvent event) {

				final int x = (int) event.getRawX();
				final int y = (int) event.getRawY();
				RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
						view.getLayoutParams();

				switch (event.getAction() & MotionEvent.ACTION_MASK) {

					case MotionEvent.ACTION_DOWN:
						Log.i(TAG, "DOWN" + scale);
						menuFragment.hide();
						if (scale > MIN_ZOOM) {
							mode = Mode.DRAG;
							startX = event.getX() - prevDx;
							startY = event.getY() - prevDy;
						}
						if (mode == Mode.NONE || mode == Mode.DRAG) {
							xDelta = x - lParams.leftMargin;
							yDelta = y - lParams.topMargin;
						}
						break;
					case MotionEvent.ACTION_UP:
						Log.i(TAG, "UP");
						mode = Mode.NONE;
						prevDx = dx;
						prevDy = dy;
						menuFragment.show();
						break;
					case MotionEvent.ACTION_MOVE:
						Log.i(TAG, "MOVE , mode : " + mode);
						if (mode == Mode.DRAG) {
							dx = event.getX() - startX;
							dy = event.getY() - startY;
						}
						if (mode == Mode.NONE || (mode == Mode.DRAG)) {
							;
							lParams.leftMargin = x - xDelta;
							lParams.topMargin = y - yDelta;
							lParams.rightMargin = 0;
							lParams.bottomMargin = 0;
							menuFragment.hide();
						}
						break;
					case MotionEvent.ACTION_POINTER_DOWN:
						Log.i(TAG, "ACTION_POINTER_DOWN");
						mode = Mode.ZOOM;
						break;
					case MotionEvent.ACTION_POINTER_UP:
						Log.i(TAG, "ACTION_POINTER_UP");
						mode = Mode.DRAG;
						break;
				}
				scaleDetector.onTouchEvent(event);

				if ((mode == Mode.DRAG && scale >= MIN_ZOOM) || mode == Mode.ZOOM) {
					float maxDx = (view.getWidth() - (view.getWidth() / scale)) / 2 * scale;
					float maxDy = (view.getHeight() - (view.getHeight() / scale)) / 2 * scale;
					dx = Math.min(Math.max(dx, -maxDx), maxDx);
					dy = Math.min(Math.max(dy, -maxDy), maxDy);
					Log.i(TAG, "Width: " + view.getWidth() + ", scale " + scale + ", dx " + dx
							+ ", max " + maxDx);
					view.setScaleX(scale);
					view.setScaleY(scale);
					view.setTranslationX(dx);
					view.setTranslationY(dy);
				}
				if (mode == Mode.DRAG || mode == Mode.NONE) {
					view.setLayoutParams(lParams);
				}
				mainLayout.invalidate();
				return true;
			}
		};
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scaleFactor = detector.getScaleFactor();
		Log.i(TAG, "onScale" + scaleFactor);
		if (lastScaleFactor == 0 || (Math.signum(scaleFactor) == Math.signum(lastScaleFactor))) {
			scale *= scaleFactor;
			scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
			lastScaleFactor = scaleFactor;
		} else {
			lastScaleFactor = 0;
		}
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		Log.i(TAG, "onScaleBegin");
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		Log.i(TAG, "onScaleEnd");
	}
}
