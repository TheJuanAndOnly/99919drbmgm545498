package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Robo on 11/1/2015.
 */
public class CustomSubjectAdapter extends ArrayAdapter<Subject> {
    public CustomSubjectAdapter(Context context, ArrayList<Subject> subjects) {
        super(context, 0, subjects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Subject subject = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.subject_listview, parent, false);
        }

        TextView nameTV = (TextView) convertView.findViewById(R.id.subjectName);
        TextView averageTV = (TextView) convertView.findViewById(R.id.subjectLVAverageText);

        nameTV.setText(subject.subject);
        averageTV.setText(subject.average);

        String s = averageTV.getText().toString();
        if (s.equals("")){
            TextView separator = (TextView) convertView.findViewById(R.id.separatorLV);
            separator.setBackgroundColor(Color.TRANSPARENT);
        }

        SharedPreferences preferences = getContext().getSharedPreferences("themeSave", Context.MODE_PRIVATE);
        int theme = preferences.getInt("theme", 0);

        TextView left = (TextView) convertView.findViewById(R.id.leftTV);
        TextView separator = (TextView) convertView.findViewById(R.id.separatorLV);

        switch (theme) {
            case 1:

                left.setBackgroundColor(getContext().getResources().getColor(R.color.orange));
                separator.setBackgroundColor(getContext().getResources().getColor(R.color.orange));

                break;
            case 2:

                left.setBackgroundColor(getContext().getResources().getColor(R.color.green));
                separator.setBackgroundColor(getContext().getResources().getColor(R.color.green));

                break;
            case 3:

                left.setBackgroundColor(getContext().getResources().getColor(R.color.blue));
                separator.setBackgroundColor(getContext().getResources().getColor(R.color.blue));

                break;
            case 4:

                left.setBackgroundColor(getContext().getResources().getColor(R.color.grey));
                separator.setBackgroundColor(getContext().getResources().getColor(R.color.grey));

                break;
            case 5:

                left.setBackgroundColor(getContext().getResources().getColor(R.color.teal));
                separator.setBackgroundColor(getContext().getResources().getColor(R.color.teal));

                break;
            case 6:

                left.setBackgroundColor(getContext().getResources().getColor(R.color.brown));
                separator.setBackgroundColor(getContext().getResources().getColor(R.color.brown));

                break;
            default:

                left.setBackgroundColor(getContext().getResources().getColor(R.color.red));
                separator.setBackgroundColor(getContext().getResources().getColor(R.color.red));

        }

        return convertView;
    }

}
