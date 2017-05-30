package tech.msociety.terawhere.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import tech.msociety.terawhere.R;
import tech.msociety.terawhere.events.PushNotificationReceivedEvent;
import tech.msociety.terawhere.screens.activities.MainActivity;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FMS", "onMessageReceived: " + remoteMessage.getMessageId());
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;
        if (remoteMessage.getNotification() != null) {
            // Firebase in foreground
            String sound = remoteMessage.getNotification().getSound();
            NotificationCompat.Builder builder = new  NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setSound(sound != null && !sound.isEmpty() && !sound.equals("default") ? Uri.parse(sound) : Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent);
            notification = builder.build();
        } else {
            // Mixpanel
            NotificationCompat.Builder builder = new  NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_notification)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(remoteMessage.getData().get("mp_message"))
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent);
            notification = builder.build();
        }

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(remoteMessage.hashCode(), notification);

        EventBus.getDefault().post(new PushNotificationReceivedEvent());
    }

}
