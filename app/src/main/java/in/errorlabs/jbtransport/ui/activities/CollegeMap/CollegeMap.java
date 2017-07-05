package in.errorlabs.jbtransport.ui.activities.CollegeMap;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;

import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.fragments.HomeMapFragment;
import in.errorlabs.jbtransport.ui.fragments.HomeRouteFragment;

public class CollegeMap extends AppCompatActivity {

    private GoogleMap mMap;
    String route;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_map);
        try{
            Bundle bundle= getIntent().getExtras();
            route= bundle.getString("Gmaps");
        }catch (Exception e){
            e.printStackTrace();
        }
        if (route!=null && route.length()>0){
            HomeMapFragment homeMapFragment = new HomeMapFragment();
            homeMapFragment.setRouteNumber(route);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.college_map_container,homeMapFragment).commit();
        }else {
            HomeRouteFragment homeRouteFragment = new HomeRouteFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.college_map_container,homeRouteFragment).commit();
        }
    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//        mMap.setMyLocationEnabled(true);
//
//    }
}
