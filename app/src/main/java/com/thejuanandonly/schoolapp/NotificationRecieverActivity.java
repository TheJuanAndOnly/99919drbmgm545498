package com.thejuanandonly.schoolapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

public class NotificationRecieverActivity extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
        JSONArray arrayName, arrayWhat, arrayTime;

        int numberOfTask = prefs.getInt("NumberOfTask", 0);
        int taskPosition = 0;

        intent.getIntExtra("LastTask", taskPosition);

        try {
            arrayName = new JSONArray(prefs.getString("TaskName", null));
            arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
            arrayTime = new JSONArray(prefs.getString("TaskTime", null));
        } catch (Exception e) {
            arrayName = new JSONArray();
            arrayWhat = new JSONArray();
            arrayTime = new JSONArray();
        }

        long time = 0;

        ArrayList<Date> timess = new ArrayList<Date>();

        for (int taskToShow = 0; taskToShow < numberOfTask; taskToShow++) {
            try {
                time = arrayTime.getLong(taskToShow);
                timess.add(taskToShow, new Date(time));
            } catch (Exception e) {
            }
        }

        for (int timePosition = 0; timePosition < numberOfTask; timePosition++) {
            if (timess.get(timePosition).getTime() < System.currentTimeMillis() && timess.get(timePosition).getTime() > System.currentTimeMillis() - 10000) {
                try {
                    notifyUser(context, arrayName.getString(timePosition), arrayWhat.getString(timePosition), timePosition);
                    taskPosition = timePosition;
                } catch (Exception e) {
                }
            }
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        PendingIntent mAlarmSender = PendingIntent.getBroadcast(context, 0, intent.putExtra("LastTask", taskPosition), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, mAlarmSender);
    }

    public void notifyUser(Context context, String name, String what, int id) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("fromNotification", true);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(name)
                .setContentText(what)
                .setVibrate(new long[]{500, 500})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setSmallIcon(R.drawable.ic_event_available_white_24dp)
                .setContentIntent(contentIntent).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(id, notification);
    }
}