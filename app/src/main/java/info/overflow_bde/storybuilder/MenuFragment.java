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

public class MenuFragment extends Fragment {

    private ShareOrSaveFragment shareOrSaveFragment;
    private FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.menu_editor_fragment, container, false);

        FragmentManager fragmentManager = getFragmentManager();
        this.fragmentTransaction = fragmentManager.beginTransaction();

        this.shareOrSaveActionMenuButton(view);
        this.exitMenuActionButton(view);

        return view;
    }

    private void shareOrSaveActionMenuButton(View view) {
        this.shareOrSaveFragment = new ShareOrSaveFragment();
        this.fragmentTransaction.add(R.id.editor_fragment, shareOrSaveFragment, "shareOrSave");
        this.fragmentTransaction.commit();
        Button buttonShareOrSave = (Button) view.findViewById(R.id.save_or_share);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOrSaveFragment.hidden();
            }
        });

        buttonShareOrSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOrSaveFragment.show();
            }
        });
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
}
