package com.thejuanandonly.schoolapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Toast;


import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    LinearLayout drawerFull;
    public ImageView userPhotoimgview;
    public TextView userNicktxtview;
    RelativeLayout NAVd;
    public int actualFragment = 1;
    public static int api;
    public static int theme;
    public boolean willSend = false;
    public static boolean taskAdded = false;
    android.support.v7.widget.Toolbar toolbar;
    String reset;
    private Tracker mTracker;
    public String userNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        api = android.os.Build.VERSION.SDK_INT;

        drawerFull = (LinearLayout) findViewById(R.id.drawerFull);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        userPhotoimgview = (ImageView) findViewById(R.id.usersPhoto);
        userNicktxtview = (TextView) findViewById(R.id.usersNickname);

        checkStoragePermission();
        setLevel();
        setQuote();
        setOverall();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int height = displaymetrics.heightPixels;

        NAVd = (RelativeLayout)findViewById(R.id.userDetails);
        if (height <= 853) {
            NAVd.setBackgroundDrawable(getResources().getDrawable(R.drawable.blueprint));
        } else {
            NAVd.setBackgroundDrawable(getResources().getDrawable(R.drawable.nav_bckg));
        }

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new OverviewFragment()).commit();

        boolean fromNotification = getIntent().getBooleanExtra("fromNotification", false);
        if (fromNotification == true) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, new TasksFragment()).commit();
            actualFragment = 2;
            invalidateOptionsMenu();
        }

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                setLevel();
                setOverall();

                if (menuItem.getItemId() == R.id.nav_item_overview) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new OverviewFragment()).commit();
                    actualFragment = 1;
                    invalidateOptionsMenu();
                }

                if (menuItem.getItemId() == R.id.nav_item_tasks) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TasksFragment()).commit();
                    actualFragment = 2;
                    invalidateOptionsMenu();
                }

                if (menuItem.getItemId() == R.id.nav_item_notes) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new NotesFragment(), "NotesFragment").commit();
                    actualFragment = 3;
                    invalidateOptionsMenu();
                }

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new SettingsFragment()).commit();
                    actualFragment = 4;
                    invalidateOptionsMenu();
                }

                if (menuItem.getItemId() == R.id.nav_item_support) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new SupportFragment()).commit();
                    actualFragment = 5;
                    invalidateOptionsMenu();
                }

                return false;
            }

        });
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(drawerFull)) {
            mDrawerLayout.closeDrawer(drawerFull);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        if (actualFragment == 2 && taskAdded == true) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent mAlarmSender = PendingIntent.getBroadcast(this, 0, new Intent(this, NotificationRecieverActivity.class), 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), mAlarmSender);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, new TasksFragment()).commit();
            taskAdded = false;
        }

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (actualFragment == 1) {
            getMenuInflater().inflate(R.menu.menu_overview, menu);
        } else if (actualFragment == 2) {
            getMenuInflater().inflate(R.menu.menu_tasks, menu);
        } else if (actualFragment == 3) {
            getMenuInflater().inflate(R.menu.menu_notes, menu);
        } else if (actualFragment == 4){
            getMenuInflater().inflate(R.menu.menu_settings, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_support, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_schedule) {
            Intent startScheduleActivity = new Intent(MainActivity.this, ScheduleActivity.class);
            startActivity(startScheduleActivity);
        } else if (id == R.id.action_new_subject) {
            subjectDialog();
        } else if (id == R.id.action_new_task) {
            Intent TaskAdderActivity = new Intent(MainActivity.this, TaskAdder.class);
            startActivity(TaskAdderActivity);
        } else if (id == R.id.action_new_note_subject) {
            notesDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLevel(){

        TextView levelText = (TextView) findViewById(R.id.levelText);
        TextView levelDot = (TextView) findViewById(R.id.levelDot);
        ProgressBar bar = (ProgressBar) findViewById(R.id.levelProgress);
        TextView xp = (TextView) findViewById(R.id.xpProgress);

        SharedPreferences arrayPrefs = getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);

        int[] array = getResources().getIntArray(R.array.level_ints);
        int[] borders = getResources().getIntArray(R.array.borders);

        int points = 0;

        JSONArray arrayOfSubjects = new JSONArray();
        try {
            arrayOfSubjects = new JSONArray(arrayPrefs.getString("List", null));
        }catch (Exception e) {}

        for (int i = 0; i < arrayOfSubjects.length(); i++){

            String currentSubj = "";
            try {
                currentSubj = arrayOfSubjects.getString(i);
            } catch (JSONException e) {
            }

            SharedPreferences prefs = getSharedPreferences("Subject" + currentSubj, Context.MODE_PRIVATE);
            double avg = Double.parseDouble(prefs.getString("AvgGrade", "0"));
            int gradeType = prefs.getInt("GradeType" , 0);

            if (avg != 0) {

                points += array[0];
                Log.d("debug", String.valueOf(array[0]));

                switch (gradeType) {
                    case 0:
                        if (avg >= 4.5) {
                            points += array[1];
                            Log.d("debug", String.valueOf(array[1]));
                        } else if (avg >= 3.5) {
                            points += array[2];
                            Log.d("debug", String.valueOf(array[2]));
                        } else if (avg >= 2.5) {
                            points += array[3];
                            Log.d("debug", String.valueOf(array[3]));
                        } else if (avg >= 1.5) {
                            points += array[4];
                            Log.d("debug", String.valueOf(array[4]));
                        } else {
                            points += array[5];
                            Log.d("debug", String.valueOf(array[5]));
                        }
                        break;
                    case 1:
                        if (avg < 30) {
                            points += array[1];
                            Log.d("debug", String.valueOf(array[1]));
                        } else if (avg < 50) {
                            points += array[2];
                            Log.d("debug", String.valueOf(array[2]));
                        } else if (avg < 75) {
                            points += array[3];
                            Log.d("debug", String.valueOf(array[3]));
                        } else if (avg < 90) {
                            points += array[4];
                            Log.d("debug", String.valueOf(array[4]));
                        } else {
                            points += array[5];
                            Log.d("debug", String.valueOf(array[5]));
                        }
                        break;
                    case 2:
                        if (avg < 0.67) {
                            points += array[1];
                            Log.d("debug", String.valueOf(array[1]));
                        } else if (avg < 1.67) {
                            points += array[2];
                            Log.d("debug", String.valueOf(array[2]));
                        } else if (avg < 2.67) {
                            points += array[3];
                            Log.d("debug", String.valueOf(array[3]));
                        } else if (avg < 3.67) {
                            points += array[4];
                            Log.d("debug", String.valueOf(array[4]));
                        } else {
                            points += array[5];
                            Log.d("debug", String.valueOf(array[5]));
                        }
                        break;
                }

                try {

                    JSONArray categories = new JSONArray(prefs.getString("ListOfCategories", null));

                    for (int j = 0; j < categories.length(); j++){

                        JSONArray arrayOfGrades = new JSONArray(prefs.getString(categories.getString(j) + "Grades" + prefs.getInt("GradeType" , 0), null));

                        for (int k = 0; k < arrayOfGrades.length(); k++){

                            switch (prefs.getInt("GradeType", 0)){
                                case 0:
                                    switch ((int) Math.round(Double.parseDouble(arrayOfGrades.getString(k)))){
                                        case 1:
                                            points += array[8];
                                            Log.d("debug", String.valueOf(array[8]));
                                            break;
                                        case 2:
                                            points += array[9];
                                            Log.d("debug", String.valueOf(array[9]));
                                            break;
                                        case 3:
                                            points += array[10];
                                            Log.d("debug", String.valueOf(array[10]));
                                            break;
                                        case 4:
                                            points += array[11];
                                            Log.d("debug", String.valueOf(array[11]));
                                            break;
                                        case 5:
                                            points += array[12];
                                            Log.d("debug", String.valueOf(array[12]));
                                            break;
                                    }
                                    break;
                                case 1:
                                    int grade = (int) Math.round(Double.parseDouble(arrayOfGrades.getString(k)));
                                    if (grade >= 90) {
                                        points += array[8];
                                        Log.d("debug", String.valueOf(array[8]));

                                    } else if (grade < 90 && grade >= 75) {
                                        points += array[9];
                                        Log.d("debug", String.valueOf(array[9]));

                                    } else if (grade < 75 && grade >= 50) {
                                        points += array[10];
                                        Log.d("debug", String.valueOf(array[10]));

                                    } else if (grade < 50 && grade >= 30) {
                                        points += array[11];
                                        Log.d("debug", String.valueOf(array[11]));

                                    } else if (grade < 30) {
                                        points += array[12];
                                        Log.d("debug", String.valueOf(array[12]));

                                    }
                                    break;
                                case 2:
                                    String gradeS = arrayOfGrades.getString(k);
                                    if (gradeS.contains("A")) {
                                        points += array[8];
                                        Log.d("debug", String.valueOf(array[8]));

                                    } else if (gradeS.contains("B")) {
                                        points += array[9];
                                        Log.d("debug", String.valueOf(array[9]));

                                    } else if (gradeS.contains("C")) {
                                        points += array[10];
                                        Log.d("debug", String.valueOf(array[10]));

                                    } else if (gradeS.contains("D")) {
                                        points += array[11];
                                        Log.d("debug", String.valueOf(array[11]));

                                    } else if (gradeS.contains("F")) {
                                        points += array[12];
                                        Log.d("debug", String.valueOf(array[12]));

                                    }
                                    break;
                            }
                        }

                    }
                }catch (Exception e){}
            }
        }

        SharedPreferences prefsToDo = getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
        int numberOfTasks = prefsToDo.getInt("NumberOfTask", 0);

        points += numberOfTasks * array[6];
        Log.d("debug", String.valueOf(numberOfTasks * array[6]));

        SharedPreferences prefsDone = getSharedPreferences("ListOfDoneTasks", Context.MODE_PRIVATE);
        int numberOfDoneTask = prefsDone.getInt("NumberOfDoneTask", 0);

        points += numberOfDoneTask * array[7];
        Log.d("debug", String.valueOf(numberOfDoneTask * array[7]));

        if (points <= borders[0]) {
            levelText.setText("Level 1");
            levelDot.setText("1");
            levelDot.setBackgroundResource(R.drawable.dot1);

            bar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dotBlue), PorterDuff.Mode.SRC_IN);
            bar.setMax(borders[0]);
            bar.setProgress(points);

            xp.setText(String.valueOf(points) + "xp" + " / " + String.valueOf(borders[0]) + "xp");

        } else if (points <= borders[1]) {
            levelText.setText("Level 2");
            levelDot.setText("2");
            levelDot.setBackgroundResource(R.drawable.dot2);

            bar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dotYellow), PorterDuff.Mode.SRC_IN);
            bar.setMax(borders[1] - borders[0]);
            bar.setProgress(points - borders[0]);

            xp.setText(String.valueOf(points) + "xp" + " / " + String.valueOf(borders[1]) + "xp");

        } else if (points <= borders[2]) {
            levelText.setText("Level 3");
            levelDot.setText("3");
            levelDot.setBackgroundResource(R.drawable.dot3);

            bar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dotGreen), PorterDuff.Mode.SRC_IN);
            bar.setMax(borders[2] - borders[1]);
            bar.setProgress(points - borders[1]);

            xp.setText(String.valueOf(points) + "xp" + " / " + String.valueOf(borders[2]) + "xp");

        } else if (points <= borders[3]) {
            levelText.setText("Level 4");
            levelDot.setText("4");
            levelDot.setBackgroundResource(R.drawable.dot4);

            bar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dotOrange), PorterDuff.Mode.SRC_IN);
            bar.setMax(borders[3] - borders[2]);
            bar.setProgress(points - borders[2]);

            xp.setText(String.valueOf(points) + "xp" + " / " + String.valueOf(borders[3]) + "xp");

        } else if (points <= borders[4]) {
            levelText.setText("Level 5");
            levelDot.setText("5");
            levelDot.setBackgroundResource(R.drawable.dot5);

            bar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dotRed), PorterDuff.Mode.SRC_IN);
            bar.setMax(borders[4] - borders[3]);
            bar.setProgress(points - borders[3]);

            xp.setText(String.valueOf(points) + "xp" + " / " + String.valueOf(borders[4]) + "xp");

        } else if (points <= borders[5]) {
            levelText.setText("Level 6");
            levelDot.setText("6");
            levelDot.setBackgroundResource(R.drawable.dot6);

            bar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dotViolet), PorterDuff.Mode.SRC_IN);
            bar.setMax(borders[5] - borders[4]);
            bar.setProgress(points - borders[4]);

            xp.setText(String.valueOf(points) + "xp" + " / " + String.valueOf(borders[5]) + "xp");

        }else if (points >= borders[5]) {

            int finalBorder = borders[5];
            int level = 6;
            int imglvl = 0;
            while (points >= finalBorder){
                finalBorder += borders[5];
                level++;

                imglvl++;
                if (imglvl == 7) imglvl = 0;
            }

            levelText.setText("Level " + level);
            levelDot.setText(String.valueOf(level));

            final TypedArray imgs = getResources().obtainTypedArray(R.array.dots_array);
            final int resID = imgs.getResourceId(imglvl, 0);
            levelDot.setBackgroundResource(resID);

            bar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.dotPink), PorterDuff.Mode.SRC_IN);
            bar.setMax(borders[5]);
            bar.setProgress(points - (finalBorder - borders[5]));

            xp.setText(String.valueOf(points) + "xp" + " / " + String.valueOf(finalBorder) + "xp");
        }

    }

    public void setOverall(){

        TextView overallTv = (TextView) findViewById(R.id.overall);

        int ovr = 0;
        int ovrCnt = 0;
        ArrayList<Integer> types = new ArrayList<>();
        int normal = 0;
        int percent = 0;
        int alphab = 0;
        int tenGrade = 0;

        SharedPreferences arrayPrefs = getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);

        JSONArray arrayOfSubjects = new JSONArray();
        try {
            arrayOfSubjects = new JSONArray(arrayPrefs.getString("List", null));
        }catch (Exception e) {}

        if (arrayOfSubjects.length() == 0){
            overallTv.setText("");
            return;
        }

        for (int i = 0; i < arrayOfSubjects.length(); i++){

            String currentSubj = "";
            try {
                currentSubj = arrayOfSubjects.getString(i);
            } catch (JSONException e) {
            }

            SharedPreferences prefs = getSharedPreferences("Subject" + currentSubj, Context.MODE_PRIVATE);
            int gradeType = prefs.getInt("GradeType" , 0);

            switch (gradeType){
                case 0:
                    normal++;
                    break;
                case 1:
                    percent++;
                    break;
                case 2:
                    alphab++;
                    break;
                case 3:
                    tenGrade++;
                    break;
            }
            types.add(gradeType);
        }

        if ((normal != 0 || percent != 0) && alphab == 0 && tenGrade == 0){
            for (int i = 0; i < arrayOfSubjects.length(); i++) {

                String currentSubj = "";
                try {
                    currentSubj = arrayOfSubjects.getString(i);
                } catch (JSONException e) {
                }

                SharedPreferences prefs = getSharedPreferences("Subject" + currentSubj, Context.MODE_PRIVATE);
                int gradeType = prefs.getInt("GradeType", 0);
                double avg = Double.parseDouble(prefs.getString("AvgGrade", "0"));

                if (gradeType == 0){
                    ovr += avg;
                    ovrCnt++;
                }
                else if (gradeType == 1){
                    SharedPreferences conv = getSharedPreferences("Global", Context.MODE_PRIVATE);
                    try{
                        JSONArray conversionArray = new JSONArray(conv.getString("conversion", null));

                        if (avg >= conversionArray.getInt(0)){
                            ovr += 1;
                            ovrCnt++;
                        }
                        else if (avg >= conversionArray.getInt(1)){
                            ovr += 2;
                            ovrCnt++;
                        }
                        else if (avg >= conversionArray.getInt(2)){
                            ovr += 3;
                            ovrCnt++;
                        }
                        else if (avg >= conversionArray.getInt(3)){
                            ovr += 4;
                            ovrCnt++;
                        }else {
                            ovr += 5;
                            ovrCnt++;
                        }
                    }
                    catch (Exception e){}
                }
            }

            try{
                double d = Double.parseDouble(String.valueOf(ovr)) / Double.parseDouble(String.valueOf(ovrCnt));
                String s;
                try{
                    s = String.valueOf(d).substring(0, 4);
                }catch (StringIndexOutOfBoundsException e){
                    s = String.valueOf(d);
                }
                overallTv.setText("Overall: " + s);

                Log.d("debugC", ovr + ", " + ovrCnt + ", " + d);
            }catch (ArithmeticException e){
                //overallTv.setText("");
                Log.e("debug", e.toString());
            }

        }else {
            for (int i = 0; i < arrayOfSubjects.length(); i++) {

                String currentSubj = "";
                try {
                    currentSubj = arrayOfSubjects.getString(i);
                } catch (JSONException e) {
                }

                SharedPreferences prefs = getSharedPreferences("Subject" + currentSubj, Context.MODE_PRIVATE);
                int gradeType = prefs.getInt("GradeType", 0);
                double avg = Double.parseDouble(prefs.getString("AvgGrade", "0"));

                switch (gradeType){
                    case 0:
                        ovr += avg * 2;
                        ovrCnt++;
                        break;
                    case 1:
                        ovr += Math.round(avg / 10);
                        ovrCnt++;
                        break;
                    case 2:
                        ovr += Math.round((avg / 4) * 10);
                        ovrCnt++;
                        break;
                    case 3:
                        ovr += avg;
                        ovrCnt++;
                        break;
                }
            }

            try{
                double d = Double.parseDouble(String.valueOf(ovr)) / Double.parseDouble(String.valueOf(ovrCnt));
                String s;
                try{
                    s = String.valueOf(d).substring(0, 4);
                }catch (StringIndexOutOfBoundsException e){
                    s = String.valueOf(d);
                }

                overallTv.setText("Overall: " + s + " / 10");

                Log.d("debugC", ovr + ", " + ovrCnt + ", " + d);
            }catch (ArithmeticException e){
                //overallTv.setText("");
                Log.e("debug", e.toString());
            }
        }
    }

    public void setQuote(){
        TextView quoteTv = (TextView) findViewById(R.id.quote);
        TextView authorTv = (TextView) findViewById(R.id.author);

        String[] quotes = getResources().getStringArray(R.array.quotes);
        String[] authors = getResources().getStringArray(R.array.authors);

        int rnd = (int) (Math.random() * 26);

        quoteTv.setText(quotes[rnd]);
        authorTv.setText(authors[rnd]);

        if (quotes[rnd].length() >= 120){
            quoteTv.setTextSize(16);
            authorTv.setTextSize(14);
        }else {
            quoteTv.setTextSize(18);
            authorTv.setTextSize(16);
        }
    }

    public void notificationsClick(View view) {
        Switch notificationsCheckBox, soundsCheckBox, vibrationsCheckBox, activeTasksCheckBox;
        notificationsCheckBox = (Switch) findViewById(R.id.notificationsCheckBox);
        soundsCheckBox = (Switch) findViewById(R.id.soundsNotificationCheckBox);
        vibrationsCheckBox = (Switch) findViewById(R.id.vibrationsNotificationCheckBox);

        boolean isChecked = notificationsCheckBox.isChecked();
        soundsCheckBox.setChecked(isChecked);
        vibrationsCheckBox.setChecked(isChecked);

        if (!isChecked) {
            soundsCheckBox.setVisibility(View.GONE);
            vibrationsCheckBox.setVisibility(View.GONE);
        } else {
            soundsCheckBox.setVisibility(View.VISIBLE);
            vibrationsCheckBox.setVisibility(View.VISIBLE);
        }

        SharedPreferences prefs = getSharedPreferences("notificationsSave", Context.MODE_PRIVATE);
        boolean n = !prefs.getBoolean("notifications", true),
                s = !prefs.getBoolean("sounds", true),
                v = !prefs.getBoolean("vibrations", true);
        if (n == false) {
            s = false;
            v = false;
        }
        prefs.edit().putBoolean("notifications", n).putBoolean("sounds", s).putBoolean("vibrations", v).apply();
        prefs.edit().commit();
    }

    public void soundsNotificationClick(View view) {
        SharedPreferences prefs = getSharedPreferences("notificationsSave", Context.MODE_PRIVATE);
        boolean s = !prefs.getBoolean("sounds", true);
        prefs.edit().putBoolean("sounds", s).apply();
        prefs.edit().commit();
    }

    public void vibrationsNotificationClick(View view) {
        SharedPreferences prefs = getSharedPreferences("notificationsSave", Context.MODE_PRIVATE);
        boolean v = !prefs.getBoolean("vibrations", true);
        prefs.edit().putBoolean("vibrations", v).apply();
        prefs.edit().commit();
    }

    public void activeTasksClick(View view) {
        SharedPreferences prefs = getSharedPreferences("notificationsSave", Context.MODE_PRIVATE);
        boolean a = !prefs.getBoolean("active", true);
        prefs.edit().putBoolean("active", a).apply();
        prefs.edit().commit();

        updateNotification();
    }

    public void setTheme(View view) {
        willSend = true;
        super.recreate();
    }

    public void deleteAll(View view) {
        deleteDialogBox();
    }

    public void deleteDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);


        builder.setTitle("Reset All");

        builder.setMessage("Are you sure you want to reset everything?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg0) {

                        clearApplicationData();

                        Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();

                        checkStoragePermission();


                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.cancel(0);

                        System.exit(0);

                        Intent intent = new Intent("com.thejuanandonly.schoolapp");
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg0) {
                    }
                });

        builder.show();
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    public void subjectDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a subject");

        final EditText input = new EditText(this);
        input.setHint("Subject name");
        input.setPadding(50, 50, 50, 30);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        builder.setView(input);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String subjectInput = input.getText().toString();

                saveSubject(subjectInput);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void saveSubject(String subject) {

        JSONArray set = new JSONArray();

        SharedPreferences prefs = getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);
        try {
            set = new JSONArray(prefs.getString("List", null));

        } catch (Exception e) {
        }

        for (int i = 0; i < set.length(); i++){

            try {
                if (subject.equals(set.getString(i))){

                    Toast.makeText(this, "This subject already exists", Toast.LENGTH_LONG).show();
                    subjectDialog();
                    return;

                }
            } catch (JSONException e) {
            }
        }

        if (subject != null && subject.length() > 0) {

            try {
                set.put(subject);
            } catch (Exception e) {}

        } else {
            Toast.makeText(this, "Don't leave the space blank!", Toast.LENGTH_LONG).show();
            subjectDialog();
            return;
        }

        //SP pre kazdy predmet
        SharedPreferences preferences = getSharedPreferences("Subject" + subject, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("subject", subject).apply();

        //Zoznam predmetov
        SharedPreferences arrayPrefs = getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);
        SharedPreferences.Editor arrayPrefsEditor = arrayPrefs.edit();
        arrayPrefsEditor.putString("List", set.toString()).apply();


        OverviewFragment.reset(this);
    }

    public void notesDialog() {
        AlertDialog.Builder builder_notes = new AlertDialog.Builder(this);
        builder_notes.setTitle("Add a subject");

        final EditText input_notes = new EditText(this);
        input_notes.setHint("Subject name");
        input_notes.setPadding(50, 50, 50, 30);
        input_notes.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        builder_notes.setView(input_notes);

        builder_notes.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String subjectInputNotes = input_notes.getText().toString();

                saveSubjectNotes(subjectInputNotes);
                NotesFragment.reset(MainActivity.this);
            }
        });
        builder_notes.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder_notes.show();

    }

    private void saveSubjectNotes(String subjectNotes) {
        SharedPreferences prefs_notes = getSharedPreferences("ListOfSubjectsNotes", Context.MODE_PRIVATE);
        ArrayList<String> m_listItems_notes = new ArrayList<>();
        JSONArray set_notes = new JSONArray();

        try {
            set_notes = new JSONArray(prefs_notes.getString("ListNotes", null));
        } catch (Exception e) {
        }

        if (subjectNotes != null && subjectNotes.length() > 0) {
            set_notes.put(subjectNotes);
        } else {
            Toast.makeText(this, "Don't leave the space blank!", Toast.LENGTH_LONG).show();
            notesDialog();
        }

        //SP pre kazdy predmet
        SharedPreferences preferences_notes = getSharedPreferences("SubjectNotes" + subjectNotes, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor_notes = preferences_notes.edit();
        editor_notes.putString("subjectNotes", subjectNotes).apply();

        //Zoznam predmetov
        SharedPreferences arrayPrefs_notes = getSharedPreferences("ListOfSubjectsNotes", Context.MODE_PRIVATE);
        SharedPreferences.Editor arrayPrefsEditor_notes = arrayPrefs_notes.edit();
        arrayPrefsEditor_notes.putString("ListNotes", set_notes.toString()).apply();

        NotesFragment.reset(this);
    }

    public void updateListTasks() {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerView, new TasksFragment()).commit();
    }

    public void checkStoragePermission(){

        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userNickname = sharedPreferences.getString("nickname", null);
        if (userNickname == null) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        if (getSharedPreferences("Global", Context.MODE_PRIVATE).getBoolean("doPermissionCheck", true)) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                RelativeLayout user = (RelativeLayout) findViewById(R.id.userDetailLayout);
                LinearLayout permis = (LinearLayout) findViewById(R.id.permissionLayout);
                user.setVisibility(View.GONE);
                permis.setVisibility(View.VISIBLE);

                Button turnOn = (Button) findViewById(R.id.turnOn);
                Button notNow = (Button) findViewById(R.id.notNow);

                turnOn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                    }
                });
                notNow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getSharedPreferences("Global", Context.MODE_PRIVATE).edit().putBoolean("doPermissionCheck", false).apply();
                        updateUserDetails(false);
                    }
                });
            } else {
                updateUserDetails(true);
            }
        }else {
            updateUserDetails(false);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    updateUserDetails(true);

                } else {


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    updateUserDetails(false);
                }
                break;
            }
            case 2:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    super.recreate();
                }

                break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void updateUserDetails(boolean usePhotos) {
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userNickname = sharedPreferences.getString("nickname", null);
        String imageUriString = sharedPreferences.getString("avatar", null);

        RelativeLayout user = (RelativeLayout) findViewById(R.id.userDetailLayout);
        LinearLayout permis = (LinearLayout) findViewById(R.id.permissionLayout);
        user.setVisibility(View.VISIBLE);
        permis.setVisibility(View.GONE);

        if (usePhotos && imageUriString != null) {
            Bitmap bitmap = null;
            int w = 0, h = 0;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(imageUriString));
                w = bitmap.getWidth();
                h = bitmap.getHeight();
            } catch (IOException e) {
                Toast.makeText(this, "exception", Toast.LENGTH_SHORT).show();
            }
            int radius = w > h ? h : w;
            Bitmap roundBitmap = ImageToCircle.getCroppedBitmap(bitmap, radius);

            userPhotoimgview.setImageBitmap(roundBitmap);
        }
        else {

          DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

            int height = displaymetrics.heightPixels;

            NAVd = (RelativeLayout)findViewById(R.id.userDetails);
            if (height <= 853) {
                user.setBackgroundResource(R.drawable.not_now_lowres);
            } else {
                user.setBackgroundResource(R.drawable.not_now);
            }


            RelativeLayout photos = (RelativeLayout) findViewById(R.id.usersPhotoLayout);
            photos.setVisibility(View.INVISIBLE);
        }

        userNicktxtview.setText(userNickname);
    }

    public void updateNotification() {
        SharedPreferences prefss = getSharedPreferences("notificationsSave", Context.MODE_PRIVATE);
        SharedPreferences prefsss = getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);

        boolean a = prefss.getBoolean("active", true);
        int numberOfTasks = prefsss.getInt("NumberOfTask", 0);

        if (a == true && numberOfTasks > 0) {
            SharedPreferences prefs = getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
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

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("fromNotification", true);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

                Notification notification = new Notification.Builder(this)
                        .setContentTitle(numberOfTask + nameForAlways)
                        .setContentText(childWithNames.substring(1, childWithNames.length()-1))
                        .setSmallIcon(R.drawable.ic_mail_white_24dp)
                        .setContentIntent(contentIntent).build();

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notification.flags |= Notification.FLAG_NO_CLEAR;
                notificationManager.notify(0, notification);
            }
        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        }
    }
}


