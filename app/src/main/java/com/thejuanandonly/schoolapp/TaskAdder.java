package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.content.Context;
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
import android.widget.Toast;


public class TaskAdder extends ActionBarActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_adder_activity);

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
            finish();
            return true;
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

}
