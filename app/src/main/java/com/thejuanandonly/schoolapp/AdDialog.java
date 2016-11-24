package com.thejuanandonly.schoolapp;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.NativeExpressAdView;

/**
 * Created by Daniel on 20-Nov-16.
 */

public class AdDialog {

    ImageView adImageView;
    Button bContinue;
    ArrayList<Drawable> bckgPictures;

    public void dialog(Activity activity) {

        bckgPictures = new ArrayList<>();
        bckgPictures.add(activity.getResources().getDrawable(R.drawable.ad_blue_bckg));
        bckgPictures.add(activity.getResources().getDrawable(R.drawable.ad_green_bckg));
        bckgPictures.add(activity.getResources().getDrawable(R.drawable.ad_purple_bckg));
        bckgPictures.add(activity.getResources().getDrawable(R.drawable.ad_turquoise_bckg));
        bckgPictures.add(activity.getResources().getDrawable(R.drawable.adr_red_bckg));

        final Dialog dialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.ad_layout);


        int randomInt = new Random().nextInt(4);
        adImageView = (ImageView) dialog.findViewById(R.id.ad_ImageView);
        adImageView.setImageDrawable(bckgPictures.get(randomInt));

        bContinue = (Button) dialog.findViewById(R.id.dialogContinue);
        bContinue.setVisibility(View.GONE);
        bContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        CountDownTimer timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                bContinue.setVisibility(View.VISIBLE);
            }
        }.start();


        NativeExpressAdView adView = (NativeExpressAdView)dialog.findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("2B452CE5360489F25CBE4A321D3CC218")
                .build();
        adView.loadAd(adRequest);

        dialog.show();

    }

}
