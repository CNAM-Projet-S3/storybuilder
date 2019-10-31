package info.overflow_bde.storybuilder;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class EditorActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Uri imgURI = Uri.parse(this.getIntent().getStringExtra("Uri"));
        ImageView imageview = findViewById(R.id.imageView);
        imageview.setImageURI(imgURI);

        //@TODO not forgot tmp img in "this.getCacheDir().getAbsolutePath() + "/tmpImage""
    }
}
