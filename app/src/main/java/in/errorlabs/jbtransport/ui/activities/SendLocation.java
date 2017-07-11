package in.errorlabs.jbtransport.ui.activities;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.utils.Constants;
import in.errorlabs.jbtransport.utils.SharedPrefs;

/**
 * Created by root on 7/9/17.
 */

public class SendLocation extends Service implements LocationListener{
    LocationManager locationManager;
    SharedPrefs sharedPrefs;
    String provider;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefs = new SharedPrefs(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(),false);
        locationManager.requestLocationUpdates(provider,0,0,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Double lat= location.getLatitude();
        Double lng = location.getLongitude();
        if (lat>0 && lng>0){
            sendLocation(lat,lng);
            locationManager.removeUpdates(this);
            stopSelf();
        }else {
            onCreate();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
    }
        public void sendLocation(Double lat,Double lng){
            String fcmToken = sharedPrefs.getLocationSendFcmID();
            String username = sharedPrefs.getUserName();
            String email = sharedPrefs.getEmail();
            String rollnumber = sharedPrefs.getRollNumber();
        Toast.makeText(getApplicationContext(),lat.toString()+lng.toString(),Toast.LENGTH_SHORT).show();
        AndroidNetworking.post(Constants.FirebasePushtoDevice)
                .addBodyParameter(Constants.AppKey,getString(R.string.transportAppKey))
                .addBodyParameter(Constants.ReceiverFcmToken,fcmToken)
                .addBodyParameter(Constants.FirebaseLatitude,lat.toString())
                .addBodyParameter(Constants.FirebaseLongitude,lng.toString())
                .addBodyParameter(getString(R.string.gmailname),username)
                .addBodyParameter(getString(R.string.gmailemail),email)
                .addBodyParameter(getString(R.string.rollnumber),rollnumber)
                .addBodyParameter(getString(R.string.locationsent),lat+","+lat)
                .addBodyParameter(getString(R.string.receiveremail),sharedPrefs.getReceiverEmail())
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            if (!response.has(getString(R.string.AuthError)) && !response.has(getString(R.string.ErrorSelecting))) {
                                Toast.makeText(getApplicationContext(), R.string.shared_successfully, Toast.LENGTH_LONG).show();
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
        Toast.makeText(getApplicationContext(), R.string.failedtosendlocation,Toast.LENGTH_SHORT).show();
    }

}
