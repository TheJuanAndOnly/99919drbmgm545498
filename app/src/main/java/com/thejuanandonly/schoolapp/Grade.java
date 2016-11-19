package com.thejuanandonly.schoolapp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robo on 10/29/2015.
 */
public class Grade {
    public String category;
    public List<String> grades;

    public Grade(String category,  List<String> grades) {
        this.category = category;
        this.grades = grades;
    }

<<<<<<< HEAD
    public static List<Grade> getGrades(final List<String> arrayOfCategories, final List<List<String>> allGrades) {

        final List<Grade> subject = new ArrayList<>();
=======
    /*public static ArrayList<Grade> getGrades(JSONArray arrayOfCategories, ArrayList<JSONArray> allGrades) {
        ArrayList<Grade> subject = new ArrayList<Grade>();
>>>>>>> origin/master

        /*new Thread(new Runnable() {
            @Override
            public void run() {*/
                for (String category : arrayOfCategories){

                    List<String> arrayOfGrades = allGrades.get(arrayOfCategories.indexOf(category));

                    subject.add(new Grade(category, arrayOfGrades));
                }
        /*    }
        }).start();*/

        return subject;
    }
<<<<<<< HEAD
=======

    public static Context getContext(){
        return SubjectDetailActivity.initialContext;
    }*/

>>>>>>> origin/master
}
