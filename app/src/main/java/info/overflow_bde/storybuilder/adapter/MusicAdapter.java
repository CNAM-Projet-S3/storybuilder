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
import info.overflow_bde.storybuilder.entity.MusicEntity;

public class MusicAdapter extends ArrayAdapter<MusicEntity> {

    public MusicAdapter(Context context, ArrayList<MusicEntity> musicEntities) {
        super(context, 0, musicEntities);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MusicEntity entity = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_music, parent, false);
        }

        if (entity != null) {
            ImageView icon = convertView.findViewById(R.id.item_music_icon);
            TextView title = convertView.findViewById(R.id.item_music_title);
            TextView artist = convertView.findViewById(R.id.item_music_artist);

            if (entity.icon != null)
                icon.setImageBitmap(entity.icon);
            title.setText(entity.title);
            artist.setText(entity.artist);
        }

        return convertView;
    }
}