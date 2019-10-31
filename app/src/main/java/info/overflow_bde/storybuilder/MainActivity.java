package info.overflow_bde.storybuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    final int PICTURE_TAKEN = 1;
    final int PICTURE_CHOSEN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICTURE_TAKEN);
    }

    public void choicePicture(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICTURE_CHOSEN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent(this, EditorActivity.class);
        String uri = this.getCacheDir().getAbsolutePath() + "/tmpImage";
        Bitmap image = null;

        switch (requestCode) {
            case PICTURE_TAKEN:
                System.out.println("image was taken");
                image = (Bitmap) data.getExtras().get("data");
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

        //create temporary image in cache
        try {
            FileOutputStream out = new FileOutputStream(uri);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            intent.putExtra("Uri", uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }
}
