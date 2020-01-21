package info.overflow_bde.storybuilder;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

public class TextFragment extends Fragment {
	private int    size;
	private int color;

	private int     yDelta;
	private int     xDelta;
	private boolean isFocused = false;

	private MenuFragment menuFragment;
	private EditText     editText;
	private FloatingActionButton buttonTextColor;
	private RelativeLayout mainLayout;

	public TextFragment() {
		this.size = 20;
		this.color = Color.BLACK;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.text_fragment, container, false);
		editText = view.findViewById(R.id.layer_text);
		editText.setHint("VOTRE TEXTE");
		editText.setTextSize(this.size);
		editText.setGravity(Gravity.CENTER);
		editText.setForegroundGravity(Gravity.CENTER);

		menuFragment = (MenuFragment) Objects.requireNonNull(this.getFragmentManager()).findFragmentByTag("menu");

		buttonTextColor = (FloatingActionButton)getActivity().findViewById(R.id.editor_color_text);
		buttonTextColor.setVisibility(View.INVISIBLE);
		buttonTextColor.setBackgroundTintList(ColorStateList.valueOf(this.color));

		mainLayout = container.findViewById(R.id.editor_content);


		mainLayout.setOnClickListener(onClickListener());
		buttonTextColor.setOnClickListener(onButtonTextColorClickListener());
		editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				TextFragment.this.onFocusChange(v, hasFocus);
			}
		});
		view.setOnTouchListener(onTouchListener());

		return view;
	}

	/*
	move the edittext on action
	 */
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

	/*
		show/hide the button color button
		if the edittext is empty, remove the listener then remove the edittext
	 */

	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			this.isFocused = true;
			buttonTextColor.setVisibility(View.VISIBLE);
			menuFragment.hideEditorContentChildren(this);
			menuFragment.hideMenuButtons(buttonTextColor);
		}
		else {
			this.isFocused = false;
			buttonTextColor.setVisibility(View.INVISIBLE);
			menuFragment.showEditorContentChildren(this);
			menuFragment.showMenuButtons(buttonTextColor);
		}
	}

	/*
	on click, show the colorpicker
	on chose, change the color of the text and the color of the button
	on cancel, keep the previous color
	 */

	private View.OnClickListener onButtonTextColorClickListener() {
		return new View.OnClickListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public void onClick(View view) {
				ColorPicker colorPicker = new ColorPicker(TextFragment.this.getActivity());
				colorPicker.show();
				colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
					@Override
					public void onChooseColor(int position,int color) {

						editText.setTextColor(color);
						buttonTextColor.setBackgroundTintList(ColorStateList.valueOf(color));
						TextFragment.this.color=color;
					}
					@Override
					public void onCancel(){
						editText.setTextColor(TextFragment.this.color);
					}
				});
			}
		};
	}

	/*
	on click outside of the edittext, clear the focus and hide the keyboard
	 */
	public View.OnClickListener onClickListener() {
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

	public boolean getIsFocused() {
		return this.isFocused;
	}

	public void defocused(){
		this.mainLayout.performClick();
	}


}
