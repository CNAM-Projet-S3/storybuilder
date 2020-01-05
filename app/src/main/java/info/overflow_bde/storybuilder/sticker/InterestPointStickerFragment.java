package info.overflow_bde.storybuilder.sticker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import info.overflow_bde.storybuilder.LayerFragment;
import info.overflow_bde.storybuilder.MainActivity;
import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.StickersFragment;
import info.overflow_bde.storybuilder.adapter.InterestPointAdapter;
import info.overflow_bde.storybuilder.entity.InterestPointEntity;

public class InterestPointStickerFragment extends Fragment {

    private ListView interestPointList;
    private ProgressBar circleProgress;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.interest_point_sticker_fragment, container, false);
        this.interestPointList = view.findViewById(R.id.list_interest_point);
        this.circleProgress = view.findViewById(R.id.interest_point_progress);
        //display progress
        this.circleProgress.setVisibility(View.VISIBLE);
        //call api interest point
        this.fetchInterestPoints();

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

    /**
     * call api here to fetch interest points from position
     */
    private void fetchInterestPoints() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Create URL
                URL interestPointEndpoint = null;
                try {
                    interestPointEndpoint = new URL("https://places.cit.api.here.com/places/v1/discover/around?app_id=BtwXteauhryQRC1XteJR&app_code=Rwpnxvb-uv6B1ipbqcfXkQ&at=49.242300,4.061021%3Br%3D1");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                // Create connection
                try {
                    HttpsURLConnection myConnection = (HttpsURLConnection) interestPointEndpoint.openConnection();

                    if (myConnection.getResponseCode() == 200) {
                        //populate list view
                        Log.i("InterestPoint", "success fetch interest point");
                        InputStream responseBody = myConnection.getInputStream();
                        setInterestPointEntities(readJsonStream(responseBody));
                    } else {
                        Log.i("InterestPoint", "error fetch interest point");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * create interest point entity
     *
     * @param reader
     * @return InterestPointEntity
     * @throws IOException
     */
    private InterestPointEntity readInterestPoint(JsonReader reader) throws IOException {
        String title = null;
        String icon = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("title")) {
                title = reader.nextString();
            } else if (name.equals("icon")) {
                icon = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new InterestPointEntity(title, this.getBitmapFromURL(icon));
    }


    /**
     * fetch bitmap from url
     *
     * @param src
     * @return Bitmap | null
     */
    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    /**
     * populate list view in the main thread activity
     *
     * @param interestPointEntities
     */
    private void setInterestPointEntities(final ArrayList<InterestPointEntity> interestPointEntities) {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                InterestPointAdapter interestPointAdapter = new InterestPointAdapter(view.getContext(), interestPointEntities);
                interestPointList.setAdapter(interestPointAdapter);
                //remove progress
                circleProgress.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Parse interest point response
     *
     * @param in InputStream
     * @return ArrayList<InterestPointEntity>
     * @throws IOException
     */
    private ArrayList<InterestPointEntity> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        ArrayList<InterestPointEntity> interestPointEntities = new ArrayList<>();
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("results")) {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        name = reader.nextName();
                        if (name.equals("items")) {
                            interestPointEntities = readInterestPointsArray(reader);
                        } else {
                            reader.skipValue();
                        }
                    }

                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
        } finally {
            reader.close();
        }
        return interestPointEntities;
    }

    /**
     * cross eah interest points
     *
     * @param reader JsonReader
     * @return ArrayList<InterestPointEntity>
     * @throws IOException
     */
    private ArrayList<InterestPointEntity> readInterestPointsArray(JsonReader reader) throws IOException {
        ArrayList<InterestPointEntity> interestPointEntities = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            interestPointEntities.add(readInterestPoint(reader));
        }
        reader.endArray();
        return interestPointEntities;
    }
}
