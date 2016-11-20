package com.thejuanandonly.schoolapp;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.transitionseverywhere.TransitionManager;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;


public class TasksFragment extends Fragment {

    android.support.v7.widget.Toolbar toolbarMain, toolbar;

    private ListView listView;
    private TasksListviewAdapter tasksListviewAdapter;

    private EditText etName, etBody;
    private Button btnTime, btnDate, btnSave, btnClose;
    private Switch switchCurrent;

    private ViewGroup head, main, list;

    private LinearLayout llTime;
    private RelativeLayout rlSwitch;

    public TimePicker timePicker;
    public DatePicker datePicker;

    public Date time;

    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.tasks_layout, null);

        ImageView img = (ImageView) getActivity().findViewById(R.id.overviewImg);
        img.setVisibility(View.GONE);
        TextView quote = (TextView) getActivity().findViewById(R.id.quote);
        quote.setVisibility(View.GONE);
        TextView author = (TextView) getActivity().findViewById(R.id.author);
        author.setVisibility(View.GONE);


        prefs = getActivity().getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);

        toolbarMain = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarMain.setVisibility(View.GONE);


        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_head);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle(null);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(getActivity(), ((MainActivity) getActivity()).mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        ((MainActivity) getActivity()).mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        listView = (ListView) rootView.findViewById(R.id.lw_tasks);

        list = (ViewGroup) rootView.findViewById(R.id.lw_tasks);

        head = (ViewGroup) rootView.findViewById(R.id.head);

        etName = (EditText) rootView.findViewById(R.id.et_name);
        etBody = (EditText) rootView.findViewById(R.id.et_body);

        btnTime = (Button) rootView.findViewById(R.id.btnTime);
        btnDate = (Button) rootView.findViewById(R.id.btnDate);
        btnSave = (Button) rootView.findViewById(R.id.btn_save);
        btnClose = (Button) rootView.findViewById(R.id.btn_close);

        switchCurrent = (Switch) rootView.findViewById(R.id.switch_current);

        llTime = (LinearLayout) head.findViewById(R.id.ll_time);

        rlSwitch = (RelativeLayout) head.findViewById(R.id.rl_switch);
        main = (ViewGroup) rootView.findViewById(R.id.main);

        time = new Date();

        initializeHeader(false);

        updateTasks(true);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        toolbarMain.setVisibility(View.VISIBLE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbarMain);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(getActivity(), ((MainActivity) getActivity()).mDrawerLayout, toolbarMain, R.string.app_name, R.string.app_name);
        ((MainActivity) getActivity()).mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    public void initializeHeader(boolean adding) {
        if (adding) {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addTask();
                    etName.setText("");
                    etBody.setText("");
                    btnTime.setText("Set time");
                    btnDate.setText("Set date");
                    time = new Date();

                    btnClose.performClick();
                }
            });

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Rect r = new Rect();
                    getView().getWindowVisibleDisplayFrame(r);
                    int screenHeight = getView().getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }

                    initializeHeader(false);
                }
            });

            btnTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (time.getTime() == 0) {
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

            etName.setCursorVisible(true);
            etName.getBackground().mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            etBody.getBackground().mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

            TransitionManager.beginDelayedTransition(main);
            rlSwitch.setVisibility(View.GONE);

            TransitionManager.beginDelayedTransition(main);
            etName.setHint("Name your task");
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 16, 48, 0);
            etName.setLayoutParams(params);

            toolbar.getMenu().clear();

            TransitionManager.beginDelayedTransition(main);
            etBody.setVisibility(View.VISIBLE);
            llTime.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            btnClose.setVisibility(View.VISIBLE);

        } else {
            etName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initializeHeader(true);
                }
            });

            switchCurrent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateTasks(!isChecked);
                }
            });

            etName.setCursorVisible(false);
            etName.getBackground().mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

            TransitionManager.beginDelayedTransition(main);
            etBody.setVisibility(View.INVISIBLE);
            llTime.setVisibility(View.INVISIBLE);
            btnSave.setVisibility(View.INVISIBLE);
            btnClose.setVisibility(View.INVISIBLE);

            TransitionManager.beginDelayedTransition(main);
            etBody.setVisibility(View.GONE);
            llTime.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);
            btnClose.setVisibility(View.GONE);

            MenuItem edit_item = toolbar.getMenu().add(0, R.id.action_schedule, 100, "Schedule");
            edit_item.setIcon(R.drawable.ic_event_white_24dp);
            edit_item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 16, 0, 0);
            etName.setLayoutParams(params);

            TransitionManager.beginDelayedTransition(main);
            etName.setHint("Add new task");
            rlSwitch.setVisibility(View.VISIBLE);

        }
    }

    private void setTime(int hour, int minute) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_set_time);
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
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_set_date);
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

    public void updateTasks(boolean todo) {
        ArrayList<String> listViewItems = new ArrayList<>();

        JSONArray arrayName, arrayWhat, arrayTime;
        if (todo) {
            try {
                arrayName = new JSONArray(prefs.getString("TaskName", null));
                arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
                arrayTime = new JSONArray(prefs.getString("TaskTime", null));
            } catch (Exception e) {
                arrayName = new JSONArray();
                arrayWhat = new JSONArray();
                arrayTime = new JSONArray();
            }
        } else {
            try {
                arrayName = new JSONArray(prefs.getString("DoneTaskName", null));
                arrayWhat = new JSONArray(prefs.getString("DoneTaskWhat", null));
                arrayTime = new JSONArray(prefs.getString("DoneTaskTime", null));
            } catch (Exception e) {
                arrayName = new JSONArray();
                arrayWhat = new JSONArray();
                arrayTime = new JSONArray();
            }
        }


        ArrayList<Date> dates = new ArrayList<>();
        for (int a = 0; a < arrayTime.length(); a++) {
            try {
                dates.add(new Date(arrayTime.getLong(a)));
            } catch (Exception e) {
            }
        }

        for (int a = 0; a < arrayName.length(); a++) {
            String item = null;
            try {
                item = arrayName.get(a) + "|name|" + arrayWhat.get(a) + "|body|" + arrayTime.getLong(a);
            } catch (Exception e) {
            }

            listViewItems.add(item);
        }

        ArrayList<Date> datez = new ArrayList<>();
        for (int a = 0; a < listViewItems.size(); a++) {
            int earliest = 0;
            for (int b = 0; b < dates.size(); b++) {
                if (dates.get(b).getTime() < dates.get(earliest).getTime()) {
                    earliest = b;
                }
            }
            listViewItems.add(a, listViewItems.get(earliest+a));
            datez.add(dates.get(earliest));

            if (a <= earliest) {
                listViewItems.remove(earliest+1+a);
            } else {
                listViewItems.remove(earliest+a);
            }

            dates.remove(earliest);
        }

        long previousTime = 0;
        for (int a = 0; a < arrayName.length(); a++) {
            Date loadedDate = datez.get(a);

            Date previous = new Date(previousTime);
            if (loadedDate.getDate() == previous.getDate() && loadedDate.getMonth() == previous.getMonth()) {

            } else {
                previousTime = loadedDate.getTime();

                listViewItems.set(a, listViewItems.get(a)+"@new");
            }
        }

        String[] forAdapter = new String[listViewItems.size()];
        for (int a = 0; a < listViewItems.size(); a++) {
            forAdapter[a] = listViewItems.get(a);
        }

        tasksListviewAdapter = new TasksListviewAdapter(getContext(), forAdapter, todo);
        listView.setAdapter(tasksListviewAdapter);
    }

    Dialog dialog;

//    private void startTransition(View view, Element element) {
//        Intent i = new Intent(getContext(), dialog.getClass());
//        i.putExtra("ITEM_ID", element.getId());
//
//        Pair<View, String>[] transitionPairs = new Pair[4];
//        transitionPairs[0] = Pair.create(getView().findViewById(R.id.toolbar), "toolbar"); // Transition the Toolbar
//        transitionPairs[1] = Pair.create(view, "content_area"); // Transition the content_area (This will be the content area on the detail screen)
//
//        // We also want to transition the status and navigation bar barckground. Otherwise they will flicker
//        transitionPairs[2] = Pair.create(getView().findViewById(android.R.id.statusBarBackground), Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
//        transitionPairs[3] = Pair.create(getView().findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
//        Bundle b = ActivityOptionsCompat.makeSceneTransitionAnimation(dialog.getOwnerActivity(), transitionPairs).toBundle();
//
//        ActivityCompat.startActivity(dialog.getOwnerActivity(), i, b);
//    }

    public void addTask() {
        SharedPreferences prefs = getActivity().getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

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

        arrayName.put(etName.getText().toString());
        arrayWhat.put(etBody.getText().toString());
        arrayTime.put(time.getTime());

        editor.putString("TaskName", arrayName.toString()).apply();
        editor.putString("TaskWhat", arrayWhat.toString()).apply();
        editor.putString("TaskTime", arrayTime.toString()).apply();

        editor.commit();

        updateTasks(true);
    }

    public void doneTask(int position) {
        SharedPreferences prefs = getActivity().getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String name, what;
        long time;
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

        ArrayList<String> arrayListNames = new ArrayList<>();
        ArrayList<String> arrayListWhats = new ArrayList<>();
        long[] times = new long[arrayTime.length()];

        for (int a = 0; a < arrayName.length(); a++) {
            try {
                arrayListNames.add(a, arrayName.getString(a));
                arrayListWhats.add(a, arrayWhat.getString(a));
                times[a] = arrayTime.getLong(a);
            } catch (Exception e) {
            }
        }

        try {
            name = arrayName.getString(position);
            what = arrayWhat.getString(position);
            time = arrayTime.getLong(position);
        } catch (Exception e) {
            name = "";
            what = "";
            time = 0;
        }

        arrayName = new JSONArray();
        arrayWhat = new JSONArray();
        arrayTime = new JSONArray();

        for (int a = 0; a < arrayListNames.size(); a++) {
            if (a != position) {
                arrayName.put(arrayListNames.get(a));
                arrayWhat.put(arrayListWhats.get(a));
                arrayTime.put(times[a]);
            }
        }

        editor.putString("TaskName", arrayName.toString());
        editor.putString("TaskWhat", arrayWhat.toString());
        editor.putString("TaskTime", arrayTime.toString());
        editor.apply();

        try {
            arrayName = new JSONArray(prefs.getString("DoneTaskName", null));
            arrayWhat = new JSONArray(prefs.getString("DoneTaskWhat", null));
            arrayTime = new JSONArray(prefs.getString("DoneTaskTime", null));
        } catch (Exception e) {
            arrayName = new JSONArray();
            arrayWhat = new JSONArray();
            arrayTime = new JSONArray();
        }

        arrayName.put(name);
        arrayWhat.put(what);
        arrayTime.put(time);

        editor.putString("DoneTaskName", arrayName.toString()).apply();
        editor.putString("DoneTaskWhat", arrayWhat.toString()).apply();
        editor.putString("DoneTaskTime", arrayTime.toString()).apply();

        editor.apply();

        updateTasks(true);
    }



//    public void editTask() {
//        if (nameTask.getText().toString() != null && nameTask.getText().toString().length() > 0 && whatTask.getText().toString() != null && whatTask.getText().toString().length() > 0 && time.getTime() > System.currentTimeMillis()) {
//            SharedPreferences prefs = getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = prefs.edit();
//
//            try {
//                arrayName = new JSONArray(prefs.getString("TaskName", null));
//                arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
//                arrayTime = new JSONArray(prefs.getString("TaskTime", null));
//            } catch (Exception e) {
//                arrayName = new JSONArray();
//                arrayWhat = new JSONArray();
//                arrayTime = new JSONArray();
//            }
//
//            ArrayList<String> listName = new ArrayList<String>();
//            for (int i = 0; i < arrayName.length(); i++){
//                try {
//                    listName.add(arrayName.getString(i));
//                } catch (Exception e) {
//                }
//            }
//
//            listName.remove(editName);
//            arrayName = new JSONArray(listName);
//
//            ArrayList<String> listWhat = new ArrayList<String>();
//            if (arrayWhat != null) {
//                int len = arrayWhat.length();
//                for (int i = 0; i < len ; i++){
//                    try {
//                        listWhat.add(arrayWhat.getString(i));
//                    } catch (Exception e) {
//                    }
//                }
//            }
//            listWhat.remove(editWhat);
//            arrayWhat = new JSONArray(listWhat);
//
//            ArrayList<Long> listTime = new ArrayList<Long>();
//            for (int i = 0; i < arrayTime.length(); i++){
//                try {
//                    listTime.add(arrayTime.getLong(i));
//                } catch (Exception e) {
//                }
//            }
//
//            listTime.remove(editTime);
//            arrayTime = new JSONArray(listTime);
//
//            arrayName.put(nameTask.getText().toString());
//            arrayWhat.put(whatTask.getText().toString());
//            arrayTime.put(time.getTime());
//
//            editor.putString("TaskName", arrayName.toString()).apply();
//            editor.putString("TaskWhat", arrayWhat.toString()).apply();
//            editor.putString("TaskTime", arrayTime.toString()).apply();
//            editor.commit();
//
//            MainActivity.taskAdded = true;
//            finish();
//
//            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//            PendingIntent mAlarmSender = PendingIntent.getBroadcast(this, 0, new Intent(this, NotificationRecieverActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), mAlarmSender);
//
//        }
//    }
//
//    public void alwaysOnScreen(Context context, String name) {
//        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
//
//        boolean a = prefs.getBoolean("active", true);
//
//        if (a == true) {
//            String nameForAlways = null;
//            if (numberOfTask == 1) {
//                nameForAlways = " active task";
//            } else if (numberOfTask > 1) {
//                nameForAlways = " active tasks";
//            }
//
//            ArrayList<String> listNamez = new ArrayList<String>();
//            for (int i = 0; i < arrayName.length(); i++){
//                try {
//                    listNamez.add(arrayName.getString(i));
//                } catch (Exception e) {
//                }
//            }
//
//            String childWithNames = listNamez.toString();
//
//            Intent intent = new Intent(context, MainActivity.class);
//            intent.putExtra("fromNotification", true);
//            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);
//
//            Notification notification = new Notification.Builder(context)
//                    .setContentTitle(name + nameForAlways)
//                    .setContentText(childWithNames.substring(1, childWithNames.length()-1))
//                    .setSmallIcon(R.drawable.ic_active_tasks)
//                    .setContentIntent(contentIntent).build();
//
//            NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//            notification.flags |= Notification.FLAG_NO_CLEAR;
//            notificationManager.notify(0, notification);
//        } else {
//            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
//            notificationManager.cancel(0);
//        }
//    }
}

