package in.errorlabs.jbtransport.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.fragments.HomeDataFragment;
import in.errorlabs.jbtransport.ui.fragments.HomeMapFragment;

public class RouteFullDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_full_details);
        Bundle bundle = getIntent().getExtras();
        String number = bundle.getString("DetailsView");
        if (number!=null && number.length()>0){
            HomeDataFragment homeDataFragment = new HomeDataFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            homeDataFragment.setRouteNumber(number);
            fragmentManager.beginTransaction().add(R.id.main_data_fragment,homeDataFragment).commit();
            HomeMapFragment homeMapFragment = new HomeMapFragment();
            homeMapFragment.setRouteNumber(number);
            fragmentManager.beginTransaction().add(R.id.main_map_fragment,homeMapFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slidebackin, R.anim.slidebackout);
    }
}
