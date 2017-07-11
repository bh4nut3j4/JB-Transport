package in.errorlabs.jbtransport.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.utils.SharedPrefs;


/**
 * Created by root on 6/27/17.
 */

public class FirebaseMessageReceiveService extends FirebaseMessagingService {
    public static final String TAG="FirebaseMessageReceiveService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage!=null){
            String title = remoteMessage.getNotification().getTitle();
            String message = remoteMessage.getNotification().getBody();
            String click_action = remoteMessage.getNotification().getClickAction();
            if (click_action.equals("in.errorlabs.jbtransport.ui.activities.MAP_REDIRECTION")){
                String lat = remoteMessage.getData().get("latitude");
                String lng = remoteMessage.getData().get("longitude");
                Intent intent = new Intent(click_action);
                intent.putExtra("latitude",lat);
                intent.putExtra("longitude",lng);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setSmallIcon(R.drawable.busleft)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());

            }else if (click_action.equals("in.errorlabs.jbtransport.ui.activities.Notifications_TARGET_NOTIFICATION")){
                Intent intent = new Intent(click_action);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                        PendingIntent.FLAG_ONE_SHOT);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                notificationBuilder.setSmallIcon(R.drawable.busleft)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());
            }else if (click_action.equals("in.errorlabs.jbtransport.ui.activities.LOCATION_REQUEST")){
                SharedPrefs sharedPrefs = new SharedPrefs(this);
                String token = remoteMessage.getData().get("fcmToken");
                String receiverEmail = remoteMessage.getData().get("receiverEmail");
                if (token.equals(sharedPrefs.getFirebaseInstanceToken()) && sharedPrefs.getEmail()!=null &&
                        sharedPrefs.getEmail().length()!=0 && sharedPrefs.getRollNumber()!=null &&
                        sharedPrefs.getRollNumber().length()!=0){
                    sharedPrefs.setReceiverEmail(receiverEmail);
                    Intent intent = new Intent(click_action);
                    intent.putExtra("fcmToken",token);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                            PendingIntent.FLAG_ONE_SHOT);
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
                    notificationBuilder.setSmallIcon(R.drawable.busleft)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, notificationBuilder.build());
                }
            }
        }
    }
}
