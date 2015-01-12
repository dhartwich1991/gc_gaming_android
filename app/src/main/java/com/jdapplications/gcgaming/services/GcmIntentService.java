package com.jdapplications.gcgaming.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.jdapplications.gcgaming.R;
import com.jdapplications.gcgaming.activities.AvailableRaidsActivity;
import com.jdapplications.gcgaming.broadcastreceivers.GcmBroadcastReceiver;

/**
 * Created by danielhartwich on 1/9/15.
 */
public class GcmIntentService extends IntentService {
    public static final int NEW_RAID_NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
//                sendNotification("Deleted messages on server: " +
//                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                for (int i = 0; i < 5; i++) {
                    Log.i("INTENTSERVICE GCM", "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i("INTENTSERVICE GCM", "Completed work @ " + SystemClock.elapsedRealtime());
                // Post notification of received message.

                String event = extras.getString("event");
                String name = extras.getString("name");
                String description = extras.getString("description");
                String code = extras.getString("code");
                String id = extras.getString("id");
                sendNotification(code, event, name, description, id);
                Log.i("INTENTSERVICE GCM", "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String code, String event, String value, String description, String id) {
        //TODO: Parse Message and Create appropiate Push Notification
        switch (code) {
            //New Raid Notification
            case "1":
                mNotificationManager = (NotificationManager)
                        this.getSystemService(Context.NOTIFICATION_SERVICE);

                PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                        new Intent(this, AvailableRaidsActivity.class), 0);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .setContentTitle(event)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(value + ": " + description))
                                .setContentText(value + ": " + description)
                                .setDefaults(Notification.DEFAULT_SOUND
                                        | Notification.DEFAULT_VIBRATE
                                        | Notification.FLAG_SHOW_LIGHTS);

                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(NEW_RAID_NOTIFICATION_ID, mBuilder.build());
                break;
            case "2":
                break;
            default:
                break;
        }


    }
}
