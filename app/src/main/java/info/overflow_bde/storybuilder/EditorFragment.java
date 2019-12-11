package info.overflow_bde.storybuilder;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

public class EditorFragment extends Fragment {

    private Bitmap img;

    public EditorFragment(Bitmap img) {
        this.img = img;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View      view = inflater.inflate(R.layout.editor_fragment, container, false);
        ImageView iv   = view.findViewById(R.id.image_view_editor);
        iv.setImageBitmap(this.img);

        return view;
    }
}
