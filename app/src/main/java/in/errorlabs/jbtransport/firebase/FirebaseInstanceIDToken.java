package in.errorlabs.jbtransport.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import in.errorlabs.jbtransport.utils.SharedPrefs;

/**
 * Created by root on 6/27/17.
 */

public class FirebaseInstanceIDToken extends FirebaseInstanceIdService {
    public static final String TAG="FirebaseInstanceIDToken";

    SharedPrefs sharedPrefs;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPrefs = new SharedPrefs(this);
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sharedPrefs.setFirebaseInstanceToken(refreshedToken);
        Log.d(TAG, "Refreshed token: " + sharedPrefs.getFirebaseInstanceToken());
        addToTopic();
    }

    private void addToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("Testing");
        Log.d("MSG11","topic");
    }

}
