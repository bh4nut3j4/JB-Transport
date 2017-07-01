package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import net.steamcrafted.loadtoast.LoadToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.activities.CollegeMap.CollegeMap;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.SharedPrefs;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    public static final String TAG = "HomeActivity";
    SharedPrefs sharedPrefs;
    private GoogleMap mMap;
    @BindView(R.id.homerouteslist_recyclerview)RecyclerView recyclerView;
    @BindView(R.id.layout1)LinearLayout linearLayout;
    @BindView(R.id.r1)
    RelativeLayout relativeLayout;
    LoadToast loadToast;
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        loadToast = new LoadToast(this);
        connection = new Connection(this);
        startMainActivity();

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        MenuItem allroutes = menu.getItem(R.id.nav_all_routes);
        MenuItem selectprimary = menu.getItem(R.id.nav_select_primary);
        if (sharedPrefs.getAlreadySkipped()){
            allroutes.setVisible(false);
        }else {
            selectprimary.setVisible(false);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(Gravity.START);
            }
        });
    }

    public void startMainActivity() {
        Bundle bundle = getIntent().getExtras();
        String check = bundle.getString(getString(R.string.IntentKey));
        assert check != null;
        if (check.equals(getString(R.string.SkipStatus))) {
            linearLayout.setVisibility(View.GONE);
            getAllRoutes();
        } else if (sharedPrefs.getSelectedRouteNumber() != null && sharedPrefs.getSelectedRouteFcmID() != null) {
            if (FirebaseInstanceId.getInstance().getToken() != null) {
                FirebaseMessaging.getInstance().subscribeToTopic(sharedPrefs.getSelectedRouteFcmID());
                Log.d("MSG", "topic");
            }
            relativeLayout.setVisibility(View.GONE);
            getSelectedRouteDetails();
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapfragment);
            mapFragment.getMapAsync(this);
        } else {
            relativeLayout.setVisibility(View.GONE);
            showError();
        }
    }

    public void getAllRoutes() {
        loadToast.show();
    }


    public void getSelectedRouteDetails() {

    }

    public void showError() {
        loadToast.error();
        Snackbar.make(relativeLayout, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connection.isInternet()) {
                    startMainActivity();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_all_routes) {

        } else if (id == R.id.nav_complaints) {

        } else if (id == R.id.nav_notifications) {

        } else if (id == R.id.nav_collegemap) {
            startActivity(new Intent(getApplicationContext(), CollegeMap.class));
        } else if (id == R.id.nav_aboutus) {

        } else if (id == R.id.nav_reportabug) {
            Intent i = new Intent(Intent.ACTION_SENDTO);
            String uriText = getString(R.string.mailto) + Uri.encode(getString(R.string.bhanuteja_r07email)) + getString(R.string.subjectemailqstn) +
                    Uri.encode(getString(R.string.reportingabug)) + getString(R.string.bodymail) + Uri.encode(getString(R.string.mailintro));
            Uri uri = Uri.parse(uriText);
            i.setData(uri);
            startActivity(Intent.createChooser(i, getString(R.string.sendemail)));
        } else if (id == R.id.nav_opensourcelicenses) {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle(String.format("%1$s", getString(R.string.opensource) + ":"));
            builder.setMessage(getResources().getText(R.string.licenses_text));
            builder.setPositiveButton("OK", null);
            AlertDialog welcomeAlert = builder.create();
            welcomeAlert.show();
            ((TextView) welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
