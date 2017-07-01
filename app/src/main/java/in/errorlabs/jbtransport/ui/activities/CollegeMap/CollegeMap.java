package in.errorlabs.jbtransport.ui.activities.CollegeMap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import in.errorlabs.jbtransport.R;

public class CollegeMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(17.331382, 78.297474), 20);
        mMap.animateCamera(cameraUpdate);
    }
}
