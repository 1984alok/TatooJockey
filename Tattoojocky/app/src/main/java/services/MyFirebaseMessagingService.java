package services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import applabindia.com.tattoojocky.R;
import applabindia.com.tattoojocky.SplashActivity;
import settings.Settingsmanager;

/**
 * Created by Alok on 26-11-2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCM Service";




    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        JSONObject object = null;
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> params = remoteMessage.getData();
            object = new JSONObject(params);
            Log.e("JSON_OBJECT", object.toString());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        if(new Settingsmanager(this).isNotifyStatus()) {
            if (object != null)
                sendNotification(object);
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(JSONObject messageBody) {
        String tittle = messageBody.optString("title");
        String body = messageBody.optString("body");
        String imgUrl = messageBody.optString("icon");
       // String imgUrl = "https://s-media-cache-ak0.pinimg.com/736x/83/11/02/8311022f2dae9634c8c5c6954ba80505.jpg";

        Intent intent = new Intent();
        intent.setClass(MyFirebaseMessagingService.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = null;
        try {
            notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(tittle)
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(imgUrl.equalsIgnoreCase("") ? BitmapFactory.decodeResource(
                            getResources(), R.drawable.ic_launcher)
                            : Picasso.with(MyFirebaseMessagingService.this).load(imgUrl).get())
                    //BigPicture Style
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            //This one is same as large icon but it wont show when its expanded that's why we again setting
                            .bigLargeIcon(imgUrl.equalsIgnoreCase("") ? BitmapFactory.decodeResource(
                                    getResources(), R.drawable.ic_launcher)
                                    : Picasso.with(MyFirebaseMessagingService.this).load(imgUrl).get())
                            //This is Big Banner image
                            .bigPicture(imgUrl.equalsIgnoreCase("") ? BitmapFactory.decodeResource(
                                    getResources(), R.drawable.ic_launcher)
                                    : Picasso.with(MyFirebaseMessagingService.this).load(imgUrl).get())
                            //When Notification expanded title and content text
                            .setBigContentTitle(tittle)
                            .setSummaryText(body)
                    );
        } catch (IOException e) {
            e.printStackTrace();
        }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
