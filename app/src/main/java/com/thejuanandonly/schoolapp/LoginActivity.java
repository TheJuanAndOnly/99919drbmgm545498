package com.thejuanandonly.schoolapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
    boolean nicknameSelected = false;
    private static int RESULT_LOAD_IMAGE = 1;
    public static Uri avatarURI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_layout);

        nickname = (EditText) findViewById(R.id.nickname);


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
                        Toast.makeText(LoginActivity.this, "No image was selected", Toast.LENGTH_SHORT).show();
                    }

                    startActivity(toApp);
                } else {

                    Toast.makeText(LoginActivity.this, "Please choose a valid nickname", Toast.LENGTH_SHORT).show();
                    nicknameSelected = false;

                }
            }
        });
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