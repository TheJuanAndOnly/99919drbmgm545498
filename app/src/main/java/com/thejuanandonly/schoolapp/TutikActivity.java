package com.thejuanandonly.schoolapp;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.jar.*;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragment;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.adapter.SlidesAdapter;
import agency.tango.materialintroscreen.animations.IViewTranslation;
import agency.tango.materialintroscreen.animations.ViewTranslationWrapper;

import static android.view.View.GONE;

/**
 * Created by Daniel on 23-Nov-16.
 */

public class TutikActivity extends MaterialIntroActivity {

    Button skipButton, backButton;
    SlidesAdapter adapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableLastSlideAlphaExitTransition(true);

        addSlide(new TutikFirstSlide());

        addSlide(new TutikThirdSlide());

        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.darker)
                        .buttonsColor(R.color.sexyBlue)
                        .image(R.drawable.notes_image_tutik)
                        .title("Notes")
                        .neededPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE})
                        .description("Make learning from notes much more efficient. Group relevant stuff together and perhaps even share it with friends!")
                        .build());
    addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.darker)
                        .buttonsColor(R.color.sexyOrange)
                        .image(R.drawable.achiever)
                        .title("Predictions")
                        .description("Last but not least, the main feature of our app - Predictions. Informing you about what should your upcomming marks be in order to reach your desired mark.")
                        .build());

        getNextButtonTranslationWrapper().setExitTranslation(new IViewTranslation() {
            @Override
            public void translate(View view, @FloatRange(from = 0.0, to = 1.0) float percentage) {

                SharedPreferences preferences = getSharedPreferences("User", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("nickname", "User").apply();
            }
        });



    }

    @Override
    public ViewTranslationWrapper getNextButtonTranslationWrapper() {
        return super.getNextButtonTranslationWrapper();
    }

    @Override
    public void enableLastSlideAlphaExitTransition(boolean enableAlphaExitTransition) {
        super.enableLastSlideAlphaExitTransition(enableAlphaExitTransition);
    }

    @Override
    public void addSlide(SlideFragment slideFragment, MessageButtonBehaviour messageButtonBehaviour) {
        super.addSlide(slideFragment, messageButtonBehaviour);
    }

    @Override
    public void setSkipButtonVisible() {
        backButton.setVisibility(GONE);
        skipButton.setVisibility(View.VISIBLE);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int position = viewPager.getCurrentItem(); position < adapter.getCalculatedCount(); position++) {
                    if (adapter.getItem(position).canMoveFurther() == false) {
                        viewPager.setCurrentItem(position);
                        showError(adapter.getItem(position).cantMoveFurtherErrorMessage());
                        return;
                    }
                }
                viewPager.setCurrentItem(adapter.getLastItemPosition());
            }
        });
    }
}


