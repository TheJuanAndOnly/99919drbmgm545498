package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Daniel on 11/7/2015.
 */
public class PictureGroupActivity extends AppCompatActivity {

    public static String currentPictureGroup;
    public static Uri selectedImage;
    public static ArrayList<Bitmap> ALofSelectedImgs = new ArrayList<>();
    public static ArrayList<Bitmap> ALofRSelectedImgs = new ArrayList<>();
    public static int height, width;
    private static int RESULT_LOAD_IMAGE = 1;
    private Menu menu;
    public JSONArray arrayOfImgs = new JSONArray();
    public int numberOfImgs;
    android.support.v7.widget.Toolbar toolbar;
    String picture;
    String[] thePicture;
    ImageGridAdapter imageGridapter = new ImageGridAdapter(this);
    GridView gridView;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_group_activity_layout);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        currentPictureGroup = getIntent().getExtras().getString("subjectDetailNotes", null);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        theme();

        String subject_notes = getIntent().getExtras().getString("subNote", "SchoolApp");
        toolbar.setTitle(subject_notes);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = this.getSharedPreferences("GridView" + getIntent().getExtras().getString("selected", null) + getIntent().getExtras().getString("subNote", null), Context.MODE_PRIVATE);
        try {
            arrayOfImgs = new JSONArray(prefs.getString("ListOfSubjectsGroupName", null));
        } catch (Exception e) {
            arrayOfImgs = new JSONArray();
        }

        try {
            ArrayList<String> strings = new ArrayList<String>();
            for (int i = 0; i < arrayOfImgs.length(); i++) {
                try {
                    strings.add(arrayOfImgs.getString(i));
                } catch (JSONException e) {
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Add pictures", Toast.LENGTH_SHORT).show();
        }


        gridView = (GridView) findViewById(R.id.picture_group_gridView);
        gridView.setAdapter(new ImageGridAdapter(this));
        gridView.deferNotifyDataSetChanged();
        registerForContextMenu(gridView);

        ALofSelectedImgs.clear();
        ALofRSelectedImgs.clear();

        for (int i = 0; i < numberOfImgs; i++){
            String s = null;
            try {
                s = arrayOfImgs.getString(i);
            } catch (JSONException e) {
            }
            ALofRSelectedImgs.add(BitmapScaled(s, 750, 540));
            //Collections.reverse(ALofRSelectedImgs);
            ALofSelectedImgs.add(BitmapScaled(s, 100, 100));
            //Collections.reverse(ALofSelectedImgs);

        }



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent viewPager = new Intent(getApplicationContext(), GridViewPager.class);
                viewPager.putExtra("position", position);
                startActivity(viewPager);

            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_gridview, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = info.position;

        ArrayList<String> arrayListOfImgs = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences("GridView" + getIntent().getExtras().getString("selected", null) + getIntent().getExtras().getString("subNote", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        try {
            arrayOfImgs = new JSONArray(preferences.getString("NoteImages", null));
        } catch (Exception e) {
        }

        switch (item.getItemId()) {

            case R.id.delete_gridView:


                for (int i = 0; i < arrayOfImgs.length(); i++) {
                    try {
                        arrayListOfImgs.add(arrayOfImgs.getString(i));
                    } catch (JSONException e) {
                    }}

                arrayListOfImgs.remove(id);
                arrayOfImgs = new JSONArray(arrayListOfImgs);

                if (preferences.getInt("numberOfImgs", 0) == 0) {
                    super.onResume();
                } else {
                    ALofSelectedImgs.clear();
                    ALofRSelectedImgs.clear();
                    for (int i = 0; i < preferences.getInt("numberOfImgs", 0); i++) {
                        String imageUri = "";
                        try {
                            imageUri = arrayOfImgs.getString(i);
                        } catch (Exception e) {
                        }

                        ALofSelectedImgs.add(BitmapScaled(imageUri, 100, 100));
                        //Collections.reverse(ALofSelectedImgs);

                        ALofRSelectedImgs.add(BitmapScaled(imageUri, 750, 540));
                        //Collections.reverse(ALofRSelectedImgs);

                    }
                }

                numberOfImgs = preferences.getInt("numberOfImgs", 0);
                numberOfImgs--;

                editor.putString("NoteImages", arrayOfImgs.toString()).apply();
                editor.putInt("numberOfImgs", numberOfImgs).apply();

                onResume();

                imageGridapter.notifyDataSetChanged();

                return true;


            case R.id.cancle_gridView:

                return true;

            default:
                return super.onContextItemSelected(item);
        }
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

        if (id == R.id.addPictureFolder) {

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);

        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences prefs = getSharedPreferences("GridView" + getIntent().getExtras().getString("selected", null) + getIntent().getExtras().getString("subNote", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();


        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            numberOfImgs = prefs.getInt("numberOfImgs", 0);
            numberOfImgs++;

            try {
                arrayOfImgs = new JSONArray(prefs.getString("NoteImages", null));
            } catch (Exception e) {
            }


            selectedImage = data.getData();
            Uri selectedImage = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            thePicture = filePathColumn;
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picture = cursor.getString(columnIndex);
            cursor.close();

            ALofRSelectedImgs.add(BitmapScaled(picture, 750, 540));
            //Collections.reverse(ALofRSelectedImgs);
            ALofSelectedImgs.add(BitmapScaled(picture, 100, 100));
            //Collections.reverse(ALofSelectedImgs);


            arrayOfImgs.put(picture);
            editor.putString("NoteImages", arrayOfImgs.toString()).apply();
            finish();

            editor.putInt("numberOfImgs", numberOfImgs);
            editor.apply();


            Intent restart = getIntent();
            finish();
            startActivity(restart);

        }

    }

    @Override
    protected void onResume() {


        SharedPreferences prefs = this.getSharedPreferences("GridView" + getIntent().getExtras().getString("selected", null) + getIntent().getExtras().getString("subNote", null), Context.MODE_PRIVATE);
        try {
            arrayOfImgs = new JSONArray(prefs.getString("NoteImages", null));
        } catch (Exception e) {
        }

        if (prefs.getInt("numberOfImgs", 0) == 0) {
            super.onResume();
        } else {
            ALofSelectedImgs.clear();
            ALofRSelectedImgs.clear();
            for (int i = 0; i < prefs.getInt("numberOfImgs", 0); i++) {
                String imageUri = "";
                try {
                    imageUri = arrayOfImgs.getString(i);
                } catch (Exception e) {
                }


                ALofSelectedImgs.add(BitmapScaled(imageUri, 100, 100));
                //Collections.reverse(ALofSelectedImgs);

                ALofRSelectedImgs.add(BitmapScaled(imageUri, 750, 540));
                //Collections.reverse(ALofRSelectedImgs);

            }
        }

        super.onResume();

    }

    private Bitmap BitmapScaled(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inPurgeable = true;
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int SizeSample = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            SizeSample = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return SizeSample;
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
                toolbar.setBackgroundColor(getResources().getColor(R.color.blueT));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP)
                    window.setStatusBarColor(getResources().getColor(R.color.blueTy));

                break;
        }
    }
}