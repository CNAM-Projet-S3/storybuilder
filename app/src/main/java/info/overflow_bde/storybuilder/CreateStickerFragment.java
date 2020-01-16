package info.overflow_bde.storybuilder;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgproc.Imgproc.COLOR_RGBA2RGB;
import static org.opencv.imgproc.Imgproc.GC_INIT_WITH_RECT;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class CreateStickerFragment extends Fragment implements OnTouchListener {


	RelativeLayout editorLayout;
	ImageView      imageView;
	boolean        isEnabled;
	Mat            image;
	Mat            originalImage;
	Bitmap         bitmap;
	Mat            mask;
	//	Mat            bgdModel;
//	Mat            fgdModel;
	Rect           rect;
	Mat            source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));

	static {
		if (!OpenCVLoader.initDebug()) {
			// Handle initialization error
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		this.editorLayout = getActivity().findViewById(R.id.editor_content);
		this.imageView = getActivity().findViewById(R.id.image_view_editor);
		this.editorLayout.setOnTouchListener(this);
		this.isEnabled = false;
		bitmap = ((BitmapDrawable) this.imageView.getDrawable()).getBitmap();
		image = new Mat();
		originalImage = new Mat();
		Utils.bitmapToMat(bitmap, originalImage);
		Utils.bitmapToMat(bitmap, image);
		rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//		rect = new Rect();
		mask = new Mat(image.size(), CV_8UC1, new Scalar(0, 0, 0));
//		mask = new Mat();
//		bgdModel = new Mat();
//		fgdModel = new Mat();
		return getView();
	}

	public void init() {
		Log.i("create_stciker", "initialisation");
		System.out.println("hola");

		this.isEnabled = true;
	}

	public void exit() {
		this.isEnabled = false;
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (event.getAction() != MotionEvent.ACTION_UP) {
			// get screen size
			Display                display = this.getActivity().getWindowManager().getDefaultDisplay();
			android.graphics.Point size    = new android.graphics.Point();
			display.getSize(size);
			Log.i("create_sticker", "touch --> x=" + event.getX() + "  ,y=" + event.getY());
			Log.i("create_sticker", "screenSize --> x=" + size.x + "  ,y=" + size.y);
			Log.i("create_sticker", "imageSize --> x=" + image.width() + "  ,y=" + image.height());
			Log.i("create_sticker", "new touch --> x=" + ((event.getX() * image.width()) / size.x) + "  ,y=" + ((event.getY() * image.height()) / size.y));

			Point tapPoint = new Point((event.getX() * image.width()) / size.x, (event.getY() * image.height()) / size.y);
			circle(image, tapPoint, 15, new Scalar(0, 0, 0), -1);
			circle(mask, tapPoint, 15, new Scalar(255, 0, 255), -1);
			Utils.matToBitmap(image, bitmap);
			imageView.setImageBitmap(bitmap);
		} else {
			//create rectangle from selection
			Rect rect = boundingRect(mask);
			Log.i("create_sticker", "rect : x = " + rect.x + " y =" + rect.y + " width =" + rect.width + " height = " + rect.height);
			//test where is the rectangle
//			rectangle(image, rect, new Scalar(0,0,255));
			cvtColor(originalImage, originalImage, COLOR_RGBA2RGB, 0);
			mask = new Mat(rect.size(), CV_8UC1);
			Imgproc.grabCut(originalImage, mask, rect, new Mat(), new Mat(), 3, GC_INIT_WITH_RECT);

			//draw foreground
			// Get a mask for all `1` values in matrix.
			Mat mask1vals = new Mat();
			Core.compare(mask, new Scalar(1), mask1vals, Core.CMP_EQ);

			// Get a mask for all `3` values in matrix.
			Mat mask3vals = new Mat();
			Core.compare(mask, new Scalar(3), mask3vals, Core.CMP_EQ);

			// Create a combined mask
			Mat foregroundMask = new Mat();
			Core.max(mask1vals, mask3vals, foregroundMask);

			// First convert the single channel mat to 3 channel mat
			Imgproc.cvtColor(foregroundMask, foregroundMask, Imgproc.COLOR_GRAY2BGR);

			// Now simply take min operation
			Mat out = new Mat(rect.size(), CV_8UC1);
			Core.min(foregroundMask, originalImage, out);

			// Crop the region of interest using above rect
			Mat finalImage = new Mat(out, rect);

			Bitmap finalBitmap = Bitmap.createBitmap(finalImage.width(), finalImage.height(), Bitmap.Config.ARGB_8888);
			Utils.matToBitmap(finalImage, finalBitmap);
			imageView.setImageBitmap(finalBitmap);
		}
		return true;
	}


}
