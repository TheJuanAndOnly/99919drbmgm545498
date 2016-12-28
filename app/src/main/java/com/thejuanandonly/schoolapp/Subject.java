package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Robo on 11/1/2015.
 */
public class Subject {

    public String subject;
    public String average;

    public Subject(String subject,  String average) {
        this.subject = subject;
        this.average = average;
    }

    public static ArrayList<Subject> getSubjects(Context context) {

        ArrayList<Subject> subject = new ArrayList<>();
        String subjectName = "";
        String average;
        SharedPreferences preferences = context.getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);
        JSONArray arrayOfSubjects;
        try {
            arrayOfSubjects = new JSONArray(preferences.getString("List", null));
        }catch (Exception e){
            arrayOfSubjects = new JSONArray();
        }

        for (int i = 0; i < arrayOfSubjects.length(); i++){
            try {
                subjectName = arrayOfSubjects.getString(i);
            }catch (JSONException ignored) {}

            SharedPreferences prefs = context.getSharedPreferences("Subject" + subjectName, Context.MODE_PRIVATE);

            average = prefs.getString("AvgGrade", "");

            double doubleAvg;
            try {

                doubleAvg = Double.valueOf(average);
            }catch (Exception e) {
                doubleAvg = 0;
            }

            if (doubleAvg >= 100){
                DecimalFormat df = new DecimalFormat("#");
                try {
                    doubleAvg = Double.valueOf(df.format(doubleAvg));
                } catch (NumberFormatException ignored) {
                }
            }else if (doubleAvg >= 10){
                DecimalFormat df = new DecimalFormat("#.#");
                try {
                    doubleAvg = Double.valueOf(df.format(doubleAvg));
                } catch (NumberFormatException e) {
                }
            }else {
                DecimalFormat df = new DecimalFormat("#.##");
                try {
                    doubleAvg = Double.valueOf(df.format(doubleAvg));
                } catch (NumberFormatException e) {
                }
            }

            average = String.valueOf(doubleAvg);

            if (average.length() == 3){
                average += "0";
            } else if (average.length() > 4) {
                average = average.substring(0, 4);
                String ss = average.substring(3, 4);
                if (ss.equals(".")) {
                    average = average.substring(0, 3);
                }
            }

            subject.add(new Subject(subjectName, average));
        }

        return subject;
    }

    @Override
    public String toString(){
        return subject + ": " + average;
    }
}
