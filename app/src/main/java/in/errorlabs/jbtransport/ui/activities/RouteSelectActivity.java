package in.errorlabs.jbtransport.ui.activities;

import android.os.Bundle;
import android.os.Parcelable;
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
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

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
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.OkHttpClient;

public class RouteSelectActivity extends AppCompatActivity {
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    @BindView(R.id.routeselect_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.r1)
    RelativeLayout relativeLayout;
    RouteSelectAdapter adapter;
    List<RouteSelectModel> list = new ArrayList<>();
    Connection connection;
    SharedPrefs sharedPrefs;
    LoadToast loadToast;
    NiftyDialogBuilder dialogBuilder;
    public static final String LIST_CONSTANT = "list";
    Parcelable listState;
    LinearLayoutManager layoutManager;

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
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        dialogBuilder = NiftyDialogBuilder.getInstance(this);
        if (connection.isInternet()) {
            loadToast.show();
            getRouteSelectData();
        } else {
            recyclerView.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
            Snackbar.make(relativeLayout, getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE).show();
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
                        Log.d("TAG", response.toString());
                        if (response.length() > 0 || response.has(getString(R.string.AuthError)) || response.has(getString(R.string.ErrorSelecting))) {
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
                                    adapter = new RouteSelectAdapter(list, RouteSelectActivity.this);
                                    recyclerView.setAdapter(adapter);
                                    layoutManager.onRestoreInstanceState(listState);
                                } else {
                                    showError();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showError();
                            }
                        } else {
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
        Snackbar.make(relativeLayout, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connection.isInternet()) {
                    getRouteSelectData();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.routeselect, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//        if (id == R.id.skip) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.warning);
//            builder.setMessage(getString(R.string.skipwarningmessage) + "\n\n"
//                    + getString(R.string.areyousuretoskip));
//            builder.setPositiveButton(R.string.proceed, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    sharedPrefs.setAlreadySkipped();
//                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            });
//            builder.setNegativeButton(R.string.cancel, null);
//            builder.show();
//        }
        return super.onOptionsItemSelected(item);
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
