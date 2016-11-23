package com.thejuanandonly.schoolapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import agency.tango.materialintroscreen.SlideFragment;

/**
 * Created by Daniel on 23-Nov-16.
 */
public class TutikThirdSlide extends SlideFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.intro_third_slide, container, false);
        return view;
    }


    @Override
    public int backgroundColor() {
        return R.color.darker;
    }

    @Override
    public int buttonsColor() {
        return R.color.sexyTurqiousee;
    }

    @Override
    public boolean canMoveFurther() {
        return true;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return "kappa";
    }
}