package in.errorlabs.jbtransport.ui.activities;

import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
import in.errorlabs.jbtransport.ui.adapters.NotificationAdapter;
import in.errorlabs.jbtransport.ui.models.NotificationModel;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.Constants;
import okhttp3.OkHttpClient;

public class Notifications extends AppCompatActivity {
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    @BindView(R.id.notification_recyclerview)RecyclerView recyclerView;
    @BindView(R.id.no_notifications)TextView nonotifications;
    NotificationAdapter adapter;
    List<NotificationModel> list = new ArrayList<>();
    Connection connection;
    LoadToast loadToast;
    public static final String LIST_CONSTANT = "list";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);
        connection = new Connection(this);
        loadToast = new LoadToast(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (savedInstanceState!=null){
            list = savedInstanceState.getParcelableArrayList(LIST_CONSTANT);
            adapter = new NotificationAdapter(this,list);
            recyclerView.setAdapter(adapter);
        }else {
            if (connection.isInternet()){
                getNotificationData();
            }else {
                Snackbar.make(recyclerView,R.string.nointernet,Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (connection.isInternet()){
                            getNotificationData();
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        super.onBackPressed();
    }

    private void getNotificationData() {
        loadToast.show();
        AndroidNetworking.post(Constants.NotificationURL)
                .setOkHttpClient(okHttpClient)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.AppKey,String.valueOf(R.string.transportAppKey))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length()>0){
                            parseJSON(response);
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        loadToast.error();
                        showError();
                    }
                });
    }

    private void showError(){
        Snackbar.make(recyclerView,R.string.tryagainlater,Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connection.isInternet()){
                    getNotificationData();
                }
            }
        }).show();
    }

    private void parseJSON(JSONObject response) {
        loadToast.success();
        list.clear();
        if (response.has(getString(R.string.Notifications)) && !response.has(getString(R.string.AuthError))){
            try {
                JSONArray jsonArray = response.getJSONArray(getString(R.string.Notifications));
                if (jsonArray.length()>0){
                    for (int i=0;i<=jsonArray.length();i++){
                        JSONObject object = jsonArray.getJSONObject(i);
                        NotificationModel model = new NotificationModel();
                        model.setHeading(object.getString(getString(R.string.heading)));
                        model.setMessage(object.getString(getString(R.string.message)));
                        model.setTimeStamp(object.getString(getString(R.string.timestamp)));
                        list.add(model);
                    }
                }else {
                    recyclerView.setVisibility(View.GONE);
                    nonotifications.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }adapter = new NotificationAdapter(Notifications.this,list);
            recyclerView.setAdapter(adapter);
        }else if (response.has(getString(R.string.ErrorSelecting))){
            recyclerView.setVisibility(View.GONE);
            nonotifications.setVisibility(View.VISIBLE);
        }else {
            showError();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST_CONSTANT, (ArrayList<? extends Parcelable>) list);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        list = savedInstanceState.getParcelableArrayList(LIST_CONSTANT);
        adapter = new NotificationAdapter(this,list);
        recyclerView.setAdapter(adapter);
    }
}
