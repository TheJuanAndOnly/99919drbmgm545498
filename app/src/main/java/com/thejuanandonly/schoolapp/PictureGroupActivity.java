package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

/**
 * Created by Daniel on 11/7/2015.
 */
public class PictureGroupActivity extends AppCompatActivity {

    public static Context initialContext;
    public static String currentPictureGroup;
    private Menu menu;
    android.support.v7.widget.Toolbar toolbar;
    private static int RESULT_LOAD_IMAGE = 1;
    GridView gridView;
    public static String picture;
    public static Uri selectedImage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_group_activity_layout);


        initialContext = getApplicationContext();
        currentPictureGroup = getIntent().getExtras().getString("subjectGroupName", null);

        theme();
        String subject_notes = getIntent().getExtras().getString("subjectGroupName", null);
        toolbar.setTitle(subject_notes);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        GridView gridView = (GridView)findViewById(R.id.picture_group_gridView);
        gridView.setAdapter(new ImageGridAdapter(this));

        gridView.invalidateViews();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PictureGroupActivity.this, "You clicked " + position, Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notes_detail, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String picturePath = loadPath();

        if (id == R.id.addPictureFolder) {
            if (picturePath != null) {
                Toast.makeText(this, "Click the plus button to add pictures", Toast.LENGTH_LONG).show();

                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    } else {

                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gridView = (GridView)findViewById(R.id.picture_group_gridView);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Toast.makeText(PictureGroupActivity.this, "selectedImage ma hodnotu: " + selectedImage, Toast.LENGTH_SHORT).show();


        }
    }


    /*
    public void savePath(String string){
        SharedPreferences prefs = getSharedPreferences("PicturePath", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Path", string).apply();
    }

    */

    public String loadPath() {
        SharedPreferences prefs = getSharedPreferences("PicturePath", Context.MODE_PRIVATE);
        return prefs.getString("Path", null);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void theme() {
        SharedPreferences prefs = getSharedPreferences("themeSave", Context.MODE_PRIVATE);
        int theme = prefs.getInt("theme", 0);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.subjectDetailToolbar);

        switch (theme) {
            case 1:
                toolbar.setBackgroundColor(getResources().getColor(R.color.orange));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP)
                    window.setStatusBarColor(getResources().getColor(R.color.orange800));

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
