package tech.msociety.terawhere.services;

import android.app.NotificationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import tech.msociety.terawhere.R;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FMS", "onMessageReceived: " + remoteMessage.getMessageId());
        String sound = remoteMessage.getNotification().getSound();
        NotificationCompat.Builder builder = new  NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_notification)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSound(sound != null && !sound.isEmpty() && !sound.equals("default") ? Uri.parse(sound) : Settings.System.DEFAULT_NOTIFICATION_URI);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(remoteMessage.hashCode(), builder.build());
    }

}
