package info.overflow_bde.storybuilder.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.entity.PersonalEntity;
import info.overflow_bde.storybuilder.utils.DB.PersonalSticker;

public class PersonalAdapter extends ArrayAdapter<PersonalEntity> {
	public PersonalAdapter(Context context, ArrayList<PersonalEntity> personalEntities) {

		super(context, 0, personalEntities);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final PersonalEntity personalEntity = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_personal_sticker_list, parent, false);
		}

		MaterialButton buttonRemove = convertView.findViewById(R.id.item_action_remove);
		buttonRemove.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				int success = deletePersonalSticker(personalEntity.id);
				if (success >= 1) {
					Log.i("personal_sticker", "sucess remove personal sticker" + personalEntity.id);
					remove(personalEntity);
				} else {
					Log.i("personal_sticker", "failed remove personal sticker" + personalEntity.id);
				}
			}
		});

		ImageView icon  = convertView.findViewById(R.id.item_icon);
		TextView  title = convertView.findViewById(R.id.item_title);

		icon.setImageBitmap(personalEntity.image);
		title.setText(personalEntity.title);

		return convertView;
	}

	/**
	 * Delete personal sticker by id
	 *
	 * @param id
	 * @return
	 */
	private int deletePersonalSticker(long id) {
		PersonalSticker.PersonalStickerDbHelper dbHelper = new PersonalSticker.PersonalStickerDbHelper(getContext());
		SQLiteDatabase                          db       = dbHelper.getReadableDatabase();
		// Define 'where' part of query.
		String selection = PersonalSticker.PersonalStickerEntry._ID + " LIKE ?";
		// Specify arguments in placeholder order.
		String[] selectionArgs = {Long.toString(id)};

		// Issue SQL statement.
		return db.delete(PersonalSticker.PersonalStickerEntry.TABLE_NAME, selection, selectionArgs);
	}
}