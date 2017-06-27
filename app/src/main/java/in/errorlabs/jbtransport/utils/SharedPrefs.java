package in.errorlabs.jbtransport.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by root on 6/27/17.
 */

public class SharedPrefs{
    Context context;
    public static final String myprefs = "myprefs";
    public static final String FirstOpen = "FirstOpen";
    public static final String FirebaseInstanceToken = "Token";
    public static final String LogedInKey = "Key";

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Base64 base64;

    public SharedPrefs(Context context){
        this.context=context;
        sharedPreferences = context.getSharedPreferences(myprefs,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public String encodeKey(String key) throws UnsupportedEncodingException {
        byte[] data = key.getBytes("UTF-8");
        return Base64.encodeToString(data, Base64.DEFAULT);
    }
    public String encodeValue(String key) throws UnsupportedEncodingException {
        byte[] data = key.getBytes("UTF-8");
        return Base64.encodeToString(data, Base64.DEFAULT);
    }
    public String decodeValue(String Key) throws UnsupportedEncodingException {
        byte[] data = Base64.decode(Key, Base64.DEFAULT);
        return new String(data, "UTF-8");
    }
    public void setFirebaseInstanceToken(String token) throws UnsupportedEncodingException {
        editor.putString(encodeKey(FirebaseInstanceToken),encodeValue(token));
        editor.commit();
    }
    public String getFirebaseInstanceToken() throws UnsupportedEncodingException {
        return sharedPreferences.getString(encodeKey(FirebaseInstanceToken),null);
    }

}
