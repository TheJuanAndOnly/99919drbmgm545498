package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Robo on 24.06.2016.
 */
public class SubjectData {

    public static final int GRADE_TYPE_COUNT = 4;

    private String subject;
    private String average;

    private int gradeType;
    private int testsToWrite;

    private boolean useCategories;
    private boolean usePercentages;

    private List<String> arrayOfCategories;
    private List<Integer> arrayOfPercentages;

    private List<List<List<String>>> allGrades;

    private List<Integer> percentageConversion;

    private static final String TAG = "GradeDay SubjectData";

    public SubjectData(Context context, SharedPreferences prefs){

        String categories = prefs.getString("ListOfCategories", "Grades`");
        String percentages = prefs.getString("ListOfPercentages", "100`");
        arrayOfCategories = new ArrayList<>();
        arrayOfPercentages = new ArrayList<>();
        String temp = "";
        for (char c : categories.toCharArray()){
            if (c == '`') {
                arrayOfCategories.add(temp);
                temp = "";
                continue;
            }

            temp += c;
        }
        for (char c : percentages.toCharArray()){
            if (c == '`') {
                arrayOfPercentages.add(Integer.parseInt(temp));
                temp = "";
                continue;
            }

            temp += c;
        }

        gradeType = prefs.getInt("GradeType", 0);
        testsToWrite = prefs.getInt("testsToWrite", 1);

        useCategories = prefs.getBoolean("useCategories", false);
        usePercentages = prefs.getBoolean("usePercentages", false);

        allGrades = new ArrayList<>();
        for (int i = 0; i < GRADE_TYPE_COUNT; i++){
            allGrades.add(new ArrayList<List<String>>());
        }

        for (int i = 0; i < GRADE_TYPE_COUNT; i++){
            for (String category : arrayOfCategories) {
                String grades = prefs.getString(category + "Grades" + i, "err");

                List<String> tempList = new ArrayList<>();
                for (char c : grades.toCharArray()){
                    if (c == '`') {
                        tempList.add(temp);
                        temp = "";
                        continue;
                    }

                    temp += c;
                }
                try {
                    allGrades.get(i).set(arrayOfCategories.indexOf(category), tempList);
                } catch (IndexOutOfBoundsException e){
                    allGrades.get(i).add(tempList);
                }
            }
        }

        try {
            JSONArray percentageConversion = new JSONArray(context
                    .getSharedPreferences("Global", Context.MODE_PRIVATE)
                    .getString("conversion", null));

            this.percentageConversion = new ArrayList<>(4);

            for (int i = 0; i < percentageConversion.length(); i++){
                this.percentageConversion.add(percentageConversion.getInt(i));
            }

        } catch (Exception e) {
            this.percentageConversion = new ArrayList<>(Arrays.asList(90, 75, 50, 30));
        }

        Log.d(TAG, this.toString());
    }

    public void save(SharedPreferences prefs){
        SharedPreferences.Editor editor = prefs.edit();

        if (allGrades == null) return;

        String categories = "";
        String percentages = "";
        for (int i = 0; i < arrayOfCategories.size(); i++) {
            categories += arrayOfCategories.get(i) + "`";
        }
        for (int i = 0; i < arrayOfPercentages.size(); i++) {
            percentages += arrayOfPercentages.get(i) + "`";
        }
        editor.putString("ListOfCategories", categories);
        editor.putString("ListOfPercentages", percentages);

        editor.putInt("GradeType", gradeType);
        editor.putInt("testsToWrite", testsToWrite);

        editor.putBoolean("useCategories", useCategories);
        editor.putBoolean("usePercentages", usePercentages);

        Log.d(TAG, this.toString());

        for (int i = 0; i < GRADE_TYPE_COUNT; i++){
            for (int j = 0; j < arrayOfCategories.size(); j++) {
                String category = arrayOfCategories.get(j);

                List<String> arrayOfGrades = allGrades.get(i).get(j);
                String grades = "";
                for (int k = 0; k < arrayOfGrades.size(); k++) {
                    grades += arrayOfGrades.get(k) + "`";
                }

                editor.putString(category + "Grades" + i, grades);
            }
        }

        editor.apply();
    }

    public void delete(SharedPreferences prefs){
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear().apply();

        allGrades = null;
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

    public List<String> getArrayOfCategories() {
        return arrayOfCategories;
    }

    public void setArrayOfCategories(List<String> arrayOfCategories) {
        this.arrayOfCategories = arrayOfCategories;
    }

    public List<Integer> getArrayOfPercentages() {
        return arrayOfPercentages;
    }

    public void setArrayOfPercentages(List<Integer> arrayOfPercentages) {
        this.arrayOfPercentages = arrayOfPercentages;
    }

    public void setPercentage(int pos, int percentage){
        this.arrayOfPercentages.set(pos, percentage);
    }

    /**
     * @return array of grades of specified gradeType and category
     */
    public List<String> getGrades(int gradeType, String category) {
        return allGrades.get(gradeType).get(arrayOfCategories.indexOf(category));
    }

    public List<String> getGrades(String category) {
        return allGrades.get(gradeType).get(arrayOfCategories.indexOf(category));
    }

    /**
     * @return List of lists of grades in categories, in currently used grade type
     */
    public List<List<String>> getGrades(){
        return allGrades.get(gradeType);
    }

    public List<List<List<String>>> getAllGrades(){
        return allGrades;
    }

    public void setGrades(int gradeType, String category, List<String> allGrades) {
        int index = arrayOfCategories.indexOf(category);
        try {
            this.allGrades.get(gradeType).set(index, allGrades);
        } catch (IndexOutOfBoundsException e){
            this.allGrades.get(gradeType).add(allGrades);
        }
    }

    public List<Integer> getPercentageConversion() {
        return percentageConversion;
    }

    @Override
    public String toString(){
        String ret = "";

        ret += "Subject " + subject + ":\n";

        ret += "    Grade Type " + gradeType + "\n";

        ret += "    All Grades " + allGrades.toString() + "\n";

        ret += "    Average " + average + "\n";

        ret += "    Use Categories " + useCategories + "\n";
        ret += "    Categories " + arrayOfCategories.toString() + "\n";

        ret += "    Use Percentages " + usePercentages + "\n";
        ret += "    Percentages " + arrayOfPercentages.toString() + "\n";

        ret += "    Tests To Write " + testsToWrite;

        return ret;
    }
}
