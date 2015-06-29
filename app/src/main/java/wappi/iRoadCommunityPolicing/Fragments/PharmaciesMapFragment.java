package wappi.iRoadCommunityPolicing.Fragments;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wappi.iRoadCommunityPolicing.R;

import wappi.iRoadCommunityPolicing.IroadDatabase;
import wappi.iRoadCommunityPolicing.DatabaseModals.Pharmacy;
import wappi.iRoadCommunityPolicing.PopUpWindow;

/**
 * Created by Ilakoze on 4/10/2015.
 */
public class PharmaciesMapFragment extends Fragment  implements com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback {
    private static RelativeLayout contentView;
    private View rootView;
    private static final String TAG="MapDialogue";
    private GoogleMap map;
    private CameraPosition cameraPosition;
    private double latitude=-6.3023045;
    private double longitude=34.7821248;
    private String pharmacyName;
    final String ApiKey = "AIzaSyBY_NUwyi_rV63ZtBpJ7uRQ3qIU7hd_9Fo";
    public Toolbar toolbar;
    private MapFragment fragment;
    private FragmentManager myFragmentManager;
    private IroadDatabase db;
    private LatLng defaultLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (contentView != null) {
            ViewGroup parent = (ViewGroup) contentView.getParent();
            if (parent != null)
                parent.removeView(contentView);
        }
        try {
            contentView = (RelativeLayout)inflater.inflate(R.layout.fragment_map_pharmacies, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        db = new IroadDatabase(getActivity());

        return contentView;

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myFragmentManager = getChildFragmentManager();

        fragment = (MapFragment) myFragmentManager.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = MapFragment.newInstance();
            myFragmentManager.beginTransaction().replace(R.id.map, fragment).commit();
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        if (map == null) {
            map = fragment.getMap();
            map.setMyLocationEnabled(true);

            map.setInfoWindowAdapter(new PopUpWindow(getActivity().getLayoutInflater()));

            defaultLocation = new LatLng(latitude,longitude);

            CameraPosition defaultCameraPosition=CameraPosition.builder()
                    .target(defaultLocation)
                    .zoom(5)
                    .tilt(30)
                    .build();
            map.moveCamera(CameraUpdateFactory.newCameraPosition(defaultCameraPosition));

            Pharmacy pharmacy = new Pharmacy();
            pharmacy.setLatitude(-6.7714396);
            pharmacy.setLongitude(39.2486465);
            pharmacy.setPharmacy_name("Kijitonyama Police Station");
            pharmacy.setPharmacy_email("kijitonyama@policeforce.org");
            pharmacy.setPharmacy_phone_number("25571829913");


            Pharmacy pharmacy1 = new Pharmacy();
            pharmacy1.setLatitude(-6.787465);
            pharmacy1.setLongitude(39.2540055);
            pharmacy1.setPharmacy_name("Mwananyamala Hospital");
            pharmacy1.setPharmacy_email("mwanayamal@hosptal.com");
            pharmacy1.setPharmacy_phone_number("0712365262");


            LatLng location = new LatLng(pharmacy.getLatitude(),pharmacy.getLongitude());
            map.addMarker(new MarkerOptions()
                    .title(pharmacy.getPharmacy_name())
                    .snippet(pharmacy.getPharmacy_email()+","+pharmacy.getPharmacy_phone_number() + ","+pharmacy.getPharmacy_alt_phone())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_48dp))
                    .position(location));


            LatLng location2 = new LatLng(pharmacy1.getLatitude(),pharmacy1.getLongitude());
            map.addMarker(new MarkerOptions()
                    .title(pharmacy.getPharmacy_name())
                    .snippet(pharmacy1.getPharmacy_email()+","+pharmacy1.getPharmacy_phone_number() + ","+pharmacy1.getPharmacy_alt_phone())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_place_black_48dp))
                    .position(location));


            map.setOnMapLoadedCallback(this);

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"destroying fragment" );
        if (fragment != null) {

            Log.d(TAG, "destroying fragment permanently");
            myFragmentManager.beginTransaction().remove(fragment).commit();
        }
    }


    private Location getMyLocation() {
        // Get location from GPS if it's available
        LocationManager lm = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // Location wasn't found, check the next most accurate place for the current location
        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            // Finds a provider that matches the criteria
            String provider = lm.getBestProvider(criteria, true);
            // Use the provider to get the last known location
            myLocation = lm.getLastKnownLocation(provider);
        }

        return myLocation;
    }

    @Override
    public void onMapLoaded() {
        try {
            Location myLocation = getMyLocation();
            LatLng myLatLongPosition = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            cameraPosition = CameraPosition.builder()
                    .target(myLatLongPosition)
                    .zoom(15)
                    .tilt(46)
                    .build();
        }catch (NullPointerException e){
            cameraPosition = CameraPosition.builder()
                    .target(defaultLocation)
                    .zoom(6)
                    .tilt(46)
                    .build();
        }
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                5000, null);

    }
}
