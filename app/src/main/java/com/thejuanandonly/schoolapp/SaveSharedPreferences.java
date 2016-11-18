package com.thejuanandonly.schoolapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Daniel on 01-Nov-16.
 */

public class SaveSharedPreferences  {

    public void saveToSharedPreferences(Activity activity, String name, String subName, String string) {

        SharedPreferences preferences = activity.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (string != null) {
            editor.putString(subName, string);
            editor.commit();
        }

    }

    public boolean getBoolSP(Activity activity, String name, String subName, boolean defaultValue) {
        boolean bool;
        SharedPreferences preferences = activity.getSharedPreferences(name, Context.MODE_PRIVATE);
        bool = preferences.getBoolean(subName, defaultValue);
        return bool;
    }



    public String getStringSP(Activity activity, String name, String subName) {

        String string;
        SharedPreferences preferences = activity.getSharedPreferences(name, Context.MODE_PRIVATE);
        string = preferences.getString(subName, null);

        return string;
    }

    public int getIntegerSP(Activity activity, String name, String subName, int defaultValue) {
        int integer;
        SharedPreferences preferences = activity.getSharedPreferences(name, Context.MODE_PRIVATE);
        integer = preferences.getInt(subName, defaultValue);
        return integer;
    }

    public void cleanSP(Activity activity, String name) {
        SharedPreferences preferences = activity.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();
    }

}
