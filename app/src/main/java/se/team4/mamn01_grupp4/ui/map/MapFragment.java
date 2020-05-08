package se.team4.mamn01_grupp4.ui.map;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.HashMap;
import java.util.Objects;

import se.team4.mamn01_grupp4.MainActivity;
import se.team4.mamn01_grupp4.Poi;
import se.team4.mamn01_grupp4.PoiDb;
import se.team4.mamn01_grupp4.R;
import se.team4.mamn01_grupp4.env.Logger;


public class MapFragment extends Fragment implements OnMapReadyCallback, LocationListener {


    private static final Logger LOGGER = new Logger();

    PoiDb poiDb;

    private View root;

    private LinearLayout bottomSheet;
    private BottomSheetBehavior sheetBehavior;
    private TextView scrollHeader;
    private TextView scrollDesc;
    private GoogleMap mMap;
    private LocationManager locationManager;
    LatLng myLatLng;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_map, container, false);

        poiDb = ((MainActivity) getActivity()).getPoiDb();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        bottomSheet = root.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        scrollHeader = root.findViewById(R.id.scroll_header);
        scrollDesc = root.findViewById(R.id.scroll_text);

        return root;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for(Poi marker : PoiDb.getDb().values()){
            mMap.addMarker(new MarkerOptions().position(marker.location).title(marker.name));
        }

        mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(55.70584, 13.19321) , 10) );

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 255);
        }

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerTitle = marker.getTitle();
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                scrollHeader.setText(markerTitle);
                scrollDesc.setText(Objects.requireNonNull(PoiDb.getDb().get(markerTitle)).description);
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            myLatLng = new LatLng(savedInstanceState.getDouble("myLat"), savedInstanceState.getDouble("myLong"));

            //Restore the fragment's state here
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 13);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}
