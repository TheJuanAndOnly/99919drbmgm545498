package com.thejuanandonly.gradeday;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TasksNotificationReceiver extends BroadcastReceiver {

    Context context;
    Intent intent;
    SharedPreferences prefs;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        prefs = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);

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

        int closest = -1;
        for (int i = 0; i < arrayTime.length(); i++) {
            try {
                if (arrayTime.getLong(i) > System.currentTimeMillis()) {
                    closest = i;

                    break;
                }
            } catch (Exception e) {
            }
        }

        alwaysOnScreen();

        if (closest == -1) {
            Intent intentTimer = new Intent(context, TasksNotificationService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intentTimer, PendingIntent.FLAG_UPDATE_CURRENT);
            if (alarmManager != null) alarmManager.cancel(pendingIntent);

            return;
        }

        for (int i = 0; i < arrayTime.length(); i++) {
            try {
                if (arrayTime.getLong(i) > System.currentTimeMillis()) {
                    if (arrayTime.getLong(i) < arrayTime.getLong(closest)) closest = i;
                }
            } catch (Exception e) {
            }
        }

        Toast.makeText(context, closest+"", Toast.LENGTH_SHORT).show();

        long time = 0;
        String name = null, what = null;
        try {
            time = arrayTime.getLong(closest);
            name = arrayName.getString(closest);
            what = arrayWhat.getString(closest);
        } catch (Exception e) {
        }

        Intent intentTimer = new Intent(context, TasksNotificationService.class);
        intentTimer.putExtra("name", name);
        intentTimer.putExtra("what", what);
        intentTimer.putExtra("index", closest);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intentTimer, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT < 23) alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
            else alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    public void alwaysOnScreen() {
        JSONArray arrayName;
        SharedPreferences preferences = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);

        try {
            arrayName = new JSONArray(preferences.getString("TaskName", null));
        } catch (Exception e) {
            arrayName = new JSONArray();
        }

        int numberOfTask = arrayName.length();


        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

        boolean a = prefs.getBoolean("active", true);

        if (a == true && numberOfTask > 0) {
            String nameForAlways = null;
            if (numberOfTask == 1) {
                nameForAlways = " active task";
            } else if (numberOfTask > 1) {
                nameForAlways = " active tasks";
            }

            ArrayList<String> listNamez = new ArrayList<String>();
            for (int i = 0; i < arrayName.length(); i++){
                try {
                    listNamez.add(arrayName.getString(i));
                } catch (Exception e) {
                }
            }

            String childWithNames = listNamez.toString();

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("fromNotification", true);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

            Notification.Builder builder = new Notification.Builder(context)
                    .setContentTitle(numberOfTask + nameForAlways)
                    .setContentText(childWithNames.substring(1, childWithNames.length()-1))
                    .setSmallIcon(R.drawable.ic_active_tasks)
                    .setContentIntent(contentIntent);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.drawable.ic_active_white);
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_active_white));
            }

            Notification notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notificationManager.notify(0, notification);
        } else {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        }
    }
}