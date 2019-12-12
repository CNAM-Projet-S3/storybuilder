package info.overflow_bde.storybuilder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    final int PICTURE_TAKEN  = 1;
    final int PICTURE_CHOSEN = 2;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        //display main menu
        this.showFragment(new MainFragment(), R.id.main_activity, "main_menu");
    }

    public void openCamera(View view) {
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, PICTURE_TAKEN);
        dispatchTakePictureIntent();
    }

    public void choicePicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICTURE_CHOSEN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap image = null;

        switch (requestCode) {
            case PICTURE_TAKEN:
                System.out.println("image was taken");
                try {
                   // image = (Bitmap) data.getExtras().get("data");
                    Log.i("photoPath", currentPhotoPath);
                    image = BitmapFactory.decodeFile(currentPhotoPath);
                } catch (Exception e) {

                }
                break;
            case PICTURE_CHOSEN:
                System.out.println("image was choosen");
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

        //display picture
        this.showFragment(new EditorFragment(image), R.id.main_activity, "editor");

        //display menu
        this.showFragment(new MenuFragment(), R.id.editor_fragment, "menu");
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


        Log.i("image", image.getAbsolutePath());
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void dispatchTakePictureIntent() {
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

    private void showFragment(Fragment fragment, @IdRes int containerViewId, @Nullable String tag) {
        FragmentManager     fragmentManager     = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, tag);
        fragmentTransaction.commit();
    }
}
