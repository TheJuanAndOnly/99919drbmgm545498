package com.thejuanandonly.gradeday;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Robo on 24.06.2016.
 */
public class SubjectData {

    private String subject;
    private String average;

    private int gradeType;
    private int testsToWrite;
    private int gradeToGet = 2;

    private boolean useCategories;
    private boolean usePercentages;

    private List<String> arrayOfCategories;
    private List<Integer> arrayOfPercentages;

    private List<List<List<String>>> allGrades;

    private List<Integer> percentageConversion;

    private static final String TAG = "GradeDay SubjectData";

    SubjectData(Context context, SharedPreferences prefs){

        average = prefs.getString("AvgGrade", "");

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
                String grades = prefs.getString(category + "Grades" + i, "");

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

            this.percentageConversion = new ArrayList<>(5);

            for (int i = 0; i < percentageConversion.length(); i++){
                this.percentageConversion.add(percentageConversion.getInt(i));
            }
            this.percentageConversion.add(0);

        } catch (Exception e) {
            this.percentageConversion = new ArrayList<>(Arrays.asList(90, 75, 50, 30, 0));
        }

        Log.d(TAG, this.toString());
    }

    void save(SharedPreferences prefs){
        SharedPreferences.Editor editor = prefs.edit();

        if (allGrades == null) return;

        editor.putString("AvgGrade", average);

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

        for (int i = 0; i < allGrades.size(); i++){
            for (int j = 0; j < allGrades.get(i).size(); j++) {
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

    @SafeVarargs
    SubjectData(int gradeType, int testsToWrite, boolean useCategories, boolean usePercentages, List<String>... allGrades) {
        this.gradeType = gradeType;
        this.testsToWrite = testsToWrite;
        this.useCategories = useCategories;
        this.usePercentages = usePercentages;

        this.allGrades = new ArrayList<>(allGrades.length);
        for (int i = 0; i < SubjectData.GRADE_TYPE_COUNT; i++){
            this.allGrades.add(new ArrayList<List<String>>());
            if (i == gradeType){
                Collections.addAll(this.allGrades.get(gradeType), allGrades);
            }
        }
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    String getAverage() {
        return average;
    }

    void setAverage(String average) {
        this.average = average;
    }

    int getGradeType() {
        return gradeType;
    }

    void setGradeType(int gradeType) {
        this.gradeType = gradeType;
    }

    int getTestsToWrite() {
        return testsToWrite;
    }

    void setTestsToWrite(int testsToWrite) {
        this.testsToWrite = testsToWrite;
    }

    boolean isUseCategories() {
        return useCategories;
    }

    void setUseCategories(boolean useCategories) {
        this.useCategories = useCategories;
    }

    boolean isUsePercentages() {
        return usePercentages;
    }

    void setUsePercentages(boolean usePercentages) {
        this.usePercentages = usePercentages;
    }

    List<String> getArrayOfCategories() {
        return arrayOfCategories;
    }

    void setArrayOfCategories(List<String> arrayOfCategories) {
        this.arrayOfCategories = arrayOfCategories;
    }

    List<Integer> getArrayOfPercentages() {
        return arrayOfPercentages;
    }

    void setArrayOfPercentages(List<Integer> arrayOfPercentages) {
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

    List<String> getGrades(String category) {
        return allGrades.get(gradeType).get(arrayOfCategories.indexOf(category));
    }

    /**
     * @return List of lists of grades in categories, in currently used grade type
     */
    List<List<String>> getGrades(){
        return allGrades.get(gradeType);
    }

    List<List<List<String>>> getAllGrades(){
        return allGrades;
    }

    void setAllGrades(List<List<List<String>>> allGrades){
        this.allGrades = new ArrayList<>(allGrades);
    }

    void setGrades(int gradeType, String category, List<String> allGrades) {
        int index = arrayOfCategories.indexOf(category);
        try {
            this.allGrades.get(gradeType).set(index, allGrades);
        } catch (IndexOutOfBoundsException e){
            this.allGrades.get(gradeType).add(allGrades);
        }
    }

    void setGrades(List<List<String>> grades){
        this.allGrades.set(gradeType, grades);
    }

    List<Integer> getPercentageConversion() {
        return percentageConversion;
    }

    public int getGradeToGet() {
        return gradeToGet;
    }

    public void setGradeToGet(int gradeToGet) {
        this.gradeToGet = gradeToGet;
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

        ret += "    Tests To Write " + testsToWrite + "\n";
        ret += "    Percentage Conversion " + percentageConversion.toString();

        return ret;
    }

    /*
     * Static fields and methods:
     */

    public static final int GRADE_TYPE_COUNT = 4;
    public static final int NUMERIC = 0;
    public static final int PERCENTAGE = 1;
    public static final int ALPHABETIC = 2;
    public static final int TEN_GRADE = 3;

    public static double letterToNumber(String letter){
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

    public static String numberToLetter(double number){
        if (number == 4.33) {
            return "A+";
        } else if (number == 4) {
            return "A";
        } else if (number == 3.67) {
            return "A-";
        } else if (number == 3.33) {
            return "B+";
        } else if (number == 3) {
            return "B";
        } else if (number == 2.67) {
            return "B-";
        } else if (number == 2.33) {
            return "C+";
        } else if (number == 2) {
            return "C";
        } else if (number == 1.67) {
            return "C-";
        } else if (number == 1.33) {
            return "D+";
        } else if (number == 1) {
            return "D";
        } else if (number == 0.67) {
            return "D-";
        } else if (number == 0) {
            return "F";
        } else {
            return "";
        }
    }
}
