package se.team4.mamn01_grupp4.ui.map;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.pm.PackageManager;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.HashMap;

import se.team4.mamn01_grupp4.R;


public class MapFragment extends Fragment implements OnMapReadyCallback{

    private Poi[] poiCoordinates = {
            new Poi(55.70584, 13.19321, "Minplats1", "src", "Beskrivning1"),
            new Poi(55.70584, 13.21, "Minplats2", "ställe", "beskrivning2")
    };

    private HashMap<String, Poi> poiMap = new HashMap<>();

    private View root;

    private LinearLayout bottomSheet;
    private BottomSheetBehavior sheetBehavior;
    private TextView scrollHeader;
    private TextView scrollDesc;
    private GoogleMap mMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_map, container, false);

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

        for(Poi marker : poiCoordinates){
            mMap.addMarker(new MarkerOptions().position(marker.location).title(marker.name));
            poiMap.put(marker.name, marker);
        }
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 255);
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String markerTitle = marker.getTitle();
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                scrollHeader.setText(markerTitle);
                scrollDesc.setText(poiMap.get(markerTitle).description);
                return true;
            }
        });
    }

    private class Poi{
        public LatLng location;
        public String name;
        public String imgSrc;
        public String description;

        public Poi(double v, double v1, String name, String src, String description){
            this.location = new LatLng(v, v1);
            this.name = name;
            this.description = description;
            this.imgSrc = src;
        }
    }
}