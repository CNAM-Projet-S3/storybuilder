package info.overflow_bde.storybuilder.sticker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import info.overflow_bde.storybuilder.MainActivity;
import info.overflow_bde.storybuilder.R;
import info.overflow_bde.storybuilder.StickersListFragment;
import info.overflow_bde.storybuilder.adapter.InterestPointAdapter;
import info.overflow_bde.storybuilder.entity.InterestPointEntity;
import info.overflow_bde.storybuilder.sticker.fragments.InterestPointFragment;
import info.overflow_bde.storybuilder.utils.HTTPUtils;
import info.overflow_bde.storybuilder.utils.SimpleCallback;

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
        Map<String, String> map = new HashMap<>();
        map.put("Longitude", location.getLongitude() + "");
        map.put("Latitude", location.getLatitude() + "");

        HTTPUtils.executeHttpRequest(HTTPUtils.getHostname("here"), map, new SimpleCallback() {
            @Override
            public void callback(String response) throws JSONException {
                ArrayList<InterestPointEntity> entities = new ArrayList<>();

                JSONObject resp = new JSONObject(response);
                JSONArray arr = resp.getJSONArray("items");
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject currObj = arr.getJSONObject(i);

                    Bitmap bm = HTTPUtils.getBitmapFromURL(currObj.getString("icon"));
                    entities.add(new InterestPointEntity(currObj.getString("title"), bm));
                }

                setInterestPointEntities(entities);
            }
        });
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
