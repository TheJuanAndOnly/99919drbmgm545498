package com.thejuanandonly.gradeday;

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

    public static List<Grade> getGrades(final List<String> arrayOfCategories, final List<List<String>> allGrades) {

        final List<Grade> subject = new ArrayList<>();

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
}
