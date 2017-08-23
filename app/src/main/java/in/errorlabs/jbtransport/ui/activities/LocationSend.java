package in.errorlabs.jbtransport.ui.activities;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
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
 * Created by root on 7/26/17.
 */

public class LocationSend extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    SharedPrefs sharedPrefs;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefs = new SharedPrefs(this);
        listener = new LocationListener() {
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
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {
                locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,0,listener);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            locationManager.removeUpdates(listener);
        }
    }

    public void sendLocation(Double lat,Double lng){
        String fcmToken = sharedPrefs.getLocationSendFcmID();
        String username = sharedPrefs.getUserName();
        String email = sharedPrefs.getEmail();
        String rollnumber = sharedPrefs.getRollNumber();
        Log.d("TAGGGG",fcmToken+lat+lng+username+email+rollnumber+sharedPrefs.getReceiverEmail().toString());

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
                                Toast toast = Toast.makeText(getApplicationContext(), R.string.shared_successfully, Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
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
                        Log.d("LOg",anError.toString());
                    }
                });
    }

    public void showError() {
        Toast.makeText(getApplicationContext(), R.string.failedtosendlocation,Toast.LENGTH_SHORT).show();
    }
}
