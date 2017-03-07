package com.thejuanandonly.schoolapp;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.renderer.AxisRenderer;
import com.db.chart.view.LineChartView;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    LinearLayout drawerFull;
    public int actualFragment = 1;
    public static int api;
    public static int theme;
    public boolean willSend = false;
    public static boolean taskAdded = false;
    android.support.v7.widget.Toolbar toolbar;
    String reset;
    public String userNickname;

    private LineChartView lineChartView;

    //Notes stuff
    private static int RESULT_LOAD_IMAGE = 1;
    public String picture;
    public ArrayList<String> savedUriArrayList;
    public ArrayList<ArrayList<String>> arraylistOfArraylist;
    public int position;
    public int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        api = android.os.Build.VERSION.SDK_INT;

        drawerFull = (LinearLayout) findViewById(R.id.drawerFull);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        lineChartView = (LineChartView) findViewById(R.id.lcv_nav_chart);

        updateChart();

        checkStoragePermission();
        setLevel();
        levelTimer();

        TextView overallTv = (TextView) findViewById(R.id.tv_overall);
        overallTv.setText(setOverall() + "");

        Locale.setDefault(Locale.US);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int height = displaymetrics.heightPixels;

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
                TextView overallTv = (TextView) findViewById(R.id.tv_overall);
                overallTv.setText(setOverall());

                if (menuItem.getItemId() == R.id.nav_item_overview) {
                    mNavigationView.setItemBackground(getResources().getDrawable(R.drawable.nav_overview));
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new OverviewFragment()).commit();
                    mNavigationView.setCheckedItem(R.id.nav_item_overview);
                    actualFragment = 1;
                    invalidateOptionsMenu();
                }

                if (menuItem.getItemId() == R.id.nav_item_tasks) {
                    mNavigationView.setItemBackground(getResources().getDrawable(R.drawable.nav_tasks));
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new TasksFragment()).commit();
                    mNavigationView.setCheckedItem(R.id.nav_item_tasks);
                    actualFragment = 2;
                    invalidateOptionsMenu();
                }

                if (menuItem.getItemId() == R.id.nav_item_notes) {
                    mNavigationView.setItemBackground(getResources().getDrawable(R.drawable.nav_notes));
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new NotesFragment(), "NotesFragment").commit();
                    mNavigationView.setCheckedItem(R.id.nav_item_notes);
                    actualFragment = 3;
                    invalidateOptionsMenu();
                }

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    mNavigationView.setItemBackground(getResources().getDrawable(R.drawable.nav_settings));
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new SettingsFragment()).commit();
                    mNavigationView.setCheckedItem(R.id.nav_item_settings);
                    actualFragment = 4;
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

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "vibrate", Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WAKE_LOCK)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "wake lock", Toast.LENGTH_SHORT).show();
        }

        mNavigationView.setItemBackground(getResources().getDrawable(R.drawable.nav_overview));
        mNavigationView.setCheckedItem(R.id.nav_item_overview);
        setTasksCount();

        alwaysOnScreen();

        registerAlarm(getApplicationContext());
    }

    public static void registerAlarm(Context context) {
        Intent i = new Intent(context, NotificationRecieverActivity.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, 0, i, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, sender);
        else am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000, sender);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(drawerFull)) {
            mDrawerLayout.closeDrawer(drawerFull);
        } else {
            if (actualFragment == 2) {
                FragmentManager fm = getSupportFragmentManager();
                TasksFragment fragment = (TasksFragment) fm.findFragmentById(R.id.containerView);
                if (fragment.rlSwitch.getVisibility() != View.VISIBLE) {
                    fragment.etName.setText("");
                    fragment.etBody.setText("");
                    fragment.btnTime.setText("time");
                    fragment.btnDate.setText("date");
                    fragment.time = new Date();

                    fragment.initializeHeader(false, false, 0);
                } else super.onBackPressed();
            } else super.onBackPressed();

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

        updateChart();

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
        } else if (actualFragment == 4) {
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
        } else if (id == R.id.action_new_note_group) {


            FragmentManager fm = getSupportFragmentManager();
            NotesFragment fragment = (NotesFragment) fm.findFragmentByTag("NotesFragment");
            fragment.openDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTasksCount() {
        SharedPreferences prefs = getSharedPreferences("ListOfTasks", MODE_PRIVATE);
        JSONArray arrayName;
        try {
            arrayName = new JSONArray(prefs.getString("TaskName", null));
        } catch (Exception e) {
            arrayName = new JSONArray();
        }

        int count = arrayName.length();

        mNavigationView.getMenu().findItem(R.id.nav_item_tasks).setActionView(R.layout.nav_tasks_counter);

        TextView view = (TextView) mNavigationView.getMenu().findItem(R.id.nav_item_tasks).getActionView().findViewById(R.id.tv_count);
        view.setText(count + "");
    }

    public void addingImages(int position, int count) {

        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);

        this.position = position;
        this.count = count;


    }

    public void updateChart() {
        String[] mLabels = getLabels();
        float[] mValues = getValues();

        try {
            lineChartView.reset();

            if (mValues.length > 5) {
                String[] actual = new String[mLabels.length];

                for (int a = 0; a < mLabels.length; a++) {
                    actual[a] = mLabels[a];
                    for (int b = 0; b < a; b++) {
                        if (actual[b].equals(actual[a])) {
                            actual[a] = "";
                            b = a;
                        }
                    }
                }

                ArrayList<Integer> array = new ArrayList<>();
                for (int a = 0; a < actual.length; a++) {
                    if (actual[a].length() > 1) {
                        array.add(a);
                    }
                }

                //removing duplicates from each day
                SharedPreferences prefs = getSharedPreferences("navChart", MODE_PRIVATE);
                JSONArray jsonLabels, jsonValues, savedLabels;
                try {
                    savedLabels = new JSONArray(prefs.getString("labels", null));
                } catch (Exception e) {
                    savedLabels = new JSONArray();
                }
                jsonLabels = new JSONArray();
                jsonValues = new JSONArray();

                for (int a = 0; a < array.size(); a++) {
                    int until;
                    try {
                        until = array.get(a + 1) - 1;
                    } catch (Exception e) {
                        until = mValues.length - 1;
                    }

                    try {
                        jsonLabels.put(savedLabels.get(until));
                    } catch (Exception e) {
                    }
                    jsonValues.put(mValues[until] + "");
                }

                prefs.edit().putString("labels", jsonLabels.toString()).apply();
                prefs.edit().putString("values", jsonValues.toString()).apply();

                if (array.size() > 30) {
                    int months = 1;
                    for (int a = 1; a < mLabels.length; a++) {
                        if (!mLabels[a].substring(3, 11).equals(mLabels[a - 1].substring(3, 11)))
                            months++;
                    }

                    if (months > 5) {
                        float[] values = new float[5];
                        String[] labels = new String[5];

                        for (int a = 1; a < 6; a++) {
                            float average = 0;
                            int count = 0;
                            int period = months / 5;

                            for (int b = (a - 1) * period; b < a * period; b++) {
                                average += mValues[b];
                                count++;
                            }

                            values[a - 1] = average / count;
                            labels[a - 1] = "";
                        }
                        labels[1] = "Last " + months + " months";

                        mLabels = labels;
                        mValues = values;
                    } else if (months > 2 && months < 6) {
                        float[] values = new float[months];
                        String[] labels = new String[months];

                        for (int a = 0; a < months; a++) {
                            float average = 0;
                            int count = 0;
                            int month = 0;

                            for (int b = 1; b < mValues.length; b++) {
                                if (!mLabels[b].substring(3, 11).equals(mLabels[b - 1].substring(3, 11))) {
                                    average += mValues[b - 1];
                                    month = Integer.parseInt(mLabels[b - 1].substring(3, 5));
                                    break;
                                } else average += mValues[b - 1];

                                count++;
                            }

                            values[a] = average / count;
                            labels[a] = month + "";
                        }

                        mLabels = labels;
                        mValues = values;
                    } else {
                        int period = mValues.length / 5;
                        float[] values = new float[5];
                        String[] labels = new String[5];

                        for (int a = 1; a < 6; a++) {
                            float average = 0;
                            int count = 0;
                            for (int b = (a - 1) * period; b < a * period; b++) {
                                average += mValues[b];
                                count++;
                            }

                            values[a - 1] = average / count;
                            labels[a - 1] = "";
                        }
                        labels[1] = "Last 60 days";

                        mLabels = labels;
                        mValues = values;
                    }
                } else if (array.size() > 1 && array.size() < 31) {
                    float[] values = new float[array.size()];
                    String[] labels = new String[array.size()];

                    for (int a = 0; a < array.size(); a++) {
                        int until;
                        try {
                            until = array.get(a + 1) - 1;
                        } catch (Exception e) {
                            until = mValues.length - 1;
                        }

                        values[a] = mValues[until];

                        if (array.size() > 1 && array.size() < 5)
                            labels[a] = mLabels[array.get(a)].substring(0, 6);
                        else labels[a] = mLabels[array.get(a)];
                    }

                    mLabels = labels;
                    mValues = values;

                    values = new float[5];
                    labels = new String[5];
                    if (array.size() > 5) {
                        for (int a = 1; a < 6; a++) {
                            float average = 0;
                            int count = 0;
                            int period = array.size() / 5;

                            for (int b = (a - 1) * period; b < a * period; b++) {
                                average += mValues[b];
                                count++;
                            }

                            values[a - 1] = average / count;
                            labels[a - 1] = "";
                        }
                        labels[1] = "Last 30 days";

                        mLabels = labels;
                        mValues = values;
                    }
                } else {
                    return;
                }
            } else {
                for (int a = 0; a < mLabels.length; a++) {
                    mLabels[a] = mLabels[a].substring(0, 6);
                }
            }

            LineSet dataset = new LineSet(mLabels, mValues);
            dataset.setColor(Color.parseColor("#758cbb"))
                    .setFill(Color.parseColor("#2d374c"))
                    .setDotsColor(Color.parseColor("#758cbb"))
                    .setThickness(4)
                    .setDashed(new float[]{10f, 10f})
                    .beginAt(mValues.length - 1);
            lineChartView.addData(dataset);

            dataset = new LineSet(mLabels, mValues);
            dataset.setColor(Color.parseColor("#b3b5bb"))
                    .setFill(Color.parseColor("#2d374c"))
                    .setDotsColor(Color.parseColor("#ffc755"))
                    .setThickness(4)
                    .endAt(mValues.length);
            lineChartView.addData(dataset);

            float min = mValues[0];
            for (int i = 1; i < mValues.length; i++) {
                if (mValues[i] < min) {
                    min = mValues[i];
                }
            }

            min -= 0.5;

            if (min < 1) min = 1;

            lineChartView.setBorderSpacing(Tools.fromDpToPx(10))
                    .setAxisBorderValues((int) min, 5)
                    .setYLabels(AxisRenderer.LabelPosition.OUTSIDE)
                    .setLabelsColor(Color.parseColor("#6a84c3"))
                    .setXAxis(false)
                    .setYAxis(false);

            if (mValues.length > 1) {
                lineChartView.show();
            }
        } catch (Exception e) {
        }
    }

    private String[] getLabels() {
        try {
            String[] labels;

            SharedPreferences prefs = getSharedPreferences("navChart", MODE_PRIVATE);
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(prefs.getString("labels", null));
            } catch (Exception e) {
                jsonArray = new JSONArray();
            }

            labels = new String[jsonArray.length() + 1];

            for (int a = 0; a < jsonArray.length(); a++) {
                try {
                    labels[a] = jsonArray.getString(a);
                } catch (Exception e) {
                }
            }

            long actual;
            if (setOverall().length() > 0) {
                actual = System.currentTimeMillis();
            } else {
                return labels;
            }

            labels[labels.length - 1] = actual + "";
            jsonArray.put(actual);

            prefs.edit().putString("labels", jsonArray.toString()).apply();

            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM. yyyy");
            Calendar calendar = Calendar.getInstance();

            for (int a = 0; a < labels.length; a++) {
                calendar.setTimeInMillis(Long.parseLong(labels[a]));
                labels[a] = formatter.format(calendar.getTime());
            }

            return labels;
        } catch (Exception e) {
            return null;
        }
    }

    private float[] getValues() {
        try {
            float[] values;

            SharedPreferences prefs = getSharedPreferences("navChart", MODE_PRIVATE);
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(prefs.getString("values", null));
            } catch (Exception e) {
                jsonArray = new JSONArray();
            }

            values = new float[jsonArray.length() + 1];

            for (int a = 0; a < jsonArray.length(); a++) {
                try {
                    values[a] = Float.parseFloat(jsonArray.getString(a));
                } catch (Exception e) {
                }
            }

            float actual;
            try {
                actual = 6 - Float.parseFloat(setOverall().substring(9, setOverall().length()));
            } catch (Exception e) {
                return values;
            }

            values[values.length - 1] = actual;
            jsonArray.put(actual + "");

            prefs.edit().putString("values", jsonArray.toString()).apply();

            return values;
        } catch (Exception e) {
            return null;
        }
    }

    public void alwaysOnScreen() {
        JSONArray arrayName;
        SharedPreferences preferences = getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);

        try {
            arrayName = new JSONArray(preferences.getString("TaskName", null));
        } catch (Exception e) {
            arrayName = new JSONArray();
        }

        int numberOfTask = arrayName.length();


        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);

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

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fromNotification", true);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

            Notification.Builder builder = new Notification.Builder(this)
                    .setContentTitle(numberOfTask + nameForAlways)
                    .setContentText(childWithNames.substring(1, childWithNames.length()-1))
                    .setSmallIcon(R.drawable.ic_active_tasks)
                    .setContentIntent(contentIntent);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setSmallIcon(R.drawable.ic_active_white);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_active_white));
            }

            Notification notification = builder.build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notificationManager.notify(0, notification);
        } else {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            picture = cursor.getString(columnIndex);
            cursor.close();


            SaveSharedPreferences saved = new SaveSharedPreferences();

            savedUriArrayList = new ArrayList<>();
            arraylistOfArraylist = new ArrayList<>();

            String arrayOfArraysString = saved.getStringSP(MainActivity.this, "NOTES ARRAY", "arrayofarrays");

            String temp = "";
            ArrayList<String> tempArray = new ArrayList<>();
            try {
                char[] charArray = arrayOfArraysString.toCharArray();
                for (int i = 0; i < charArray.length; i++) {
                    if (charArray[i] == '`') {
                        tempArray.add(temp);
                        temp = "";
                    } else if (charArray[i] == '~') {
                        arraylistOfArraylist.add(tempArray);
                        tempArray = new ArrayList<>();
                    } else {
                        temp += charArray[i];
                    }
                }
            } catch (NullPointerException e) {

            }


            try {
                savedUriArrayList = arraylistOfArraylist.get(position);
            } catch (IndexOutOfBoundsException e) {
                savedUriArrayList = new ArrayList<>();
            }

            savedUriArrayList.add(picture);
            try {
                arraylistOfArraylist.remove(position);
            } catch (IndexOutOfBoundsException e) {
            }
            arraylistOfArraylist.add(position, savedUriArrayList);

            String save = "";
            for (int i = 0; i < arraylistOfArraylist.size(); i++) {
                for (int j = 0; j < arraylistOfArraylist.get(i).size(); j++) {
                    save += arraylistOfArraylist.get(i).get(j) + "`";
                }
                save += "~";
            }

            saved.saveToSharedPreferences(MainActivity.this, "NOTES ARRAY", "arrayofarrays", save);


            FragmentManager fm = getSupportFragmentManager();
            NotesFragment fragment = (NotesFragment) fm.findFragmentByTag("NotesFragment");
            fragment.notifyAdapter();


        }
    }

    public void levelTimer() {
        new CountDownTimer(300000, 3000) {

            public void onTick(long millisUntilFinished) {
                if (mDrawerLayout.isDrawerOpen(drawerFull)) {
                    setLevel();
                }
            }

            public void onFinish() {
                levelTimer();
            }
        }.start();
    }

    public void setLevel() {
        TextView levelText = (TextView) findViewById(R.id.levelText);
        ProgressBar bar = (ProgressBar) findViewById(R.id.levelProgress);
        TextView xp = (TextView) findViewById(R.id.xpProgress);

        SharedPreferences arrayPrefs = getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);

        int[] array = getResources().getIntArray(R.array.level_ints);
        int[] borders = getResources().getIntArray(R.array.borders);

        TypedArray colors = getResources().obtainTypedArray(R.array.dotColors);

        int points = 0;

        JSONArray arrayOfSubjects = new JSONArray();
        try {
            arrayOfSubjects = new JSONArray(arrayPrefs.getString("List", null));
        } catch (Exception e) {
        }

        for (int i = 0; i < arrayOfSubjects.length(); i++) {

            String currentSubj = "";
            try {
                currentSubj = arrayOfSubjects.getString(i);
            } catch (JSONException e) {
            }

            SharedPreferences prefs = getSharedPreferences("Subject" + currentSubj, Context.MODE_PRIVATE);
            double avg = Double.parseDouble(prefs.getString("AvgGrade", "0"));
            int gradeType = prefs.getInt("GradeType", 0);

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

                    for (int j = 0; j < categories.length(); j++) {

                        JSONArray arrayOfGrades = new JSONArray(prefs.getString(categories.getString(j) + "Grades" + prefs.getInt("GradeType", 0), null));

                        for (int k = 0; k < arrayOfGrades.length(); k++) {

                            switch (prefs.getInt("GradeType", 0)) {
                                case 0:
                                    switch ((int) Math.round(Double.parseDouble(arrayOfGrades.getString(k)))) {
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
                } catch (Exception ignored) {
                }
            }
        }

        SharedPreferences prefsTasks = getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
        JSONArray arrayName, arrayNameDone;
        try {
            arrayName = new JSONArray(prefsTasks.getString("TaskName", null));
            arrayNameDone = new JSONArray(prefsTasks.getString("DoneTaskName", null));
        } catch (Exception e) {
            arrayName = new JSONArray();
            arrayNameDone = new JSONArray();
        }

        int numberOfTasks = arrayName.length();
        int numberOfDoneTask = arrayNameDone.length();

        points += numberOfTasks * array[6];
        Log.d("debug", String.valueOf(numberOfTasks * array[6]));


        points += numberOfDoneTask * array[7];
        Log.d("debug", String.valueOf(numberOfDoneTask * array[7]));

        if (points <= borders[0]) {
            levelText.setText("level 1");

            bar.setMax(borders[0]);
            bar.setProgress(points);

            xp.setText(String.valueOf(points) + "xp" + " / " + String.valueOf(borders[0]) + "xp");

        } else {

            int finalBorder = borders[0];
            int level = 1;
            int imglvl = 0;
            int max = 0;
            while (points >= finalBorder) {
                finalBorder += borders[6] + (0.05 * finalBorder);

                max = (int) (borders[6] + (0.05 * finalBorder));

                level++;

                Log.d("debugL", String.valueOf(finalBorder));

                imglvl++;
                if (imglvl == 7) imglvl = 0;
            }

            levelText.setText("level " + level);

            bar.setMax(max);
            bar.setProgress(points - (finalBorder - max));

            xp.setText(String.valueOf(points - (finalBorder - max)) + "xp" + " / " + String.valueOf(max) + "xp");
        }

        colors.recycle();
    }

    public String setOverall() {
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
        } catch (Exception ignored) {
        }

        if (arrayOfSubjects.length() == 0) {
            return "";
        }

        for (int i = 0; i < arrayOfSubjects.length(); i++) {

            String currentSubj = "";
            try {
                currentSubj = arrayOfSubjects.getString(i);
            } catch (JSONException ignored) {
            }

            SharedPreferences prefs = getSharedPreferences("Subject" + currentSubj, Context.MODE_PRIVATE);
            int gradeType = prefs.getInt("GradeType", 0);

            switch (gradeType) {
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

        if ((normal != 0 || percent != 0) && alphab == 0 && tenGrade == 0) {
            for (int i = 0; i < arrayOfSubjects.length(); i++) {

                String currentSubj = "";
                try {
                    currentSubj = arrayOfSubjects.getString(i);
                } catch (JSONException ignored) {
                }

                SharedPreferences prefs = getSharedPreferences("Subject" + currentSubj, Context.MODE_PRIVATE);
                int gradeType = prefs.getInt("GradeType", 0);
                double avg = Double.parseDouble(prefs.getString("AvgGrade", "0"));

                if (avg != 0) {

                    if (gradeType == 0) {
                        ovr += avg;
                        ovrCnt++;
                    } else if (gradeType == 1) {
                        SharedPreferences conv = getSharedPreferences("Global", Context.MODE_PRIVATE);
                        try {
                            JSONArray conversionArray = new JSONArray(conv.getString("conversion", null));

                            if (avg >= conversionArray.getInt(0)) {
                                ovr += 1;
                                ovrCnt++;
                            } else if (avg >= conversionArray.getInt(1)) {
                                ovr += 2;
                                ovrCnt++;
                            } else if (avg >= conversionArray.getInt(2)) {
                                ovr += 3;
                                ovrCnt++;
                            } else if (avg >= conversionArray.getInt(3)) {
                                ovr += 4;
                                ovrCnt++;
                            } else {
                                ovr += 5;
                                ovrCnt++;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            try {
                double d = Double.parseDouble(String.valueOf(ovr)) / Double.parseDouble(String.valueOf(ovrCnt));
                String s;
                try {
                    s = String.valueOf(d).substring(0, 4);
                } catch (StringIndexOutOfBoundsException e) {
                    s = String.valueOf(d);
                }
                Log.d("debugC", ovr + ", " + ovrCnt + ", " + d);

                return ("Overall: " + s);
            } catch (ArithmeticException e) {
                //overallTv.setText("");
                Log.e("debug", e.toString());
            }

        } else {
            for (int i = 0; i < arrayOfSubjects.length(); i++) {

                String currentSubj = "";
                try {
                    currentSubj = arrayOfSubjects.getString(i);
                } catch (JSONException e) {
                }

                SharedPreferences prefs = getSharedPreferences("Subject" + currentSubj, Context.MODE_PRIVATE);
                int gradeType = prefs.getInt("GradeType", 0);
                double avg = Double.parseDouble(prefs.getString("AvgGrade", "0"));

                switch (gradeType) {
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

            try {
                double d = Double.parseDouble(String.valueOf(ovr)) / Double.parseDouble(String.valueOf(ovrCnt));
                String s;
                try {
                    s = String.valueOf(d).substring(0, 4);
                } catch (StringIndexOutOfBoundsException e) {
                    s = String.valueOf(d);
                }

                Log.d("debugC", ovr + ", " + ovrCnt + ", " + d);

                return ("Overall: " + s + " / 10");
            } catch (ArithmeticException e) {
                //overallTv.setText("");
                Log.e("debug", e.toString());
            }
        }

        return "";
    }

    public void notificationsClick(final View view) {
        final SwitchCompat notificationsCheckBox, soundsCheckBox, vibrationsCheckBox, activeTasksCheckBox;
        notificationsCheckBox = (SwitchCompat) findViewById(R.id.notificationsCheckBox);
        soundsCheckBox = (SwitchCompat) findViewById(R.id.soundsNotificationCheckBox);
        vibrationsCheckBox = (SwitchCompat) findViewById(R.id.vibrationsNotificationCheckBox);

        final boolean isChecked = notificationsCheckBox.isChecked();
        soundsCheckBox.setChecked(isChecked);
        vibrationsCheckBox.setChecked(isChecked);

        Handler handler = new Handler();

        final AlphaAnimation fadeOutAnim = new AlphaAnimation(100, 0);
        fadeOutAnim.setDuration(2000);
        final AlphaAnimation fadeInAnim = new AlphaAnimation(0, 100);
        fadeInAnim.setDuration(2000);


        if (!isChecked) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    soundsCheckBox.setVisibility(View.GONE);
                    vibrationsCheckBox.setVisibility(View.GONE);
                }
            }, 300);

        } else {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    soundsCheckBox.setVisibility(View.VISIBLE);
                    vibrationsCheckBox.setVisibility(View.VISIBLE);
                }
            });

        }


        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
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
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean s = !prefs.getBoolean("sounds", true);
        prefs.edit().putBoolean("sounds", s).apply();
        prefs.edit().commit();
    }

    public void vibrationsNotificationClick(View view) {
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean v = !prefs.getBoolean("vibrations", true);
        prefs.edit().putBoolean("vibrations", v).apply();
        prefs.edit().commit();
    }

    public void activeTasksClick(View view) {
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean a = !prefs.getBoolean("active", true);
        prefs.edit().putBoolean("active", a).apply();
        prefs.edit().commit();

        alwaysOnScreen();
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


        builder.setTitle("Reset App");

        builder.setMessage("All your saved data will be deleted, are you sure you want to reset the app?")

                .setPositiveButton("RESET", new DialogInterface.OnClickListener() {
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
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
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


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }

    public void saveSubject(String subject) {

        JSONArray jsonArray = new JSONArray();

        SharedPreferences prefs = getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);
        try {
            jsonArray = new JSONArray(prefs.getString("List", null));

        } catch (Exception e) {
        }

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                if (subject.equals(jsonArray.getString(i))) {

                    Toast.makeText(this, "This subject already exists", Toast.LENGTH_LONG).show();
                    subjectDialog();
                    return;

                }
            } catch (JSONException e) {
            }
        }

        if (subject != null && subject.length() > 0) {

            try {
                jsonArray.put(subject);
            } catch (Exception e) {
            }

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
        arrayPrefsEditor.putString("List", jsonArray.toString()).apply();


        OverviewFragment.reset(this);
    }

    public void updateListTasks() {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.containerView, new TasksFragment()).commit();
    }

    public void checkStoragePermission() {

        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userNickname = sharedPreferences.getString("nickname", null);
        if (userNickname == null) { //TODO: change this
            startActivity(new Intent(this, TutikActivity.class));
            return;
        }

        if (getSharedPreferences("Global", Context.MODE_PRIVATE).getBoolean("doPermissionCheck", true)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}


