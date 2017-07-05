package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import net.steamcrafted.loadtoast.LoadToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.activities.CollegeMap.CollegeMap;
import in.errorlabs.jbtransport.ui.fragments.HomeDataFragment;
import in.errorlabs.jbtransport.ui.fragments.HomeMapFragment;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.SharedPrefs;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "HomeActivity";
    SharedPrefs sharedPrefs;
    LoadToast loadToast;
    Connection connection;
    public static final int DETAILS_LOADER_ID = 11;
    public static final int COORDINATES_LOADER_ID = 12;
    public static final int ALl_ROUTES_LOADER_ID = 13;
    public static final String DATA_BUNDLE = "bundle";
    public static final String LIST_CONSTANT = "list";
    public static final String CONSTANT_ROUTENUMBER = "list";
    public static final String CONSTANT_STARTING = "list";
    public static final String CONSTANT_ENDING = "list";
    public static final String CONSTANT_VIA = "list";
    public static final String CONSTANT_BUSNUMBER = "list";
    public static final String CONSTANT_DEPARTURETIME = "list";
    public static final String CONSTANT_LASTUPDATED = "list";
    NavigationView navigationView;
    Menu menu;
    @BindView(R.id.relative_lay)RelativeLayout relativeLayout;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        sharedPrefs = new SharedPrefs(this);
        loadToast = new LoadToast(this);
        connection = new Connection(this);
        startMainActivity();


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();

        if (sharedPrefs.getAlreadySkipped()){
            menu.findItem(R.id.nav_all_routes).setVisible(false);
        }else {
            menu.findItem(R.id.nav_select_primary).setVisible(false);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startMainActivity();
            }
        });
    }

    public void startMainActivity() {
        String s = getIntent().getStringExtra("DetailsView");
        if (s != null && s.length() > 0) {
            getSelectedRouteDetails(s);
        } else if (sharedPrefs.getRouteSelected()) {
            if (FirebaseInstanceId.getInstance().getToken() != null) {
                FirebaseMessaging.getInstance().subscribeToTopic(sharedPrefs.getSelectedRouteFcmID());
                Log.d("MSG", "topic");
            }
            getSelectedRouteDetails(sharedPrefs.getSelectedRouteNumber());
        } else {
            showError();
        }
    }


    public void getSelectedRouteDetails(String routeNumber) {
        HomeDataFragment homeDataFragment = new HomeDataFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        homeDataFragment.setRouteNumber(routeNumber);
        fragmentManager.beginTransaction().add(R.id.main_data_fragment,homeDataFragment).commit();

        HomeMapFragment homeMapFragment = new HomeMapFragment();
        homeDataFragment.setRouteNumber(routeNumber);
        fragmentManager.beginTransaction().add(R.id.main_map_fragment,homeMapFragment).commit();
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
            finish();
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

//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_all_routes) {
            menu.findItem(R.id.nav_all_routes).setVisible(false);
            menu.findItem(R.id.nav_back_to_primary).setVisible(true);
        } else if (id == R.id.nav_back_to_primary) {

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
