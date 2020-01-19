package info.overflow_bde.storybuilder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.entity.PersonalEntity;

public class PersonalAdapter extends ArrayAdapter<PersonalEntity> {
	public PersonalAdapter(Context context, ArrayList<PersonalEntity> personalEntities) {

		super(context, 0, personalEntities);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		PersonalEntity personalEntity = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
		}

		ImageView icon  = convertView.findViewById(R.id.item_icon);
		TextView  title = convertView.findViewById(R.id.item_title);

		icon.setImageBitmap(personalEntity.image);
		title.setText(personalEntity.title);

		return convertView;
	}
}