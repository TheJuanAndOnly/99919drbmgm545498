package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Robo on 10/29/2015.
 */
public class CustomGradeAdapter extends ArrayAdapter<Grade> {
    public CustomGradeAdapter(Context context, ArrayList<Grade> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Grade grade = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grade_list_view, parent, false);
        }

        TextView categoryTV = (TextView) convertView.findViewById(R.id.categoryTextView);
        TextView gradesTV = (TextView) convertView.findViewById(R.id.gradeTextView);

        Button editBtn = (Button) convertView.findViewById(R.id.gradeEditButton);
        Button deleteBtn = (Button) convertView.findViewById(R.id.gradeDeleteButton);

        if (SubjectDetailActivity.menuButtonChange%2 == 0) {
            editBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);
        }
        else {
            editBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
        }

        categoryTV.setText(grade.category);

        ArrayList<String> list = grade.grades;
        String grades = "";
        for (int i = 0; i < list.size(); i++){
            if (i == 0) grades = grades + list.get(i);
            else grades = grades + ", " + list.get(i);
        }
        gradesTV.setText(grades);

        return convertView;
    }
}