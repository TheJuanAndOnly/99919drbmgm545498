package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.thejuanandonly.schoolapp.R;

import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.IOException;

public class SettingsFragment extends Fragment {
    int position;
    android.support.v7.widget.Toolbar toolbar;
    private static int RESULT_LOAD_IMAGE = 1;
    public static Uri avatarURI;
    String picture, newUserName;
    ImageView changeAvatar, userPhotoimgview;
    EditText changeName;
    TextView userNicktxtview;
    Button set;
    private Tracker mTracker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_layout, null);

    }



    @Override
    public void onStart() {

        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        percentageListener();
        theme();

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
        Switch activeTasksCheckBox = (Switch) getView().findViewById(R.id.switch_active);

        SharedPreferences prefs = getActivity().getSharedPreferences("notificationsSave", Context.MODE_PRIVATE);

        boolean n = prefs.getBoolean("notifications", true),
                s = prefs.getBoolean("sounds", true),
                v = prefs.getBoolean("vibrations", true),
                a = prefs.getBoolean("active", true);

        notificationsCheckBox.setChecked(n);
        soundsCheckBox.setChecked(s);
        vibrationsCheckBox.setChecked(v);
        activeTasksCheckBox.setChecked(a);

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

        changeAvatar = (ImageView) getView().findViewById(R.id.avatarSettings);
        changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                newUserName = changeName.getText().toString();
                editor.putString("nickname", newUserName).apply();

                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        userPhotoimgview = (ImageView) getView().findViewById(R.id.usersPhoto);
        userNicktxtview = (TextView) getView().findViewById(R.id.usersNickname);

        changeName = (EditText) getView().findViewById(R.id.changeName);
        changeName.setTextColor(getResources().getColor(R.color.white));
        changeName.setText(sharedPreferences.getString("nickname", null));

        String imageUriString = sharedPreferences.getString("avatar", null);

        try {
            Bitmap bitmap = null;
            int w = 0, h = 0;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), Uri.parse(imageUriString));
                w = bitmap.getWidth();
                h = bitmap.getHeight();
            } catch (IOException e) {
                Toast.makeText(getContext(), "exception", Toast.LENGTH_SHORT).show();
            }
            int radius = w > h ? h : w;
            Bitmap roundBitmap = ImageToCircle.getCroppedBitmap(bitmap, radius);

            changeAvatar.setImageBitmap(roundBitmap);
        } catch (NullPointerException e) { }


        set = (Button) getView().findViewById(R.id.settings_set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newUserName = changeName.getText().toString();
                editor.putString("nickname", newUserName).apply();

                Intent updateUserDet = new Intent(getContext(), MainActivity.class);
                startActivity(updateUserDet);
            }
        });

        super.onStart();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("User", getContext().MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == getActivity().RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);

            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            picture = cursor.getString(columnIndex);
            avatarURI = selectedImage;
            cursor.close();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), avatarURI);
            } catch (IOException e) {
                Toast.makeText(getActivity().getApplicationContext(), "exce", Toast.LENGTH_SHORT).show();}

            int w = bitmap.getWidth(), h = bitmap.getHeight();
            int radius = w > h ? h : w;

            Bitmap roundBitmap = ImageToCircle.getCroppedBitmap(bitmap, radius);
            changeAvatar.setImageBitmap(roundBitmap);
            editor.putString("avatar", avatarURI.toString()).apply();

            set = (Button) getView().findViewById(R.id.settings_set);
            set.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newUserName = changeName.getText().toString();
                    editor.putString("nickname", newUserName).apply();
                    editor.putString("avatar", avatarURI.toString()).apply();

                    Intent updateUserDet = new Intent(getContext(), MainActivity.class);
                    startActivity(updateUserDet);
                }
            });

            onStop();
        }
    }

    @Override
    public void onStop() {
        Switch notificationsCheckBox = (Switch) getView().findViewById(R.id.notificationsCheckBox);
        Switch soundsCheckBox = (Switch) getView().findViewById(R.id.soundsNotificationCheckBox);
        Switch vibrationsCheckBox = (Switch) getView().findViewById(R.id.vibrationsNotificationCheckBox);
        Switch activeTasksCheckBox = (Switch) getView().findViewById(R.id.switch_active);

        boolean n = notificationsCheckBox.isChecked(),
                s = soundsCheckBox.isChecked(),
                v = vibrationsCheckBox.isChecked(),
                a = activeTasksCheckBox.isChecked();

        SharedPreferences prefs = getActivity().getSharedPreferences("notificationsSave", Context.MODE_PRIVATE);
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
        SharedPreferences preferences= getContext().getSharedPreferences("Global", Context.MODE_PRIVATE);

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

        set = (Button) getView().findViewById(R.id.settings_set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUserName = changeName.getText().toString();
                editor.putString("nickname", newUserName).apply();

                Intent updateUserDet = new Intent(getContext(), MainActivity.class);
                startActivity(updateUserDet);
            }
        });

        super.onStop();
    }

    public void percentageListener(){
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
    public void listen(){
        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.percConversionLayout);
        ImageView button = (ImageView) getView().findViewById(R.id.rollDownSettingsPerc);

        if (layout.getVisibility() == View.VISIBLE){
            layout.setVisibility(View.GONE);
            button.setBackground(getResources().getDrawable(R.drawable.ic_arrow_drop_down_white_24dp));
        }else {
            layout.setVisibility(View.VISIBLE);
            button.setBackground(getResources().getDrawable(R.drawable.ic_arrow_drop_up_white_24dp));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void theme() {

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.mainblue800));
    }

}