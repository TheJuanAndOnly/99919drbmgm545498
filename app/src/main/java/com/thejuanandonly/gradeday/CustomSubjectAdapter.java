package com.thejuanandonly.gradeday;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.ColorInt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

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



        SharedPreferences preferences = getContext().getSharedPreferences("themeSave", Context.MODE_PRIVATE);
        int theme = preferences.getInt("theme", 0);


        int pos = position;
        while(pos > 6){
            pos -= 7;
        }


        SharedPreferences arrayPrefs = getContext().getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);

        JSONArray arrayOfSubjects = new JSONArray();
        String currentSubject = "";
        try {
            arrayOfSubjects = new JSONArray(arrayPrefs.getString("List", null));
            currentSubject = arrayOfSubjects.getString(position);
        }catch (Exception e) {}

        SharedPreferences prefs = getContext().getSharedPreferences("Subject" + currentSubject, Context.MODE_PRIVATE);

        TextView rating = (TextView) convertView.findViewById(R.id.goodJobTv);
        double avg = Double.parseDouble(subject.average);

        rating.setTextColor(getContext().getResources().getColor(R.color.white));

        switch (prefs.getInt("GradeType", 0)) {
            case 0:
                if (avg == 0){
                    rating.setText(R.string.New);
                    rating.setTextColor(getContext().getResources().getColor(R.color.red));
                }else if (avg <= 1.1){
                    rating.setText(R.string.rating1);
                }else if (avg <= 2.0){
                    rating.setText(R.string.rating2);
                }else if (avg <= 3.0){
                    rating.setText(R.string.rating3);
                }else if (avg <= 4.0){
                    rating.setText(R.string.rating4);
                }else {
                    rating.setText(R.string.rating5);
                }
                break;
            case 1:
                if (avg == 0){
                    rating.setText(R.string.New);
                    rating.setTextColor(getContext().getResources().getColor(R.color.red));
                }else if (avg >= 98){
                    rating.setText(R.string.rating1);
                }else if (avg >= 90){
                    rating.setText(R.string.rating2);
                }else if (avg >= 75){
                    rating.setText(R.string.rating3);
                }else if (avg >= 50){
                    rating.setText(R.string.rating4);
                }else {
                    rating.setText(R.string.rating5);
                }
                break;
            case 2:
                if (avg == 0){
                    rating.setText(R.string.New);
                    rating.setTextColor(getContext().getResources().getColor(R.color.red));
                }else if (avg >= 4.1){
                    rating.setText(R.string.rating1);
                }else if (avg >= 3.67){
                    rating.setText(R.string.rating2);
                }else if (avg >= 2.67){
                    rating.setText(R.string.rating3);
                }else if (avg >= 1.67){
                    rating.setText(R.string.rating4);
                }else {
                    rating.setText(R.string.rating5);
                }
                break;
        }

        JSONArray arrayOfCategories = new JSONArray();
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
        }catch (Exception ex){}

        ArrayList<Integer> options = new ArrayList<>();

        for (int i = 0; i < arrayOfCategories.length(); i++){

            JSONArray arrayOfGrades;
            try {
                arrayOfGrades = new JSONArray(prefs.getString(arrayOfCategories.getString(i) + "Grades" + prefs.getInt("GradeType" , 0), null));

                switch (prefs.getInt("GradeType", 0)){
                    case 0:
                        if (arrayOfGrades.getInt(arrayOfGrades.length() - 1) < avg){
                            options.add(i, 0);
                        }
                        else if (arrayOfGrades.getInt(arrayOfGrades.length() - 1) > avg){
                            options.add(i, 1);
                        }
                        else{
                            options.add(i, 3);
                        }
                        break;
                    case 1:
                        if (arrayOfGrades.getInt(arrayOfGrades.length() - 1) > avg){
                            options.add(i, 0);
                        }
                        else if (arrayOfGrades.getInt(arrayOfGrades.length() - 1) < avg){
                            options.add(i, 1);
                        }
                        else{
                            options.add(i, 3);
                        }
                        break;
                    case 2:
                        Log.d("debugC", letterToNumber(arrayOfGrades.getString(arrayOfGrades.length() - 1)) + ", " + avg + ", " + position);

                        if (letterToNumber(arrayOfGrades.getString(arrayOfGrades.length() - 1)) > avg){
                            options.add(i, 0);
                        }
                        else if (letterToNumber(arrayOfGrades.getString(arrayOfGrades.length() - 1)) < avg){
                            options.add(i, 1);
                        }
                        else{
                            options.add(i, 3);
                        }
                    break;
                }

            } catch (Exception ex) {}

        }

        int result = 0;
        for (int i = 0; i < options.size(); i++){

            switch (options.get(i)){
                case 0:
                    result++;
                    break;
                case 1:
                    result--;
                    break;
                case 2:
                    break;
            }
        }

        ImageView arrow = (ImageView) convertView.findViewById(R.id.arrow);

        if (result > 0){

            arrow.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_trending_up));

        }else if (result < 0){

            arrow.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_trending_down));

        }else {

            arrow.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_trending_flat_white_24dp));

        }

        return convertView;
    }

    public double letterToNumber(String letter){

        switch (letter){
            case "A+":
                return 4.33;

            case "A":
                return 4;

            case "A-":
                return 4 - 0.33 ;

            case "B+":
                return 3 + 0.33;

            case "B":
                return 3;

            case "B-":
                return 3 - 0.33;

            case "C+":
                return 2 + 0.33;

            case "C":
                return 2;

            case "C-":
                return 2 - 0.33;

            case "D+":
                return  + 0.33;

            case "D":
                return 1;

            case "D-":
                return 1 - 0.33;

            case "F":
                return 0;

            default:
                return 0;
        }
    }
}
