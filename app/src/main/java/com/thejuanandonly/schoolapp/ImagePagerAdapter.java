package com.thejuanandonly.schoolapp;

import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.List;

/**
 * Created by Daniel on 11/23/2015.
 */
public class ImagePagerAdapter extends PagerAdapter implements View.OnTouchListener {

    private List<SubsamplingScaleImageView> images;

    public ImagePagerAdapter(List<SubsamplingScaleImageView> images) {
        this.images = images;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SubsamplingScaleImageView imageView = images.get(position);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(images.get(position));
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
