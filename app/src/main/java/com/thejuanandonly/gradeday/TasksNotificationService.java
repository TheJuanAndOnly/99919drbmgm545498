package com.thejuanandonly.gradeday;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

public class TasksNotificationService extends IntentService {

    public TasksNotificationService() {
        super("TasksNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);

        boolean n = prefs.getBoolean("notifications", true),
                s = prefs.getBoolean("sounds", true),
                v = prefs.getBoolean("vibrations", true);

        if (n) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "tasks_channel")
                    .setTicker(intent.getStringExtra("name"))
                    .setContentTitle(intent.getStringExtra("name"))
                    .setContentText(intent.getStringExtra("what"))
                    .setSmallIcon(R.drawable.ic_event_available_white_24dp)
                    .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class).putExtra("fromNotification", true), 0))
                    .setPriority(Notification.PRIORITY_MAX);

            if (v) builder.setVibrate(new long[]{500, 500, 500, 500});
            if (s) builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                builder.setSmallIcon(R.drawable.ic_logo); //white

                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(new NotificationChannel("tasks_channel", "Tasks", NotificationManager.IMPORTANCE_HIGH));
                }
            }

            Notification notification = builder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            if (notificationManager != null) notificationManager.notify(intent.getIntExtra("index", 0) + 1, notification);

            PowerManager powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
            if (powerManager != null) {
                PowerManager.WakeLock screenLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
                screenLock.acquire(10*60*1000);
                screenLock.release();
            }
        }

        Intent reloadIntent = new Intent(this, TasksNotificationReceiver.class);
        sendBroadcast(reloadIntent);
    }
}
