package com.thejuanandonly.schoolapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

public class NotificationRecieverActivity extends BroadcastReceiver {

    Context context;
    Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;

        SharedPreferences prefs = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
        JSONArray arrayName, arrayWhat, arrayTime;

        try {
            arrayName = new JSONArray(prefs.getString("TaskName", null));
            arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
            arrayTime = new JSONArray(prefs.getString("TaskTime", null));
        } catch (Exception e) {
            arrayName = new JSONArray();
            arrayWhat = new JSONArray();
            arrayTime = new JSONArray();
        }

        ArrayList<Date> times = new ArrayList<Date>();

        for (int taskToShow = 0; taskToShow < arrayName.length(); taskToShow++) {
            try {
                times.add(new Date(arrayTime.getLong(taskToShow)));
            } catch (Exception e) {
            }
        }

        for (int timePosition = 0; timePosition < arrayName.length(); timePosition++) {
            if (times.get(timePosition).getTime() < System.currentTimeMillis() && times.get(timePosition).getTime() > System.currentTimeMillis() - 60000) {
                try {
                    if (!arrayName.getString(timePosition).equals(null)) {
                        notifyUser(context, arrayName.getString(timePosition), arrayWhat.getString(timePosition));
                    }
                } catch (Exception e) {
                }
            }
        }

        registerAlarm(context);
    }

    public void notifyUser(Context context, String name, String what) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

        boolean n = prefs.getBoolean("notifications", true),
                s = prefs.getBoolean("sounds", true),
                v = prefs.getBoolean("vibrations", true);


        if (n) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("fromNotification", true);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle(name);
            builder.setContentText(what);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_pls));
            builder.setSmallIcon(R.drawable.ic_event_available_white_24dp);
            builder.setContentIntent(contentIntent);

            if (v) {
                builder.setVibrate(new long[]{500, 500, 500, 500});
            }
            if (s) {
                builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            }

            Notification notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(1, notification);

            PowerManager.WakeLock screenLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
            screenLock.acquire();
            screenLock.release();
        }
    }

    public static void registerAlarm(Context context) {
        Intent i = new Intent(context, NotificationRecieverActivity.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, 0, i, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, sender);
        else am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, sender);
    }
}