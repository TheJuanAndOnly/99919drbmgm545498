package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.util.Log;
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
        JSONArray arrayOfPercentages = new JSONArray(),
                    arrayOfCategories = new JSONArray();
        try {
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
            editText.setText(arrayOfPercentages.getString(position));

            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));

        }catch (Exception e) {
            editText.setText(String.valueOf(100 / arrayOfCategories.length()));
            Log.d("debug", String.valueOf(e));
        }

        int count = 0,
            percentages = 0;
        for (int i = 0; i < arrayOfPercentages.length(); i++){
            try {
                if (arrayOfPercentages.getInt(i) != 0){
                    count++;
                }
                percentages += arrayOfPercentages.getInt(i);
            }catch (JSONException e) {
                Log.d("debug", String.valueOf(e));
            }
        }

        boolean doOrange = false;

        int gradeType = prefs.getInt("GradeType", 0);
        JSONArray arrayOfGrades = new JSONArray();
        try {
            arrayOfGrades = new JSONArray(prefs.getString(arrayOfCategories.getString(position) + "Grades" + gradeType, null));
        }catch (Exception e){
            Log.d("debug", String.valueOf(e));
        }

        if (arrayOfGrades.length() != 0) {
            if (editText.getText().toString().equals("0") || editText.getText().toString().isEmpty()) {
                doOrange = true;
            }
        }
        else if (arrayOfGrades.length() == 0){
            if (!editText.getText().toString().equals("0")) {
                doOrange = true;
            }
        }

        try {
            if (doOrange){
                editText.getBackground().setColorFilter(getContext().getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_ATOP);
            }
            else if (editText.getText().toString().equals("0") || editText.getText().toString().isEmpty()) {
                editText.getBackground().setColorFilter(getContext().getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
            }
            else if (percentages == 100 || editText.getText().toString().equals(String.valueOf(100 / count))) {
                editText.getBackground().setColorFilter(getContext().getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
            }
            else {
                editText.getBackground().setColorFilter(getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
            }

        }catch (Exception e){
            Log.d("debug", String.valueOf(e));
        }

        return convertView;
    }
}