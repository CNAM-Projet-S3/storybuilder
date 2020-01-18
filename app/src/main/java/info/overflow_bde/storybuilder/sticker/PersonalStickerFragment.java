package info.overflow_bde.storybuilder.sticker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

import info.overflow_bde.storybuilder.MainActivity;
import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.StickersListFragment;
import info.overflow_bde.storybuilder.adapter.PersonalAdapter;
import info.overflow_bde.storybuilder.entity.PersonalEntity;
import info.overflow_bde.storybuilder.sticker.fragments.PersonalFragment;
import info.overflow_bde.storybuilder.utils.DB.PersonalSticker;

public class PersonalStickerFragment extends Fragment {

    private View        view;
    private ListView    personalStickerList;
    private ProgressBar progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.personal_sticker_fragment, container, false);
        this.personalStickerList = view.findViewById(R.id.personal_sticker_list_fragment);
        this.progress = view.findViewById(R.id.personal_sticker_progress_fragment);
        this.progress.setVisibility(View.VISIBLE);

        PersonalAdapter personalAdapter = new PersonalAdapter(view.getContext(), this.getPersonalSticker());
        personalStickerList.setAdapter(personalAdapter);
        this.progress.setVisibility(View.GONE);


        //click on personal sticker item, hidden menu stickers and display selected interest point
        this.personalStickerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PersonalEntity       personalEntity       = (PersonalEntity) personalStickerList.getItemAtPosition(position);
                StickersListFragment stickersListFragment = (StickersListFragment) Objects.requireNonNull(getFragmentManager()).findFragmentByTag("stickers");
                Objects.requireNonNull(stickersListFragment).hide();
                ((MainActivity) Objects.requireNonNull(getActivity())).addFragment(new PersonalFragment(personalEntity.image), R.id.editor_content, personalEntity.title);
            }
        });

        return view;
    }

    /**
     * Fetch personal stickers from local database
     *
     * @return ArrayList<PersonalEntity>
     */
    private ArrayList<PersonalEntity> getPersonalSticker() {
        PersonalSticker.PersonalStickerDbHelper dbHelper = new PersonalSticker.PersonalStickerDbHelper(getContext());
        SQLiteDatabase                          db       = dbHelper.getReadableDatabase();

        // select all order by creation
        String sortOrder =
                PersonalSticker.PersonalStickerEntry.COLUMN_NAME_CREATED_AT + " DESC";

        Cursor cursor = db.query(
                PersonalSticker.PersonalStickerEntry.TABLE_NAME,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        ArrayList<PersonalEntity> personalEntities = new ArrayList<>();
        while (cursor.moveToNext()) {
            long   id    = cursor.getLong(cursor.getColumnIndexOrThrow(PersonalSticker.PersonalStickerEntry._ID));
            String image = cursor.getString(cursor.getColumnIndexOrThrow(PersonalSticker.PersonalStickerEntry.COLUMN_NAME_IMAGE));
            String date  = cursor.getString(cursor.getColumnIndexOrThrow(PersonalSticker.PersonalStickerEntry.COLUMN_NAME_CREATED_AT));

            personalEntities.add(new PersonalEntity(id, date, this.getBitmapFromBase64String(image)));
        }
        cursor.close();

        return personalEntities;
    }

    /**
     * Convert base 64 String to Bitmap
     *
     * @param base64Str
     * @return Bitmap
     * @throws IllegalArgumentException
     */
    public Bitmap getBitmapFromBase64String(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
