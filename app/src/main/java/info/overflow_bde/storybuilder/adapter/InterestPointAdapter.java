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
import info.overflow_bde.storybuilder.entity.InterestPointEntity;

public class InterestPointAdapter extends ArrayAdapter<InterestPointEntity> {

    public InterestPointAdapter(Context context, ArrayList<InterestPointEntity> interestPointEntities) {

        super(context, 0, interestPointEntities);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        InterestPointEntity interestPointEntity = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list, parent, false);
        }

        ImageView icon = convertView.findViewById(R.id.item_icon);
        TextView title = convertView.findViewById(R.id.item_title);

        icon.setImageBitmap(interestPointEntity.icon);
        title.setText(interestPointEntity.title);

        return convertView;
    }
}