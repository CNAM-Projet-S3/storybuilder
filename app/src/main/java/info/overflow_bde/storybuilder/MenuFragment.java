package info.overflow_bde.storybuilder;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.Objects;

import info.overflow_bde.storybuilder.sticker.CreateStickerFragment;

public class MenuFragment extends Fragment {

	private ExportFragment        exportFragment;
	private FragmentTransaction   fragmentTransaction;
	private StickersListFragment  stickersListFragment;
	private CreateStickerFragment createStickerFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menu_editor_fragment, container, false);

		FragmentManager fragmentManager = getFragmentManager();
		this.fragmentTransaction = fragmentManager.beginTransaction();

		this.shareOrSaveActionMenuButton(view);
		this.exitMenuActionButton(view);
		this.stickersMenuActionButton(view);
		this.textsMenuActionButton(view);
		this.createStickerActionButton(view);
        this.drawMenuActionButton(view);
		this.restMenuActionButton(view);

		//hidden action menu
		container.findViewById(R.id.editor_content).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stickersListFragment.hide();
				exportFragment.hide();
			}
		});
		this.fragmentTransaction.commit();

		return view;
	}

	private void shareOrSaveActionMenuButton(View view) {
		this.exportFragment = new ExportFragment();
		this.fragmentTransaction.add(R.id.editor_fragment, exportFragment, "shareOrSave");
		Button buttonShareOrSave = (Button) view.findViewById(R.id.save_or_share);
		buttonShareOrSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exportFragment.show();
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

	private void stickersMenuActionButton(View view) {
		this.stickersListFragment = new StickersListFragment();
		this.fragmentTransaction.add(R.id.editor_fragment, this.stickersListFragment, "stickers");
		FloatingActionButton buttonStickers = (FloatingActionButton) view.findViewById(R.id.editor_sticker);


		buttonStickers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stickersListFragment.show();
			}
		});
	}


	private void textsMenuActionButton(View view) {

		FloatingActionButton buttonText = view.findViewById(R.id.editor_text);
		buttonText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) Objects.requireNonNull(getActivity())).addFragment(new TextFragment(), R.id.editor_content, "text");

			}
		});
	}

	private void drawMenuActionButton(View view) {
		final FloatingActionButton buttonDraw = view.findViewById(R.id.editor_draw);
        final DrawFragment df = new DrawFragment();
        ((MainActivity) Objects.requireNonNull(getActivity())).addFragment(df, R.id.editor_content, "draw");
       // this.fragmentTransaction.add(R.id.editor_fragment, this.stickersListFragment, "stickers");
        buttonDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(df.isEnable()){
                    int color =  Color.parseColor("#A7B6BC");
                    buttonDraw.setBackgroundTintList(ColorStateList.valueOf(color));
                    df.setEnable(false);
                }else {
                    buttonDraw.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    df.setEnable(true);
                }
            }
        });
	}

    private void restMenuActionButton(View view) {
        FloatingActionButton buttonText = view.findViewById(R.id.editor_reset);
        buttonText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v = null;
            }
        });
    }

	private void createStickerActionButton(View view) {
		this.createStickerFragment = new CreateStickerFragment();
		this.fragmentTransaction.add(R.id.editor_fragment, this.createStickerFragment, "create-sticker");
		FloatingActionButton buttonCreateSticker = view.findViewById(R.id.editor_create_sticker);
		buttonCreateSticker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createStickerFragment.init();
			}
		});
	}

	public void show() {
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction()
				.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
				.show(this)
				.commit();
	}

	public void hide() {
		FragmentManager fm = getFragmentManager();
		fm.beginTransaction()
				.hide(this)
				.commit();
	}
}