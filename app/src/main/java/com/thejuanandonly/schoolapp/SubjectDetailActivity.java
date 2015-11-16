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
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.DisplayMetrics;
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
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
        theme();
        String subject = getIntent().getExtras().getString("subject", null);
        toolbar.setTitle(subject);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
        }catch (Exception e) {arrayOfCategories = new JSONArray();}

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

        setListView();
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
                bottomPlus.setVisibility(View.VISIBLE);
                menu.getItem(0).setIcon(R.drawable.ic_done_white_24dp);
            }else {
                menu.getItem(0).setIcon(R.drawable.ic_mode_edit_white_24dp);
                bottomPlus.setVisibility(View.GONE);
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

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        ArrayList<Grade> arrayOfGrades = Grade.getGrades();

        CustomGradeAdapter adapter = new CustomGradeAdapter(this, arrayOfGrades);

        ListView listView = (ListView) findViewById(R.id.categoryListView);
        listView.setAdapter(adapter);

        /*View item = adapter.getView(0, null, listView);
        item.measure(0, 0);*/
        ViewGroup.LayoutParams params = listView.getLayoutParams();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        if (adapter.getCount() <= 5) {
            params.height = adapter.getCount() * (screenHeight / (screenHeight/113));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }else {
            params.height =  5 * (screenHeight / (screenHeight/113)) + 50;
            //params.height =  5 * item.getMeasuredHeight();
            listView.setLayoutParams(params);
            listView.requestLayout();
        }

        editor.putString("AvgGrade", countAverage()).apply();

        TextView averageTV = (TextView) findViewById(R.id.averageTextView);
        int gradeType = prefs.getInt("GradeType" , 0);
        if (gradeType == 2){
            averageTV.setText("GPA: " + countAverage());
        }else {
            averageTV.setText("Average: " + countAverage());
        }


        try{
            JSONArray arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));

            int count = 0;

            for (int i = 0; i < arrayOfPercentages.length(); i++){
                count += arrayOfPercentages.getInt(i);
            }

            if (count != 100){
                Toast.makeText(this, "The total must equal 100!", Toast.LENGTH_LONG).show();

                settingsDialog();
            }

        }catch (Exception e){}

    }
//////////////////////////////////////////////////////////////////////////
    public void addGrade(View view) {
        JSONArray arrayOfCategories;
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
        }catch (Exception e) {arrayOfCategories = new JSONArray();}

        ArrayList<String> listOfCategories = new ArrayList<String>();
        for (int i = 0; i < arrayOfCategories.length(); i++){
            try {
                listOfCategories.add(arrayOfCategories.getString(i));
            }catch (JSONException e){}
        }

        if(!listOfCategories.isEmpty()) {

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.grade_adder_dialog);
            dialog.setTitle("Add a Grade");

            final Spinner spinner = (Spinner) dialog.findViewById(R.id.dialogSpinner);
            ArrayAdapter spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listOfCategories);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerAdapter);

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
                    String category = spinner.getSelectedItem().toString();
                    int pos = spinner.getSelectedItemPosition();
                    saveGrade(grade, category, pos);
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
        }else{
            Toast.makeText(this, "Add a Category first", Toast.LENGTH_SHORT).show();
            Button bottomPlus = (Button) findViewById(R.id.addCategory);
            bottomPlus.setVisibility(View.VISIBLE);
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

                    }else if (percentage != -1){
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

                        int j = i + 1;
                        if (chars[j] == '+' || chars[j] == '-'){
                            string += String.valueOf(chars[j]);
                        }

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

        setListView();
    }
//////////////////////////////////////////////////////////////////////////
    public String countAverage(){
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        JSONArray arrayOfCategories, arrayOfPercentages;
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
            arrayOfPercentages = new JSONArray(prefs.getString("ListOfPercentages", null));
        }catch (Exception e) {
            arrayOfCategories = new JSONArray();
            arrayOfPercentages = new JSONArray();
        }

        ArrayList<Double> semiAverage = new ArrayList<Double>();
        for (int i = 0; i < arrayOfCategories.length(); i++){
            String s = "";
            try {
                s = arrayOfCategories.getString(i);
            }catch (JSONException e) {}
            semiAverage.add(countSemiAverage(s));
        }
        double average = 0;
        for(int i = 0; i < semiAverage.size(); i++) {
            if (semiAverage.get(i) == 0) {
                semiAverage.remove(i);
                i--;
            }
            else {
                double temp = semiAverage.get(i);
                try {
                    temp = temp / (100 / arrayOfPercentages.getDouble(i));
                }catch (JSONException e) {}
                average += temp;
            }
        }

        DecimalFormat df = new DecimalFormat("#.####");
        average = Double.valueOf(df.format(average));

        if (String.valueOf(average).equals("NaN")){
            return "";
        }
        else {
            return String.valueOf(average);
        }
    }

    public double countSemiAverage(String category){
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        JSONArray jsonArray = new JSONArray();
        int gradeType = prefs.getInt("GradeType", 0);
        try {
            jsonArray = new JSONArray(prefs.getString(category + "Grades" + gradeType, null));
        }catch (Exception e) {}

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

                switch (string){
                    case "A+":
                        average += 4.33;
                        break;

                    case "A":
                        average += 4;
                        break;

                    case "A-":
                        average += 4 - 0.33 ;
                        break;

                    case "B+":
                        average += 3 + 0.33;
                        break;

                    case "B":
                        average += 3;
                        break;

                    case "B-":
                        average += 3 - 0.33;
                        break;

                    case "C+":
                        average += 2 + 0.33;
                        break;

                    case "C":
                        average += 2;
                        break;

                    case "C-":
                        average += 2 - 0.33;
                        break;

                    case "D+":
                        average += 1 + 0.33;
                        break;

                    case "D":
                        average += 1;
                        break;

                    case "D-":
                        average += 1 - 0.33;
                        break;

                    case "F":
                        average += 0;
                        break;
                }

            }
            if (average != 0) {
                average /= jsonArray.length();
            }
        }
        return average;
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

                saveCategory(newCategory, grades);

                settingsDialog();

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
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        JSONArray arrayOfCategories;
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
        }catch (Exception e) {arrayOfCategories = new JSONArray();}

        if (!arrayOfCategories.toString().contains(name)){
            arrayOfCategories.put(name);
            editor.putString("ListOfCategories", arrayOfCategories.toString());
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

        setListView();
    }
//////////////////////////////////////////////////////////////////////////
    public void editCategory(final View view) {
        final View parentRow = (View) view.getParent();
        ListView listView = (ListView) parentRow.getParent();
        final int position = listView.getPositionForView(parentRow);

        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String category = "", grades = "";
        JSONArray arrayOfCategories;
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
            category = arrayOfCategories.getString(position);
        }catch (JSONException e) {arrayOfCategories = new JSONArray();}

        JSONArray arrayOfGrades = new JSONArray();
        int gradeType = prefs.getInt("GradeType", 0);
        try {
            arrayOfGrades = new JSONArray(prefs.getString(category + "Grades" + gradeType, null));
        }catch (Exception e) {}
        for (int i = 0; i < arrayOfGrades.length(); i++){
            try {
                grades = grades + arrayOfGrades.getString(i) + ", ";
            }catch (JSONException e) {}
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

                saveCategory(newCategory, grades);

                if (!finalCategory.equals(newCategory)) {
                    deleteOldCategory(finalCategory);
                }

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
    public void deleteOldCategory(String category){
        SharedPreferences prefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray arrayOfCategories;
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
        }catch (JSONException e) {arrayOfCategories = new JSONArray();}

        ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < arrayOfCategories.length(); i++){
            try {
                list.add(arrayOfCategories.getString(i));
            }catch (JSONException e){}
        }

        editor.remove(category + "Grades");

        list.remove(category);

        JSONArray arrayToSend = new JSONArray(list);

        editor.putString("ListOfCategories", arrayToSend.toString()).apply();

        setListView();
    }
//////////////////////////////////////////////////////////////////////////
    public void settingsDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.detail_settings_dialog);
        dialog.setTitle("Settings");

        final Spinner spinner = (Spinner) dialog.findViewById(R.id.detailSettingsSpinner);
        String[] strings = {"Numeric (1 - 5)", "Percentage", "Alphabetic (A - F)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        SharedPreferences spinnerPrefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
        int pos = spinnerPrefs.getInt("GradeType", 0);
        spinner.setSelection(pos);

        final ListView listView = (ListView) dialog.findViewById(R.id.SettingsDialogListView);
        ArrayList<Grade> arrayOfGrades = Grade.getGrades();
        CustomDialogLvAdapter arrayAdapter = new CustomDialogLvAdapter(getApplicationContext(), arrayOfGrades);
        listView.setAdapter(arrayAdapter);

        Button dialogOkButton = (Button) dialog.findViewById(R.id.DialogOkButton);
        dialogOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int spinnerPos = spinner.getSelectedItemPosition();

                SharedPreferences spinnerPrefs = getSharedPreferences("Subject" + getIntent().getExtras().getString("subject", null), Context.MODE_PRIVATE);
                SharedPreferences.Editor spinnerPrefsEditor = spinnerPrefs.edit();
                spinnerPrefsEditor.putInt("GradeType", spinnerPos);

                JSONArray arrayOfPercentages, arrayOfCategories;
                try {
                    arrayOfPercentages = new JSONArray(spinnerPrefs.getString("ListOfPercentages", null));
                    arrayOfCategories = new JSONArray(spinnerPrefs.getString("ListOfCategories", null));
                }catch (Exception e) {
                    arrayOfPercentages = new JSONArray();
                    arrayOfCategories = new JSONArray();
                }

                int count = 0;
                for (int i = 0; i < listView.getCount(); i++) {
                    View view = listView.getChildAt(i);
                    EditText editText = (EditText) view.findViewById(R.id.settingsLVEditText);
                    try {
                        arrayOfPercentages.put(i, editText.getText().toString());

                        try {
                            count += Integer.parseInt(editText.getText().toString());
                        }catch (NumberFormatException e){}

                    } catch (JSONException e) {}
                }

                spinnerPrefsEditor.putString("ListOfPercentages", arrayOfPercentages.toString());

                spinnerPrefsEditor.apply();
                setListView();
                dialog.dismiss();

            }
        });
        Button dialogCancelButton = (Button) dialog.findViewById(R.id.DialogCancelButton);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void theme() {
        SharedPreferences prefs = getSharedPreferences("themeSave", Context.MODE_PRIVATE);
        int theme = prefs.getInt("theme", 0);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.subjectDetailToolbar);

        View separator = (View) findViewById(R.id.separatorDetail);
        View separator1 = (View) findViewById(R.id.separatorDetail1);

        switch (theme) {
            case 1:

                toolbar.setBackgroundColor(getResources().getColor(R.color.orange));

                separator.setBackgroundColor(getResources().getColor(R.color.orange));
                separator1.setBackgroundColor(getResources().getColor(R.color.orange));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.orange700));

                break;
            case 2:

                toolbar.setBackgroundColor(getResources().getColor(R.color.green));

                separator.setBackgroundColor(getResources().getColor(R.color.green));
                separator1.setBackgroundColor(getResources().getColor(R.color.green));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.green800));

                break;
            case 3:

                toolbar.setBackgroundColor(getResources().getColor(R.color.blue));

                separator.setBackgroundColor(getResources().getColor(R.color.blue));
                separator1.setBackgroundColor(getResources().getColor(R.color.blue));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.blue800));

                break;
            case 4:

                toolbar.setBackgroundColor(getResources().getColor(R.color.grey));

                separator.setBackgroundColor(getResources().getColor(R.color.grey));
                separator1.setBackgroundColor(getResources().getColor(R.color.grey));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.grey700));

                break;
            case 5:

                toolbar.setBackgroundColor(getResources().getColor(R.color.teal));

                separator.setBackgroundColor(getResources().getColor(R.color.teal));
                separator1.setBackgroundColor(getResources().getColor(R.color.teal));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.teal800));

                break;
            case 6:

                toolbar.setBackgroundColor(getResources().getColor(R.color.brown));

                separator.setBackgroundColor(getResources().getColor(R.color.brown));
                separator1.setBackgroundColor(getResources().getColor(R.color.brown));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.brown700));

                break;
            default:

                toolbar.setBackgroundColor(getResources().getColor(R.color.red));

                separator.setBackgroundColor(getResources().getColor(R.color.red));
                separator1.setBackgroundColor(getResources().getColor(R.color.red));

                if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.red800));
        }
    }
}