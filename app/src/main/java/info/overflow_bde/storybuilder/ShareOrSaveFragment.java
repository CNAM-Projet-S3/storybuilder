package info.overflow_bde.storybuilder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class ShareOrSaveFragment extends Fragment {

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
        Button buttonShare = (Button) view.findViewById(R.id.share);
        Button buttonSave = (Button) view.findViewById(R.id.save);
        buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("share");
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("save");
            }
        });

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
