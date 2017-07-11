package in.errorlabs.jbtransport.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
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
import in.errorlabs.jbtransport.ui.adapters.AllRoutesAdapter;
import in.errorlabs.jbtransport.ui.constants.RoutesSelectConstants;
import in.errorlabs.jbtransport.ui.models.RouteSelectModel;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.Constants;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.OkHttpClient;

public class AllRoutes extends AppCompatActivity {
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    @BindView(R.id.routeselect_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.r1) RelativeLayout nointernet;
    @BindView(R.id.searcherror) RelativeLayout searcherror;
    @BindView(R.id.rootView) RelativeLayout rootView;
    AllRoutesAdapter adapter;
    Connection connection;
    LoadToast loadToast;
    LinearLayoutManager layoutManager;
    List<RouteSelectModel> list = new ArrayList<>();
    String areaName;
    Parcelable listState;
    public static final String LIST_CONSTANT = "Rlist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_routes);
        ButterKnife.bind(this);
        connection = new Connection(this);
        loadToast = new LoadToast(this);
        SlideInUpAnimator animator = new SlideInUpAnimator(new OvershootInterpolator(1f));
        recyclerView.setItemAnimator(animator);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if (connection.isInternet()) {
            loadToast.show();
            try{
                Bundle bundle = getIntent().getExtras();
                areaName= bundle.getString("AreaName");
            }catch (Exception e){
                e.printStackTrace();
            }
            if (areaName!=null && areaName.length()>0){
                getAllRoutesData(areaName);
            }else {
                getAllRoutesData("0");
            }
        } else {
            recyclerView.setVisibility(View.GONE);
            nointernet.setVisibility(View.VISIBLE);
            Snackbar.make(nointernet, getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE).show();
        }

    }

    private void getAllRoutesData(String areaName){
        String Url;
        if (areaName.equals("0")){
            Url= Constants.RouteSelectDataUrl;
        }else {
            Url= Constants.SearchByName;
        }
        AndroidNetworking.post(Url)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.AppKey, getString(R.string.transportAppKey))
                .addBodyParameter(Constants.AreaName, areaName)
                .setOkHttpClient(okHttpClient)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loadToast.success();
                        Log.d("TAG", response.toString());
                        if (!response.has(getString(R.string.AuthError)) && !response.has(getString(R.string.searcherrorselecting))) {
                            try {
                                JSONArray jsonArray = response.getJSONArray(getString(R.string.RoutesSelectJsonArrayName));
                                if (jsonArray.length() > 0) {
                                    int length = jsonArray.length();
                                    list.clear();
                                    for (int i = 0; i <= length; i++) {
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
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    adapter = new AllRoutesAdapter(list, AllRoutes.this);
                                    recyclerView.setAdapter(adapter);
                                    layoutManager.onRestoreInstanceState(listState);
                                } else {
                                   showError();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showError();
                            }
                        } else if (response.has(getString(R.string.searcherrorselecting))){
                            recyclerView.setVisibility(View.INVISIBLE);
                            searcherror.setVisibility(View.VISIBLE);
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

    public void showError() {
        loadToast.error();
        Snackbar.make(rootView, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connection.isInternet()) {
                    getAllRoutesData(areaName);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchmenu, menu);
        menu.findItem(R.id.reset).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.search) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.searchtitle);
            alert.setMessage(R.string.searchmessage);
            alert.setIcon(R.drawable.searchalert);
            final EditText input = new EditText(this);
            input.setHint(R.string.searchhint);
            alert.setView(input);
            alert.setPositiveButton(getString(R.string.search), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String result = input.getText().toString();
                    if (result.length()>0){
                        Intent intent = new Intent(getApplicationContext(),AllRoutes.class);
                        intent.putExtra(getString(R.string.AreaName),result);
                        startActivity(intent);
                    }else {
                        Snackbar.make(rootView,getString(R.string.invalidinput),Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
            alert.setNegativeButton(getString(R.string.cancel),null);
            alert.show();
        }else if (id ==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_back_up_in, R.anim.push_back_up_out);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = layoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_CONSTANT, listState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(LIST_CONSTANT);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (listState != null) {
            layoutManager.onRestoreInstanceState(listState);
        }
    }

}
