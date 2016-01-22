package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Robo on 10/22/2015.
 */
public class SubjectDetailActivity extends AppCompatActivity {
    public static Context initialContext;
    public static String currentSubject;
    android.support.v7.widget.Toolbar toolbar;
    private Menu menu;
    public static int menuButtonChange = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_detail_layout);
        initialContext = getApplicationContext();
        currentSubject = getIntent().getExtras().getString("subject", null);
        JSONArray arrayOfCategories;
        String subject = getIntent().getExtras().getString("subject", null);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(android.R.color.black));
        }

        String[] quotes = getResources().getStringArray(R.array.quotes);
        Log.d("debugD", quotes[0]);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.subjectDetailToolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView firstLetter = (TextView) findViewById(R.id.firstLetterTv);
        firstLetter.setText(String.valueOf(subject.charAt(0)));

        TextView subjectName = (TextView) findViewById(R.id.subjectTv);
        subjectName.setText(subject);

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
        }catch (Exception e) {

            arrayOfCategories = new JSONArray();
            arrayOfCategories.put("Grades");
            editor.putString("ListOfCategories", arrayOfCategories.toString()).apply();

            JSONArray arrayOfPercentages = new JSONArray();
            arrayOfPercentages.put("100");
            editor.putString("ListOfPercentages", arrayOfPercentages.toString()).apply();
        }

        try {
            ArrayList<String> strings = new ArrayList<String>();
            for (int i = 0; i < arrayOfCategories.length(); i++){
                try {
                    strings.add(arrayOfCategories.getString(i));
                }catch (JSONException e){}
            }
        }catch (Exception e ){
            Toast.makeText(this, "Add a category", Toast.LENGTH_SHORT).show();
        }

        editor.putBoolean("doSetLv", true).apply();

        setAvgTv();
    }

    public void setAvgTv(){
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("AvgGrade", String.valueOf(countAverage())).apply();

        TextView averageTV = (TextView) findViewById(R.id.averageTextView);
        int gradeType = prefs.getInt("GradeType" , 0);
        String average = prefs.getString("AvgGrade", "0");
        if (average.length() > 6) {
            average = average.substring(0, 6);
        }
        if (gradeType == 2){
            averageTV.setText("GPA: " + average);
        }else {
            averageTV.setText("Average: " + average);
        }


        TextView textView = (TextView) findViewById(R.id.testsToWriteEditText);
        textView.setText(String.valueOf(prefs.getInt("testsToWrite", 1)));

        LinearLayout ttwLayout = (LinearLayout) findViewById(R.id.ttwLayout);
        if (prefs.getInt("GradeType", 0) == 0 && !prefs.getBoolean("useCategories", false)){
            ttwLayout.setVisibility(View.VISIBLE);
        }else {
            ttwLayout.setVisibility(View.GONE);
            editor.putInt("testsToWrite", 1).apply();
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

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (id == R.id.action_edit) {
            Button bottomPlus = (Button) findViewById(R.id.addCategory);

            menuButtonChange++;
            if (menuButtonChange%2 == 0){

                menu.getItem(0).setIcon(R.drawable.ic_done_white_24dp);

                if (!prefs.getBoolean("useCategories", false)){
                    Button edit = (Button) findViewById(R.id.gradeEditButtonSingle);
                    edit.setVisibility(View.VISIBLE);

                }else {
                    bottomPlus.setVisibility(View.VISIBLE);
                }

                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondLayout);
                Button rollDownButton = (Button) findViewById(R.id.rollDownButton);

                int visibility = relativeLayout.getVisibility();
                if (visibility == 8) {
                    relativeLayout.setVisibility(View.VISIBLE);
                    rollDownButton.setBackgroundResource(R.drawable.ic_arrow_drop_up_white_24dp);
                }

            }else {
                menu.getItem(0).setIcon(R.drawable.ic_mode_edit_white_24dp);

                bottomPlus.setVisibility(View.GONE);

                if (!prefs.getBoolean("useCategories", false)){
                    Button edit = (Button) findViewById(R.id.gradeEditButtonSingle);
                    edit.setVisibility(View.GONE);
                }

            }

            editor.putBoolean("doSetLv", true).apply();
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

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        setAvgTv();

        if (!prefs.getBoolean("doSetLv", true)) return;

        editor.putBoolean("doSetLv", false).apply();

        editor.putString("AvgGrade", String.valueOf(countAverage())).apply();

        JSONArray arrayOfPercentages = new JSONArray();
        try {
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
        } catch (Exception e) {
            Log.e("debug", e.toString());
        }
        JSONArray arrayOfCategories = new JSONArray();
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
        } catch (Exception e) {
            Log.e("debug", e.toString());
        }

        int gradeType = prefs.getInt("GradeType", 0);

        ListView listView = (ListView) findViewById(R.id.categoryListView);
        TextView gradeTv = (TextView) findViewById(R.id.gradeTextViewNoCat);

        if (!prefs.getBoolean("useCategories", false)){

            listView.setVisibility(View.GONE);
            gradeTv.setVisibility(View.VISIBLE);
            gradeTv.setText("");

            try {
                for (int i = 0; i < arrayOfCategories.length(); i++) {
                    JSONArray arrayOfGrades = new JSONArray(prefs.getString(arrayOfCategories.getString(i) + "Grades" + gradeType, null));

                    for (int j = 0; j <arrayOfGrades.length(); j++){

                        if (gradeTv.length() == 0){
                            gradeTv.append(arrayOfGrades.getString(j));
                        }else {
                            gradeTv.append(", " + arrayOfGrades.getString(j));
                        }
                    }
                }
            }catch (Exception e){
                Log.e("debug", e.toString());
            }
        }
        else {

            gradeTv.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            ArrayList<Grade> arrayListOfGrades = Grade.getGrades();

            CustomGradeAdapter adapter = new CustomGradeAdapter(this, arrayListOfGrades);

            listView.setAdapter(adapter);

            ViewGroup.LayoutParams params = listView.getLayoutParams();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int screenHeight = displayMetrics.heightPixels;

            params.height = adapter.getCount() * (screenHeight / (screenHeight / 113));
            listView.setLayoutParams(params);
            listView.requestLayout();

            try {

                int count = 0;
                boolean isNonZeroCategory = false,
                        isIgnoredCategory = false;

                for (int i = 0; i < arrayOfPercentages.length(); i++) {
                    try {
                        count += arrayOfPercentages.getInt(i);
                    } catch (JSONException ex) {
                        count += 0;
                    }

                }

                for (int i = 0; i < arrayOfCategories.length(); i++) {

                    JSONArray arrayOfGrades = new JSONArray();
                    try {
                        arrayOfGrades = new JSONArray(prefs.getString(arrayOfCategories.getString(i) + "Grades" + gradeType, null));
                    } catch (NullPointerException ex) {
                    }

                    int percentage = 0;
                    try {
                        percentage = arrayOfPercentages.getInt(i);
                    } catch (JSONException e) {
                    }

                    if (arrayOfGrades.length() == 0 && percentage != 0) {
                        isNonZeroCategory = true;
                    } else if (arrayOfGrades.length() != 0 && percentage == 0) {
                        isIgnoredCategory = true;
                    }
                }

                if (prefs.getBoolean("usePercentages", false) && count != 100 && count != 0) {

                    final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "The total must equal 100!", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    snackbar.setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            settingsDialog();

                            snackbar.dismiss();
                        }
                    });

                } else if (prefs.getBoolean("useValues", false) && isNonZeroCategory) {

                    final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "In order for all empty categories to be ignored, set their values to 0", Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                    snackbar.setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            settingsDialog();

                            snackbar.dismiss();
                        }
                    });

                } else if (isIgnoredCategory) {

                    final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Some categories containing grades are ignored. Set their values", Snackbar.LENGTH_INDEFINITE);
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

        setPredictionListView();
    }
//////////////////////////////////////////////////////////////////////////
    public void addGrade(View view) {
        JSONArray arrayOfCategories;
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
        }catch (Exception e) {arrayOfCategories = new JSONArray();}

        ArrayList<String> listOfCategories = new ArrayList<String>();
        for (int i = 0; i < arrayOfCategories.length(); i++){
            try {
                listOfCategories.add(arrayOfCategories.getString(i));
            }catch (JSONException e){}
        }

        if(!listOfCategories.isEmpty() || !prefs.getBoolean("useCategories", false)) {

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.grade_adder_dialog);
            dialog.setTitle("Add a Grade");

            final Spinner spinner = (Spinner) dialog.findViewById(R.id.dialogSpinner);

            if (prefs.getBoolean("useCategories", false)) {

                ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfCategories);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
            }
            else {
                spinner.setVisibility(View.GONE);
            }

            final EditText editText = (EditText) dialog.findViewById(R.id.addGradeEditText);
            int gradeType = prefs.getInt("GradeType", 0);
            switch (gradeType) {
                case 0:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                    editText.setHint("Add Grades (1 - 5)");
                    break;
                case 1:
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
                    editText.setHint("Percentage Format: 94, 82, ...");
                    break;
                default:
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    editText.setHint("Add Grades (A - F)");
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
                        pos = 0;
                    }

                    saveGrade(grade, category, pos);
                    editor.putBoolean("doSetLv", true).apply();
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

            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.secondLayout);
            Button rollDownButton = (Button) findViewById(R.id.rollDownButton);

            int visibility = relativeLayout.getVisibility();
            if (visibility == 8) {
                relativeLayout.setVisibility(View.VISIBLE);
                rollDownButton.setBackgroundResource(R.drawable.ic_arrow_drop_up_black_24dp);
            }
        }
    }
    public void saveGrade(String grade, String category, int pos){

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int gradeType = prefs.getInt("GradeType", 0);
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(prefs.getString(category + "Grades" + gradeType, null));
        }catch (Exception e) {
            jsonArray = new JSONArray();
        }

        grade = grade + "*";
        char[] chars = grade.toCharArray();
        switch (gradeType){
            case 1:
                int percentage = 0;
                for (int i = 0; i < chars.length; i++){
                    if (chars[i] == '1' || chars[i] == '2' || chars[i] == '3' || chars[i] == '4' || chars[i] == '5' ||
                        chars[i] == '6' || chars[i] == '7' || chars[i] == '8' || chars[i] == '9' || chars[i] == '0'){

                        int num = Character.getNumericValue(chars[i]);
                        if (percentage == -1) percentage = 0;
                        percentage = (percentage * 10) + num;

                    }else if (percentage != -1 || i+1 == chars.length){
                        jsonArray.put(String.valueOf(percentage));
                        percentage = -1;
                    }
                }
                editor.putString(category + "Grades" + gradeType, jsonArray.toString()).apply();
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

                        jsonArray.put(string);
                    }
                }
                editor.putString(category + "Grades" + gradeType, jsonArray.toString()).apply();
                break;
            default:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == '1' || chars[i] == '2' || chars[i] == '3' || chars[i] == '4' || chars[i] == '5') {
                        String string = String.valueOf(chars[i]);
                        jsonArray.put(string);
                    }
                }
                editor.putString(category + "Grades" + gradeType, jsonArray.toString()).apply();
                break;
        }
    }
//////////////////////////////////////////////////////////////////////////
    public double countAverage(){
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        JSONArray arrayOfCategories, arrayOfPercentages;
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
        }catch (Exception e) {
            arrayOfCategories = new JSONArray();
            arrayOfPercentages = new JSONArray();
        }
        int gradeType = prefs.getInt("GradeType", 0);

        ArrayList<Double> semiAverage = new ArrayList<Double>();

        if (!prefs.getBoolean("useCategories", false)){

            for (int i = 0; i < arrayOfCategories.length(); i++){

                JSONArray arrayOfGrades = new JSONArray();
                try {
                    arrayOfGrades = new JSONArray(prefs.getString(arrayOfCategories.getString(i) + "Grades" + gradeType, null));
                } catch (Exception e) {
                    Log.e("debug", e.toString());
                }

                for (int j = 0; j < arrayOfGrades.length(); j++){

                    if (gradeType == 2){

                        try {
                            semiAverage.add(letterToNumber(arrayOfGrades.getString(j)));
                        } catch (JSONException e) {
                            Log.e("debug", e.toString());
                        }

                    }
                    else {

                        try {
                            semiAverage.add(Double.parseDouble(arrayOfGrades.getString(j)));
                        } catch (JSONException e) {
                            Log.e("debug", e.toString());
                        }
                    }
                }
            }

            double average = 0;
            for (int i = 0; i < semiAverage.size(); i++) {

                average += semiAverage.get(i);
            }

            average /= semiAverage.size();

            DecimalFormat df = new DecimalFormat("#.####");
            try {
                average = Double.valueOf(df.format(average));
            } catch (NumberFormatException e) {
            }



            if (String.valueOf(average).equals("NaN")) {
                return 0;
            } else {
                return average;
            }

        }
        else if (!prefs.getBoolean("useValues", false)) {

            for (int i = 0; i < arrayOfCategories.length(); i++){
                String s = "";
                try {
                    s = arrayOfCategories.getString(i);
                }catch (JSONException e) {}
                semiAverage.add(countSemiAverage(s));
            }

            double average = 0;
            int count = 0;
            for(int i = 0; i < semiAverage.size(); i++) {

                if (semiAverage.get(i) != -1){

                    average += semiAverage.get(i);
                    count++;
                }
            }
            average /= count;

            DecimalFormat df = new DecimalFormat("#.####");
            try {
                average = Double.valueOf(df.format(average));
            } catch (NumberFormatException e) {
            }

            if (String.valueOf(average).equals("NaN")){
                return 0;
            }
            else {
                return average;
            }

        }
        else {

            for (int i = 0; i < arrayOfCategories.length(); i++){
                String s = "";
                try {
                    s = arrayOfCategories.getString(i);
                }catch (JSONException e) {}
                semiAverage.add(countSemiAverage(s));
            }

            double average = 0;
            for(int i = 0; i < semiAverage.size(); i++) {

                try {
                    average += semiAverage.get(i) / (100 / arrayOfPercentages.getDouble(i));
                }catch (JSONException e){
                    Log.e("debug", e.toString());
                }
            }

            DecimalFormat df = new DecimalFormat("#.####");
            try {
                average = Double.valueOf(df.format(average));
            } catch (NumberFormatException e) {
            }

            if (String.valueOf(average).equals("NaN")){
                return 0;
            }
            else {
                return average;
            }
        }
    }

    public double countSemiAverage(String category){
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        int gradeType = prefs.getInt("GradeType", 0);
        try {
            jsonArray = new JSONArray(prefs.getString(category + "Grades" + gradeType, null));
        }catch (Exception e) {}

        if (prefs.getBoolean("useCategories", false) && !prefs.getBoolean("useValues", false) && jsonArray.length() == 0) return -1;

        double average;
        if (gradeType != 2) {
            average = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    average += jsonArray.getInt(i);
                } catch (JSONException e) {
                }
            }
            if (average != 0) {
                average /= jsonArray.length();
            }

        }else {
            average = 0;
            for (int i = 0; i < jsonArray.length(); i++) {
                String string = "";
                try {
                    string = jsonArray.getString(i);
                } catch (JSONException e) {}

                average += letterToNumber(string);

            }
            if (average != 0) {
                average /= jsonArray.length();
            }
        }

        return average;
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
    public String numberToLetter(double letter){
        if (letter == 4.33) {
            return "A+";
        } else if (letter == 4) {
            return "A";
        } else if (letter == 3.67) {
            return "A-";
        } else if (letter == 3.33) {
            return "B+";
        } else if (letter == 3) {
            return "B";
        } else if (letter == 2.67) {
            return "B-";
        } else if (letter == 2.33) {
            return "C+";
        } else if (letter == 2) {
            return "C";
        } else if (letter == 1.67) {
            return "C-";
        } else if (letter == 1.33) {
            return "D+";
        } else if (letter == 1) {
            return "D";
        } else if (letter == 0.67) {
            return "D-";
        } else if (letter == 0) {
            return "F";
        } else {
            return "";
        }
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
            default:
                gradeEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                gradeEditText.setHint("Add Grades (A - F)");
                break;
        }

        Button dialogOkButton = (Button) dialog.findViewById(R.id.categoryDialogOkButton);
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCategory = nameEditText.getText().toString();
                String grades = gradeEditText.getText().toString();

                saveCategory(newCategory, grades, false);

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
    public void saveCategory(String name, String gradesString, boolean edited){
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray arrayOfCategories, arrayOfPercentages;
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
        }catch (Exception e) {
            arrayOfCategories = new JSONArray();
            arrayOfPercentages = new JSONArray();
        }

        int different = 0;
        for (int i = 0; i < arrayOfCategories.length(); i++) {
            try {
                if (!arrayOfCategories.getString(i).equals(name)) {
                    different++;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        if (different == arrayOfCategories.length()){

            try {
                arrayOfCategories.put(name);
                editor.putString("ListOfCategories", arrayOfCategories.toString());

                arrayOfPercentages.put(String.valueOf(100 / (arrayOfPercentages.length() + 1)));
                editor.putString("ListOfPercentages", arrayOfPercentages.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        char[] chars = gradesString.toCharArray();
        JSONArray arrayOfGrades = new JSONArray();

        int gradeType = prefs.getInt("GradeType", 0);
        switch (gradeType){
            case 1:
                int percentage = 0;
                for (int i = 0; i < chars.length; i++){
                    if (chars[i] == '1' || chars[i] == '2' || chars[i] == '3' || chars[i] == '4' || chars[i] == '5' ||
                            chars[i] == '6' || chars[i] == '7' || chars[i] == '8' || chars[i] == '9' || chars[i] == '0'){

                        int num = Character.getNumericValue(chars[i]);
                        if (percentage == -1) percentage = 0;
                        percentage = (percentage * 10) + num;

                    }else if (percentage != -1){
                        arrayOfGrades.put(String.valueOf(percentage));
                        percentage = -1;
                    }
                }
                if (percentage != -1) arrayOfGrades.put(String.valueOf(percentage));
                break;
            case 2:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == 'A' || chars[i] == 'B' || chars[i] == 'C' || chars[i] == 'D' || chars[i] == 'F') {
                        String string = String.valueOf(chars[i]);

                        try {
                            int j = i + 1;

                            if (chars[j] == '+' || chars[j] == '-') {
                                string += String.valueOf(chars[j]);
                            }
                        }catch (ArrayIndexOutOfBoundsException e){}

                        arrayOfGrades.put(string);
                    }
                }
                break;
            default:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == '1' || chars[i] == '2' || chars[i] == '3' || chars[i] == '4' || chars[i] == '5') {
                        String string = String.valueOf(chars[i]);
                        arrayOfGrades.put(string);
                    }
                }
                break;
        }

        editor.putString(name + "Grades" + gradeType, arrayOfGrades.toString());
        editor.apply();

        editor.putBoolean("doSetLv", true).apply();
        setListView();
    }
    public void saveCategory(String gradesString){
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray arrayOfCategories;
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
        }catch (Exception e) {
            arrayOfCategories = new JSONArray();
        }

        char[] chars = gradesString.toCharArray();
        JSONArray arrayOfGrades = new JSONArray();

        int gradeType = prefs.getInt("GradeType", 0);
        switch (gradeType){
            case 1:
                int percentage = 0;
                for (int i = 0; i < chars.length; i++){
                    if (chars[i] == '1' || chars[i] == '2' || chars[i] == '3' || chars[i] == '4' || chars[i] == '5' ||
                            chars[i] == '6' || chars[i] == '7' || chars[i] == '8' || chars[i] == '9' || chars[i] == '0'){

                        int num = Character.getNumericValue(chars[i]);
                        if (percentage == -1) percentage = 0;
                        percentage = (percentage * 10) + num;

                    }else if (percentage != -1){
                        arrayOfGrades.put(String.valueOf(percentage));
                        percentage = -1;
                    }
                }
                if (percentage != -1) arrayOfGrades.put(String.valueOf(percentage));
                break;
            case 2:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == 'A' || chars[i] == 'B' || chars[i] == 'C' || chars[i] == 'D' || chars[i] == 'F') {
                        String string = String.valueOf(chars[i]);

                        try {
                            int j = i + 1;

                            if (chars[j] == '+' || chars[j] == '-') {
                                string += String.valueOf(chars[j]);
                            }
                        }catch (ArrayIndexOutOfBoundsException e){}

                        arrayOfGrades.put(string);
                    }
                }
                break;
            default:
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == '1' || chars[i] == '2' || chars[i] == '3' || chars[i] == '4' || chars[i] == '5') {
                        String string = String.valueOf(chars[i]);
                        arrayOfGrades.put(string);
                    }
                }
                break;
        }

        for (int i = 0; i < arrayOfCategories.length(); i++){

            int length = arrayOfGrades.length() / arrayOfCategories.length();

            JSONArray arrayOfGrades2 = new JSONArray();

            for (int j = 0; j < length; j++){

                try {
                    arrayOfGrades2.put(arrayOfGrades.get((i * length) + j));
                } catch (JSONException e) {}

            }

            try {
                editor.putString(arrayOfCategories.get(i) + "Grades" + gradeType, arrayOfGrades2.toString());
            } catch (JSONException e) {}

        }

        editor.apply();

        editor.putBoolean("doSetLv", true).apply();
        setListView();
    }
//////////////////////////////////////////////////////////////////////////
    public void deleteCategory(View view) {
        View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray arrayOfCategories, arrayOfPercentages;
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
        }catch (JSONException e) {
            arrayOfCategories = new JSONArray();
            arrayOfPercentages = new JSONArray();
        }

        ArrayList<String> listC = new ArrayList<String>();
        ArrayList<String> listP = new ArrayList<String>();

        for (int i = 0; i < arrayOfCategories.length(); i++){
            try {
                listC.add(arrayOfCategories.getString(i));
            }catch (JSONException e){}
        }

        for (int i = 0; i < arrayOfPercentages.length(); i++){
            try {
                listP.add(arrayOfPercentages.getString(i));
            }catch (JSONException e){}
        }

        editor.remove(listC.get(position) + "Grades");

        listC.remove(position);
        listP.remove(position);

        JSONArray arrayToSendC = new JSONArray(listC);
        JSONArray arrayToSendP = new JSONArray(listP);

        editor.putString("ListOfCategories", arrayToSendC.toString());
        editor.putString("ListOfPercentages", arrayToSendP.toString());
        editor.apply();

        editor.putBoolean("doSetLv", true).apply();
        setListView();
    }
//////////////////////////////////////////////////////////////////////////
    public void editCategory(final View view) {

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        final int gradeType = prefs.getInt("GradeType", 0);

        if (view == findViewById(R.id.gradeEditButtonSingle)){

            String grades = "";
            JSONArray arrayOfCategories;
            try {
                arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
            } catch (JSONException e) {
                arrayOfCategories = new JSONArray();
            }

            for (int i = 0; i < arrayOfCategories.length(); i++){

                JSONArray arrayOfGrades = new JSONArray();
                try {
                    arrayOfGrades = new JSONArray(prefs.getString(arrayOfCategories.get(i) + "Grades" + gradeType, null));
                } catch (Exception e) {
                }
                for (int j = 0; j < arrayOfGrades.length(); j++) {
                    try {
                        grades = grades + arrayOfGrades.getString(j) + ", ";
                    } catch (JSONException e) {
                    }
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
                    gradeEditText.setHint("Percentage Format: 94, 82, ...");
                    break;
                default:
                    gradeEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    gradeEditText.setHint("Add Grades (A - F)");
                    break;
            }
            gradeEditText.setSingleLine(false);
            gradeEditText.setText(grades);

            final String finalGrades = grades;

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

            String category = "", grades = "";
            JSONArray arrayOfCategories;
            try {
                arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
                category = arrayOfCategories.getString(position);
            } catch (JSONException e) {
                arrayOfCategories = new JSONArray();
            }

            JSONArray arrayOfGrades = new JSONArray();
            try {
                arrayOfGrades = new JSONArray(prefs.getString(category + "Grades" + gradeType, null));
            } catch (Exception e) {
            }
            for (int i = 0; i < arrayOfGrades.length(); i++) {
                try {
                    grades = grades + arrayOfGrades.getString(i) + ", ";
                } catch (JSONException e) {
                }
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
                default:
                    gradeEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                    gradeEditText.setHint("Add Grades (A - F)");
                    break;
            }
            gradeEditText.setSingleLine(false);
            gradeEditText.setText(grades);
            final String finalCategory = category;

            Button dialogOkButton = (Button) dialog.findViewById(R.id.categoryDialogOkButton);
            dialogOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String newCategory = nameEditText.getText().toString();
                    String grades = gradeEditText.getText().toString();

                    if (!finalCategory.equals(newCategory)) {
                        deleteOldCategory(finalCategory, position);
                    }

                    saveCategory(newCategory, grades, true);

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
    public void deleteOldCategory(String category, int pos){
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray arrayOfCategories, arrayOfPercentages;
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
        }catch (JSONException e) {
            arrayOfCategories = new JSONArray();
            arrayOfPercentages = new JSONArray();
        }

        ArrayList<String> listC = new ArrayList<String>();
        ArrayList<String> listP = new ArrayList<String>();

        for (int i = 0; i < arrayOfCategories.length(); i++){
            try {
                listC.add(arrayOfCategories.getString(i));
            }catch (JSONException e){}
        }

        for (int i = 0; i < arrayOfPercentages.length(); i++){
            try {
                listP.add(arrayOfPercentages.getString(i));
            }catch (JSONException e){}
        }

        editor.remove(listC.get(pos) + "Grades");

        listC.remove(pos);
        listP.remove(pos);

        JSONArray arrayToSendC = new JSONArray(listC);
        JSONArray arrayToSendP = new JSONArray(listP);

        editor.putString("ListOfCategories", arrayToSendC.toString());
        editor.putString("ListOfPercentages", arrayToSendP.toString());
        editor.apply();

        editor.putBoolean("doSetLv", true).apply();
        setListView();
    }
//////////////////////////////////////////////////////////////////////////
    public void settingsDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.detail_settings_dialog);
        dialog.setTitle("Settings");

        final Spinner spinner = (Spinner) dialog.findViewById(R.id.detailSettingsSpinner);
        String[] strings = {"Numeric (1 - 5)", "Percentage", "Alphabetic (A - F)"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        final SharedPreferences spinnerPrefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = spinnerPrefs.edit();
        int pos = spinnerPrefs.getInt("GradeType", 0);
        spinner.setSelection(pos);

        JSONArray arrayOfCategories;
        try {
            arrayOfCategories = new JSONArray(spinnerPrefs.getString("ListOfCategories", null));
        } catch (Exception e) {
            arrayOfCategories = new JSONArray();
        }


        final ListView listView = (ListView) dialog.findViewById(R.id.SettingsDialogListView);
        ArrayList<Grade> arrayOfGrades = Grade.getGrades();
        CustomDialogLvAdapter arrayAdapter = new CustomDialogLvAdapter(getApplicationContext(), arrayOfGrades);
        listView.setAdapter(arrayAdapter);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = arrayOfCategories.length() * (125);
        listView.setLayoutParams(params);
        listView.requestLayout();

        final LinearLayout lvLayout = (LinearLayout) dialog.findViewById(R.id.settingsLvLayout);

        final Switch switch1 = (Switch) dialog.findViewById(R.id.detailSettingsCheckbox);
        final Switch switch2 = (Switch) dialog.findViewById(R.id.detailSettingsCheckbox2);

        if (spinnerPrefs.getBoolean("useCategories", false)){
            switch1.setChecked(true);
        }else {
            switch1.setChecked(false);
        }
        if (spinnerPrefs.getBoolean("useValues", false)){
            switch2.setChecked(true);
            lvLayout.setVisibility(View.VISIBLE);
        }else {
            switch2.setChecked(false);
            lvLayout.setVisibility(View.GONE);
        }

        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch1.isChecked()){
                    editor.putBoolean("useCategories", true).apply();
                }else {
                    editor.putBoolean("useCategories", false).apply();

                    if (switch2.isChecked()){
                        switch2.setChecked(false);
                        editor.putBoolean("useValues", false).apply();
                        lvLayout.setVisibility(View.GONE);
                    }

                }
            }
        });
        switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch2.isChecked()){
                    editor.putBoolean("useValues", true).apply();
                    lvLayout.setVisibility(View.VISIBLE);

                    if (!switch1.isChecked()){
                        switch1.setChecked(true);
                        editor.putBoolean("useCategories", true).apply();
                    }

                }else {
                    editor.putBoolean("useValues", false).apply();
                    lvLayout.setVisibility(View.GONE);
                }

                balancePercentages(dialog);
            }
        });

        Button dialogOkButton = (Button) dialog.findViewById(R.id.DialogOkButton);
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int spinnerPos = spinner.getSelectedItemPosition();

                SharedPreferences spinnerPrefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
                SharedPreferences.Editor spinnerPrefsEditor = spinnerPrefs.edit();
                spinnerPrefsEditor.putInt("GradeType", spinnerPos);

                JSONArray arrayOfPercentages;
                try {
                    arrayOfPercentages = new JSONArray(spinnerPrefs.getString("ListOfPercentages", null));
                } catch (Exception e) {
                    arrayOfPercentages = new JSONArray();
                }

                for (int i = 0; i < listView.getCount(); i++) {
                    View view = getViewByPos(i, listView);
                    EditText editText = (EditText) view.findViewById(R.id.settingsLVEditText);
                    try {
                        arrayOfPercentages.put(i, editText.getText().toString());
                    } catch (JSONException e) {
                    }
                }

                spinnerPrefsEditor.putString("ListOfPercentages", arrayOfPercentages.toString());

                spinnerPrefsEditor.apply();

                editor.putBoolean("doSetLv", true).apply();
                setListView();
                dialog.dismiss();

            }
        });
        Button dialogCancelButton = (Button) dialog.findViewById(R.id.DialogCancelButton);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                editor.putBoolean("doSetLv", true).apply();
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

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray arrayOfPercentages;
        try {
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
        } catch (Exception e) {
            arrayOfPercentages = new JSONArray();
        }

        JSONArray array = new JSONArray();
        int over = 0, abc = 0;
        for (int i = 0; i < arrayOfPercentages.length(); i++){

            int toBePut = (int) Math.round(100 / arrayOfPercentages.length());

            array.put(String.valueOf(toBePut));
            abc += toBePut;
        }
        over = 100 - abc;

        if (over != 0){

            int i = 0;
            while (over > 0){

                try {
                    array.put(i, String.valueOf(Integer.parseInt(array.getString(i)) + 1));
                } catch (JSONException e) {
                    Log.e("debug", e.toString());
                }

                over--;

                i++;
                if (i == array.length()) {
                    i = 0;
                }
            }
        }

        editor.putString("ListOfPercentages", array.toString()).apply();

        final ListView listView = (ListView) dialog.findViewById(R.id.SettingsDialogListView);
        ArrayList<Grade> arrayOfGrades = Grade.getGrades();
        CustomDialogLvAdapter arrayAdapter = new CustomDialogLvAdapter(getApplicationContext(), arrayOfGrades);
        listView.setAdapter(arrayAdapter);
    }
    public void balancePercentages(){

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray arrayOfPercentages;
        try {
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
        } catch (Exception e) {
            arrayOfPercentages = new JSONArray();
        }

        JSONArray array = new JSONArray();
        int over = 0, abc = 0;
        for (int i = 0; i < arrayOfPercentages.length(); i++){

            int toBePut = (int) Math.round(100 / arrayOfPercentages.length());

            array.put(String.valueOf(toBePut));
            abc += toBePut;
        }
        over = 100 - abc;

        if (over != 0){

            int i = 0;
            while (over > 0){

                try {
                    array.put(i, String.valueOf(Integer.parseInt(array.getString(i)) + 1));
                } catch (JSONException e) {
                    Log.e("debug", e.toString());
                }

                over--;

                i++;
                if (i == array.length()) {
                    i = 0;
                }
            }
        }

        editor.putString("ListOfPercentages", array.toString()).apply();
    }

    public void deleteSubject(){

        try {
            SharedPreferences prefs = getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            JSONArray array = new JSONArray(prefs.getString("List", null));

            ArrayList<String> arrayList = new ArrayList<String>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    arrayList.add(array.getString(i));
                } catch (JSONException e) {}
            }
            arrayList.remove(getIntent().getExtras().getString("subject", null));
            SharedPreferences preferences = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor2 = preferences.edit();
            editor2.clear().apply();
            array = new JSONArray(arrayList);
            editor.putString("List", array.toString()).apply();
        } catch (Exception e) {}

        finish();
    }
//////////////////////////////////////////////////////////////////////////
    public void titleClick(View view) {

        RelativeLayout relativeLayout;
        Button rollDownButton;

        if (view == findViewById(R.id.rollDownButton) || view == findViewById(R.id.firstLayout)) {
            relativeLayout = (RelativeLayout) findViewById(R.id.secondLayout);
            rollDownButton = (Button) findViewById(R.id.rollDownButton);
        }
        else {
            relativeLayout = (RelativeLayout) findViewById(R.id.fourthLayout);
            rollDownButton = (Button) findViewById(R.id.rollDownButton2);
        }

        int visibility = relativeLayout.getVisibility();
        if (visibility == 8) {
            relativeLayout.setVisibility(View.VISIBLE);
            rollDownButton.setBackgroundResource(R.drawable.ic_arrow_drop_up_white_24dp);
        } else {
            relativeLayout.setVisibility(View.GONE);
            rollDownButton.setBackgroundResource(R.drawable.ic_arrow_drop_down_white_24dp);
        }

        setListView();
    }

    public void testsToWriteBtn(View view) {
        TextView textView = (TextView) findViewById(R.id.testsToWriteEditText);

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject"), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (view == findViewById(R.id.ttwAddBtn) || view == findViewById(R.id.ttwAddLayout)){
            if (Integer.parseInt(textView.getText().toString()) < 3) {
                int ttw = Integer.parseInt(textView.getText().toString()) + 1;
                textView.setText(String.valueOf(ttw));

                editor.putInt("testsToWrite", Integer.parseInt(textView.getText().toString())).apply();

                if (findViewById(R.id.fourthLayout).getVisibility() == View.VISIBLE) {

                    setPredictionListView();
                }else {
                    editor.putBoolean("doSetLv", true).apply();
                }
            }
        }
        else {
            if (Integer.parseInt(textView.getText().toString()) > 1) {
                int ttw = Integer.parseInt(textView.getText().toString()) - 1;
                textView.setText(String.valueOf(ttw));

                editor.putInt("testsToWrite", Integer.parseInt(textView.getText().toString())).apply();

                if (findViewById(R.id.fourthLayout).getVisibility() == View.VISIBLE) {

                    setPredictionListView();
                }else {
                    editor.putBoolean("doSetLv", true).apply();
                }
            }
        }
    }

    public void setPredictionListView(){
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject"), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray arrayOfCategories = new JSONArray();
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
        } catch (Exception e) {
            Log.e("debug0", e.toString());
        }
        int gradeType = prefs.getInt("GradeType", 0);

        ArrayList<Double> avgsAfter = new ArrayList<Double>();

        if (!prefs.getBoolean("useCategories", false)){

            JSONArray arrayOfGrades = new JSONArray();
            try {
                arrayOfGrades = new JSONArray(prefs.getString(arrayOfCategories.getString(0) + "Grades" + gradeType, ""));
            } catch (Exception e) {
                Log.e("debug1", e.toString());
            }

            TextView textView = (TextView) findViewById(R.id.testsToWriteEditText);

            int pocetZnamok = 1;
            switch (gradeType){
                case 0:
                    pocetZnamok = 5;
                    break;
                case 1:
                    pocetZnamok = 101;
                    break;
                case 2:
                    pocetZnamok = 13;
                    break;
            }

            String cat1;
            try {
                cat1 = arrayOfCategories.getString(0);
            } catch (JSONException e) {
                cat1 = "Grades";
                Log.e("debug2", e.toString());
            }

            switch (gradeType) {
                case 0:

                    for (int c = 1; c <= 5; c++) {

                        if (Integer.parseInt(textView.getText().toString()) >= 3){
                            arrayOfGrades.put(String.valueOf(c));
                        }

                        ArrayList<String> arrayList = new ArrayList<>();

                        for (int d = c; d <= 5; d++) {

                            if (Integer.parseInt(textView.getText().toString()) >= 2){
                                arrayOfGrades.put(String.valueOf(d));
                            }

                            for (int e = d; e <= 5; e++) {

                                arrayOfGrades.put(String.valueOf(e));

                                editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                                avgsAfter.add(countAverage());

                                arrayList = new ArrayList<>();
                                for (int k = 0; k < arrayOfGrades.length(); k++){
                                    try {
                                        arrayList.add(arrayOfGrades.getString(k));
                                    } catch (JSONException ex) {
                                        Log.e("debug3", ex.toString());
                                    }
                                }
                                arrayList.remove(arrayList.size() - 1);

                                arrayOfGrades = new JSONArray(arrayList);

                                editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                            }
                            if (Integer.parseInt(textView.getText().toString()) == 1){
                                break;
                            }

                            arrayList.remove(arrayList.size() - 1);
                            arrayOfGrades = new JSONArray(arrayList);
                            editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                        }
                        if (Integer.parseInt(textView.getText().toString()) <= 2){
                            break;
                        }

                        arrayList.remove(arrayList.size() - 1);
                        arrayOfGrades = new JSONArray(arrayList);
                        editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                    }

                    break;

                case 1:

                    for (int c = 0; c <= 100; c++) {

                        ArrayList<String> arrayOfGrades2 = new ArrayList<String>();

                        if (Integer.parseInt(textView.getText().toString()) >= 3){
                            arrayOfGrades.put(String.valueOf(c));
                        }

                        for (int d = c; d <= 100; d++) {

                            if (Integer.parseInt(textView.getText().toString()) >= 2){
                                arrayOfGrades.put(String.valueOf(d));
                            }

                            for (int e = d; e <= 100; e++) {

                                arrayOfGrades.put(String.valueOf(e));

                                editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                                avgsAfter.add(countAverage());

                                arrayOfGrades2 = new ArrayList<String>();

                                for (int k = 0; k < arrayOfGrades.length(); k++) {
                                    try {
                                        arrayOfGrades2.add(arrayOfGrades.getString(k));
                                    } catch (JSONException ex) {
                                        Log.e("debug3", ex.toString());
                                    }
                                }
                                arrayOfGrades2.remove(arrayOfGrades.length() - 1);

                                arrayOfGrades = new JSONArray(arrayOfGrades2);

                                editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                            }
                            if (Integer.parseInt(textView.getText().toString()) == 1){
                                break;
                            }

                            arrayOfGrades2.remove(arrayOfGrades2.size() - 1);
                            arrayOfGrades = new JSONArray(arrayOfGrades2);
                            editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                        }
                        if (Integer.parseInt(textView.getText().toString()) <= 2){
                            break;
                        }

                        arrayOfGrades2.remove(arrayOfGrades2.size() - 1);
                        arrayOfGrades = new JSONArray(arrayOfGrades2);
                        editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                    }

                    break;

                case 2:

                    for (int c = 433; c >= 0;) {

                        ArrayList<String> arrayOfGrades2 = new ArrayList<String>();

                        if (Integer.parseInt(textView.getText().toString()) >= 3){
                            double j = c;
                            j /= 100;
                            arrayOfGrades.put(numberToLetter(j));
                        }

                        ArrayList<String> arrayList = new ArrayList<>();

                        for (int d = c; d >= 0;) {

                            if (Integer.parseInt(textView.getText().toString()) >= 2){
                                double j = d;
                                j /= 100;
                                arrayOfGrades.put(numberToLetter(j));
                            }

                            for (int e = d; e >= 0;) {

                                double j = e;
                                j /= 100;
                                arrayOfGrades.put(numberToLetter(j));

                                editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                                avgsAfter.add(countAverage());

                                arrayOfGrades2 = new ArrayList<String>();

                                for (int k = 0; k < arrayOfGrades.length(); k++) {
                                    try {
                                        arrayOfGrades2.add(arrayOfGrades.getString(k));
                                    } catch (JSONException ex) {
                                        Log.e("debug3", ex.toString());
                                    }
                                }
                                arrayOfGrades2.remove(arrayOfGrades.length() - 1);

                                arrayOfGrades = new JSONArray(arrayOfGrades2);

                                editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                                if (e == 67) {
                                    e = 0;
                                } else if (String.valueOf(e).endsWith("67")) {
                                    e -= 34;
                                } else {
                                    e -= 33;
                                }
                            }
                            if (Integer.parseInt(textView.getText().toString()) == 1){
                                break;
                            }

                            arrayOfGrades2.remove(arrayOfGrades2.size() - 1);
                            arrayOfGrades = new JSONArray(arrayOfGrades2);
                            editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                            if (d == 67) {
                                d = 0;
                            } else if (String.valueOf(d).endsWith("67")) {
                                d -= 34;
                            } else {
                                d -= 33;
                            }
                        }
                        if (Integer.parseInt(textView.getText().toString()) <= 2){
                            break;
                        }

                        arrayOfGrades2.remove(arrayOfGrades2.size() - 1);
                        arrayOfGrades = new JSONArray(arrayOfGrades2);
                        editor.putString(cat1 + "Grades" + gradeType, arrayOfGrades.toString()).apply();

                        if (c == 67) {
                            c = 0;
                        } else if (String.valueOf(c).endsWith("67")) {
                            c -= 34;
                        } else {
                            c -= 33;
                        }
                    }

                    break;
            }

        }
        else {

            for (int i = 0; i < arrayOfCategories.length(); i++) {

                JSONArray arrayOfGrades = new JSONArray();
                try {
                    arrayOfGrades = new JSONArray(prefs.getString(arrayOfCategories.getString(i) + "Grades" + gradeType, ""));
                } catch (Exception e) {
                    Log.e("debug1", e.toString());
                }

                switch (gradeType) {
                    case 0:

                        for (int j = 1; j <= 5; j++) {
                            arrayOfGrades.put(String.valueOf(j));

                            try {
                                editor.putString(arrayOfCategories.getString(i) + "Grades" + gradeType, arrayOfGrades.toString()).apply();
                            } catch (JSONException e) {
                                Log.e("debug2", e.toString());
                            }

                            avgsAfter.add(countAverage());

                            ArrayList<String> arrayOfGrades2 = new ArrayList<String>();

                            for (int k = 0; k < arrayOfGrades.length(); k++) {
                                try {
                                    arrayOfGrades2.add(arrayOfGrades.getString(k));
                                } catch (JSONException e) {
                                    Log.e("debug3", e.toString());
                                }
                            }
                            arrayOfGrades2.remove(arrayOfGrades.length() - 1);

                            arrayOfGrades = new JSONArray(arrayOfGrades2);

                            try {
                                editor.putString(arrayOfCategories.getString(i) + "Grades" + gradeType, arrayOfGrades.toString()).apply();
                            } catch (JSONException e) {
                                Log.e("debug4", e.toString());
                            }
                        }

                        break;

                    case 1:

                        for (int j = 0; j <= 100; j++) {
                            arrayOfGrades.put(String.valueOf(j));

                            try {
                                editor.putString(arrayOfCategories.getString(i) + "Grades" + gradeType, arrayOfGrades.toString()).apply();
                            } catch (JSONException e) {
                                Log.e("debug2", e.toString());
                            }

                            avgsAfter.add(countAverage());

                            ArrayList<String> arrayOfGrades2 = new ArrayList<String>();

                            for (int k = 0; k < arrayOfGrades.length(); k++) {
                                try {
                                    arrayOfGrades2.add(arrayOfGrades.getString(k));
                                } catch (JSONException e) {
                                    Log.e("debug3", e.toString());
                                }
                            }
                            arrayOfGrades2.remove(arrayOfGrades.length() - 1);

                            arrayOfGrades = new JSONArray(arrayOfGrades2);

                            try {
                                editor.putString(arrayOfCategories.getString(i) + "Grades" + gradeType, arrayOfGrades.toString()).apply();
                            } catch (JSONException e) {
                                Log.e("debug4", e.toString());
                            }
                        }

                        break;

                    case 2:

                        for (int j = 433; j >= 0; ) {

                            double d = j;
                            d /= 100;
                            arrayOfGrades.put(numberToLetter(d));

                            try {
                                editor.putString(arrayOfCategories.getString(i) + "Grades" + gradeType, arrayOfGrades.toString()).apply();
                            } catch (JSONException e) {
                                Log.e("debug2", e.toString());
                            }

                            avgsAfter.add(countAverage());

                            ArrayList<String> arrayOfGrades2 = new ArrayList<String>();

                            for (int k = 0; k < arrayOfGrades.length(); k++) {
                                try {
                                    arrayOfGrades2.add(arrayOfGrades.getString(k));
                                } catch (JSONException e) {
                                    Log.e("debug3", e.toString());
                                }
                            }
                            arrayOfGrades2.remove(arrayOfGrades.length() - 1);

                            arrayOfGrades = new JSONArray(arrayOfGrades2);

                            try {
                                editor.putString(arrayOfCategories.getString(i) + "Grades" + gradeType, arrayOfGrades.toString()).apply();
                            } catch (JSONException e) {
                                Log.e("debug4", e.toString());
                            }

                            if (j == 67) {
                                j = 0;
                            } else if (String.valueOf(j).endsWith("67")) {
                                j -= 34;
                            } else {
                                j -= 33;
                            }
                        }

                        break;
                }
            }
        }

        int currentGrade = (int) Math.round(Double.parseDouble(prefs.getString("AvgGrade", "1")));

        Log.d("debugA", String.valueOf(avgsAfter.size()));
        Log.d("debugA", String.valueOf(avgsAfter));

        String[] strings = new String[]{};
        switch (gradeType){
            case 0:
                strings = new String[]{"1", "2", "3", "4", "5"};
                break;
            case 1:
                strings = new String[]{"90+", "75+", "50+", "30+", "0+"};
                break;
            case 2:
                strings = new String[]{"4", "3", "2", "1", "0"};
                break;
        }


        editor.putInt("currentGrade", currentGrade);
        editor.putString("avgsAfter", new JSONArray(avgsAfter).toString());
        editor.apply();

        try {
            strings[currentGrade - 1] = String.valueOf(currentGrade) + " - You are here";
        }catch (IndexOutOfBoundsException e){
            Log.e("debug", e.toString());
        }

        ListView listView = (ListView) findViewById(R.id.predictionListView);
        PredictionLvAdapter adapter = new PredictionLvAdapter(this, strings);
        listView.setAdapter(adapter);

        setTotalHeightOfListView(listView);
    }


    public void setTotalHeightOfListView(ListView listView) {

        ListAdapter mAdapter = listView.getAdapter();

        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);

            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}