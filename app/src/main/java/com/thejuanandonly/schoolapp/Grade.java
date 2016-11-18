package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Robo on 10/29/2015.
 */
public class Grade {
    public String category;
    public ArrayList<String> grades;

    public Grade(String category,  ArrayList<String> grades) {
        this.category = category;
        this.grades = grades;
    }

    /*public static ArrayList<Grade> getGrades(JSONArray arrayOfCategories, ArrayList<JSONArray> allGrades) {
        ArrayList<Grade> subject = new ArrayList<Grade>();

        for (int i = 0; i < arrayOfCategories.length(); i++){
            String category;
            try {
                category = arrayOfCategories.getString(i);
            }catch (JSONException e){category = null;}

            JSONArray arrayOfGrades = allGrades.get(i);

            ArrayList<String> listOfGrades = new ArrayList<>();
            if (arrayOfGrades != null) {
                for (int  k= 0; k < arrayOfGrades.length(); k++){
                    try {
                        listOfGrades.add(arrayOfGrades.get(k).toString());
                    }catch (JSONException e) {}
                }
            }
            subject.add(new Grade(category, listOfGrades));
        }

        return subject;
    }

    public static Context getContext(){
        return SubjectDetailActivity.initialContext;
    }*/

}
