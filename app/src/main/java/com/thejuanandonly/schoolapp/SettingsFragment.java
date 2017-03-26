package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.support.v7.widget.SwitchCompat;
import android.widget.TextView;

import org.json.JSONArray;
import org.w3c.dom.Text;

public class SettingsFragment extends Fragment {
    int position;
    android.support.v7.widget.Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_layout, null);
    }

    @Override
    public void onStart() {
        percentageListener();

        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        toolbar.setBackgroundColor(getResources().getColor(R.color.darker));

        SwitchCompat notificationsCheckBox = (SwitchCompat) getView().findViewById(R.id.notificationsCheckBox);
        SwitchCompat soundsCheckBox = (SwitchCompat) getView().findViewById(R.id.soundsNotificationCheckBox);
        SwitchCompat vibrationsCheckBox = (SwitchCompat) getView().findViewById(R.id.vibrationsNotificationCheckBox);
        SwitchCompat activeTasksCheckBox = (SwitchCompat) getView().findViewById(R.id.switch_active);

        SharedPreferences prefs = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);

        boolean n = prefs.getBoolean("notifications", true),
                s = prefs.getBoolean("sounds", true),
                v = prefs.getBoolean("vibrations", true),
                a = prefs.getBoolean("active", true);

        notificationsCheckBox.setChecked(n);
        soundsCheckBox.setChecked(s);
        vibrationsCheckBox.setChecked(v);
        activeTasksCheckBox.setChecked(a);

        TextView infoTextview = (TextView) getView().findViewById(R.id.textView4);

        if (!n) {
            soundsCheckBox.setVisibility(View.GONE);
            vibrationsCheckBox.setVisibility(View.GONE);
        } else {
            infoTextview.setVisibility(View.GONE);
            soundsCheckBox.setVisibility(View.VISIBLE);
            vibrationsCheckBox.setVisibility(View.VISIBLE);
        }

        SharedPreferences preferences = getContext().getSharedPreferences("Global", Context.MODE_PRIVATE);
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

        } catch (Exception e) {
        }


        notificationsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((MainActivity) getContext()).notificationsClick(getView());

                Handler handler = new Handler();
                final TextView infoTextview = (TextView) getView().findViewById(R.id.textView4);

                if (isChecked) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            infoTextview.setVisibility(View.GONE);
                            infoTextview.setVisibility(View.GONE);
                        }
                    });

                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            infoTextview.setVisibility(View.VISIBLE);
                            infoTextview.setVisibility(View.VISIBLE);
                        }
                    });


                }

            }
        });



        soundsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((MainActivity) getContext()).soundsNotificationClick(getView());
            }
        });

        vibrationsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                ((MainActivity) getContext()).vibrationsNotificationClick(getView());
            }
        });

        activeTasksCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ((MainActivity) getContext()).activeTasksClick(getView());
            }
        });

        super.onStart();
    }


    @Override
    public void onStop() {
        SwitchCompat notificationsCheckBox = (SwitchCompat) getView().findViewById(R.id.notificationsCheckBox);
        SwitchCompat soundsCheckBox = (SwitchCompat) getView().findViewById(R.id.soundsNotificationCheckBox);
        SwitchCompat vibrationsCheckBox = (SwitchCompat) getView().findViewById(R.id.vibrationsNotificationCheckBox);
        SwitchCompat activeTasksCheckBox = (SwitchCompat) getView().findViewById(R.id.switch_active);

        boolean n = notificationsCheckBox.isChecked(),
                s = soundsCheckBox.isChecked(),
                v = vibrationsCheckBox.isChecked(),
                a = activeTasksCheckBox.isChecked();

        SharedPreferences prefs = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean("notifications", n);
        editor.putBoolean("sounds", s);
        editor.putBoolean("vibrations", v);
        editor.putBoolean("active", a);
        editor.apply();

        SharedPreferences themePrefs = getActivity().getSharedPreferences("themeSave", Context.MODE_PRIVATE);
        SharedPreferences.Editor themeEditor = themePrefs.edit();

        if (((MainActivity) getActivity()).willSend) {

            themeEditor.putInt("theme", position);
            themeEditor.apply();

        }

        JSONArray conversionArray = new JSONArray();
        SharedPreferences preferences = getContext().getSharedPreferences("Global", Context.MODE_PRIVATE);

        EditText et1 = (EditText) getView().findViewById(R.id.etPerc1);
        EditText et2 = (EditText) getView().findViewById(R.id.etPerc2);
        EditText et3 = (EditText) getView().findViewById(R.id.etPerc3);
        EditText et4 = (EditText) getView().findViewById(R.id.etPerc4);

        conversionArray.put(et1.getText().toString());
        conversionArray.put(et2.getText().toString());
        conversionArray.put(et3.getText().toString());
        conversionArray.put(et4.getText().toString());
        conversionArray.put("0");

        preferences.edit().putString("conversion", conversionArray.toString()).apply();

        super.onStop();
    }

    public void percentageListener() {
        ImageView arrow = (ImageView) getView().findViewById(R.id.rollDownSettingsPerc);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen();
            }
        });
        RelativeLayout layout = (RelativeLayout) getView().findViewById(R.id.percConversionTv);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listen();
            }
        });
    }

    public void listen() {
        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.percConversionLayout);
        ImageView button = (ImageView) getView().findViewById(R.id.rollDownSettingsPerc);

        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
            button.setBackground(getResources().getDrawable(R.drawable.ic_arrow_drop_down_white_24dp));
        } else {
            layout.setVisibility(View.VISIBLE);
            button.setBackground(getResources().getDrawable(R.drawable.ic_arrow_drop_up_white_24dp));
        }
    }

}