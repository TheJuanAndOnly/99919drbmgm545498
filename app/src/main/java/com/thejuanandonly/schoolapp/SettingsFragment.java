package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.thejuanandonly.schoolapp.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class SettingsFragment extends Fragment {
    private Spinner themeSpinner;
    String[] stringArray;
    int position;
    android.support.v7.widget.Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_layout, null);
    }

    @Override
    public void onStart() {
        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");

        CheckBox notificationsCheckBox = (CheckBox) getView().findViewById(R.id.notificationsCheckBox);
        CheckBox soundsCheckBox = (CheckBox) getView().findViewById(R.id.soundsNotificationCheckBox);
        CheckBox vibrationsCheckBox = (CheckBox) getView().findViewById(R.id.vibrationsNotificationCheckBox);

        SharedPreferences prefs = getActivity().getSharedPreferences("notificationsSave", Context.MODE_PRIVATE);

        boolean n = prefs.getBoolean("notifications", true),
                s = prefs.getBoolean("sounds", true),
                v = prefs.getBoolean("vibrations", true);

        notificationsCheckBox.setChecked(n);
        soundsCheckBox.setChecked(s);
        vibrationsCheckBox.setChecked(v);

        if (!n) {
            soundsCheckBox.setVisibility(View.GONE);
            vibrationsCheckBox.setVisibility(View.GONE);
        } else {
            soundsCheckBox.setVisibility(View.VISIBLE);
            vibrationsCheckBox.setVisibility(View.VISIBLE);
        }

        theme();

        addItemsToSpinner();
        addListenerToSpinner();

        super.onStart();
    }

    @Override
    public void onStop() {
        CheckBox notificationsCheckBox = (CheckBox) getView().findViewById(R.id.notificationsCheckBox);
        CheckBox soundsCheckBox = (CheckBox) getView().findViewById(R.id.soundsNotificationCheckBox);
        CheckBox vibrationsCheckBox = (CheckBox) getView().findViewById(R.id.vibrationsNotificationCheckBox);

        boolean n = notificationsCheckBox.isChecked(),
                s = soundsCheckBox.isChecked(),
                v = vibrationsCheckBox.isChecked();

        SharedPreferences prefs = getActivity().getSharedPreferences("notificationsSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("notifications", n);
        editor.putBoolean("sounds", s);
        editor.putBoolean("vibrations", v);
        editor.apply();

        SharedPreferences themePrefs = getActivity().getSharedPreferences("themeSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor themeEditor = themePrefs.edit();

        if (((MainActivity) getActivity()).willSend) {

            themeEditor.putInt("theme", position);
            themeEditor.apply();

        }


        super.onStop();
    }

    public void addItemsToSpinner() {

        themeSpinner = (Spinner) getView().findViewById(R.id.themeSpinner);

        String red = "Red",
               orange = "Orange",
               green = "Green",
               blue = "Blue",
               grey = "Grey",
               teal = "Teal",
               brown = "Brown";

        SharedPreferences prefs = getActivity().getSharedPreferences("themeSave", Context.MODE_PRIVATE);
        int pos = prefs.getInt("theme", 0);

        stringArray = new String[]{red, orange, green, blue, grey, teal, brown};

        ArrayAdapter<String> SpinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, stringArray);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        themeSpinner.setAdapter(SpinnerAdapter);

        themeSpinner.setSelection(pos);
    }

    public void addListenerToSpinner() {
        themeSpinner = (Spinner) getView().findViewById(R.id.themeSpinner);
        themeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View arg1, int pos, long arg3) {
                position = pos;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO maybe add something here later
            }
        });
    }

    public void theme() {
        SharedPreferences prefs = getContext().getSharedPreferences("themeSave", Context.MODE_PRIVATE);
        int theme = prefs.getInt("theme", 0);

        View view1 = (View) getView().findViewById(R.id.separator1);
        View view2 = (View) getView().findViewById(R.id.separator2);
        View view3 = (View) getView().findViewById(R.id.separator3);

        switch (theme) {
            default:

                view1.setBackgroundColor(getResources().getColor(R.color.mainblue));
                view2.setBackgroundColor(getResources().getColor(R.color.mainblue));
                view3.setBackgroundColor(getResources().getColor(R.color.mainblue));
        }
    }

}