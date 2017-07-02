package in.errorlabs.jbtransport.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import in.errorlabs.jbtransport.R;

/**
 * Created by root on 6/27/17.
 */

public class SharedPrefs{
    Context context;
    public static final String myprefs = "myprefs";
    public static final String FirstOpen = "FirstOpen";
    public static final String FirebaseInstanceToken = "Token";
    public static final String RouteSelected = "Route";
    public static final String SelectedRouteNumber = "selectedRoute";
    public static final String SelectedRouteFcmID = "fcm";
    public static final String AlreadySkipped = "skipped";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SharedPrefs(Context context){
        this.context=context;
        sharedPreferences = context.getSharedPreferences(myprefs,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public String encodeKey(String key){
        if (key==null){
            return null;
        }else {
            return Base64.encodeToString(key.getBytes(), Base64.DEFAULT);
        }
    }
    private String encodeValue(String key){
        if (key==null){
            return null;
        }else {
            return Base64.encodeToString(key.getBytes(), Base64.DEFAULT);
        }
    }
    private String decodeValue(String Key){
        if (Key==null){
            return null;
        }else {
            return new String(Base64.decode(Key, Base64.DEFAULT));
        }

    }
    public void setFirstOpen(){
        editor.putString(encodeKey(FirstOpen), String.valueOf(R.string.truestring));
        editor.commit();
    }
    public String getFirstOpen(){
        return sharedPreferences.getString(encodeKey(FirstOpen),null);
    }
    public void setFirebaseInstanceToken(String token){
        editor.putString(encodeKey(FirebaseInstanceToken),encodeValue(token));
        editor.commit();
    }
    public String getFirebaseInstanceToken(){
        String value = sharedPreferences.getString(encodeKey(FirebaseInstanceToken),null);
        return decodeValue(value);
    }

    public void setSelectedRouteNumber(String number){
        editor.putString(encodeKey(SelectedRouteNumber),encodeValue(number));
        editor.commit();
    }
    public String getSelectedRouteNumber(){
        String value =  sharedPreferences.getString(encodeKey(SelectedRouteNumber),null);
        return decodeValue(value);
    }

    public void setSelectedRouteFcmID(String fcmID){
        editor.putString(encodeKey(SelectedRouteFcmID),encodeValue(fcmID));
        editor.commit();
    }
    public String getSelectedRouteFcmID(){
        String value = sharedPreferences.getString(encodeKey(SelectedRouteFcmID),null);
        return decodeValue(value);
    }

    public void setAlreadySkipped(){
        editor.putBoolean(encodeKey(AlreadySkipped),true);
        editor.commit();
    }

    public boolean getAlreadySkipped(){
        return sharedPreferences.getBoolean(encodeKey(AlreadySkipped),false);
    }

}
