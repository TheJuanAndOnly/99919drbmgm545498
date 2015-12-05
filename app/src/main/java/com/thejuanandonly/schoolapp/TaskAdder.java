package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Set;


public class TaskAdder extends ActionBarActivity {

    private EditText nameTask, whatTask;
    private Toolbar toolbar;
    public String editName, editWhat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_adder_activity);

        Intent intent = getIntent();
        editName = intent.getStringExtra("EditTaskName");
        editWhat = intent.getStringExtra("EditTaskWhat");

        nameTask = (EditText) findViewById(R.id.editText_nameTask);
        whatTask = (EditText) findViewById(R.id.editText_whatTask);

        if (editName != null && editName.length() > 0 && editWhat != null && editWhat.length() > 0) {
            nameTask.setText(editName);
            whatTask.setText(editWhat);
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        theme();
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
                editTask();
            } else {
                addTask();
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

    public JSONArray arrayName;
    public JSONArray arrayWhat;
    public int numberOfTask;

    public void addTask() {

        if (nameTask.getText().toString() != null && nameTask.getText().toString().length() > 0 && whatTask.getText().toString() != null && whatTask.getText().toString().length() > 0) {
            SharedPreferences prefs = getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            numberOfTask = prefs.getInt("NumberOfTask", 0);
            numberOfTask++;

            try {
                arrayName = new JSONArray(prefs.getString("TaskName", null));
                arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
            } catch (Exception e) {
                arrayName = new JSONArray();
                arrayWhat = new JSONArray();

            }

            arrayName.put(nameTask.getText().toString());
            arrayWhat.put(whatTask.getText().toString());

            editor.putString("TaskName", arrayName.toString()).apply();
            editor.putString("TaskWhat", arrayWhat.toString()).apply();

            editor.putInt("NumberOfTask", numberOfTask).apply();
            editor.commit();

            finish();
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
            } catch (Exception e) {
                arrayName = new JSONArray();
                arrayWhat = new JSONArray();
            }

            ArrayList<String> listName = new ArrayList<String>();
            for (int i = 0; i < arrayName.length(); i++){
                try {
                    listName.add(arrayName.get(i).toString());
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
                        listWhat.add(arrayWhat.get(i).toString());
                    } catch (Exception e) {
                    }
                }
            }
            listWhat.remove(editWhat);
            arrayWhat = new JSONArray(listWhat);

            arrayName.put(nameTask.getText().toString());
            arrayWhat.put(whatTask.getText().toString());

            editor.putString("TaskName", arrayName.toString()).apply();
            editor.putString("TaskWhat", arrayWhat.toString()).apply();
            editor.commit();

            finish();
        }
    }

}
