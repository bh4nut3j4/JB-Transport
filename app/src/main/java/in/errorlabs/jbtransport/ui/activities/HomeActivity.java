package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.activities.CollegeMap.CollegeMap;
import in.errorlabs.jbtransport.ui.constants.HomeConstants;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.Constants;
import in.errorlabs.jbtransport.utils.SharedPrefs;
import okhttp3.OkHttpClient;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,LoaderManager.LoaderCallbacks<Void>{
    public static final String TAG = "HomeActivity";
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    SharedPrefs sharedPrefs;
    private GoogleMap mMap;
    @BindView(R.id.homerouteslist_recyclerview)RecyclerView recyclerView;
    @BindView(R.id.layout1)LinearLayout linearLayout;
    @BindView(R.id.r1)RelativeLayout relativeLayout;
    @BindView(R.id.routenumber) TextView routeNumber;
    @BindView(R.id.startingpoint) TextView startingPoint;
    @BindView(R.id.endingpoint) TextView endingPoint;
    @BindView(R.id.viapoint) TextView viaPoint;
    @BindView(R.id.busumber_end) TextView busNumber;
    @BindView(R.id.departuretime) TextView departureTime;
    LoadToast loadToast;
    Connection connection;
    public static final int DETAILS_LOADER_ID = 11;
    public static final int COORDINATES_LOADER_ID = 12;
    ArrayList<LatLng> coordinatesarray_List = new ArrayList<>();

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
        sharedPrefs = new SharedPrefs(this);
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

        if (sharedPrefs.getAlreadySkipped()){
            menu.findItem(R.id.nav_all_routes).setVisible(false);
        }else {
            menu.findItem(R.id.nav_select_primary).setVisible(false);
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
            linearLayout.setVisibility(View.VISIBLE);
            relativeLayout.setVisibility(View.GONE);

            getSelectedRouteDetails();
        } else {
            relativeLayout.setVisibility(View.GONE);
            showError();
        }
    }

    public void getAllRoutes() {
        loadToast.show();
    }

    public void getSelectedRouteDetails() {
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Object> details = loaderManager.getLoader(DETAILS_LOADER_ID);
        if (details==null){
            loaderManager.initLoader(DETAILS_LOADER_ID,null,this);
        }else {
            loaderManager.restartLoader(DETAILS_LOADER_ID,null,this);
        }
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
    public void showDataError() {
        loadToast.error();
        Snackbar.make(relativeLayout, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).show();
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
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        if (id==DETAILS_LOADER_ID){
            return new AsyncTaskLoader<Void>(this) {
                @Override
                protected void onStartLoading(){
                    loadToast.show();
                    forceLoad();
                }
                @Override
                public Void loadInBackground() {
                    AndroidNetworking.post(Constants.RouteGetDetailsById)
                            .setOkHttpClient(okHttpClient)
                            .setPriority(Priority.HIGH)
                            .addBodyParameter(Constants.AppKey, String.valueOf(R.string.transportAppKey))
                            .addBodyParameter(Constants.RouteNumber,sharedPrefs.getSelectedRouteNumber())
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    if (response.length()>0){
                                        if (response.has(Constants.HomeRouteObjectName)){
                                            try {
                                                JSONArray routeArray = response.getJSONArray(Constants.HomeRouteObjectName);
                                                for (int i=0;i<=routeArray.length();i++){
                                                    JSONObject routeObject = routeArray.getJSONObject(i);
                                                    routeNumber.setText(routeObject.getString(HomeConstants.routeNumber));
                                                    startingPoint.setText(routeObject.getString(HomeConstants.startPoint));
                                                    endingPoint.setText(routeObject.getString(HomeConstants.endPoint));
                                                    viaPoint.setText(routeObject.getString(HomeConstants.viaPoint));
                                                    busNumber.setText(routeObject.getString(HomeConstants.busNumber));
                                                    departureTime.setText(routeObject.getString(HomeConstants.departureTime));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }else {
                                        showDataError();
                                    }
                                }
                                @Override
                                public void onError(ANError anError) {
                                    showDataError();
                                }
                            });
                    return null;
                }
            };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {
        int id = loader.getId();
        if (id==DETAILS_LOADER_ID){
            loadToast.success();
        }
    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {
    }
}
