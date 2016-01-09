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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.thejuanandonly.schoolapp.R;

import org.json.JSONArray;
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
        toolbar.setBackgroundColor(getResources().getColor(R.color.mainblue));

        ImageView img = (ImageView) getActivity().findViewById(R.id.overviewImg);
        img.setVisibility(View.GONE);

        TextView quote = (TextView) getActivity().findViewById(R.id.quote);
        quote.setVisibility(View.GONE);

        TextView author = (TextView) getActivity().findViewById(R.id.author);
        author.setVisibility(View.GONE);

        Switch notificationsCheckBox = (Switch) getView().findViewById(R.id.notificationsCheckBox);
        Switch soundsCheckBox = (Switch) getView().findViewById(R.id.soundsNotificationCheckBox);
        Switch vibrationsCheckBox = (Switch) getView().findViewById(R.id.vibrationsNotificationCheckBox);

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

        SharedPreferences preferences= getContext().getSharedPreferences("Global", Context.MODE_PRIVATE);
        try {
            JSONArray conversionArray = new JSONArray(preferences.getString("conversion", null));

            EditText et1 = (EditText) getView().findViewById(R.id.etPerc1);
            EditText et2 = (EditText) getView().findViewById(R.id.etPerc2);
            EditText et3 = (EditText) getView().findViewById(R.id.etPerc3);
            EditText et4 = (EditText) getView().findViewById(R.id.etPerc4);

            et1.setText(conversionArray.getString(0));
            et2.setText(conversionArray.getString(1));
            et3.setText(conversionArray.getString(2));
            et4.setText(conversionArray.getString(3));

        }catch (Exception e){}

        super.onStart();
    }

    @Override
    public void onStop() {
        Switch notificationsCheckBox = (Switch) getView().findViewById(R.id.notificationsCheckBox);
        Switch soundsCheckBox = (Switch) getView().findViewById(R.id.soundsNotificationCheckBox);
        Switch vibrationsCheckBox = (Switch) getView().findViewById(R.id.vibrationsNotificationCheckBox);

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

        JSONArray conversionArray = new JSONArray();
        SharedPreferences preferences= getContext().getSharedPreferences("Global", Context.MODE_PRIVATE);

        EditText et1 = (EditText) getView().findViewById(R.id.etPerc1);
        EditText et2 = (EditText) getView().findViewById(R.id.etPerc2);
        EditText et3 = (EditText) getView().findViewById(R.id.etPerc3);
        EditText et4 = (EditText) getView().findViewById(R.id.etPerc4);

        conversionArray.put(et1.getText().toString());
        conversionArray.put(et2.getText().toString());
        conversionArray.put(et3.getText().toString());
        conversionArray.put(et4.getText().toString());

        preferences.edit().putString("conversion", conversionArray.toString()).apply();

        super.onStop();
    }

}