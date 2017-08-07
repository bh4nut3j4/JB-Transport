package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.Constants;
import in.errorlabs.jbtransport.utils.SharedPrefs;

public class Splash extends AppCompatActivity {
    SharedPrefs sharedPrefs;
    Connection connection;
    LoadToast loadToast;
    @BindView(R.id.linear)RelativeLayout linearLayout;
    @BindView(R.id.progress_bar)ProgressBar progressBar;
    @BindView(R.id.conn_text)TextView conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loadToast = new LoadToast(this);
        loadToast.show();
        ButterKnife.bind(this);
        sharedPrefs = new SharedPrefs(this);
        connection = new Connection(this);
        if (connection.isInternet()) {
            if (!sharedPrefs.getFirstOpen()) {
                startActivity(new Intent(getApplicationContext(), Intro.class));
                finish();
            } else {
                if (sharedPrefs.getRouteSelected()) {
                    initialCheck();
                } else {
                    loadToast.success();
                    startActivity(new Intent(getApplicationContext(), RouteSelectActivity.class));
                    finish();
                }
            }
        } else {
            progressBar.setVisibility(View.GONE);
            conn.setVisibility(View.GONE);
            Snackbar.make(linearLayout, getString(R.string.nointernet), Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private void initialCheck(){
        String routeNumber = sharedPrefs.getSelectedRouteNumber();
        if (routeNumber!=null && !routeNumber.equals("")){
//            check(routeNumber);
            backcheck backcheck = new backcheck();
            backcheck.execute();
        }else {
            sharedPrefs.setRouteSelectedAsFalse();
            sharedPrefs.setSelectedRouteNumber(null);
            loadToast.success();
            startActivity(new Intent(getApplicationContext(),RouteSelectActivity.class));
            finish();
        }
    }

    private class backcheck extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... params) {
            String routeNumber = sharedPrefs.getSelectedRouteNumber();
            check(routeNumber);
            return null;
        }
    }
    private void check(String routeNumber){
        loadToast.show();
        AndroidNetworking.post(Constants.InitialCheck)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.AppKey,getString(R.string.transportAppKey))
                .addBodyParameter(Constants.RouteNumber,routeNumber)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            if (!response.has(getString(R.string.AuthError)) && !response.has(getString(R.string.ErrorSelecting))) {
                                try {
                                    JSONArray status = response.getJSONArray(getString(R.string.status));
                                    JSONObject obj = status.getJSONObject(0);
                                    String number = obj.getString(getString(R.string.number));
                                    if (number.equals("1")){
                                        sharedPrefs.setSelectedRouteNumber(null);
                                        sharedPrefs.setRouteSelectedAsFalse();
                                        Toast toast = Toast.makeText(getApplicationContext(), R.string.route_reset, Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        startActivity(new Intent(getApplicationContext(),RouteSelectActivity.class));
                                        finish();
                                    }else if(number.equals("0")){
                                        loadToast.success();
                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                showError();
                            }
                        } else {
                            showError();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        showError();
                    }
                });
    }

    public void showError() {
        Toast.makeText(getApplicationContext(), R.string.tryagainlater,Toast.LENGTH_SHORT).show();
    }
}
