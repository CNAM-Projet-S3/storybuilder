package info.overflow_bde.storybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MenuFragment extends Fragment {

    private FragmentTransaction fragmentTransaction;
    private StickersFragment    stickersFragment;
    private EditorFragment      editorFragment;
    private ExportFragment      exportFragment;

    public MenuFragment(EditorFragment frg) {
        this.editorFragment = frg;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_editor_fragment, container, false);

        FragmentManager fragmentManager = getFragmentManager();
        this.fragmentTransaction = fragmentManager.beginTransaction();

        this.shareOrSaveActionMenuButton(view);
        this.exitMenuActionButton(view);
        this.stickersMenuActionButton(view);

        //hidden action menu
        view.setOnClickListener(v -> {
            stickersFragment.hidden();
            exportFragment.hidden();
        });
        this.fragmentTransaction.commit();

        return view;
    }

    private void shareOrSaveActionMenuButton(View view) {
        this.exportFragment = new ExportFragment(this.editorFragment);
        this.fragmentTransaction.add(R.id.editor_fragment, exportFragment, "shareOrSave");
        Button buttonShareOrSave = view.findViewById(R.id.save_or_share);
        buttonShareOrSave.setOnClickListener(v -> exportFragment.show());
    }

    private void exitMenuActionButton(View view) {
        Button buttonExit = (Button) view.findViewById(R.id.action_menu_exit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getActivity().getIntent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getActivity().overridePendingTransition(0, 0);
                getActivity().finish();
                getActivity().overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });
    }

    private void stickersMenuActionButton(View view) {
        this.stickersFragment = new StickersFragment();
        this.fragmentTransaction.add(R.id.editor_fragment, this.stickersFragment, "stickers");
        FloatingActionButton buttonStickers = (FloatingActionButton) view.findViewById(R.id.editor_sticker);


        buttonStickers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stickersFragment.show();
            }
        });
    }
}
