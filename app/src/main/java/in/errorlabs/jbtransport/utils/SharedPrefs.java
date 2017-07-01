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

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Base64 base64;

    public SharedPrefs(Context context){
        this.context=context;
        sharedPreferences = context.getSharedPreferences(myprefs,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public String encodeKey(String key){
        if (key==null){
            return null;
        }else {
            byte[] data = Base64.encode(key.getBytes(), Base64.DEFAULT);
            return new String(data);
        }

    }
    public String encodeValue(String key){
        if (key==null){
            return null;
        }else {
            byte[] data = Base64.encode(key.getBytes(), Base64.DEFAULT);
            return new String(data);
        }
    }
    public String decodeValue(String Key){
        byte[] data = Base64.decode(Key, Base64.DEFAULT);
        return new String(data);
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
    public void setRouteSelectedStatus(){
        editor.putString(encodeKey(RouteSelected), String.valueOf(R.string.truestring));
        editor.putBoolean(encodeKey(RouteSelected),true);
        editor.commit();
    }
    public boolean getRouteSelected(){
        return sharedPreferences.getBoolean(encodeKey(RouteSelected),false);
    }

}
