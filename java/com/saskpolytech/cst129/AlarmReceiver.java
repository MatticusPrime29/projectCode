package com.saskpolytech.cst129;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by cst129 on 5/23/2018.
 */

/**
 * Class will be used as a Service to set an alarm. When this is instansiated it will set the appropriate notification
 */
public class AlarmReceiver extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    } //Not being used but is implemented

    /**
     * Will create the notification for the alarm set
     */
    public void onCreate()
    {
        //This will run when the service starts
        //Display a notification (same as the other code)
        // Get a reference to the manager
        NotificationManager notifyMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Declare a Notification
        Notification n;

        //Define a pending intent that will indicate which activity to open if the notification is tapped
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // If using oreo, need to define a channel, and use a different builder
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            // Configure a channel
            String channel = "1111";
            CharSequence name = "Channel1111";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(channel, name, importance);

            // Create the channel in the system using the manager
            notifyMgr.createNotificationChannel(notificationChannel);

            // Use a builder to configure and produce a notification object. Similar idea to a constructor
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channel)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setChannelId(channel)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setTicker("Time to take a Photo!")
                    .setContentTitle("Time to take a Photo")
                    .setContentText("It's that time of day again! update your daily picture")
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent);
            n = builder.build();
        }
        else
        {
            // Older versions didn't have channels, used a different builder

            // Use a builder to configure and produce a notification object. Similar idea to a constructor
            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setTicker("Time to take a Photo!")
                    .setContentTitle("Time to take a Photo")
                    .setContentText("It's that time of day again! update your daily picture")
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent);
            n = builder.build();
        }

        // ID can be a unique code for your app to tell different notifications apart. Doesn't matter if you only have one type like we do
        notifyMgr.notify(1,n);

    }



}
