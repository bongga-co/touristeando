package co.bongga.touristeando.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Patterns;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import co.bongga.touristeando.R;
import co.bongga.touristeando.activities.Main;
import co.bongga.touristeando.models.Notification;
import co.bongga.touristeando.utils.UtilityManager;
import io.realm.Realm;

/**
 * Created by bongga on 2/12/17.
 */

public class FCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if(remoteMessage == null)
            return;

        if(remoteMessage.getData().size() > 0){
            Notification notification = new Notification();

            notification.setTitle(remoteMessage.getData().get("title"));
            notification.setDescription(remoteMessage.getData().get("description"));

            if(Integer.parseInt(remoteMessage.getData().get("type")) == Notification.OFFER_TYPE){
                notification.setExpityDate(remoteMessage.getData().get("expiry_date"));
                notification.setDiscount(Float.parseFloat(remoteMessage.getData().get("discount")));
                notification.setType(Notification.OFFER_TYPE);
            }
            else{
                notification.setType(Notification.GNAL_TYPE);
                notification.setImage(remoteMessage.getData().get("image"));
            }

            saveNotification(notification);
            displayNotification(notification);
            didStoreNotification(notification);
        }
    }

    private void saveNotification(Notification notification){
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.beginTransaction();
            realm.copyToRealm(notification);
            realm.commitTransaction();
        }
        finally {
            realm.close();
        }
    }

    private void displayNotification(Notification notification) {
        Intent intent = new Intent(this, Main.class);

        intent.putExtra("hasNotification", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_toury)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getDescription())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);

        String imageUrl = notification.getImage();
        if(imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()){
            Bitmap bitmap = UtilityManager.getBitmapFromURL(imageUrl);

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(notification.getTitle());
            bigPictureStyle.setSummaryText(notification.getDescription());
            bigPictureStyle.bigPicture(bitmap);

            notificationBuilder.setStyle(bigPictureStyle);
        }
        else{
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.addLine(notification.getDescription());

            notificationBuilder.setStyle(inboxStyle);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }

    private void didStoreNotification(Notification notification){
        final DatabaseReference dbReference = FirebaseDatabase.getInstance()
                .getReference("notifications");
        //dbReference.push().setValue(notification);
    }
}
