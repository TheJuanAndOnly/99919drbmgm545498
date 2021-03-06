package com.thejuanandonly.gradeday;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.transitionseverywhere.TransitionManager;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;


import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

import static com.facebook.FacebookSdk.getApplicationContext;


public class TasksFragment extends Fragment {

    android.support.v7.widget.Toolbar toolbarMain, toolbar;

    public ListView listView;
    private TasksListviewAdapter tasksListviewAdapter;

    public ArrayList<Integer> indexArray;

    public EditText etName, etBody;
    private TextView tvTodo, tvDone;
    public Button btnTime, btnDate, btnSave;
    private SwitchCompat switchCurrent;

    private ImageView ivTime, ivDate, ivAdd;

    private ViewGroup head, main, list;

    public RelativeLayout rlTime, rlSwitch;

    public TimePicker timePicker;
    public DatePicker datePicker;

    public Date time;

    private SharedPreferences prefs;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.tasks_layout, null);

        prefs = getActivity().getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4648715887566496~3996876969");
        AdView mAdView = (AdView) rootView.findViewById(R.id.adViewBanner);

        if (hasNetworkConnection()) {
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }

        toolbarMain = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbarMain.setVisibility(View.GONE);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_head);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setPadding(0, 0, 0, 0);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle(null);

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), ((MainActivity) getActivity()).mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
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

        ivTime = (ImageView) rootView.findViewById(R.id.iv_time);
        ivDate = (ImageView) rootView.findViewById(R.id.iv_date);
        ivAdd = (ImageView) rootView.findViewById(R.id.iv_add);

        tvTodo = (TextView) rootView.findViewById(R.id.tv_todo);
        tvDone = (TextView) rootView.findViewById(R.id.tv_done);

        tvDone.setTypeface(null, Typeface.NORMAL);
        tvTodo.setTypeface(tvTodo.getTypeface(), Typeface.BOLD);

        switchCurrent = (SwitchCompat) rootView.findViewById(R.id.switch_current);
        switchCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchCurrent.isChecked()) {
                    tvDone.setTypeface(tvDone.getTypeface(), Typeface.BOLD);
                    tvTodo.setTypeface(null, Typeface.NORMAL);
                } else {
                    tvDone.setTypeface(null, Typeface.NORMAL);
                    tvTodo.setTypeface(tvTodo.getTypeface(), Typeface.BOLD);
                }
            }
        });

        tvTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchCurrent.isChecked()) switchCurrent.performClick();
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!switchCurrent.isChecked()) switchCurrent.performClick();
            }
        });

        rlTime = (RelativeLayout) head.findViewById(R.id.rl_time);

        rlSwitch = (RelativeLayout) head.findViewById(R.id.rl_switch);
        main = (ViewGroup) rootView.findViewById(R.id.main);

        time = new Date();

        initializeHeader(false, false, 0);

        updateTasks(true);

        return rootView;
    }

    private boolean hasNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
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

    public void initializeHeader(boolean adding, final boolean editing, final int position) {
        if (adding) {
            if (editing) btnSave.setText("Save");
            else btnSave.setText("Add");

            etName.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editing) {
                        addTask(true, position);
                    } else {
                        addTask(false, 0);
                    }

                    Rect r = new Rect();
                    getView().getWindowVisibleDisplayFrame(r);
                    int screenHeight = getView().getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;

                    if (keypadHeight > screenHeight * 0.15) {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }

                    etName.setText("");
                    etBody.setText("");
                    btnTime.setText("time");
                    btnDate.setText("date");
                    time = new Date();

                    initializeHeader(false, false, 0);
                }
            });

            ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnSave.performClick();
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

            ivTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnTime.performClick();
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

            ivDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnDate.performClick();
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
            params.setMargins(48, 16, 48, 0);
            etName.setLayoutParams(params);

            toolbar.getMenu().clear();

            mDrawerToggle.setDrawerIndicatorEnabled(false);

            TransitionManager.beginDelayedTransition(main);
            etBody.setVisibility(View.VISIBLE);
            rlTime.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
        } else {
            etName.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    initializeHeader(true, false, 0);
                    return false;
                }
            });

            switchCurrent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateTasks(!isChecked);
                }
            });

            listView.setOnTouchListener(null);

            etName.setCursorVisible(false);
            etName.getBackground().mutate().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

            TransitionManager.beginDelayedTransition(main);
            etBody.setVisibility(View.INVISIBLE);
            rlTime.setVisibility(View.INVISIBLE);
            btnSave.setVisibility(View.INVISIBLE);

            TransitionManager.beginDelayedTransition(main);
            etBody.setVisibility(View.GONE);
            rlTime.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);

            MenuItem edit_item = toolbar.getMenu().add(0, R.id.action_schedule, 100, "Schedule");
            edit_item.setIcon(R.drawable.ic_event_white_24dp);
            edit_item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);

            mDrawerToggle.setDrawerIndicatorEnabled(true);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 16, 0, 0);
            etName.setLayoutParams(params);

            TransitionManager.beginDelayedTransition(main);
            etName.setHint("Add new task");
            rlSwitch.setVisibility(View.VISIBLE);

            switchCurrent.setChecked(false);
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

                btnDate.setText(time.getDate() + ". " + month + ".");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void updateTasks(boolean todo) {
        ArrayList<String> listViewItems = new ArrayList<>();
        indexArray = new ArrayList<>();

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

        ArrayList<String> listItemsSorted = new ArrayList<>();
        ArrayList<Date> datez = new ArrayList<>();
        ArrayList<Date> temp = new ArrayList<>(dates);

        int length = listViewItems.size();
        for (int a = 0; a < length; a++) {
            int earliest = 0;
            for (int b = 0; b < dates.size(); b++) {
                if (dates.get(b).getTime() < dates.get(earliest).getTime()) {
                    earliest = b;
                }
            }

            listItemsSorted.add(listViewItems.get(earliest));
            datez.add(dates.get(earliest));

            listViewItems.remove(earliest);
            dates.remove(earliest);
        }

        for (int a = 0; a < datez.size(); a++) {
            indexArray.add(temp.indexOf(datez.get(a)));
        }

        long previousTime = 0;
        for (int a = 0; a < arrayName.length(); a++) {
            Date loadedDate = datez.get(a);

            Date previous = new Date(previousTime);
            if (loadedDate.getDate() == previous.getDate() && loadedDate.getMonth() == previous.getMonth()) {

            } else {
                previousTime = loadedDate.getTime();

                listItemsSorted.set(a, listItemsSorted.get(a)+"@new");
            }
        }

        String[] forAdapter = new String[listItemsSorted.size()];
        for (int a = 0; a < listItemsSorted.size(); a++) {
            forAdapter[a] = listItemsSorted.get(a);
        }

        tasksListviewAdapter = new TasksListviewAdapter(getContext(), forAdapter, todo);
        listView.setAdapter(tasksListviewAdapter);

        ((MainActivity) getActivity()).setTasksCount();
        ((MainActivity) getActivity()).alwaysOnScreen();
    }

    public void addTask(boolean edit, int position) {
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

        if (!edit) {
            arrayName.put(etName.getText().toString());
            arrayWhat.put(etBody.getText().toString());
            arrayTime.put(time.getTime());
        } else {
            try {
                arrayName.put(position, etName.getText().toString());
                arrayWhat.put(position, etBody.getText().toString());
                arrayTime.put(position, time.getTime());
            } catch (Exception e) {
            }
        }

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



    public void editTask(int position) {
        SharedPreferences prefs = getActivity().getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
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

        initializeHeader(true, true, position);

        try {
            etName.setText(arrayName.getString(position));
            etBody.setText(arrayWhat.getString(position));
            time.setTime(arrayTime.getLong(position));
        } catch (Exception e) {
        }

        String timeString = (String) android.text.format.DateFormat.format("HH : mm", time);
        String dateString = (String) android.text.format.DateFormat.format("dd. MM.", time);

        btnTime.setText(timeString);
        btnDate.setText(dateString);
    }

    public void removeTask(int position) {
        SharedPreferences prefs = getActivity().getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray arrayName, arrayWhat, arrayTime;

        try {
            arrayName = new JSONArray(prefs.getString("DoneTaskName", null));
            arrayWhat = new JSONArray(prefs.getString("DoneTaskWhat", null));
            arrayTime = new JSONArray(prefs.getString("DoneTaskTime", null));
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

        editor.putString("DoneTaskName", arrayName.toString());
        editor.putString("DoneTaskWhat", arrayWhat.toString());
        editor.putString("DoneTaskTime", arrayTime.toString());
        editor.apply();
    }


}

