package com.thejuanandonly.schoolapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

public class ScheduleActivity extends Activity {

    private static int RESULT_LOAD_IMAGE = 1;
    ImageView imageView;
    String picture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity_layout);
        String picturePath = loadPath();

        SubsamplingScaleImageView imageView = (SubsamplingScaleImageView) findViewById(R.id.schedule_image_view);

        if (picturePath != null) {
            Toast.makeText(this, "Long click on the image to change it", Toast.LENGTH_LONG).show(); //Sem dáme SNackbaríček
            imageView.setImage(ImageSource.bitmap(BitmapScaled(picturePath, 800, 800)));

            imageView.setLongClickable(true);
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                    return true;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            picture = cursor.getString(columnIndex);
            cursor.close();

            SubsamplingScaleImageView imageView = new SubsamplingScaleImageView(this);
            imageView.setImage(ImageSource.bitmap(BitmapScaled(picture, 800, 800)));

            savePath(picture);
        }
    }

    private Bitmap BitmapScaled(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
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

    public String loadPath() {
        SharedPreferences prefs = getSharedPreferences("PicturePath", Context.MODE_PRIVATE);
        return prefs.getString("Path", null);
    }
    public void savePath(String string){
        SharedPreferences prefs = getSharedPreferences("PicturePath", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Path", string).apply();
    }
}