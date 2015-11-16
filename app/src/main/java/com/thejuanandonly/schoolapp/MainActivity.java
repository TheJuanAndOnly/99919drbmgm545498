package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
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
import org.json.JSONException;

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
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        api = android.os.Build.VERSION.SDK_INT;
        theme();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view) ;

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView,new OverviewFragment()).commit();

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

                if (menuItem.getItemId() == R.id.nav_item_settings) {
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.containerView, new SettingsFragment()).commit();
                    actualFragment = 3;
                    invalidateOptionsMenu();
                }
                return false;
            }

        });
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

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
            case 1:

                toolbar.setBackgroundColor(getResources().getColor(R.color.orange));

                if (api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.orange700));

                break;
            case 2:

                toolbar.setBackgroundColor(getResources().getColor(R.color.green));

                if (api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.green800));

                break;
            case 3:

                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));

                if (api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.blue800));

                break;
            case 4:

                toolbar.setBackgroundColor(getResources().getColor(R.color.grey));

                if (api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.grey700));

                break;
            case 5:

                toolbar.setBackgroundColor(getResources().getColor(R.color.teal));

                if (api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.teal800));

                break;
            case 6:

                toolbar.setBackgroundColor(getResources().getColor(R.color.brown));

                if (api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.brown700));

                break;
            default:

                toolbar.setBackgroundColor(getResources().getColor(R.color.red));

                if (api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.red800));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (actualFragment == 1) {
            getMenuInflater().inflate(R.menu.menu_overview, menu);
        } else if (actualFragment == 2) {
            getMenuInflater().inflate(R.menu.menu_tasks, menu);
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
            Intent startScheduleActivity = new Intent (MainActivity.this, ScheduleActivity.class);
            startActivity(startScheduleActivity);
        } else if (id == R.id.action_new_subject) {
            subjectDialog();
        } else if (id == R.id.action_new_task) {
            Intent TaskAdderActivity = new Intent(MainActivity.this, TaskAdder.class);
            startActivity(TaskAdderActivity);
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
                            JSONArray array;
                            try {
                                array = new JSONArray(prefs.getString("List", null));
                            } catch (Exception e) {array = new JSONArray();}

                            for (int i = 0; i < array.length(); i++) {
                                try {
                                    SharedPreferences preferences = getSharedPreferences("Subject" + array.get(i), Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = preferences.edit();
                                    editor2.clear().apply();
                                } catch (JSONException e) {}
                            }
                            editor.clear().apply();

                        } catch (NullPointerException e) {}

                        Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg0) {
                    }
                });

        builder.show();
    }

    public void subjectDialog(){

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

    public void saveSubject(String subject){
        SharedPreferences prefs = getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);
        JSONArray m_listItems;
        try {
            m_listItems = new JSONArray(prefs.getString("List", null));
        }catch (Exception e) {m_listItems = new JSONArray();}


        if(subject != null && subject.length() > 0) {
            if (!m_listItems.toString().contains(subject)) {
                m_listItems.put(subject);
            }else {
                Toast.makeText(this, "This Subject already exists", Toast.LENGTH_LONG).show();
                subjectDialog();
            }
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
        arrayPrefsEditor.putString("List", m_listItems.toString()).apply();

        OverviewFragment.reset(this);
    }

}