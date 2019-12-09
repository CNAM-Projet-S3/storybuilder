package info.overflow_bde.storybuilder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    final int PICTURE_TAKEN = 1;
    final int PICTURE_CHOSEN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_main);

        //display main menu
        this.showFragment(new MainFragment(), R.id.main_activity, "main_menu");
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
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap image = null;

        switch (requestCode) {
            case PICTURE_TAKEN:
                System.out.println("image was taken");
                try {
                    image = (Bitmap) data.getExtras().get("data");
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

    private void showFragment(Fragment fragment, @IdRes int containerViewId, @Nullable String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(containerViewId, fragment, tag);
        fragmentTransaction.commit();
    }
}
