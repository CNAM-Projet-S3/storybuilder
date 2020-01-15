package info.overflow_bde.storybuilder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

import java.util.Objects;

public class TextFragment extends Fragment {
	private String text;
	private int    size;

	private int yDelta;
	private int xDelta;

	private MenuFragment menuFragment;
	private ViewGroup    mainLayout;
	private EditText     editText;

	public TextFragment() {
		this.size = 20;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.text_fragment, container, false);
		editText = view.findViewById(R.id.layer_text);
		editText.setHint("VOTRE TEXTE");
		editText.setTextSize(this.size);
		editText.setGravity(Gravity.CENTER);

		editText.setBackgroundColor(0xAAFFFFFF);

		mainLayout = (RelativeLayout) container.findViewById(R.id.editor_content);
		menuFragment = (MenuFragment) Objects.requireNonNull(this.getFragmentManager()).findFragmentByTag("menu");

		mainLayout.setOnClickListener(onClickListener());
		view.setOnTouchListener(onTouchListener());
		return view;
	}

	private View.OnTouchListener onTouchListener() {

		return new View.OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				final int x = (int) event.getRawX();
				final int y = (int) event.getRawY();

				switch (event.getAction() & MotionEvent.ACTION_MASK) {

					case MotionEvent.ACTION_DOWN:
						menuFragment.hide();
						RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
								view.getLayoutParams();

						xDelta = x - lParams.leftMargin;
						yDelta = y - lParams.topMargin;
						break;
					case MotionEvent.ACTION_UP:
						menuFragment.show();
						break;
					case MotionEvent.ACTION_MOVE:
						menuFragment.hide();
						RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
								.getLayoutParams();
						layoutParams.leftMargin = x - xDelta;
						layoutParams.topMargin = y - yDelta;
						layoutParams.rightMargin = 0;
						layoutParams.bottomMargin = 0;
						view.setLayoutParams(layoutParams);
						break;
				}
				return true;
			}
		};
	}

	private View.OnClickListener onClickListener() {
		return new View.OnClickListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public void onClick(View view) {
				editText.clearFocus();
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			}
		};
	}


}
