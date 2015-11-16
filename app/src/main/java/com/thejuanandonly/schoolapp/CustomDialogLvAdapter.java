package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Robo on 11/6/2015.
 */
public class CustomDialogLvAdapter extends ArrayAdapter<Grade> {
    public CustomDialogLvAdapter(Context context, ArrayList<Grade> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Grade grade = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.subject_settings_listview, parent, false);
        }

        TextView categoryTV = (TextView) convertView.findViewById(R.id.dialogCategoryTextView);
        categoryTV.setText(grade.category);


        EditText editText  = (EditText) convertView.findViewById(R.id.settingsLVEditText);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        SharedPreferences prefs = getContext().getSharedPreferences("Subject" + SubjectDetailActivity.currentSubject, Context.MODE_PRIVATE);
        JSONArray arrayOfPercentages;
        try {
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
            editText.setText(arrayOfPercentages.getString(position));
        }catch (Exception e) {
            JSONArray arrayOfCategories;
            try {
                arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
            }catch (Exception ex){arrayOfCategories = new JSONArray();}
            editText.setText(String.valueOf(100 / arrayOfCategories.length()));
        }

        return convertView;
    }
}