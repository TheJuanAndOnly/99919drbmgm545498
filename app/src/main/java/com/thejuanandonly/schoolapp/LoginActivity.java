package com.thejuanandonly.schoolapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Daniel on 1/5/2016.
 */
public class LoginActivity extends Activity {

    EditText nickname;
    ImageView avatar_circle;
    Button getStarted;
    String picture;
    String getNickname;
    ImageView logo, character;
    boolean nicknameSelected = false;
    private static int RESULT_LOAD_IMAGE = 1;
    public static Uri avatarURI;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        nickname = (EditText) findViewById(R.id.nickname);

        logo = (ImageView) findViewById(R.id.login_icons);
        character = (ImageView) findViewById(R.id.character);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        if (height <= 853) {
            logo.getLayoutParams().width = 360;
            logo.getLayoutParams().height = 220;
            logo.requestLayout();

            character.getLayoutParams().width = 50;
            character.getLayoutParams().height = 80;
            character.requestLayout();
        }


        avatar_circle = (ImageView) findViewById(R.id.avatar_circle);
        getStarted = (Button) findViewById(R.id.getStarted);

        nickname.setTextColor(getResources().getColor(R.color.white));

        avatar_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loadImages = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(loadImages, RESULT_LOAD_IMAGE);

            }
        });



        final Snackbar  snackbar = Snackbar.make(findViewById(android.R.id.content), "Click the circle to choose an avatar", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        snackbar.setAction("got it", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });

        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                nicknameSelected = false;
                checkGetNickname();

                if(nicknameSelected == true) {
                    Intent toApp = new Intent(LoginActivity.this, MainActivity.class);

                    toApp.putExtra("nickname", getNickname);

                    try {
                        toApp.putExtra("avatar", avatarURI.toString());
                    } catch (NullPointerException e) {

                    }

                    startActivity(toApp);
                    finish();
                } else {

                    Toast.makeText(LoginActivity.this, "Please choose a valid nickname", Toast.LENGTH_SHORT).show();
                    nicknameSelected = false;

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        quitApplication();
    }

    public void quitApplication(){

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void checkGetNickname() {

        SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        getNickname = nickname.getText().toString();

        if (getNickname != null && getNickname.length() > 0) {
            nicknameSelected = true;
        } else {
            nicknameSelected = false;
        }

        editor.putString("nickname", getNickname).apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            picture = cursor.getString(columnIndex);
            avatarURI = selectedImage;

            cursor.close();

            Bitmap bitmap = null;
           try {
               bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), avatarURI);
            } catch (IOException e) {
                Toast.makeText(this, "exce", Toast.LENGTH_SHORT).show();
            }
            int w = bitmap.getWidth(), h = bitmap.getHeight();
            int radius = w > h ? h:w;
            Bitmap roundBitmap = ImageToCircle.getCroppedBitmap(bitmap, radius);

            avatar_circle.setImageBitmap(roundBitmap);

            editor.putString("avatar", avatarURI.toString()).apply();
        }



    }

}