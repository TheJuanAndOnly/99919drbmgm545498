package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class TaskAdder extends ActionBarActivity {

    private EditText nameTask, whatTask;
    private Toolbar toolbar;
    public String editName, editWhat;
    public long editTime;
    public Date time;
    private Button btnTime, btnDate;

    public boolean editing;

    public JSONArray arrayName, arrayWhat, arrayTime;
    public int numberOfTask;

    public TimePicker timePicker;
    public DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_adder_activity);

        Intent intent = getIntent();
        editName = intent.getStringExtra("EditTaskName");
        editWhat = intent.getStringExtra("EditTaskWhat");
        editTime = intent.getLongExtra("EditTaskTime", 0);

        nameTask = (EditText) findViewById(R.id.editText_nameTask);
        whatTask = (EditText) findViewById(R.id.editText_whatTask);

        time = new Date();

        btnTime = (Button) findViewById(R.id.btnTime);
        btnDate = (Button) findViewById(R.id.btnDate);

        editing = false;

        if (editName != null && editName.length() > 0 && editWhat != null && editWhat.length() > 0) {
            nameTask.setText(editName);
            whatTask.setText(editWhat);
            time.setTime(editTime);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(editTime);

            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);

            if (minutes < 10) {
                btnTime.setText(hours + " : 0" + minutes);
            } else {
                btnTime.setText(hours + " : " + minutes);
            }
            btnDate.setText(day + "." + month + ". " + year);
            editing = true;
        } else {
            time.setTime(System.currentTimeMillis());
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editing == false && time.getTime() == 0) {
                    time.setTime(System.currentTimeMillis());
                }
                setTime(time.getHours(), time.getMinutes());
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int yearplus = 0;
                if (time.getYear() < 1900) {
                    yearplus = 1900;
                }
                setDate(time.getDate(), time.getMonth(), time.getYear() + yearplus);
            }
        });
        theme();
    }

    private void setTime(int hour, int minute) {
        final Dialog dialog = new Dialog(TaskAdder.this);
        dialog.setContentView(R.layout.dialog_set_time);
        dialog.setTitle("Remind time");
        dialog.setCancelable(true);

        timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        Button button = (Button) dialog.findViewById(R.id.btnTime);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time == null) {
                    time = new Date();
                }
                time.setHours(timePicker.getCurrentHour());
                time.setMinutes(timePicker.getCurrentMinute());
                time.setSeconds(0);

                String minutes = "";
                if (time.getMinutes() < 10) {
                    minutes = "0" + time.getMinutes();
                } else {
                    minutes = "" + time.getMinutes();
                }

                btnTime.setText(time.getHours() + " : " + minutes);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setDate(int day, int month, int year) {
        final Dialog dialog = new Dialog(TaskAdder.this);
        dialog.setContentView(R.layout.dialog_set_date);
        dialog.setTitle("Remind date");
        dialog.setCancelable(true);

        datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
        datePicker.updateDate(year, month, day);

        Button button = (Button) dialog.findViewById(R.id.btnDate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (time == null) {
                    time = new Date();
                }
                time.setDate(datePicker.getDayOfMonth());
                time.setMonth(datePicker.getMonth());
                time.setYear(datePicker.getYear() - 1900);

                int month = time.getMonth() + 1;

                btnDate.setText(time.getDate() + "." + month + ". " + datePicker.getYear());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_adder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            if (editName != null && editName.length() > 0 && editWhat != null && editWhat.length() > 0) {
                if (time.getTime() > System.currentTimeMillis()) {
                    editTask();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter valid date and time", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (time.getTime() > System.currentTimeMillis()) {
                    addTask();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter valid date and time", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void theme() {
        SharedPreferences prefs = getSharedPreferences("themeSave", Context.MODE_PRIVATE);
        int theme = prefs.getInt("theme", 0);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);

        switch (theme) {
            case 1:
                toolbar.setBackgroundColor(getResources().getColor(R.color.orange));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP)
                    window.setStatusBarColor(getResources().getColor(R.color.orange700));

                break;
            case 2:
                toolbar.setBackgroundColor(getResources().getColor(R.color.green));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP)
                    window.setStatusBarColor(getResources().getColor(R.color.green800));

                break;
            case 3:

                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP)
                    window.setStatusBarColor(getResources().getColor(R.color.blue800));

                break;
            case 4:
                toolbar.setBackgroundColor(getResources().getColor(R.color.grey));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP)
                    window.setStatusBarColor(getResources().getColor(R.color.grey600));

                break;
            default:
                toolbar.setBackgroundColor(getResources().getColor(R.color.red));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP)
                    window.setStatusBarColor(getResources().getColor(R.color.red800));
        }

    }

    public void addTask() {

        if (nameTask.getText().toString() != null && nameTask.getText().toString().length() > 0 && whatTask.getText().toString() != null && whatTask.getText().toString().length() > 0 && !btnTime.getText().toString().equals("Set time") && !btnDate.getText().toString().equals("Set day")) {
            SharedPreferences prefs = getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            numberOfTask = prefs.getInt("NumberOfTask", 0);
            numberOfTask++;

            try {
                arrayName = new JSONArray(prefs.getString("TaskName", null));
                arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
                arrayTime = new JSONArray(prefs.getString("TaskTime", null));
            } catch (Exception e) {
                arrayName = new JSONArray();
                arrayWhat = new JSONArray();
                arrayTime = new JSONArray();
            }

            arrayName.put(nameTask.getText().toString());
            arrayWhat.put(whatTask.getText().toString());
            arrayTime.put(time.getTime());

            editor.putString("TaskName", arrayName.toString()).apply();
            editor.putString("TaskWhat", arrayWhat.toString()).apply();
            editor.putString("TaskTime", arrayTime.toString()).apply();

            editor.putInt("NumberOfTask", numberOfTask).apply();
            editor.commit();

            finish();

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent mAlarmSender = PendingIntent.getBroadcast(this, 0, new Intent(this, NotificationRecieverActivity.class), 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), mAlarmSender);

        } else if (nameTask.getText().toString() != null && nameTask.getText().toString().length() > 0 && whatTask.getText().toString() != null && whatTask.getText().toString().length() > 0 && btnTime.getText().toString().equals("Set time") || nameTask.getText().toString() != null && nameTask.getText().toString().length() > 0 && whatTask.getText().toString() != null && whatTask.getText().toString().length() > 0 && btnDate.getText().toString().equals("Set day")){
            Toast.makeText(this, "Please select time when do you want to remind", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Don't leave the space blank!", Toast.LENGTH_LONG).show();
        }

    }

    public void editTask() {
        if (nameTask.getText().toString() != null && nameTask.getText().toString().length() > 0 && whatTask.getText().toString() != null && whatTask.getText().toString().length() > 0) {
            SharedPreferences prefs = getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            try {
                arrayName = new JSONArray(prefs.getString("TaskName", null));
                arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
                arrayTime = new JSONArray(prefs.getString("TaskTime", null));
            } catch (Exception e) {
                arrayName = new JSONArray();
                arrayWhat = new JSONArray();
                arrayTime = new JSONArray();
            }

            ArrayList<String> listName = new ArrayList<String>();
            for (int i = 0; i < arrayName.length(); i++){
                try {
                    listName.add(arrayName.getString(i));
                } catch (Exception e) {
                }
            }

            listName.remove(editName);
            arrayName = new JSONArray(listName);

            ArrayList<String> listWhat = new ArrayList<String>();
            if (arrayWhat != null) {
                int len = arrayWhat.length();
                for (int i = 0; i < len ; i++){
                    try {
                        listWhat.add(arrayWhat.getString(i));
                    } catch (Exception e) {
                    }
                }
            }
            listWhat.remove(editWhat);
            arrayWhat = new JSONArray(listWhat);

            ArrayList<Long> listTime = new ArrayList<Long>();
            for (int i = 0; i < arrayTime.length(); i++){
                try {
                    listTime.add(arrayTime.getLong(i));
                } catch (Exception e) {
                }
            }

            listTime.remove(editTime);
            arrayTime = new JSONArray(listTime);

            arrayName.put(nameTask.getText().toString());
            arrayWhat.put(whatTask.getText().toString());
            arrayTime.put(time.getTime());

            editor.putString("TaskName", arrayName.toString()).apply();
            editor.putString("TaskWhat", arrayWhat.toString()).apply();
            editor.putString("TaskTime", arrayTime.toString()).apply();
            editor.commit();

            finish();

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent mAlarmSender = PendingIntent.getBroadcast(this, 0, new Intent(this, NotificationRecieverActivity.class), 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), mAlarmSender);
        }
    }

}
