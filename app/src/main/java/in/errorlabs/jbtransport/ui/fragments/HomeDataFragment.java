package in.errorlabs.jbtransport.ui.fragments;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import in.errorlabs.jbtransport.ui.constants.HomeConstants;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.Constants;
import okhttp3.OkHttpClient;

import static in.errorlabs.jbtransport.ui.activities.HomeActivity.DETAILS_LOADER_ID;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeDataFragment extends Fragment implements LoaderManager.LoaderCallbacks<Void>{

    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    @BindView(R.id.routenumber) TextView route_Number;
    @BindView(R.id.startingpoint) TextView startingPoint;
    @BindView(R.id.endingpoint) TextView endingPoint;
    @BindView(R.id.viapoint) TextView viaPoint;
    @BindView(R.id.busumber_end) TextView busNumber;
    @BindView(R.id.departuretime) TextView departureTime;
    @BindView(R.id.main_last_updated) TextView lastUpdatedMain;
    @BindView(R.id.notice_card_view) CardView notice_card_view;
    @BindView(R.id.noticebard_text) TextView notice_text;
    @BindView(R.id.notice_last_updated_text) TextView notice_last_updated_text;
    @BindView(R.id.data_l1) LinearLayout rootView;
    LoadToast loadToast;
    Connection connection;
    public String routeNumber;

    public HomeDataFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home_data, container, false);
        ButterKnife.bind(this,rootView);
        loadToast = new LoadToast(getContext());
        getData();
        return rootView;
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public void getData(){
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
        if (id==DETAILS_LOADER_ID){
            return new AsyncTaskLoader<Void>(getContext()) {
                String RouteNumber;
                @Override
                protected void onStartLoading(){
                    loadToast.show();
                    if (routeNumber!=null && routeNumber.length()>0){
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
        }return null;
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
                        loadToast.success();
                        if (response.length()>0){
                            if (response.has(Constants.HomeRouteObjectName)){
                                try {
                                    JSONArray routeArray = response.getJSONArray(Constants.HomeRouteObjectName);
                                    for (int i=0;i<=routeArray.length();i++){
                                        JSONObject routeObject = routeArray.getJSONObject(i);
                                        route_Number.setText(routeObject.getString(HomeConstants.routeNumber));
                                        startingPoint.setText(routeObject.getString(HomeConstants.startPoint));
                                        endingPoint.setText(routeObject.getString(HomeConstants.endPoint));
                                        viaPoint.setText(routeObject.getString(HomeConstants.viaPoint));
                                        busNumber.setText(routeObject.getString(HomeConstants.busNumber));
                                        departureTime.setText(routeObject.getString(HomeConstants.departureTime));
                                        lastUpdatedMain.setText(routeObject.getString(HomeConstants.lastUpdatedTime));
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

    public void showError() {
        loadToast.error();
        Snackbar.make(rootView, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connection.isInternet()) {
                    getData();
                }
            }
        });
    }
    public void showDataError() {
        loadToast.error();
        Snackbar.make(rootView, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).show();
    }
}