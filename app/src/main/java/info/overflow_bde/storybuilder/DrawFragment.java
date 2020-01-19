package info.overflow_bde.storybuilder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;


public class DrawFragment extends Fragment{

    private MenuFragment menuFragment;
    private DrawingView dv;
    private Paint paint;
    private boolean enable;
    private int color;
    private FloatingActionButton buttonColor;
    public DrawFragment(){
        Log.i("Draw","Constructeur drawFragment");
        this.color = Color.BLUE;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.draw_fragment, container, false);
        dv = new DrawingView(this.getContext());
        view.addView(dv);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(12);
        Log.i("Draw","onCreateView");
        Log.i("width",String.valueOf(view.getWidth()));
        Log.i("height",String.valueOf(view.getHeight()));
        menuFragment = (MenuFragment) Objects.requireNonNull(this.getFragmentManager()).findFragmentByTag("menu");
        enable = false;
        buttonColor = (FloatingActionButton)getActivity().findViewById(R.id.editor_color_text);
        buttonColor.setVisibility(View.INVISIBLE);
        buttonColor.setBackgroundTintList(ColorStateList.valueOf(this.color));
        buttonColor.setOnClickListener(onButtonTextColorClickListener());
        return  view;
    }

    public void setEnable(boolean b){
        Log.i("b", String.valueOf(b));
        if(b) {
            buttonColor.setVisibility(View.VISIBLE);
        }else{
            buttonColor.setVisibility(View.INVISIBLE);
        }
        enable = b;
    }

    public boolean isEnable(){
        return this.enable;
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
                ColorPicker colorPicker = new ColorPicker(DrawFragment.this.getActivity());
                colorPicker.show();
                colorPicker.setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position,int color) {

                        paint.setColor(color);
                        buttonColor.setBackgroundTintList(ColorStateList.valueOf(color));
                        DrawFragment.this.color=color;
                    }
                    @Override
                    public void onCancel(){
                        paint.setColor(DrawFragment.this.color);
                    }
                });
            }
        };
    }

    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap bitmap;
        private Canvas canvas;
        private Path path;
        private Paint bitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context=c;
            path = new Path();
            bitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(color);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(12);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
            canvas.drawPath(path, paint);
            canvas.drawPath( circlePath, paint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            circlePaint.setColor(color);
            path.reset();
            path.moveTo(x, y);
            mX = x;
            mY = y;
            menuFragment.hide();
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                path.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            path.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            canvas.drawPath(path,  circlePaint);
            // kill this so we don't double draw
            path.reset();
            menuFragment.show();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Log.i("enable",String.valueOf(enable));
            if (enable) {
                float x = event.getX();
                float y = event.getY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touch_start(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        touch_move(x, y);
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        touch_up();
                        invalidate();
                        break;
                }
            }
            return true;
        }
    }

}
