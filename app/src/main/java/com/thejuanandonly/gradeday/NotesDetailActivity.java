package com.thejuanandonly.gradeday;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Daniel on 10/31/2015.
 */
public class NotesDetailActivity extends AppCompatActivity {

    public static Context initialContext;
    public static String currentNote;
    android.support.v7.widget.Toolbar toolbar;

    CustomViewPager mViewPager;
    ViewPagerAdapter mAdapter;

    //Zooming image + span
    PhotoViewAttacher mAttacher;

    //HorizontalScrooview with images
    HorizontalScrollView horizontalScrollView;
    public LinearLayout images, linearWithPicture;

    public Animation downUp;

    Intent intent;
    int positionFromIntent;
    String uriFromIntent;

    ArrayList<ArrayList<String>> arrayOfArrays;
    ArrayList<String> arrayOfPictures;

    //CustomSharedPreferences
    SaveSharedPreferences customSharedPrefs = new SaveSharedPreferences();

    //Which picture to open
    int openPicture = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_detail_layout);

        mViewPager = (CustomViewPager) findViewById(R.id.viewPager);
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.scrollViewActivity);
        images = (LinearLayout) findViewById(R.id.scrollviewLinearActivity);

        downUp = AnimationUtils.loadAnimation(this, R.anim.down_up);
        downUp.setDuration(1000);

        linearWithPicture = (LinearLayout) findViewById(R.id.LinearWithPictures);
        linearWithPicture.setVisibility(View.GONE);

        intent = getIntent();
        positionFromIntent = intent.getIntExtra("position", 0);
        uriFromIntent = intent.getStringExtra("uri");

        initialContext = getApplicationContext();
        currentNote = getIntent().getExtras().getString("subjectNotes", null);

        arrayOfArrays = getArrayOfArrays();
        arrayOfPictures = new ArrayList<>();
        arrayOfPictures = arrayOfArrays.get(positionFromIntent);

        mAdapter = new ViewPagerAdapter(this.getApplicationContext(), arrayOfPictures);
        mViewPager.setAdapter(mAdapter);

        for (int i = 0; i < arrayOfPictures.size(); i++) {
            if (uriFromIntent.equals(arrayOfPictures.get(i))) {
                break;
            } else {
                openPicture++;
            }
        }

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(openPicture,true);
            }
        });


        for (int i = 0; i < arrayOfPictures.size(); i++) {

            final ImageView imageView = new ImageView(this.getApplicationContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(params);

//          imageView.setImageBitmap(BitmapScaled(arrayOfPictures.get(i), 100, 100));
//          imageView.setTag(i);
            imageView.setTag(arrayOfPictures.get(i));

            loadBitmap(arrayOfPictures.get(i), imageView, true);
            imageView.setRotation(90f);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = Integer.parseInt(imageView.getTag().toString());
                    mViewPager.setCurrentItem(position);
                    mAdapter.notifyDataSetChanged();

                }
            });
            images.addView(imageView);

        }

    }



    public void loadBitmap(String uri, ImageView imageView, Boolean resize) {
        ImageLoader task = new ImageLoader(imageView, uri, resize, this);
        task.execute(uri);
    }

    private ArrayList<ArrayList<String>> getArrayOfArrays() {

        String arrayOfArraysString = customSharedPrefs.getStringSP(NotesDetailActivity.this, "NOTES ARRAY", "arrayofarrays");
        ArrayList<ArrayList<String>> array = new ArrayList<>();

        try {
            String temp = "";
            ArrayList<String> tempArray = new ArrayList<>();
            char[] charArray = arrayOfArraysString.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                if (charArray[i] == '`') {
                    tempArray.add(temp);
                    temp = "";
                } else if (charArray[i] == '~') {
                    array.add(tempArray);
                    tempArray = new ArrayList<>();
                } else {
                    temp += charArray[i];
                }
            }
        } catch (NullPointerException e) {

        }

        return array;

    }

}


class ViewPagerAdapter extends PagerAdapter {

    ArrayList<String> images = new ArrayList<>();
    Context mContext;
    PhotoViewAttacher mAttacher;
    Animation downUp;



    public ViewPagerAdapter(Context cotext, ArrayList<String> images) {

        this.images = images;
        mContext = cotext;


    }



    @Override
    public Object instantiateItem(final ViewGroup collection, int position) {

        final PhotoView imageView = new PhotoView(mContext);
        collection.addView(imageView);
        imageView.setTag(images.get(position));
        loadBitmap(images.get(position), imageView, false);

        return imageView;

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
    }


    @Override
    public int getCount() {
        return this.images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void loadBitmap(String uri, ImageView imageView, boolean resize) {
        ImageLoader task = new ImageLoader(imageView, uri, resize, mContext);
        task.execute(uri);
    }
}