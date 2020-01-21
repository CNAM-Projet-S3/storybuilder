package info.overflow_bde.storybuilder;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import info.overflow_bde.storybuilder.sticker.CreateStickerFragment;
import info.overflow_bde.storybuilder.TextFragment;

public class MenuFragment extends Fragment {

	private ExportFragment        exportFragment;
	private FragmentTransaction   fragmentTransaction;
	private StickersListFragment  stickersListFragment;
	private CreateStickerFragment createStickerFragment;
	private DrawFragment          drawFragment;
	private MaterialButton        buttonShareOrSave;
	private MaterialButton        buttonExit;
	private FloatingActionButton  buttonStickers;
	private FloatingActionButton  buttonDraw;
	private FloatingActionButton  buttonText;
	private FloatingActionButton  buttonReset;
	private FloatingActionButton  buttonCreateSticker;
	private FloatingActionButton  buttonFilter;


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
		this.resetMenuActionButton(view);

		//hidden action menu
		container.findViewById(R.id.editor_content).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stickersListFragment.hide();
				exportFragment.hide();
			}
		});

		this.fragmentTransaction.commit();

		this.buttonFilter = view.findViewById(R.id.editor_filters);

		return view;
	}

	private void shareOrSaveActionMenuButton(View view) {
		this.exportFragment = new ExportFragment();
		this.fragmentTransaction.add(R.id.editor_fragment, exportFragment, "shareOrSave");
		buttonShareOrSave = view.findViewById(R.id.save_or_share);
		buttonShareOrSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exportFragment.show();
			}
		});
	}

	private void exitMenuActionButton(View view) {
		buttonExit = view.findViewById(R.id.action_menu_exit);
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
		buttonStickers = (FloatingActionButton) view.findViewById(R.id.editor_sticker);


		buttonStickers.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stickersListFragment.show();
			}
		});
	}


	private void textsMenuActionButton(View view) {
		buttonText = view.findViewById(R.id.editor_text);
		buttonText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) Objects.requireNonNull(getActivity())).addFragment(new TextFragment(), R.id.editor_content, "text");
			}
		});
	}

	private void drawMenuActionButton(View view) {
		buttonDraw = view.findViewById(R.id.editor_draw);
		this.drawFragment = new DrawFragment();
		((MainActivity) Objects.requireNonNull(getActivity())).addFragment(drawFragment, R.id.editor_content, "draw");
		buttonDraw.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (drawFragment.isEnable()) {
					buttonDraw.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A7B6BC")));
					drawFragment.setEnable(false);

					//show all fragment in editor content
					showEditorContentChildren(drawFragment);
					showMenuButtons(buttonDraw);

				} else {
					// hide all fragment in editor content
					hideMenuButtons(buttonDraw);
					hideEditorContentChildren(drawFragment);
					buttonDraw.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
					drawFragment.setEnable(true);
				}
			}
		});
	}

	private void resetMenuActionButton(View view) {
		buttonReset = view.findViewById(R.id.editor_reset);
		buttonReset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager     fragmentManager     = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				for (Fragment fragment : fragmentManager.getFragments()) {
					if (((ViewGroup) fragment.getView().getParent()).getId() == R.id.editor_content && !fragment.equals(createStickerFragment)) {
						fragmentTransaction.remove(fragment);
					}
				}
				fragmentTransaction.commit();
				drawFragment = new DrawFragment();
				((MainActivity) Objects.requireNonNull(getActivity())).addFragment(drawFragment, R.id.editor_content, "draw");
			}
		});
	}

	private void createStickerActionButton(View view) {
		this.createStickerFragment = new CreateStickerFragment();
		this.fragmentTransaction.add(R.id.editor_content, this.createStickerFragment, "create-sticker");
		buttonCreateSticker = view.findViewById(R.id.editor_create_sticker);
		buttonCreateSticker.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (createStickerFragment.isEnabled()) {
					buttonCreateSticker.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A7B6BC")));
					showEditorContentChildren(createStickerFragment);
					showMenuButtons(buttonCreateSticker);
					createStickerFragment.setEnabled(false);
				} else {
					buttonCreateSticker.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
					hideEditorContentChildren(createStickerFragment);
					hideMenuButtons(buttonCreateSticker);
					createStickerFragment.init();
				}
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

	/**
	 * hide all fragment of parent id fragment pass in parameter except fragment pass in parameter
	 *
	 * @param showFragment
	 */
	public void hideEditorContentChildren(Fragment showFragment) {
		FragmentManager     fragmentManager     = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		if (showFragment.isHidden()) {
			fragmentTransaction.show(showFragment);
		}

		for (Fragment fragment : fragmentManager.getFragments()) {
			if (((ViewGroup) showFragment.getView().getParent()).getId() == ((ViewGroup) fragment.getView().getParent()).getId() && !fragment.equals(showFragment)) {
				fragmentTransaction.hide(fragment);
			}
		}

		fragmentTransaction.commit();
	}

	/**
	 * show all fragment of parent id fragment pass in parameter
	 *
	 * @param showFragment
	 */
	public void showEditorContentChildren(Fragment showFragment) {
		FragmentManager     fragmentManager     = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		for (Fragment fragment : fragmentManager.getFragments()) {
			if (((ViewGroup) showFragment.getView().getParent()).getId() == ((ViewGroup) fragment.getView().getParent()).getId() && fragment.isHidden()) {
				fragmentTransaction.show(fragment);
			}
		}

		fragmentTransaction.commit();
	}

	/**
	 * hide all menu button except the button pass in parameter
	 *
	 * @param showButton
	 */
	public void hideMenuButtons(FloatingActionButton showButton) {
		if (!showButton.equals(this.buttonCreateSticker))
			this.buttonCreateSticker.hide();
		if (!showButton.equals(this.buttonDraw))
			this.buttonDraw.hide();
		if (!showButton.equals(this.buttonReset))
			this.buttonReset.hide();
		if (!showButton.equals(this.buttonStickers))
			this.buttonStickers.hide();
		if (!showButton.equals(this.buttonText))
			this.buttonText.hide();
		if (!showButton.equals(this.buttonFilter))
			this.buttonFilter.hide();
	}

	/**
	 * show all menu button except the button pass in parameter who are already display
	 *
	 * @param showButton
	 */
	public void showMenuButtons(FloatingActionButton showButton) {
		if (!showButton.equals(this.buttonCreateSticker))
			this.buttonCreateSticker.show();
		if (!showButton.equals(this.buttonDraw))
			this.buttonDraw.show();
		if (!showButton.equals(this.buttonReset))
			this.buttonReset.show();
		if (!showButton.equals(this.buttonStickers))
			this.buttonStickers.show();
		if (!showButton.equals(this.buttonText))
			this.buttonText.show();
		if (!showButton.equals(this.buttonFilter))
			this.buttonFilter.show();
	}

	/**
	 * Return sticker button create action
	 * @return FloatingActionButton
	 */
	public FloatingActionButton getButtonCreateSticker() {
		return this.buttonCreateSticker;
	}

	/**
	 * Return sticker button draw action
	 * @return FloatingActionButton
	 */
	public FloatingActionButton getButtonDraw() {
		return this.buttonDraw;
	}

	public void setDrawFragment(DrawFragment drawFragment) {
		this.drawFragment = drawFragment;
	}
}