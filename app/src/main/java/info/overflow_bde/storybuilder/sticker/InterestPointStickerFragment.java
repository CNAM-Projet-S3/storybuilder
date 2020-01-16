package info.overflow_bde.storybuilder.sticker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import androidx.core.app.ActivityCompat;
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

import info.overflow_bde.storybuilder.sticker.fragments.InterestPointFragment;
import info.overflow_bde.storybuilder.MainActivity;
import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.StickersListFragment;
import info.overflow_bde.storybuilder.adapter.InterestPointAdapter;
import info.overflow_bde.storybuilder.entity.InterestPointEntity;

import static androidx.core.content.PermissionChecker.checkSelfPermission;

public class InterestPointStickerFragment extends Fragment {

    private ListView        interestPointList;
    private ProgressBar     circleProgress;
    private LocationManager locationManager;
    private View            view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        this.locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        this.view = inflater.inflate(R.layout.interest_point_sticker_fragment, container, false);
        this.interestPointList = view.findViewById(R.id.list_interest_point);
        this.circleProgress = view.findViewById(R.id.interest_point_progress);
        this.circleProgress.setVisibility(View.GONE);


        if (this.checkPermissions()) {
            //init location
            this.initLocation();
        } else {
            //request permission
            this.requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 8);
        }

        //click on interest point item, hidden menu stickers and display selected interest point
        interestPointList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InterestPointEntity o                = (InterestPointEntity) interestPointList.getItemAtPosition(position);
                StickersListFragment stickersListFragment = (StickersListFragment) Objects.requireNonNull(getFragmentManager()).findFragmentByTag("stickers");
                Objects.requireNonNull(stickersListFragment).hide();
                ((MainActivity) Objects.requireNonNull(getActivity())).addFragment(new InterestPointFragment(o.icon, o.title), R.id.editor_content, o.title);
            }
        });

        return view;
    }

    /**
     * call api here to fetch interest points from position
     */
    private void fetchInterestPoints(final Location location) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Create URL
                URL interestPointEndpoint = null;
                try {
                    interestPointEndpoint = new URL("https://places.cit.api.here.com/places/v1/discover/around?app_id=BtwXteauhryQRC1XteJR&app_code=Rwpnxvb-uv6B1ipbqcfXkQ&at=" + location.getLatitude() + "," + location.getLongitude());
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
        String icon  = null;

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
            URL               url        = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input    = connection.getInputStream();
            Bitmap      myBitmap = BitmapFactory.decodeStream(input);
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
        Objects.requireNonNull(this.getActivity()).runOnUiThread(new Runnable() {
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
        JsonReader                     reader                = new JsonReader(new InputStreamReader(in, "UTF-8"));
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

    /**
     * check if user accept to share a location, i ask here else if it has hem get location
     */
    private boolean checkPermissions() {
        return checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.initLocation();
        }
    }


    /**
     * get current location and when they have fetch interest points
     */
    @SuppressLint("MissingPermission")
    private void initLocation() {
        //display progress
        this.circleProgress.setVisibility(View.VISIBLE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        locationManager.requestSingleUpdate(criteria, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Location Changes", location.toString());
                //call api interest point
                fetchInterestPoints(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Status Changed", String.valueOf(status));
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Provider Enabled", provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Provider Disabled", provider);
            }
        }, null);
    }
}
