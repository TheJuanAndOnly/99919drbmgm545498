package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


/**
 * Created by Daniel on 11/15/2015.
 */
public class ImageGridAdapter extends BaseAdapter {


    public Uri imageUri;


    private Context mContext;

    public ImageGridAdapter(Context c) {
        this.mContext = c;
    }

    @Override
    public int getCount() {

        return 52;
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        imageUri = PictureGroupActivity.selectedImage;


        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(10, 10, 10, 10);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageURI(null);
        imageView.setImageURI(imageUri);

        return imageView;
    }




}
