package in.errorlabs.jbtransport.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;

import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.fragments.HomeMapFragment;
import in.errorlabs.jbtransport.ui.fragments.CollegeMapFragment;

public class MapViewActivity extends AppCompatActivity {

    private GoogleMap mMap;
    String route,routename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_map);
        try{
            Bundle bundle= getIntent().getExtras();
            route= bundle.getString("Gmaps");
            routename = bundle.getString("Route");
            if(routename!=null || !routename.equals(" ")){
                getSupportActionBar().setTitle(routename);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (route!=null && route.length()>0){
            HomeMapFragment homeMapFragment = new HomeMapFragment();
            homeMapFragment.setRouteNumber(route);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.college_map_container,homeMapFragment).commit();
        }else {
            CollegeMapFragment collegeMapFragment = new CollegeMapFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.college_map_container, collegeMapFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_back_up_in, R.anim.push_back_up_out);
    }
}
