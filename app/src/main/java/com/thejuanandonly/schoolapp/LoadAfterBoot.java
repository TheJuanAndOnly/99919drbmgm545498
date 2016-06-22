package com.thejuanandonly.schoolapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import org.json.JSONArray;

import java.util.ArrayList;

public class LoadAfterBoot extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        updateNotification(context);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        PendingIntent mAlarmSender = PendingIntent.getBroadcast(context, 0, new Intent(context, NotificationRecieverActivity.class), 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), mAlarmSender);
    }

    public void updateNotification(Context context) {
        SharedPreferences prefss = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences prefsss = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);

        boolean a = prefss.getBoolean("active", true);
        int numberOfTasks = prefsss.getInt("NumberOfTask", 0);

        if (a == true && numberOfTasks > 0) {
            SharedPreferences prefs = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
            JSONArray arrayName;
            int numberOfTask = prefs.getInt("NumberOfTask", 0);
            try {
                arrayName = new JSONArray(prefs.getString("TaskName", null));
            } catch (Exception e) {
                arrayName = new JSONArray();
            }

            ArrayList<String> listNamez = new ArrayList<String>();
            for (int i = 0; i < arrayName.length(); i++){
                try {
                    listNamez.add(arrayName.getString(i));
                } catch (Exception e) {
                }
            }

            String childWithNames = listNamez.toString();

            if (numberOfTask > 0) {
                String nameForAlways = null;
                if (numberOfTask == 1) {
                    nameForAlways = " active task";
                } else if (numberOfTask > 1) {
                    nameForAlways = " active tasks";
                }

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("fromNotification", true);
                PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

                Notification notification = new Notification.Builder(context)
                        .setContentTitle(numberOfTask + nameForAlways)
                        .setContentText(childWithNames.substring(1, childWithNames.length()-1))
                        .setSmallIcon(R.drawable.ic_active_tasks)
                        .setContentIntent(contentIntent).build();

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                notification.flags |= Notification.FLAG_NO_CLEAR;
                notificationManager.notify(0, notification);
            }
        } else {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        }
    }
}