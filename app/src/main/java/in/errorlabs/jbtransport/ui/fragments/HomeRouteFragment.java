package in.errorlabs.jbtransport.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import in.errorlabs.jbtransport.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeRouteFragment extends Fragment implements OnMapReadyCallback {

    FragmentManager fragmentManager;
    public HomeRouteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_route, container, false);
        fragmentManager = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap = googleMap;
        LatLng jbiet_entrance = new LatLng(17.331382, 78.297474);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.addMarker(new MarkerOptions().position(jbiet_entrance).title(getString(R.string.JBIET_Entrance)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330222, 78.297701)).title(getString(R.string.mainblock)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330406, 78.297228)).title(getString(R.string.frstyrblck)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.331039, 78.297505)).title(getString(R.string.basktbal)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.331188, 78.297355)).title(getString(R.string.sae)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330632, 78.297685)).title(getString(R.string.cokehub)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.329808, 78.300068)).title(getString(R.string.cricktground)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.331444, 78.297610)).title(getString(R.string.bank)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330048, 78.297193)).title(getString(R.string.cafe)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.331339, 78.297978)).title(getString(R.string.cc)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330186, 78.297791)).title(getString(R.string.mnr)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330877, 78.297867)).title(getString(R.string.prkn1)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330845, 78.297234)).title(getString(R.string.prkn2)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330455, 78.298562)).title(getString(R.string.cvlece)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330284, 78.298522)).title(getString(R.string.eee)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330134, 78.298629)).title(getString(R.string.cvl)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330788, 78.297339)).title(getString(R.string.mech)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330194, 78.298081)).title(getString(R.string.cse)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330089, 78.298318)).title(getString(R.string.ece)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.329968, 78.297962)).title(getString(R.string.it)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.329865, 78.298197)).title(getString(R.string.ecm)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330789, 78.297375)).title(getString(R.string.transprtoffce)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330739, 78.297404)).title(getString(R.string.stationery)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330040, 78.297923)).title(getString(R.string.sac)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.329821, 78.298285)).title(getString(R.string.sprts)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330316, 78.297852)).title(getString(R.string.placmnt)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330096, 78.297708)).title(getString(R.string.admin)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(17.330657, 78.297293)).title(getString(R.string.mba)));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(17.331382, 78.297474))
                .zoom(19)
                .bearing(180)
                .tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
