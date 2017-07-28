package in.errorlabs.jbtransport.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.activities.Splash;
import in.errorlabs.jbtransport.ui.constants.HomeConstants;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.Constants;
import in.errorlabs.jbtransport.utils.SharedPrefs;
import okhttp3.OkHttpClient;

import static in.errorlabs.jbtransport.ui.activities.HomeActivity.DETAILS_LOADER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeDataFragment extends Fragment implements LoaderManager.LoaderCallbacks<Void> {

    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    @BindView(R.id.routenumber)
    TextView route_Number;
    @BindView(R.id.startingpoint)
    TextView startingPoint;
    @BindView(R.id.endingpoint)
    TextView endingPoint;
    @BindView(R.id.viapoint)
    TextView viaPoint;
    @BindView(R.id.busumber_end)
    TextView busNumber;
    @BindView(R.id.departuretime)
    TextView departureTime;
    @BindView(R.id.main_last_updated)
    TextView lastUpdatedMain;
    @BindView(R.id.data_l1)
    LinearLayout rootView;
    @BindView(R.id.busimg)
    ImageView bus_img;
    public static final String CONSTANT_ROUTENUMBER = "Rnumber";
    public static final String CONSTANT_STARTING = "Straring";
    public static final String CONSTANT_ENDING = "ending";
    public static final String CONSTANT_VIA = "via";
    public static final String CONSTANT_BUSNUMBER = "bus";
    public static final String CONSTANT_DEPARTURETIME = "deptime";
    public static final String CONSTANT_LASTUPDATED = "lastupdate";
    LoadToast loadToast;
    Connection connection;
    public String routeNumber;
    SharedPrefs sharedPrefs;

    public HomeDataFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_data, container, false);
        ButterKnife.bind(this, rootView);
        sharedPrefs = new SharedPrefs(getContext());
        loadToast = new LoadToast(getContext());
        connection = new Connection(getContext());
        if (savedInstanceState == null) {
            getData();
        } else {
            try {
                route_Number.setText(savedInstanceState.getString(CONSTANT_ROUTENUMBER));
                startingPoint.setText(savedInstanceState.getString(CONSTANT_STARTING));
                endingPoint.setText(savedInstanceState.getString(CONSTANT_ENDING));
                viaPoint.setText(savedInstanceState.getString(CONSTANT_VIA));
                busNumber.setText(savedInstanceState.getString(CONSTANT_BUSNUMBER));
                departureTime.setText(savedInstanceState.getString(CONSTANT_DEPARTURETIME));
                lastUpdatedMain.setText(savedInstanceState.getString(CONSTANT_LASTUPDATED));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rootView;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public void getData() {
        LoaderManager loaderManager = getLoaderManager();
        Loader<Object> details = loaderManager.getLoader(DETAILS_LOADER_ID);
        if (details == null) {
            loaderManager.initLoader(DETAILS_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(DETAILS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        if (id == DETAILS_LOADER_ID) {
            return new AsyncTaskLoader<Void>(getContext()) {
                String RouteNumber;

                @Override
                protected void onStartLoading() {
                    if (routeNumber != null && routeNumber.length() > 0) {
                        loadToast.show();
                        RouteNumber = routeNumber;
                        forceLoad();
                    }
                }
                @Override
                public Void loadInBackground() {
                    fetchData(RouteNumber);
                    return null;
                }
            };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {
        int id = loader.getId();
        if (id == DETAILS_LOADER_ID) {
        }
    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {
    }

    public void fetchData(String RNumber) {
        AndroidNetworking.post(Constants.RouteGetDetailsById)
                .setOkHttpClient(okHttpClient)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.AppKey, getString(R.string.transportAppKey))
                .addBodyParameter(Constants.RouteNumber, RNumber)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            if (response.has(Constants.HomeRouteObjectName)) {
                                try {
                                    String wayTo = response.getString(Constants.WayTO);
                                    if (wayTo.equals("0")){
                                        bus_img.setImageResource(R.drawable.busright);
                                    }
                                    JSONArray routeArray = response.getJSONArray(Constants.HomeRouteObjectName);
                                    for (int i = 0; i <= routeArray.length(); i++) {
                                        JSONObject routeObject = routeArray.getJSONObject(i);
                                        String available = routeObject.getString(HomeConstants.available);
                                        if (available.equals("0")) {
                                            Snackbar.make(rootView, getString(R.string.routenotavailable), Snackbar.LENGTH_INDEFINITE).show();
                                            route_Number.setText(routeObject.getString(HomeConstants.routeNumber));
                                            startingPoint.setText(routeObject.getString(HomeConstants.startPoint));
                                            endingPoint.setText(routeObject.getString(HomeConstants.endPoint));
                                            viaPoint.setText(routeObject.getString(HomeConstants.viaPoint));
                                            busNumber.setText(getString(R.string.checknoticeboard));
                                            departureTime.setText(routeObject.getString(HomeConstants.departureTime));
                                            lastUpdatedMain.setText(routeObject.getString(HomeConstants.lastUpdatedTime));
                                        } else {
                                            route_Number.setText(routeObject.getString(HomeConstants.routeNumber));
                                            startingPoint.setText(routeObject.getString(HomeConstants.startPoint));
                                            endingPoint.setText(routeObject.getString(HomeConstants.endPoint));
                                            viaPoint.setText(routeObject.getString(HomeConstants.viaPoint));
                                            busNumber.setText(routeObject.getString(HomeConstants.busNumber));
                                            departureTime.setText(routeObject.getString(HomeConstants.departureTime));
                                            lastUpdatedMain.setText(routeObject.getString(HomeConstants.lastUpdatedTime));
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                loadToast.success();
                            } else if (response.has(Constants.ErrorSelecting)) {
                                if (sharedPrefs.getSelectedRouteFcmID() == null || sharedPrefs.getSelectedRouteFcmID().equals("")) {
                                    sharedPrefs.setSelectedRouteFcmID(null);
                                    sharedPrefs.setRouteSelectedAsFalse();
                                    sharedPrefs.setSelectedRouteNumber(null);
                                    Intent intent = new Intent(getContext(), Splash.class);
                                    startActivity(intent);
//                                    startActivity(new Intent(getContext(), Splash.class));
                                    ((Activity) getContext()).finish();
                                }

                            } else {
                                showDataError();
                            }
                        } else {
                            showDataError();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        showError();
                    }
                });
    }

    public void showError() {
        loadToast.error();
        Snackbar.make(rootView, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connection.isInternet()) {
                    getData();
                }
            }
        }).show();
    }

    public void showDataError() {
        loadToast.error();
        Snackbar.make(rootView, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(CONSTANT_ROUTENUMBER, route_Number.getText().toString());
        savedInstanceState.putString(CONSTANT_STARTING, startingPoint.getText().toString());
        savedInstanceState.putString(CONSTANT_ENDING, endingPoint.getText().toString());
        savedInstanceState.putString(CONSTANT_VIA, viaPoint.getText().toString());
        savedInstanceState.putString(CONSTANT_BUSNUMBER, route_Number.getText().toString());
        savedInstanceState.putString(CONSTANT_DEPARTURETIME, busNumber.getText().toString());
        savedInstanceState.putString(CONSTANT_LASTUPDATED, lastUpdatedMain.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            route_Number.setText(savedInstanceState.getString(CONSTANT_ROUTENUMBER));
            startingPoint.setText(savedInstanceState.getString(CONSTANT_STARTING));
            endingPoint.setText(savedInstanceState.getString(CONSTANT_ENDING));
            viaPoint.setText(savedInstanceState.getString(CONSTANT_VIA));
            busNumber.setText(savedInstanceState.getString(CONSTANT_BUSNUMBER));
            departureTime.setText(savedInstanceState.getString(CONSTANT_DEPARTURETIME));
            lastUpdatedMain.setText(savedInstanceState.getString(CONSTANT_LASTUPDATED));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
