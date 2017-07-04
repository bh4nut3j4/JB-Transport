package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.FragMethodCallInterface;
import in.errorlabs.jbtransport.ui.activities.CollegeMap.CollegeMap;
import in.errorlabs.jbtransport.ui.adapters.AllRoutesAdapter;
import in.errorlabs.jbtransport.ui.constants.HomeConstants;
import in.errorlabs.jbtransport.ui.constants.RoutesSelectConstants;
import in.errorlabs.jbtransport.ui.fragments.HomeRouteFragment;
import in.errorlabs.jbtransport.ui.models.RouteSelectModel;
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
    AllRoutesAdapter adapter;
    List<RouteSelectModel> list = new ArrayList<>();
    @BindView(R.id.homerouteslist_recyclerview)RecyclerView recyclerView;
    @BindView(R.id.layout1)LinearLayout linearLayout;
    @BindView(R.id.r1)LinearLayout recyclerview_layout;
    @BindView(R.id.c1)LinearLayout notice_card_layout;
    @BindView(R.id.rootView)LinearLayout rootView;
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
    public static final int ALl_ROUTES_LOADER_ID = 13;
    public static final String DATA_BUNDLE = "bundle";
    FragMethodCallInterface methodCallInterface;


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
        String s = getIntent().getStringExtra("DetailsView");
        if (s!=null && s.length()>0){
            linearLayout.setVisibility(View.VISIBLE);
            recyclerview_layout.setVisibility(View.GONE);
            getSelectedRouteDetails(s);
            methodCallInterface = new HomeRouteFragment();
            HomeRouteFragment homeRouteFragment = new HomeRouteFragment();
            homeRouteFragment.setBusNumber(s);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.mapcontainer,homeRouteFragment).commit();
            //methodCallInterface.startMapActivity(s);
        }else {
            startMainActivity();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

        if (sharedPrefs.getAlreadySkipped()) {
            linearLayout.setVisibility(View.GONE);
            recyclerview_layout.setVisibility(View.VISIBLE);
            getAllRoutes();
        } else if (sharedPrefs.getRouteSelected()) {
            if (FirebaseInstanceId.getInstance().getToken() != null) {
                FirebaseMessaging.getInstance().subscribeToTopic(sharedPrefs.getSelectedRouteFcmID());
                Log.d("MSG", "topic");
            }
            linearLayout.setVisibility(View.VISIBLE);
            recyclerview_layout.setVisibility(View.GONE);
            getSelectedRouteDetails(sharedPrefs.getSelectedRouteNumber());
        } else {
            recyclerview_layout.setVisibility(View.GONE);
            linearLayout.setVisibility(View.GONE);
            showError();
        }
    }

    public void getAllRoutes() {
        loadToast.show();
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Object> details = loaderManager.getLoader(ALl_ROUTES_LOADER_ID);
        if (details==null){
            loaderManager.initLoader(ALl_ROUTES_LOADER_ID,null,this);
        }else {
            loaderManager.restartLoader(ALl_ROUTES_LOADER_ID,null,this);
        }
    }

    public void getSelectedRouteDetails(String routeNumber) {
        Bundle bundle = new Bundle();
        bundle.putString(DATA_BUNDLE,routeNumber);
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<Object> details = loaderManager.getLoader(DETAILS_LOADER_ID);
        if (details==null){
            loaderManager.initLoader(DETAILS_LOADER_ID,bundle,this);
        }else {
            loaderManager.restartLoader(DETAILS_LOADER_ID,bundle,this);
        }
    }

    public void showError() {
        loadToast.error();
        Snackbar.make(rootView, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
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
        Snackbar.make(rootView, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).show();
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
            getAllRoutes();

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
    public Loader<Void> onCreateLoader(int id, final Bundle args) {
        if (id==DETAILS_LOADER_ID){
            return new AsyncTaskLoader<Void>(this) {
                String RouteNumber;
                @Override
                protected void onStartLoading(){
                    if (args!=null){
                        RouteNumber = args.getString(DATA_BUNDLE);
                        loadToast.show();
                        forceLoad();
                    }
                }
                @Override
                public Void loadInBackground() {
                    fetchData(RouteNumber);
                    return null;
                }
            };
        }else if (id==ALl_ROUTES_LOADER_ID){
            return new AsyncTaskLoader<Void>(this) {
                protected void onStartLoading(){
                    loadToast.show();
                    forceLoad();
                }
                @Override
                public Void loadInBackground() {
                    AndroidNetworking.post(Constants.RouteSelectDataUrl)
                            .setOkHttpClient(okHttpClient)
                            .setPriority(Priority.HIGH)
                            .addBodyParameter(Constants.AppKey, String.valueOf(R.string.transportAppKey))
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    loadToast.success();
                                    Log.d("TAG",response.toString());
                                    if (response.length()>0 || response.has(getString(R.string.AuthError)) || response.has(getString(R.string.ErrorSelecting))){
                                        try {
                                            JSONArray jsonArray = response.getJSONArray(getString(R.string.RoutesSelectJsonArrayName));
                                            if (jsonArray.length()>0){
                                                int length = jsonArray.length();
                                                list.clear();
                                                for (int i=0;i<=length;i++) {
                                                    try {
                                                        JSONObject object = jsonArray.getJSONObject(i);
                                                        RouteSelectModel model = new RouteSelectModel();
                                                        model.setRouteNumber(object.getString(RoutesSelectConstants.routeNumber));
                                                        model.setFcmRouteID(object.getString(RoutesSelectConstants.fcmRouteId));
                                                        model.setRouteFullPath(object.getString(RoutesSelectConstants.fullRoute));
                                                        model.setRouteStartPoint(object.getString(RoutesSelectConstants.startPoint));
                                                        model.setRouteEndPoint(object.getString(RoutesSelectConstants.endPoint));
                                                        model.setRouteViaPoint(object.getString(RoutesSelectConstants.viaPoint));
                                                        list.add(model);
                                                        Log.d("TAG", list.toString());
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                adapter = new AllRoutesAdapter(list,HomeActivity.this);
                                                recyclerView.setAdapter(adapter);
                                            }else {
                                                showError();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            showError();
                                        }
                                    }else {
                                        showError();
                                    }
                                }
                                @Override
                                public void onError(ANError anError) {
                                    loadToast.error();
                                    showError();
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

    public void fetchData(String RNumber){
        AndroidNetworking.post(Constants.RouteGetDetailsById)
                .setOkHttpClient(okHttpClient)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.AppKey, String.valueOf(R.string.transportAppKey))
                .addBodyParameter(Constants.RouteNumber,RNumber)
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
                            }else {
                                showDataError();
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
    }
}
