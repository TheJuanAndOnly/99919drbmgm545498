package com.thejuanandonly.schoolapp;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Created by Robo on 10/22/2015.
 */
public class SubjectDetailActivity extends AppCompatActivity {

    android.support.v7.widget.Toolbar toolbar;
    private Menu menu;
    public static int menuButtonChange = 1;

    private SubjectData subjectData;
    Thread predictionThread = new Thread();

    private static final String TAG = "GradeDay SubjectDetail";

    //WaveProgress
    WaveView waveProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_detail_layout);

        // Obtain the shared Tracker instance.

        subjectData = new SubjectData(this, getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE));
        subjectData.setSubject(getIntent().getExtras().getString("subject", ""));

        Log.d(TAG, subjectData.toString());

        //WaveProgress initialization
        waveProgress = (WaveView) findViewById(R.id.waveProgress);
        waveProgress.setBackgroundColor(getResources().getColor(R.color.subjectDetailItem));
        waveProgress.setMaxProgress(100);
        waveProgress.setProgress((int)(100 * getGulickaPercentage()));


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(android.R.color.black));
        }

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.subjectDetailToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView subjectName = (TextView) findViewById(R.id.subjectTv);
        subjectName.setText(subjectData.getSubject());

        setAvgTv();

        Toast.makeText(this, String.valueOf(getGulickaPercentage()), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        setListView();
    }

    @Override
    protected void onPause() {

        subjectData.save(getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE));

        menuButtonChange = 1;

        Button edit = (Button) findViewById(R.id.gradeEditButtonSingle);
        edit.setVisibility(View.GONE);

        super.onPause();
    }

    public void setAvgTv(){

        subjectData.setAverage(String.valueOf(countAverage()));
        waveProgress.setProgress((int)(100 * getGulickaPercentage()));
        Toast.makeText(this, "progress " + (int)(100 * getGulickaPercentage()), Toast.LENGTH_SHORT).show();

        TextView averageTV = (TextView) findViewById(R.id.averageTextView);
        String avg = subjectData.getAverage();

        if (avg.length() > 6) {
            avg = avg.substring(0, 6);
        }
        if (subjectData.getGradeType() == 2){
            averageTV.setText("GPA\n" + avg);
        }else {
            averageTV.setText("Average\n" + avg);
        }

        TextView textView = (TextView) findViewById(R.id.testsToWriteEditText);

        textView.setText(String.valueOf(subjectData.getTestsToWrite()));

        LinearLayout ttwLayout = (LinearLayout) findViewById(R.id.ttwLayout);
        if (!subjectData.isUseCategories()){
            ttwLayout.setVisibility(View.VISIBLE);
        }else {
            ttwLayout.setVisibility(View.GONE);
            subjectData.setTestsToWrite(1);
        }
    }

    public float getGulickaPercentage(){
        switch (subjectData.getGradeType()){
            case SubjectData.PERCENTAGE:
                return Float.parseFloat(subjectData.getAverage()) / ((float) 100);

            case SubjectData.ALPHABETIC:
                return Float.parseFloat(subjectData.getAverage()) / ((float) 4.33);

            case SubjectData.TEN_GRADE:
                return ((float) 10 - Float.parseFloat(subjectData.getAverage())) / ((float) 9);

            default:
                return ((float) 5 - Float.parseFloat(subjectData.getAverage())) / ((float) 4);
        }
    }

//////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject_detail, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_edit) {
            Button bottomPlus = (Button) findViewById(R.id.addCategory);

            menuButtonChange++;
            if (menuButtonChange%2 == 0){

                menu.getItem(0).setIcon(R.drawable.ic_done_white_24dp);

                if (!subjectData.isUseCategories()){
                    Button edit = (Button) findViewById(R.id.gradeEditButtonSingle);
                    edit.setVisibility(View.VISIBLE);

                }else {
                    bottomPlus.setVisibility(View.VISIBLE);
                }

                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.grades_big_layout);
                //Button rollDownButton = (Button) findViewById(R.id.rollDownButton);

                int visibility = relativeLayout.getVisibility();
                if (visibility == 8) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    //rollDownButton.setBackgroundResource(R.drawable.ic_arrow_drop_up_white_24dp);
                }

            }else {
                menu.getItem(0).setIcon(R.drawable.ic_mode_edit_white_24dp);

                bottomPlus.setVisibility(View.GONE);

                if (!subjectData.isUseCategories()){
                    Button edit = (Button) findViewById(R.id.gradeEditButtonSingle);
                    edit.setVisibility(View.GONE);
                }

            }

            setListView();
            return true;
        } else if (id == R.id.action_settings) {
            settingsDialog();

        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
//////////////////////////////////////////////////////////////////////////
    public void setListView() {
        setAvgTv();

        ListView listView = (ListView) findViewById(R.id.categoryListView);
        TextView gradeTv = (TextView) findViewById(R.id.gradeTextViewNoCat);

        boolean isError = false;

        if (!subjectData.isUseCategories()){

            listView.setVisibility(View.GONE);
            gradeTv.setVisibility(View.VISIBLE);
            gradeTv.setText("");

            Log.d(TAG, subjectData.getAllGrades().toString() + subjectData.getGradeType());

            for (String category : subjectData.getArrayOfCategories()) {
                for (String grade : subjectData.getGrades(category)){

                    if (gradeTv.length() == 0){
                        gradeTv.append(grade);
                    }else {
                        gradeTv.append(", " + grade);
                    }
                }
            }
        }
        else {

            gradeTv.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            List<Grade> arrayListOfGrades = Grade.getGrades(subjectData.getArrayOfCategories(), subjectData.getGrades());

            CustomGradeAdapter adapter = new CustomGradeAdapter(this, arrayListOfGrades);

            listView.setAdapter(adapter);

            try {

                int count = 0;
                boolean isNonZeroCategory = false,
                        isIgnoredCategory = false,
                        isEmptyCategory = false;

                for (int percentage : subjectData.getArrayOfPercentages()) {
                    count += percentage;
                }

                List<String> arrayOfCategories = subjectData.getArrayOfCategories();
                for (int i = 0; i < arrayOfCategories.size(); i++) {
                    String category = arrayOfCategories.get(i);

                    List<String> arrayOfGrades = subjectData.getGrades(category);

                    int percentage = subjectData.getArrayOfPercentages().get(i);

                    if (arrayOfGrades.size() == 0){
                        isEmptyCategory = true;
                    }
                    if (arrayOfGrades.size() == 0 && percentage != 0) {
                        isNonZeroCategory = true;
                    }
                    if (arrayOfGrades.size() != 0 && percentage == 0) {
                        isIgnoredCategory = true;
                    }
                }

                if (!subjectData.isUsePercentages() && isEmptyCategory) {
                    isError = true;

                    Toast.makeText(this, R.string.empty_cat, Toast.LENGTH_LONG).show();

                } else if (subjectData.isUsePercentages() && count != 100 && count != 0) {
                    isError = true;

                    final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.weight_must_be_100, Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    snackbar.setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            settingsDialog();
                            snackbar.dismiss();

                        }
                    });

                } else if (subjectData.isUsePercentages() && isNonZeroCategory) {
                    isError = true;

                    final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.non_zero_cat, Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    snackbar.setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            settingsDialog();

                            snackbar.dismiss();
                        }
                    });

                } else if (isIgnoredCategory) {
                    isError = true;

                    final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.ignored_cat, Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    snackbar.setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            settingsDialog();

                            snackbar.dismiss();
                        }
                    });
                }


            } catch (Exception e) {
                Log.e("debug", e.toString());
            }
        }

        if (!isError) {
            setPredictionListView();
        }
    }
//////////////////////////////////////////////////////////////////////////
    public void addGrade(View view) {

        final ArrayList<String> arrayOfCategories = new ArrayList<>(subjectData.getArrayOfCategories());

        if(!arrayOfCategories.isEmpty() || !subjectData.isUseCategories()) {

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.grade_adder_dialog);
            dialog.setTitle("Add a Grade");

            final Spinner spinner = (Spinner) dialog.findViewById(R.id.dialogSpinner);

            if (subjectData.isUseCategories()) {

                ArrayAdapter spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayOfCategories);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
            }
            else {
                spinner.setVisibility(View.GONE);
            }

            final EditText editText = (EditText) dialog.findViewById(R.id.addGradeEditText);
            int gradeType = subjectData.getGradeType();
            switch (gradeType) {
                case 0:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                    editText.setHint("Add Grades (1 - 5)");
                    break;
                case 1:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                    editText.setHint("Percentage Format: 94, 82, ...");
                    break;
                case 2:
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    editText.setHint("Add Grades (A - F)");
                    break;
                case 3:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                    editText.setHint("Add Grades (1 - 10)");
                    break;
            }

            Button dialogOkButton = (Button) dialog.findViewById(R.id.dialogOkButton);
            dialogOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String grade = editText.getText().toString();

                    String category;
                    int pos;
                    try {
                        category = spinner.getSelectedItem().toString();
                        pos = spinner.getSelectedItemPosition();

                    } catch (NullPointerException e) {
                        category = "Grades";
                        if (arrayOfCategories.contains(category)){
                            pos = arrayOfCategories.indexOf(category);
                        }else {
                            pos = arrayOfCategories.size();
                        }

                    }

                    saveGrade(grade, category, pos);
                    setListView();
                    dialog.dismiss();
                }
            });
            Button dialogCancelButton = (Button) dialog.findViewById(R.id.dialogCancelButton);
            dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
        else{
            Toast.makeText(this, "Add a Category first", Toast.LENGTH_SHORT).show();
            Button bottomPlus = (Button) findViewById(R.id.addCategory);
            bottomPlus.setVisibility(View.VISIBLE);

            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.grades_big_layout);
            //Button rollDownButton = (Button) findViewById(R.id.rollDownButton);

            int visibility = relativeLayout.getVisibility();
            if (visibility == 8) {
                relativeLayout.setVisibility(View.VISIBLE);
                //rollDownButton.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp);
            }
        }
    }
    public void saveGrade(String grade, String category, int pos){
        List<String> arrayOfCategories = new ArrayList<>(subjectData.getArrayOfCategories());

        if (pos == arrayOfCategories.size()){
            saveCategory(category, "");
        }

        List<String> arrayOfGrades = new ArrayList<>(subjectData.getGrades(category));

        grade = grade + "*";
        char[] chars = grade.toCharArray();
        switch (subjectData.getGradeType()){
            case 1:
                int percentage = 0;
                for (int i = 0; i < chars.length; i++){
                    if (chars[i] == '1' || chars[i] == '2' || chars[i] == '3' || chars[i] == '4' || chars[i] == '5' ||
                        chars[i] == '6' || chars[i] == '7' || chars[i] == '8' || chars[i] == '9' || chars[i] == '0'){

                        int num = Character.getNumericValue(chars[i]);
                        if (percentage == -1) percentage = 0;
                        percentage = (percentage * 10) + num;

                    }else if (percentage != -1 || i+1 == chars.length){
                        arrayOfGrades.add(String.valueOf(percentage));
                        percentage = -1;
                    }
                }
                subjectData.setGrades(subjectData.getGradeType(), category, arrayOfGrades);
                break;
            case 2:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == 'A' || chars[i] == 'B' || chars[i] == 'C' || chars[i] == 'D' || chars[i] == 'F') {

                        String string = String.valueOf(chars[i]);

                        try {
                            int j = i + 1;
                            if (chars[j] == '+' || chars[j] == '-'){
                                string += String.valueOf(chars[j]);
                            }
                        }catch (IndexOutOfBoundsException e){}

                        arrayOfGrades.add(string);
                    }
                }
                subjectData.setGrades(subjectData.getGradeType(), category, arrayOfGrades);
                break;
            case 3:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == '1' || chars[i] == '2' || chars[i] == '3' || chars[i] == '4' || chars[i] == '5' ||
                        chars[i] == '6' || chars[i] == '7' || chars[i] == '8' || chars[i] == '9') {
                        String string = String.valueOf(chars[i]);

                        try {
                            if (chars[i] == '1' && chars[i + 1] == '0') {
                                string += chars[i + 1];
                                i++;
                            }
                        }catch (ArrayIndexOutOfBoundsException ignored){}

                        arrayOfGrades.add(string);
                    }
                }
                subjectData.setGrades(subjectData.getGradeType(), category, arrayOfGrades);
                break;
            default:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == '1' || chars[i] == '2' || chars[i] == '3' || chars[i] == '4' || chars[i] == '5') {
                        String string = String.valueOf(chars[i]);
                        arrayOfGrades.add(string);
                    }
                }

                Log.d(TAG, arrayOfGrades.toString());

                subjectData.setGrades(subjectData.getGradeType(), category, arrayOfGrades);

                Log.d(TAG, subjectData.toString());

                break;
        }
    }
//////////////////////////////////////////////////////////////////////////

    public double countAverage(){
        List<List<String>> gradesInCategories = new ArrayList<>(subjectData.getGrades());

        List<Double> averages = new ArrayList<>(gradesInCategories.size());

        for (int i = 0; i < gradesInCategories.size(); i++) {
            List<String> grades = gradesInCategories.get(i);

            if (grades.isEmpty()) averages.add(0.0);

            for (String grade : grades) {
                if (subjectData.getGradeType() == SubjectData.ALPHABETIC) {

                    try {
                        averages.set(i, averages.get(i) + SubjectData.letterToNumber(grade));
                    }catch (IndexOutOfBoundsException e){
                        averages.add(SubjectData.letterToNumber(grade));
                    }
                }
                else {
                    try {
                        averages.set(i, averages.get(i) + Double.parseDouble(grade));
                    } catch (IndexOutOfBoundsException e) {
                        try {
                            averages.add(Double.parseDouble(grade));
                        } catch (NumberFormatException ignored) {
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            averages.set(i, averages.get(i) / (double) grades.size());


            if (subjectData.isUsePercentages()) {
                double temp = averages.get(i);
                temp = temp * (double) subjectData.getArrayOfPercentages().get(i) / 100.0;
                averages.set(i, temp);
            }
        }

        double temp = 0;
        for (double grade : averages){
            temp += grade;
        }
        if (!subjectData.isUsePercentages()) {
            temp /= (double) averages.size();
        }

        return temp;
    }
//////////////////////////////////////////////////////////////////////////
    public void addCategory(View view) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.category_adder_dialog);
        dialog.setTitle("Add a Category");

        final EditText nameEditText = (EditText) dialog.findViewById(R.id.categoryNameEditText);
        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        final EditText gradeEditText = (EditText) dialog.findViewById(R.id.categoryGradesEditText);
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        int gradeType = prefs.getInt("GradeType", 0);
        switch (gradeType) {
            case 0:
                gradeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                gradeEditText.setHint("Add Grades (1 - 5)");
                break;
            case 1:
                gradeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                gradeEditText.setHint("Percentage Format: 94, 82, ...");
                break;
            case 2:
                gradeEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                gradeEditText.setHint("Add Grades (A - F)");
                break;
            case 3:
                gradeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                gradeEditText.setHint("Add Grades (1 - 10)");
                break;
        }

        Button dialogOkButton = (Button) dialog.findViewById(R.id.categoryDialogOkButton);
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCategory = nameEditText.getText().toString();
                if (newCategory.isEmpty()){
                    Toast.makeText(getApplicationContext(), R.string.name_the_category_first, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (subjectData.getArrayOfCategories().contains(newCategory)){
                    Toast.makeText(getApplicationContext(), R.string.this_category_exists, Toast.LENGTH_SHORT).show();
                    return;
                }

                String grades = gradeEditText.getText().toString();

                List<String> arrayOfCategories = new ArrayList<>(subjectData.getArrayOfCategories());
                List<Integer> arrayOfPercentages = new ArrayList<>(subjectData.getArrayOfPercentages());
                arrayOfCategories.add(newCategory);
                arrayOfPercentages.add(100 / (arrayOfPercentages.size() + 1));
                subjectData.setArrayOfCategories(arrayOfCategories);
                subjectData.setArrayOfPercentages(arrayOfPercentages);

                saveCategory(newCategory, grades);

                dialog.dismiss();
            }
        });
        Button dialogCancelButton = (Button) dialog.findViewById(R.id.categoryDialogCancelButton);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void saveCategory(String name, String gradesString){

        char[] chars = gradesString.toCharArray();
        List<String> arrayOfGrades = new ArrayList<>();

        int gradeType = subjectData.getGradeType();
        switch (gradeType){
            case SubjectData.PERCENTAGE:
                int percentage = 0;
                for (char aChar : chars) {
                    if (aChar == '1' || aChar == '2' || aChar == '3' || aChar == '4' || aChar == '5' ||
                            aChar == '6' || aChar == '7' || aChar == '8' || aChar == '9' || aChar == '0') {

                        int num = Character.getNumericValue(aChar);
                        if (percentage == -1) percentage = 0;
                        percentage = (percentage * 10) + num;

                    } else if (percentage != -1) {
                        arrayOfGrades.add(String.valueOf(percentage));
                        percentage = -1;
                    }
                }
                if (percentage != -1 && chars.length != 0) arrayOfGrades.add(String.valueOf(percentage));
                break;

            case SubjectData.ALPHABETIC:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == 'A' || chars[i] == 'B' || chars[i] == 'C' || chars[i] == 'D' || chars[i] == 'F') {
                        String string = String.valueOf(chars[i]);

                        try {
                            int j = i + 1;

                            if (chars[j] == '+' || chars[j] == '-') {
                                string += String.valueOf(chars[j]);
                            }
                        }catch (ArrayIndexOutOfBoundsException ignored){}

                        arrayOfGrades.add(string);
                    }
                }
                break;

            case SubjectData.TEN_GRADE:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == '1' || chars[i] == '2' || chars[i] == '3' || chars[i] == '4' || chars[i] == '5' ||
                            chars[i] == '6' || chars[i] == '7' || chars[i] == '8' || chars[i] == '9' || chars[i] == '0') {
                        String string = String.valueOf(chars[i]);

                        if (chars[i] == '1' && chars[i+1] == '0'){
                            string += chars[i+1];
                            i++;
                        }

                        arrayOfGrades.add(string);
                    }
                }
                break;

            default:
                for (char aChar : chars) {
                    if (aChar == '1' || aChar == '2' || aChar == '3' || aChar == '4' || aChar == '5') {
                        arrayOfGrades.add(String.valueOf(aChar));
                    }
                }
                break;
        }

        subjectData.setGrades(gradeType, name, arrayOfGrades);

        setListView();
    }

    public void saveCategory(String gradesString){

        char[] chars = gradesString.toCharArray();
        List<String> arrayOfGrades = new ArrayList<>();

        int gradeType = subjectData.getGradeType();
        switch (gradeType){
            case SubjectData.PERCENTAGE:
                if (chars.length == 0) break;

                int percentage = 0;
                for (char aChar1 : chars) {
                    if (aChar1 == '1' || aChar1 == '2' || aChar1 == '3' || aChar1 == '4' || aChar1 == '5' ||
                            aChar1 == '6' || aChar1 == '7' || aChar1 == '8' || aChar1 == '9' || aChar1 == '0') {

                        int num = Character.getNumericValue(aChar1);
                        if (percentage == -1) percentage = 0;
                        percentage = (percentage * 10) + num;

                    } else if (percentage != -1) {
                        arrayOfGrades.add(String.valueOf(percentage));
                        percentage = -1;
                    }
                }
                if (percentage != -1) arrayOfGrades.add(String.valueOf(percentage));
                break;
            case SubjectData.ALPHABETIC:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == 'A' || chars[i] == 'B' || chars[i] == 'C' || chars[i] == 'D' || chars[i] == 'F') {
                        String string = String.valueOf(chars[i]);

                        try {
                            int j = i + 1;

                            if (chars[j] == '+' || chars[j] == '-') {
                                string += String.valueOf(chars[j]);
                            }
                        }catch (ArrayIndexOutOfBoundsException ignored){}

                        arrayOfGrades.add(string);
                    }
                }
                break;
            case SubjectData.TEN_GRADE:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == '1' || chars[i] == '2' || chars[i] == '3' || chars[i] == '4' || chars[i] == '5' ||
                            chars[i] == '6' || chars[i] == '7' || chars[i] == '8' || chars[i] == '9' || chars[i] == '0') {
                        String string = String.valueOf(chars[i]);

                        try {
                            if (chars[i] == '1' && chars[i + 1] == '0') {
                                string += chars[i + 1];
                                i++;
                            }
                        }catch (ArrayIndexOutOfBoundsException ignored){}

                        arrayOfGrades.add(string);
                    }
                }
                break;
            default:
                for (char aChar : chars) {
                    if (aChar == '1' || aChar == '2' || aChar == '3' || aChar == '4' || aChar == '5') {
                        String string = String.valueOf(aChar);
                        arrayOfGrades.add(string);
                    }
                }
                break;
        }

        subjectData.setGrades(subjectData.getGradeType(), "Grades", arrayOfGrades);

        setListView();
    }
//////////////////////////////////////////////////////////////////////////
    public void deleteCategory(View view) {
        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);

        List<String> arrayOfCategories = new ArrayList<>(subjectData.getArrayOfCategories());
        List<Integer> arrayOfPercentages = new ArrayList<>(subjectData.getArrayOfPercentages());
        List<List<String>> grades = subjectData.getGrades();

        arrayOfCategories.remove(position);
        arrayOfPercentages.remove(position);
        grades.remove(position);

        subjectData.setArrayOfCategories(arrayOfCategories);
        subjectData.setArrayOfPercentages(arrayOfPercentages);
        subjectData.setGrades(grades);

        setListView();
    }
//////////////////////////////////////////////////////////////////////////
    public void editCategory(final View view) {
        final int gradeType = subjectData.getGradeType();

        if (view == findViewById(R.id.gradeEditButtonSingle)){

            String grades = "";
            List<String> arrayOfCategories = new ArrayList<>(subjectData.getArrayOfCategories());

            for (int i = 0; i < arrayOfCategories.size(); i++){

                List<String> arrayOfGrades = new ArrayList<>(subjectData.getGrades(arrayOfCategories.get(i)));

                for (int j = 0; j < arrayOfGrades.size(); j++) {
                    grades = grades + arrayOfGrades.get(j) + ", ";
                }
            }

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.category_adder_dialog);
            dialog.setTitle("Edit your grades");

            final EditText nameEditText = (EditText) dialog.findViewById(R.id.categoryNameEditText);
            nameEditText.setVisibility(View.GONE);

            final EditText gradeEditText = (EditText) dialog.findViewById(R.id.categoryGradesEditText);
            switch (gradeType) {
                case 0:
                    gradeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                    gradeEditText.setHint("Add Grades (1 - 5)");
                    break;
                case 1:
                    gradeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                    gradeEditText.setHint("Add Percentage (0 - 100)");
                    break;
                case 2:
                    gradeEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    gradeEditText.setHint("Add Grades (A - F)");
                    break;
                case 3:
                    gradeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                    gradeEditText.setHint("Add Grades (1 - 5)");
                    break;
            }
            gradeEditText.setSingleLine(false);
            gradeEditText.setText(grades);

            Button dialogOkButton = (Button) dialog.findViewById(R.id.categoryDialogOkButton);
            dialogOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String grades = gradeEditText.getText().toString();

                    saveCategory(grades);

                    dialog.dismiss();
                }
            });
            Button dialogCancelButton = (Button) dialog.findViewById(R.id.categoryDialogCancelButton);
            dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        else {

            final View parentRow = (View) view.getParent();
            ListView listView = (ListView) parentRow.getParent();
            final int position = listView.getPositionForView(parentRow);

            String grades = "";
            List<String> arrayOfCategories = new ArrayList<>(subjectData.getArrayOfCategories());
            String category = arrayOfCategories.get(position);

            List<String> arrayOfGrades = new ArrayList<>(subjectData.getGrades(category));

            for (int i = 0; i < arrayOfGrades.size(); i++) {
                grades = grades + arrayOfGrades.get(i) + ", ";
            }

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.category_adder_dialog);
            dialog.setTitle("Edit " + category + " Category");

            final EditText nameEditText = (EditText) dialog.findViewById(R.id.categoryNameEditText);
            nameEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            nameEditText.setText(category);

            final EditText gradeEditText = (EditText) dialog.findViewById(R.id.categoryGradesEditText);
            switch (gradeType) {
                case 0:
                    gradeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                    gradeEditText.setHint("Add Grades (1 - 5)");
                    break;
                case 1:
                    gradeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                    gradeEditText.setHint("Percentage Format: 94, 82, ...");
                    break;
                case 2:
                    gradeEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    gradeEditText.setHint("Add Grades (A - F)");
                    break;
                case 3:
                    gradeEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                    gradeEditText.setHint("Add Grades (1 - 5)");
                    break;
            }
            gradeEditText.setSingleLine(false);
            gradeEditText.setText(grades);

            Button dialogOkButton = (Button) dialog.findViewById(R.id.categoryDialogOkButton);
            dialogOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newCategory = nameEditText.getText().toString();
                    if (newCategory.isEmpty()){
                        Toast.makeText(getApplicationContext(), R.string.name_the_category_first, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (subjectData.getArrayOfCategories().contains(newCategory) &&
                            subjectData.getArrayOfCategories().indexOf(newCategory) != position){
                        Toast.makeText(getApplicationContext(), R.string.this_category_exists, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String grades = gradeEditText.getText().toString();

                    List<String> arrayOfCategories = new ArrayList<>(subjectData.getArrayOfCategories());
                    arrayOfCategories.set(position, newCategory);
                    subjectData.setArrayOfCategories(arrayOfCategories);

                    saveCategory(newCategory, grades);

                    dialog.dismiss();
                }
            });
            Button dialogCancelButton = (Button) dialog.findViewById(R.id.categoryDialogCancelButton);
            dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

//////////////////////////////////////////////////////////////////////////
    public void settingsDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.detail_settings_dialog);
        dialog.setTitle("Settings");

        final Spinner spinner = (Spinner) dialog.findViewById(R.id.detailSettingsSpinner);
        String[] strings = {"Numeric (1 - 5)", "Percentage", "Alphabetic (A - F)", "10 Grade"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        int pos = subjectData.getGradeType();
        spinner.setSelection(pos);

        List<String> arrayOfCategories = new ArrayList<>(subjectData.getArrayOfCategories());

        final ListView listView = (ListView) dialog.findViewById(R.id.SettingsDialogListView);
        List<Grade> arrayOfGrades = Grade.getGrades(arrayOfCategories, subjectData.getGrades());

        CustomDialogLvAdapter arrayAdapter = new CustomDialogLvAdapter(getApplicationContext(), arrayOfGrades, subjectData);
        listView.setAdapter(arrayAdapter);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = arrayOfCategories.size() * (125);
        listView.setLayoutParams(params);
        listView.requestLayout();

        final LinearLayout lvLayout = (LinearLayout) dialog.findViewById(R.id.settingsLvLayout);

        final Switch switch1 = (Switch) dialog.findViewById(R.id.detailSettingsCheckbox);
        final Switch switch2 = (Switch) dialog.findViewById(R.id.detailSettingsCheckbox2);

        if (subjectData.isUseCategories()){
            switch1.setChecked(true);
        }else {
            switch1.setChecked(false);
        }
        if (subjectData.isUsePercentages()){
            switch2.setChecked(true);
            lvLayout.setVisibility(View.VISIBLE);
        }else {
            switch2.setChecked(false);
            lvLayout.setVisibility(View.GONE);
        }

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!switch1.isChecked()){
                    if (switch2.isChecked()){
                        switch2.setChecked(false);
                        lvLayout.setVisibility(View.GONE);
                    }
                }
            }
        });
        switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subjectData.getArrayOfCategories().size() != 0) {

                    if (switch2.isChecked()) {
                        lvLayout.setVisibility(View.VISIBLE);

                        if (!switch1.isChecked()) {
                            switch1.setChecked(true);
                        }

                    } else {
                        lvLayout.setVisibility(View.GONE);
                    }

                    balancePercentages(dialog);
                }
            }
        });

        Button dialogOkButton = (Button) dialog.findViewById(R.id.DialogOkButton);
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int spinnerPos = spinner.getSelectedItemPosition();

                subjectData.setGradeType(spinnerPos);

                subjectData.setUseCategories(switch1.isChecked());
                subjectData.setUsePercentages(switch2.isChecked());

                if (!subjectData.isUseCategories()){
                    List<List<String>> grades = new ArrayList<>(subjectData.getGrades());
                    List<String> newGrades = new ArrayList<>(1);

                    for (List<String> list : grades){
                        newGrades.addAll(list);
                    }

                    grades = new ArrayList<>(Collections.singletonList(newGrades));

                    subjectData.setGrades(grades);

                    subjectData.setArrayOfCategories(Collections.singletonList("Grades"));
                    subjectData.setArrayOfPercentages(Collections.singletonList(100));

                    mergeAllCats();
                }

                if (subjectData.isUsePercentages()) {
                    List<Integer> arrayOfPercentages = new ArrayList<>(subjectData.getArrayOfPercentages());

                    for (int i = 0; i < listView.getCount(); i++) {
                        View view = getViewByPos(i, listView);
                        EditText editText = (EditText) view.findViewById(R.id.settingsLVEditText);
                        arrayOfPercentages.set(i, Integer.parseInt(editText.getText().toString()));
                    }

                    subjectData.setArrayOfPercentages(arrayOfPercentages);
                }

                setListView();
                dialog.dismiss();

            }
        });
        Button dialogCancelButton = (Button) dialog.findViewById(R.id.DialogCancelButton);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                setListView();
            }
        });

        Button dialogDeleteButton = (Button) dialog.findViewById(R.id.DialogDeleteButton);
        dialogDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSubject();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void mergeAllCats(){
        List<List<List<String>>> allGrades = new ArrayList<>(subjectData.getAllGrades());

        for (int i = 0; i < allGrades.size(); i++) {
            List<List<String>> gradeType = allGrades.get(i);

            List<String> newGrades = new ArrayList<>(gradeType.get(0).size() * gradeType.size());
            for (List<String> cat : gradeType) {
                newGrades.addAll(cat);
            }
            gradeType = new ArrayList<>(Collections.singletonList(newGrades));
            allGrades.set(i, gradeType);
        }

        subjectData.setAllGrades(allGrades);
    }

    public View getViewByPos(int position, ListView listView){

        final int firstPos = listView.getFirstVisiblePosition();

        final int lastPos = firstPos + listView.getChildCount() - 1;

        if (position < firstPos || position > lastPos){
            return listView.getAdapter().getView(position, listView.getChildAt(position), listView);
        }else {
            final int childIndex = position - firstPos;

            return listView.getChildAt(childIndex);
        }
    }

    public void balancePercentages(Dialog dialog){

        List<Integer> arrayOfPercentages = subjectData.getArrayOfPercentages();

        List<Integer> array = new ArrayList<>(arrayOfPercentages.size());
        int over = 0, abc = 0;
        for (int i = 0; i < arrayOfPercentages.size(); i++){

            int toBePut = Math.round(100 / arrayOfPercentages.size());

            array.add(toBePut);
            abc += toBePut;
        }
        over = 100 - abc;

        if (over != 0){

            int i = 0;
            while (over > 0){
                array.add(i, array.get(i) + 1);

                over--;

                i++;
                if (i == array.size()) {
                    i = 0;
                }
            }
        }

        subjectData.setArrayOfPercentages(array);

        final ListView listView = (ListView) dialog.findViewById(R.id.SettingsDialogListView);
        List<Grade> arrayOfGrades = Grade.getGrades(subjectData.getArrayOfCategories(), subjectData.getGrades());
        CustomDialogLvAdapter arrayAdapter = new CustomDialogLvAdapter(getApplicationContext(), arrayOfGrades, subjectData);
        listView.setAdapter(arrayAdapter);
    }
    public void balancePercentages(){

        List<Integer> arrayOfPercentages = subjectData.getArrayOfPercentages();

        List<Integer> array = new ArrayList<>(arrayOfPercentages.size());
        int over = 0, abc = 0;
        for (int i = 0; i < arrayOfPercentages.size(); i++){

            int toBePut = Math.round(100 / arrayOfPercentages.size());

            array.add(toBePut);
            abc += toBePut;
        }
        over = 100 - abc;

        if (over != 0){

            int i = 0;
            while (over > 0){
                array.add(i, array.get(i) + 1);

                over--;

                i++;
                if (i == array.size()) {
                    i = 0;
                }
            }
        }

        subjectData.setArrayOfPercentages(array);
    }

    public void deleteSubject(){

        subjectData.delete(getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE));

        try {
            SharedPreferences prefs = getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            JSONArray array = new JSONArray(prefs.getString("List", null));

            ArrayList<String> arrayList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    arrayList.add(array.getString(i));
                } catch (JSONException ignored) {}
            }
            arrayList.remove(getIntent().getExtras().getString("subject", null));

            array = new JSONArray(arrayList);
            editor.putString("List", array.toString()).apply();
        } catch (Exception ignored) {}

        finish();
    }
//////////////////////////////////////////////////////////////////////////

    public void testsToWriteBtn(View view) {
        TextView textView = (TextView) findViewById(R.id.testsToWriteEditText);

        if (view == findViewById(R.id.ttwAddBtn) || view == findViewById(R.id.ttwAddLayout)){
            if (Integer.parseInt(textView.getText().toString()) < 5) {
                int ttw = Integer.parseInt(textView.getText().toString()) + 1;
                textView.setText(String.valueOf(ttw));

                subjectData.setTestsToWrite(Integer.parseInt(textView.getText().toString()));

                if (findViewById(R.id.prediction_big_layout).getVisibility() == View.VISIBLE) {

                    setPredictionListView();
                }
            }
        }
        else {
            if (Integer.parseInt(textView.getText().toString()) > 1) {
                int ttw = Integer.parseInt(textView.getText().toString()) - 1;
                textView.setText(String.valueOf(ttw));

                subjectData.setTestsToWrite(Integer.parseInt(textView.getText().toString()));

                if (findViewById(R.id.prediction_big_layout).getVisibility() == View.VISIBLE) {

                    setPredictionListView();
                }
            }
        }
    }

    public void setPredictionListView(){

        final ListView listView = (ListView) findViewById(R.id.predictionListView);

        if (predictionThread != null && predictionThread.isAlive()) {
            predictionThread.interrupt();
        }
        final PredictionListViewImplementor predictionListViewImplementor = new PredictionListViewImplementor(this, subjectData, listView);
        predictionThread = new Thread(predictionListViewImplementor);
        predictionThread.start();

        if (subjectData.getGradeType() == SubjectData.TEN_GRADE){

            findViewById(R.id.grade_to_get_layout).setVisibility(View.GONE);
            findViewById(R.id.grade_to_get_scroll_view).setVisibility(View.VISIBLE);

            LinearLayout gradeToGetLayout = (LinearLayout) findViewById(R.id.grade_to_get_scroll_layout);
            gradeToGetLayout.removeAllViews();

            final List<LinearLayout> viewsFromTheSix = new ArrayList<>(10);

            for (int i = 0; i < (subjectData.getGradeType() == SubjectData.TEN_GRADE ? 10 : 5); i++) {
                final int gradeToGet = i;

                View child = View.inflate(this, R.layout.grade_to_get_item, null);
                ((TextView) child.findViewById(R.id.grade_to_get_text_view)).setText(String.valueOf(i + 1));

                viewsFromTheSix.add((LinearLayout) child);

                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        for (LinearLayout v : viewsFromTheSix){
                            v.getChildAt(0).setBackground(getResources().getDrawable(R.drawable.circle_blue));
                        }

                        ((LinearLayout) view).getChildAt(0).setBackground(getResources().getDrawable(R.drawable.circle_accentblue));

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        if (predictionListViewImplementor.isDoneLoading()) {
                                            predictionListViewImplementor.display(gradeToGet);
                                            break;
                                        }

                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                    }
                });

                gradeToGetLayout.addView(child);
            }
        }
        else {
            final View[] buttons = new View[]{
                    findViewById(R.id.grade_to_get_1),
                    findViewById(R.id.grade_to_get_2),
                    findViewById(R.id.grade_to_get_3),
                    findViewById(R.id.grade_to_get_4),
                    findViewById(R.id.grade_to_get_5),
            };

            findViewById(R.id.grade_to_get_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.grade_to_get_scroll_view).setVisibility(View.GONE);

            for (int i = 0; i < buttons.length; i++) {
                final int gradeToGet = i;

                ((ViewGroup) buttons[gradeToGet]).getChildAt(1).setVisibility(
                        i == 0 ? View.VISIBLE : View.GONE
                );

                final Context context = this;

                buttons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (View button : buttons) {
                                            ((ViewGroup) button).getChildAt(1).setVisibility(View.GONE);
                                        }
                                        ((ViewGroup) buttons[gradeToGet]).getChildAt(1).setVisibility(View.VISIBLE);
                                    }
                                });

                                while (true) {
                                    try {
                                        if (predictionListViewImplementor.isDoneLoading()) {
                                            predictionListViewImplementor.display(gradeToGet);
                                            break;
                                        }

                                        Thread.sleep(500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }).start();
                    }
                });
            }
        }
    }
}