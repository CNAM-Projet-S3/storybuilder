package info.overflow_bde.storybuilder;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

public class ExportFragment extends Fragment {

    private EditorFragment editor;
    private BottomSheetBehavior behavior;

    public ExportFragment(EditorFragment frg) {
        this.editor = frg;
    }

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
        Context ctx         = view.getContext();
        Button  buttonShare = view.findViewById(R.id.share);
        Button  buttonSave  = view.findViewById(R.id.save);
        buttonShare.setOnClickListener(v -> {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "tmp");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = ctx.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            try {
                OutputStream outstream = ctx.getContentResolver().openOutputStream(uri);
                this.editor.getImage().compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                outstream.close();
            } catch (Exception e) {
                System.err.println(e.toString());
            }

            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share Image"));
        });

        buttonSave.setOnClickListener(v -> Log.d("STORYBUILDER", "Button save"));

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
