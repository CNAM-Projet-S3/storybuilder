package info.overflow_bde.storybuilder;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static EditorFragment ef;
    public static MenuFragment mf;
    final int PICTURE_TAKEN  = 1;
    final int PICTURE_CHOSEN = 2;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        //display main menu
        this.showFragment(new MainFragment(), R.id.main_activity, "main_menu");
    }

    public void openCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "info.overflow_bde.storybuilder.provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, PICTURE_TAKEN);
            }
        }
    }

    public void choicePicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICTURE_CHOSEN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap image = null;
            switch (requestCode) {
                case PICTURE_TAKEN:
                    Log.i("image", "image was taken");
                    image = BitmapFactory.decodeFile(this.currentPhotoPath);
                    break;
                case PICTURE_CHOSEN:
                    Log.i("image", "image was choosen");
                    try {
                        image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                        this.writeBitmapToFile(image, data.getData(), this.createImageFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            int rotate = 0;
            ExifInterface exif = null;
            System.out.println(currentPhotoPath);
            try {
                exif = new ExifInterface(this.currentPhotoPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = Objects.requireNonNull(exif).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            System.out.println("orientation " + orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 2;
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);

            image = Bitmap.createBitmap(Objects.requireNonNull(image), 0, 0, image.getWidth(), image.getHeight(), matrix, true);

            //display picture
            this.showFragment(ef = new EditorFragment(image), R.id.main_activity, "editor");

            //display menu
            this.showFragment(mf = new MenuFragment(), R.id.editor_fragment, "menu");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + "storybuilder"+ "_";
        File storageDir = new File("/tmp/");
        try {
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            //storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
        }catch(NullPointerException npe){
            Log.e("exception", "Null Exception");
        }
        File image = null;
        try {
            image = File.createTempFile(imageFileName, "." +
                    "jpg", storageDir);
        }catch (IOException io){
            Log.i("IO","EXCEPTION");
            image = new File("/tmp/");
        }

        // Save a file: path for use with ACTION_VIEW intents
        this.currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    public void showFragment(Fragment fragment, @IdRes int containerViewId, @Nullable String tag) {
        FragmentManager     fragmentManager     = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, tag);
        fragmentTransaction.commit();
    }

    private void writeBitmapToFile(Bitmap bitmapOri, Uri UriOri, File destination) {
        try (FileOutputStream out = new FileOutputStream(destination)) {
            bitmapOri.compress(Bitmap.CompressFormat.JPEG, 100, out);
            String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            Cursor cur = getContentResolver().query(UriOri, orientationColumn, null, null, null);
            int orientation = -1;
            if (cur != null && cur.moveToFirst()) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                switch (orientation) {
                    case 270:
                        orientation = ExifInterface.ORIENTATION_ROTATE_270;
                        break;
                    case 180:
                        orientation = ExifInterface.ORIENTATION_ROTATE_180;
                        break;
                    case 90:
                        orientation = ExifInterface.ORIENTATION_ROTATE_90;
                        break;
                    default:
                        orientation = 0;
                        break;
                }
            }
            cur.close();

            ExifInterface newExif = new ExifInterface(destination.getAbsolutePath());
            newExif.setAttribute(ExifInterface.TAG_ORIENTATION, String.valueOf(orientation));
            newExif.saveAttributes();

        } catch (Exception ex) {
            Log.i("image", String.format("Error writing bitmapOri to %s: %s", destination.getAbsoluteFile(), ex.getMessage()));
        }
    }
}
