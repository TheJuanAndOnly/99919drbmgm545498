package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    public int actualFragment = 1;
    public static int api;
    public static int theme;
    public boolean willSend = false;
    public static boolean taskAdded = false;
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = android.os.Build.VERSION.SDK_INT;
        theme();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new OverviewFragment()).commit();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

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
                    fragmentTransaction.replace(R.id.containerView, new NotesFragment()).commit();
                    actualFragment = 3;
                    invalidateOptionsMenu();
                }

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new SettingsFragment()).commit();
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

    }

    @Override
    protected void onResume() {
        if (actualFragment == 2 && taskAdded == true) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent mAlarmSender = PendingIntent.getBroadcast(this, 0, new Intent(this, NotificationRecieverActivity.class), 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), mAlarmSender);
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.containerView, new TasksFragment()).commit();;
            taskAdded = false;
        }
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void theme() {
        SharedPreferences prefs = getSharedPreferences("themeSave", Context.MODE_PRIVATE);
        theme = prefs.getInt("theme", 0);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        switch (theme) {
            default:

                toolbar.setBackgroundColor(getResources().getColor(R.color.mainblue));

                if (api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.mainblue800));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (actualFragment == 1) {
            getMenuInflater().inflate(R.menu.menu_overview, menu);
        } else if (actualFragment == 2) {
            getMenuInflater().inflate(R.menu.menu_tasks, menu);
        } else if (actualFragment == 3) {
            getMenuInflater().inflate(R.menu.menu_notes, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
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

    public void notificationsClick(View view) {
        CheckBox notificationsCheckBox = (CheckBox) findViewById(R.id.notificationsCheckBox);
        CheckBox soundsCheckBox = (CheckBox) findViewById(R.id.soundsNotificationCheckBox);
        CheckBox vibrationsCheckBox = (CheckBox) findViewById(R.id.vibrationsNotificationCheckBox);

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
    }

    public void soundsNotificationClick(View view) {
    }

    public void vibrationsNotificationClick(View view) {
    }

    public void setTheme(View view) {
        willSend = true;
        super.recreate();
    }

    public void deleteAll(View view) {
        deleteDialogBox();
        SharedPreferences preferences = getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();

        SharedPreferences prefs = getSharedPreferences("ListOfDoneTasks", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();

    }

    public void deleteDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Delete Everything");

        builder.setMessage("Are you sure you want to delete everything?")

                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg0) {

                        try {
                            SharedPreferences prefs = getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            JSONArray arrayOfSubjects = new JSONArray(prefs.getString("List", null));

                            for (int i = 0; i < arrayOfSubjects.length(); i++) {
                                SharedPreferences preferences = getSharedPreferences("Subject" + arrayOfSubjects.get(i), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = preferences.edit();
                                editor2.clear().apply();
                            }
                            editor.clear().apply();

                        } catch (Exception e) {
                        }

                        try {
                            SharedPreferences prefs_notes = getSharedPreferences("ListOfSubjectsNotes", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs_notes.edit();
                            JSONArray set_notes = new JSONArray(prefs_notes.getString("ListNotes", null));
                            for (int i = 0; i < set_notes.length(); i++) {
                                SharedPreferences preferences = getSharedPreferences("SubjectNotes" + set_notes.get(i), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = preferences.edit();
                                editor2.clear().apply();
                            }
                            editor.clear().apply();

                        } catch (Exception e) {
                        }

                        try {
                            SharedPreferences prefs_groupName = getSharedPreferences("ListOfSubjectsGroupName", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor_groupName = prefs_groupName.edit();
                            JSONArray set_groupName = new JSONArray(prefs_groupName.getString("ListGroupName", null));

                            for (int i = 0; i < set_groupName.length(); i++) {
                                SharedPreferences preferences_groupName = getSharedPreferences("SubjectGroupName" + set_groupName.get(i), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor2_groupName = preferences_groupName.edit();
                                editor2_groupName.clear().apply();
                            }
                            editor_groupName.clear().apply();

                        } catch (Exception e) {
                        }

                        Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg0) {
                    }
                });

        builder.show();
    }

    public void subjectDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a subject");

        final EditText input = new EditText(this);
        input.setHint("Subject name");
        input.setPadding(50, 50, 50, 30);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        builder.setView(input);

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
        ArrayList<String> m_listItems = new ArrayList<>();
        try {
             set = new JSONArray(prefs.getString("List", null));

        } catch (Exception e) {
        }


        if (subject != null && subject.length() > 0) {

            try {
                set.put(subject);
            } catch (Exception e) {}

        } else {
            Toast.makeText(this, "Don't leave the space blank!", Toast.LENGTH_LONG).show();
            subjectDialog();
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
}
