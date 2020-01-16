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

public class InterestPointFragment extends Fragment {

    private Bitmap icon;
    private String title;
    private ViewGroup mainLayout;
    private MenuFragment menuFragment;

    private int xDelta;
    private int yDelta;

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
        mainLayout = (RelativeLayout) container.findViewById(R.id.editor_content);
        menuFragment = (MenuFragment) Objects.requireNonNull(this.getFragmentManager()).findFragmentByTag("menu");

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
                mainLayout.invalidate();
                return true;
            }
        };
    }
}
