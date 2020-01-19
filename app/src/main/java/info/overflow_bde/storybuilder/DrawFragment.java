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
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;


public class DrawFragment extends Fragment{

    private MenuFragment menuFragment;
    private DrawingView dv;
    private Paint mPaint;
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
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(color);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
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

                        mPaint.setColor(color);
                        buttonColor.setBackgroundTintList(ColorStateList.valueOf(color));
                        DrawFragment.this.color=color;
                    }
                    @Override
                    public void onCancel(){
                        mPaint.setColor(DrawFragment.this.color);
                    }
                });
            }
        };
    }

    public class DrawingView extends View {

        public int width;
        public  int height;
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
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

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath( mPath, mPaint);
            canvas.drawPath( circlePath,  mPaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            circlePaint.setColor(color);
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            menuFragment.hide();
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath,  circlePaint);
            // kill this so we don't double draw
            mPath.reset();
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
