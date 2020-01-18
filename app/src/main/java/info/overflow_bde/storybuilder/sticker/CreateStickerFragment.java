package info.overflow_bde.storybuilder.sticker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Objects;

import info.overflow_bde.storybuilder.MainActivity;
import info.overflow_bde.storybuilder.MenuFragment;
import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.sticker.fragments.PersonalFragment;
import info.overflow_bde.storybuilder.utils.DB.PersonalSticker;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.GC_INIT_WITH_RECT;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.circle;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.resize;

public class CreateStickerFragment extends Fragment implements OnTouchListener {


	private ImageView    imageView;
	private boolean      isEnabled;
	private Mat          image;
	private Mat          originalImage;
	private Bitmap       bitmap;
	private Mat          mask;
	private MenuFragment menuFragment;

	static {
		if (!OpenCVLoader.initDebug()) {
			// Handle initialization error
		}
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		RelativeLayout editorLayout = Objects.requireNonNull(getActivity()).findViewById(R.id.editor_content);
		this.imageView = getActivity().findViewById(R.id.image_view_editor);
		editorLayout.setOnTouchListener(this);
		this.menuFragment = (MenuFragment) this.getActivity().getSupportFragmentManager().findFragmentByTag("menu");
		this.isEnabled = false;

		return getView();
	}

	public void init() {
		Log.i("create_sticker", "initialisation");
		// hide menu
		this.menuFragment.hide();

		//@TODO hide editor content

		// enabled on touch listner
		this.isEnabled = true;

		// init variables for grabcut process
		this.bitmap = ((BitmapDrawable) this.imageView.getDrawable()).getBitmap();
		this.image = new Mat();
		Utils.bitmapToMat(this.bitmap, this.image);
		this.originalImage = this.image.clone();
		this.mask = new Mat(image.size(), CV_8UC1, new Scalar(0, 0, 0));
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (this.isEnabled) {
			if (event.getAction() != MotionEvent.ACTION_UP) {

				// logs
				Log.i("create_sticker", "touch --> x=" + event.getX() + "  ,y=" + event.getY());
				Log.i("create_sticker", "imageSize --> x=" + this.image.width() + "  ,y=" + this.image.height());

				// to determine thichness
				int nbPixelBitmap = bitmap.getWidth() * bitmap.getHeight();
				int nbPixelView   = imageView.getWidth() * imageView.getHeight();

				// radius = (900*nbPixelBitmap)/nbPixelView;
				// determine radius from aera of 6400 (circle 80px*80px)
				BigInteger nbPixelBitmapBig = new BigInteger(BigInteger.valueOf(nbPixelBitmap).toByteArray());
				BigInteger aeraBig          = new BigInteger(BigInteger.valueOf(6400).toByteArray());

				BigInteger multiplied = nbPixelBitmapBig.multiply(aeraBig);
				BigInteger divided    = multiplied.divide(new BigInteger(BigInteger.valueOf(nbPixelView).toByteArray()));
				double     radius     = Math.sqrt(divided.doubleValue() / Math.PI);

				// calculate inverse matrix
				Matrix inverse = new Matrix();
				imageView.getImageMatrix().invert(inverse);

				// map touch point from ImageView to image
				float[] touchPoint = new float[] {event.getX(), event.getY()};
				inverse.mapPoints(touchPoint);
				// touchPoint now contains x and y in image's coordinate system
				Point tapPoint = new Point(touchPoint[0], touchPoint[1]);
				Log.i("create_sticker", "naturalImage --> x=" + (this.image.width() / this.imageView.getWidth()) * event.getX() + "  ,y=" + (this.image.height() / this.imageView.getHeight()) * event.getY());

				//draw circle according to user selection to the image from image view
				circle(image, tapPoint, (int) radius, new Scalar(255, 255, 255), -1);

				//draw circle according to user selection for grabcut processing
				circle(mask, tapPoint, (int) radius, new Scalar(255, 255, 255), -1);

				Utils.matToBitmap(this.image, this.bitmap);
				imageView.setImageBitmap(bitmap);
			} else {

				//set original image during the processing
				Utils.matToBitmap(originalImage, bitmap);
				imageView.setImageBitmap(bitmap);

				//create rectangle from selection
				Rect rect = boundingRect(mask);
				Log.i("create_sticker", "rect : x = " + rect.x + " y =" + rect.y + " width =" + rect.width + " height = " + rect.height);

				//remove alpha chanel
				Mat originalImageNoAlpha = new Mat();
				cvtColor(originalImage, originalImageNoAlpha, Imgproc.COLOR_RGBA2RGB);

				//resize image and select rect to grabcut
				int  scalePercent   = 25; // percent of original size
				int  newWidthImage  = originalImage.width() * scalePercent / 100;
				int  newHeightImage = originalImage.height() * scalePercent / 100;
				int  newWidthRect   = rect.width * scalePercent / 100;
				int  newHeightRect  = rect.height * scalePercent / 100;
				int  newXRect       = rect.x * scalePercent / 100;
				int  newYRect       = rect.y * scalePercent / 100;
				Rect newRec         = new Rect(newXRect, newYRect, newWidthRect, newHeightRect);
				resize(originalImageNoAlpha, originalImageNoAlpha, new Size(newWidthImage, newHeightImage));

				//process grabcut
				Imgproc.grabCut(originalImageNoAlpha, mask, newRec, new Mat(), new Mat(), 5, GC_INIT_WITH_RECT);

				//resize mask to apply to original image
				resize(mask, mask, new Size(originalImage.width(), originalImage.height()));

				// extract selection from mask
				Mat imageExtract = new Mat(1, 1, CvType.CV_8U, new Scalar(3.0));
				Core.compare(mask, imageExtract, mask, Core.CMP_EQ);

				// alpha channel
				Mat imageAlpha = new Mat(originalImage.size(), CV_8UC3, new Scalar(255, 255, 255, 255));
				originalImage.copyTo(imageAlpha, mask);

				// Crop the region of interest using above rect
				Mat finalImage = new Mat(imageAlpha, rect);

				// create final bitmap
				Bitmap finalBitmap = Bitmap.createBitmap(finalImage.width(), finalImage.height(), Bitmap.Config.ARGB_8888);
				Utils.matToBitmap(finalImage, finalBitmap);

				// add image to movable fragment
				((MainActivity) Objects.requireNonNull(getActivity())).addFragment(new PersonalFragment(finalBitmap), R.id.editor_content, "personal");

				// disabled selection
				this.isEnabled = false;
				this.menuFragment.show();

				//@TODO hide editor content

				//save selection grap to database
				this.saveInDatabase(finalBitmap);

				//warn user
				Toast t = Toast.makeText(this.getActivity(), "Succès", Toast.LENGTH_SHORT);
				t.show();
			}
		}
		return true;
	}

	/**
	 * Save selection to personal sticker database
	 *
	 * @param bitmap
	 * @return
	 */
	private void saveInDatabase(Bitmap bitmap) {
		PersonalSticker.PersonalStickerDbHelper dbHelper = new PersonalSticker.PersonalStickerDbHelper(getContext());

		// Gets the data repository in write mode
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(PersonalSticker.PersonalStickerEntry.COLUMN_NAME_IMAGE, this.convertBitmapToBase64(bitmap));

		// Insert the new row, returning the primary key value of the new row
		db.insert(PersonalSticker.PersonalStickerEntry.TABLE_NAME, null, values);
	}

	/**
	 * Convert Bitmap to base64
	 *
	 * @param bitmap
	 * @return
	 */
	private String convertBitmapToBase64(Bitmap bitmap) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
		return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
	}
}
