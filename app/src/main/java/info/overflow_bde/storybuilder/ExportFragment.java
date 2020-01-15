package info.overflow_bde.storybuilder;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class ExportFragment extends Fragment {

    private BottomSheetBehavior behavior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.save_or_share, container, false);

        View bottomSheetBehavior = view.findViewById(R.id.behavior_editor_share_or_save);
        this.behavior = BottomSheetBehavior.from(bottomSheetBehavior);
        this.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        this.setButtonShareAndSave(view);


        return view;
    }

    private void setButtonShareAndSave(View view) {
        Button buttonShare = view.findViewById(R.id.share);
        Button buttonSave  = view.findViewById(R.id.save);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("share");
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshotUglyHack();
                System.out.println("save");
            }
        });

    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private Bitmap getScreenshot() {
        View v1 = ((EditorFragment)getActivity().getSupportFragmentManager().findFragmentByTag("editor")).getView();
        v1.setDrawingCacheEnabled(true);

        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        return bitmap;
    }

    private void takeScreenshotUglyHack() {
        ((MenuFragment)getActivity().getSupportFragmentManager().findFragmentByTag("menu")).hide();
        this.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        Thread t  = new Thread(){
            public void run() {
                try {
                    Thread.sleep(1000);
                    Activity a = ExportFragment.this.getActivity();
                    a.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ExportFragment.this.takeScreenshot();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        t.start();
    }

    private void takeScreenshot() {
        verifyStoragePermissions(this.getActivity());

       boolean b1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
       boolean b2 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;

        if (b1 || b2) {
            Toast t = Toast.makeText(this.getActivity(), "Pas la permission!", Toast.LENGTH_LONG);
            t.show();
            return;
        }

        Date now = new Date();
        CharSequence cs = android.text.format.DateFormat.format("yyyyMMdd_hhmmss", now);

        try {
            String mPath = Environment.getExternalStorageDirectory().toString() + "/storybuilder/" + cs.toString() + ".jpg";

            Bitmap bitmap = this.getScreenshot();

            File imageFile = new File(mPath);
            imageFile.getParentFile().mkdirs();

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        MainActivity.mf.show();
    }


    public void show() {
        if (this.behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            this.behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void hidden() {
        if (this.behavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            this.behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }
}
