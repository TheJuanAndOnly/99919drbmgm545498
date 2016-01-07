package com.thejuanandonly.schoolapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.support.v4.view.ViewPager;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Daniel on 11/23/2015.
 */
public class GridViewPager extends Activity {
    int position;
    int id;
    public JSONArray arrayOfImgs = new JSONArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_layout);


        Intent intent = getIntent();
        position = intent.getExtras().getInt("position");


        ImageGridAdapter imageGridAdapter = new ImageGridAdapter(this);
        List<SubsamplingScaleImageView> images = new ArrayList<SubsamplingScaleImageView>();
        imageGridAdapter.notifyDataSetChanged();


        SharedPreferences prefs = this.getSharedPreferences("GridView", Context.MODE_PRIVATE);
        try {
            arrayOfImgs = new JSONArray(prefs.getString("NoteRImages", null));
        } catch (Exception e) {
        }


        try {

            for (int i = 0; i < imageGridAdapter.getCount(); i++) {

                SubsamplingScaleImageView imageView = new SubsamplingScaleImageView(this);
                imageView.setImage(ImageSource.bitmap(PictureGroupActivity.ALofRSelectedImgs.get(i)));
                images.add(imageView);
            }
        } catch (Exception e) {
            Toast.makeText(GridViewPager.this, "The arrayList is empty", Toast.LENGTH_SHORT).show();
        }

        ImagePagerAdapter pagerAdapter = new ImagePagerAdapter(images);
        ViewPager viewPager = (ViewPager)findViewById(R.id.viewPager);
        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(position);
        pagerAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
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



}