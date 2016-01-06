package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
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
        JSONArray arrayOfPercentages = new JSONArray(),
                    arrayOfCategories = new JSONArray();
        try {
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
            editText.setText(arrayOfPercentages.getString(position));
        }catch (Exception e) {

            try {
                arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
            }catch (Exception ex){
                arrayOfCategories = new JSONArray();
            }
            editText.setText(String.valueOf(100 / arrayOfCategories.length()));
        }

        int count = 0;
        for (int i = 0; i < arrayOfPercentages.length(); i++){



            try {
                //Toast.makeText(getContext(), arrayOfPercentages.getString(i), Toast.LENGTH_SHORT).show();
                if (arrayOfPercentages.getInt(i) != 0){
                    count++;
                }
            }catch (JSONException e) {}
        }

        try {
            if (editText.getText().toString().equals("0")) {
                editText.getBackground().setColorFilter(getContext().getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
            }
            else if (editText.getText().toString().equals(String.valueOf(100 / count))) {
                editText.getBackground().setColorFilter(getContext().getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
            }
            else {
                editText.getBackground().setColorFilter(getContext().getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
            }
        }catch (Exception e){}




        return convertView;
    }
}