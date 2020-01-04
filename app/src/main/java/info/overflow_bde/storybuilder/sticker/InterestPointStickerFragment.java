package info.overflow_bde.storybuilder.sticker;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

import info.overflow_bde.storybuilder.LayerFragment;
import info.overflow_bde.storybuilder.MainActivity;
import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.StickersFragment;
import info.overflow_bde.storybuilder.adapter.InterestPointAdapter;
import info.overflow_bde.storybuilder.entity.InterestPointEntity;

public class InterestPointStickerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.interest_point_sticker_fragment, container, false);
        final ListView interestPointList = view.findViewById(R.id.list_interest_point);

        //populate list view
        ArrayList<InterestPointEntity> interestPointEntities = this.getInterestPoint();
        InterestPointAdapter interestPointAdapter = new InterestPointAdapter(view.getContext(), interestPointEntities);
        interestPointList.setAdapter(interestPointAdapter);


        //click on interest point item, hidden menu stickers and display selected interest point
        interestPointList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InterestPointEntity o = (InterestPointEntity) interestPointList.getItemAtPosition(position);
                StickersFragment stickersFragment = (StickersFragment) Objects.requireNonNull(getFragmentManager()).findFragmentByTag("stickers");
                Objects.requireNonNull(stickersFragment).hidden();
                ((MainActivity) Objects.requireNonNull(getActivity())).showFragment(new LayerFragment(o.icon, o.title), R.id.editor_content, o.title);
            }
        });

        return view;
    }

    private ArrayList<InterestPointEntity> getInterestPoint() {
        //@TODO call api
        ArrayList<InterestPointEntity> interestPointEntities = new ArrayList<>();
        interestPointEntities.add(new InterestPointEntity("Test 1 ", BitmapFactory.decodeResource(getResources(),
                R.drawable.main)));
        interestPointEntities.add(new InterestPointEntity("Test 2 ", BitmapFactory.decodeResource(getResources(),
                R.drawable.main)));
        return interestPointEntities;
    }
}
