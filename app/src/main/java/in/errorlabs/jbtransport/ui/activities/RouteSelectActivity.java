package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

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
import in.errorlabs.jbtransport.ui.adapters.RouteSelectAdapter;
import in.errorlabs.jbtransport.ui.constants.RoutesSelectConstants;
import in.errorlabs.jbtransport.ui.models.RouteSelectModel;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.Constants;
import in.errorlabs.jbtransport.utils.SharedPrefs;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.OkHttpClient;

public class RouteSelectActivity extends AppCompatActivity {
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    @BindView(R.id.routeselect_recyclerview)RecyclerView recyclerView;
    @BindView(R.id.r1)RelativeLayout relativeLayout;
    RouteSelectAdapter adapter;
    List<RouteSelectModel> list = new ArrayList<>();
    Connection connection;
    SharedPrefs sharedPrefs;
    LoadToast loadToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_select);
        ButterKnife.bind(this);
        connection = new Connection(this);
        sharedPrefs = new SharedPrefs(this);
        loadToast = new LoadToast(this);
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        recyclerView.setItemAnimator(animator);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (connection.isInternet()){
            if (sharedPrefs.getRouteSelected()){
                Log.d("LOG","true");
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            }else {
                loadToast.show();
                getRouteSelectData();
            }
        }else {
            recyclerView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
            Snackbar.make(relativeLayout,getString(R.string.nointernet),Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private void getRouteSelectData() {
        AndroidNetworking.post(Constants.RouteSelectDataUrl)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.AppKey, String.valueOf(R.string.transportAppKey))
                .setOkHttpClient(okHttpClient)
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
                                    for (int i=0;i<=length;i++){
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
                                            Log.d("TAG",list.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }adapter = new RouteSelectAdapter(list,getApplicationContext());
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
    }


    public void showError(){
        loadToast.error();
        Snackbar.make(relativeLayout,getString(R.string.tryagainlater),Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connection.isInternet()){
                    getRouteSelectData();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.routeselect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.skip) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
