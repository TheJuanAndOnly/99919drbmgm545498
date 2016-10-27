package com.thejuanandonly.schoolapp;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by Robo on 24.06.2016.
 */
public class SubjectData {
    private String subject;
    private String average;

    private int gradeType;
    private int testsToWrite;

    private boolean useCategories;
    private boolean usePercentages;
    private boolean useValues;

    private JSONArray arrayOfCategories;
    private JSONArray arrayOfPercentages;

    private ArrayList<JSONArray> allGrades;

    private static final String TAG = "GradeDay SubjectData";

    public SubjectData(SharedPreferences prefs){
        SharedPreferences.Editor editor = prefs.edit();

        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
        }catch (Exception e) {

            arrayOfCategories = new JSONArray();
            arrayOfCategories.put("Grades");
            editor.putString("ListOfCategories", arrayOfCategories.toString());

            arrayOfPercentages = new JSONArray();
            arrayOfPercentages.put("100");
            editor.putString("ListOfPercentages", arrayOfPercentages.toString());
        }

        gradeType = prefs.getInt("GradeType", 0);
        testsToWrite = prefs.getInt("testsToWrite", 1);

        useCategories = prefs.getBoolean("useCategories", false);
        usePercentages = prefs.getBoolean("usePercentages", false);
        useValues = prefs.getBoolean("useValues", false);

        try {
            for (int i = 0; i < arrayOfCategories.length(); i++) {
                allGrades.add(new JSONArray(prefs.getString(arrayOfCategories.getString(i) + "Grades" + gradeType, null)));
            }
        }catch (Exception e){
            Log.e(TAG, e.toString());
        }

        editor.apply();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getAverage() {
        return average;
    }

    public void setAverage(String average) {
        this.average = average;
    }

    public int getGradeType() {
        return gradeType;
    }

    public void setGradeType(int gradeType) {
        this.gradeType = gradeType;
    }

    public int getTestsToWrite() {
        return testsToWrite;
    }

    public void setTestsToWrite(int testsToWrite) {
        this.testsToWrite = testsToWrite;
    }

    public boolean isUseCategories() {
        return useCategories;
    }

    public void setUseCategories(boolean useCategories) {
        this.useCategories = useCategories;
    }

    public boolean isUsePercentages() {
        return usePercentages;
    }

    public void setUsePercentages(boolean usePercentages) {
        this.usePercentages = usePercentages;
    }

    public boolean isUseValues() {
        return useValues;
    }

    public void setUseValues(boolean useValues) {
        this.useValues = useValues;
    }

    public JSONArray getArrayOfCategories() {
        return arrayOfCategories;
    }

    public void setArrayOfCategories(JSONArray arrayOfCategories) {
        this.arrayOfCategories = arrayOfCategories;
    }

    public JSONArray getArrayOfPercentages() {
        return arrayOfPercentages;
    }

    public void setArrayOfPercentages(JSONArray arrayOfPercentages) {
        this.arrayOfPercentages = arrayOfPercentages;
    }

    public ArrayList<JSONArray> getAllGrades() {
        return allGrades;
    }

    public void setAllGrades(ArrayList<JSONArray> allGrades) {
        this.allGrades = allGrades;
    }
}
