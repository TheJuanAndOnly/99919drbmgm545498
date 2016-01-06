package com.thejuanandonly.schoolapp;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.sql.Array;
import java.util.ArrayList;


/**
 * Created by Daniel on 11/15/2015.
 */

public class ImageGridAdapter extends BaseAdapter {


    private Context mContext;

    public ImageGridAdapter(Context c) {

        this.mContext = c;
    }

    @Override
    public int getCount() {
        try {
            return PictureGroupActivity.ALofSelectedImgs.size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {

        return PictureGroupActivity.ALofSelectedImgs.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {



        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(210, 210));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        }  else {
            imageView = (ImageView) convertView;
        }

        try {
                imageView.setImageBitmap(PictureGroupActivity.ALofSelectedImgs.get(position));

        }catch (Exception e) {}

        return imageView;
    }

}
