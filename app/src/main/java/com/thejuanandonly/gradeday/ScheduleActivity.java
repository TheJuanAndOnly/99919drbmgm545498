package com.thejuanandonly.gradeday;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uk.co.senab.photoview.PhotoView;

public class ScheduleActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    PhotoView imageView;
    String picture;
    Snackbar snackbar;
    boolean changeImageViewd;
    boolean spCheck;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_activity_layout);

        // Obtain the shared Tracker instance.

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(ScheduleActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }


        String picturePath = loadPath();
        imageView = (PhotoView) findViewById(R.id.schedule_image_view);


        if (picturePath != null) {
            imageView.setImageBitmap(BitmapScaled(picturePath, 800, 800));
        } else {
            snackbar = Snackbar.make(findViewById(android.R.id.content), "Please import your schedule", Snackbar.LENGTH_INDEFINITE);
            snackbar.show();
            snackbar.setAction("import", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            });
        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    finish();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences prefs = this.getSharedPreferences("ImgChange", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            picture = cursor.getString(columnIndex);
            cursor.close();


            imageView.setImageBitmap(BitmapScaled(picture, 800, 800));

            savePath(picture);

            try {
                spCheck = prefs.getBoolean("ImgChangeViewed", false);

                if (!spCheck) {
                    snackbar = Snackbar.make(findViewById(android.R.id.content), "To change the image, click and hold the current one", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    snackbar.setAction("got it", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changeImageViewd = true;
                            snackbar.dismiss();
                            editor.putBoolean("ImgChangeViewed", changeImageViewd).apply();
                        }
                    });
                }
            } catch (Exception e) {
            }

        }
    }

    public void changeImageSnackbar() {

        SharedPreferences prefs = this.getSharedPreferences("ImgChange", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();


        snackbar = Snackbar.make(findViewById(android.R.id.content), "To change the image, click and hold your current image", Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        snackbar.setAction("got it", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImageViewd = true;
                snackbar.dismiss();
            }
        });

        editor.putBoolean("ImgChangeViewed", changeImageViewd).apply();
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

    public void savePath(String string) {
        SharedPreferences prefs = getSharedPreferences("PicturePath", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Path", string).apply();
    }
}