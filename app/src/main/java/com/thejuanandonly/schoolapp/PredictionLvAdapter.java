package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Robo on 11/28/2015.
 */
public class PredictionLvAdapter extends ArrayAdapter<String> {
    public PredictionLvAdapter(Context context, String[] strings){
        super(context, 0, strings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.prediction_listview, parent, false);
        }

        TextView t1 = (TextView) convertView.findViewById(R.id.grade_text_view);
        TextView t2 = (TextView) convertView.findViewById(R.id.dash_text_view);

        SharedPreferences prefs = getContext().getSharedPreferences("Subject" + SubjectDetailActivity.currentSubject, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt("subLvPos", position).apply();

        int currentGrade = prefs.getInt("currentGrade", 0);
        int gradeType = prefs.getInt("GradeType", 0);

        String[] strings = new String[]{};
        switch (gradeType){
            case 0:
                strings = new String[]{"1", "2", "3", "4", "5"};
                break;
            case 1:
                strings = new String[]{"90+", "75+", "50+", "30+", "0+"};
                break;
            case 2:
                strings = new String[]{"0", "1", "2", "3", "4"};
                break;
        }
        t1.setText(strings[position]);

        JSONArray arrayOfCategories = new JSONArray();
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfCategories", null));
        } catch (Exception e) {
            Log.e("debug", e.toString());
        }

        JSONArray array = new JSONArray();
        try {
            array = new JSONArray(prefs.getString("avgsAfter", null));
        } catch (Exception e) {
            Log.e("debug", e.toString());
        }
        ArrayList<Integer> avgsAfter = new ArrayList<>();
        for (int i = 0; i < array.length(); i++){
            try {
                avgsAfter.add( (int) Math.round(array.getDouble(i)));
            }catch (JSONException e){
                Log.e("debug", e.toString());
            }
        }

        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();
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

        for (int k = 0; k < (avgsAfter.size() / pocetZnamok); k++){

            for (int i = (pocetZnamok * k); i < ((pocetZnamok * k) + pocetZnamok); i++){
                int prediction = (int) Math.round(avgsAfter.get(i));

                ArrayList<String> line = new ArrayList<>();
                line.add(0, "Get");
                line.add(1, "0");
                line.add(2, "from");
                line.add(3, "Cat");

                //if (prediction != currentGrade) {                 Keby chcem napisat ze ktoru znamku teraz mas

                boolean ci = false;

                try {

                    switch (gradeType){
                        case 0:

                            if (prediction == (position + 1)) {

                                ci = true;

                                try {
                                    line.set(1, String.valueOf((i + 1) - (pocetZnamok * k)));
                                } catch (IndexOutOfBoundsException e) {
                                    line.add(1, String.valueOf((i + 1) - (pocetZnamok * k)));
                                }

                                try {
                                    line.set(2, "from");
                                } catch (IndexOutOfBoundsException e) {
                                    line.add(2, "from");
                                }

                                try {
                                    line.set(3, arrayOfCategories.getString(k));
                                } catch (IndexOutOfBoundsException e) {
                                    line.add(3, arrayOfCategories.getString(k));
                                }
                            }

                            break;

                        case 1:

                            int percentage = (Integer.parseInt(String.valueOf(strings[position].charAt(0))) * 10);
                            try{
                                percentage += Integer.parseInt(String.valueOf(strings[position].charAt(1)));
                            }catch (NumberFormatException e){
                                percentage /= 10;
                            }

                            int percentageNext;
                            try {
                                percentageNext = (Integer.parseInt(String.valueOf(strings[position - 1].charAt(0))) * 10);
                                try {
                                    percentageNext += Integer.parseInt(String.valueOf(strings[position - 1].charAt(1)));
                                } catch (NumberFormatException e) {
                                    percentageNext /= 10;
                                }
                            }catch (ArrayIndexOutOfBoundsException e){
                                percentageNext = 100;
                            }

                            if (prediction >= percentage && prediction < percentageNext) {

                                Log.d("debugBC", String.valueOf(prediction) + ", " + percentage + " - " + percentageNext);

                                ci = true;

                                try {
                                    line.set(1, String.valueOf((i) - (pocetZnamok * k)));
                                } catch (IndexOutOfBoundsException e) {
                                    line.add(1, String.valueOf((i) - (pocetZnamok * k)));
                                }

                                try {
                                    line.set(2, "from");
                                } catch (IndexOutOfBoundsException e) {
                                    line.add(2, "from");
                                }

                                try {
                                    line.set(3, arrayOfCategories.getString(k));
                                } catch (IndexOutOfBoundsException e) {
                                    line.add(3, arrayOfCategories.getString(k));
                                }
                            }

                            break;

                        case 2:

                            if (prediction == position) {

                                ci = true;

                                try {
                                    line.set(1, numberToLetter(alphabeticGradeRound((i) - (pocetZnamok * k))));
                                } catch (IndexOutOfBoundsException e) {
                                    line.add(1, numberToLetter(alphabeticGradeRound((i) - (pocetZnamok * k))));
                                }

                                try {
                                    line.set(2, "from");
                                } catch (IndexOutOfBoundsException e) {
                                    line.add(2, "from");
                                }

                                try {
                                    line.set(3, arrayOfCategories.getString(k));
                                } catch (IndexOutOfBoundsException e) {
                                    line.add(3, arrayOfCategories.getString(k));
                                }
                            }

                            break;
                    }

                } catch (JSONException e){
                    Log.e("debug", e.toString());
                }

                if (ci) {

                    try {
                        if (line.get(3).equals(arrayLists.get(arrayLists.size() - 1).get(3))) {

                            switch (gradeType) {
                                case 0:

                                    if (arrayLists.get(arrayLists.size() - 1).get(1).length() == 1) {

                                        line.set(1, (arrayLists.get(arrayLists.size() - 1).get(1) + "/" + line.get(1)));
                                    }else {

                                        line.set(1, (arrayLists.get(arrayLists.size() - 1).get(1).charAt(0) + " - " + line.get(1)));
                                    }
                                    break;

                                case 1:

                                    int digits = 1;

                                    try{

                                        int c = Integer.parseInt(String.valueOf(arrayLists.get(arrayLists.size() - 1).get(1).charAt(1)));
                                        digits = 2;

                                    }catch (IndexOutOfBoundsException | NumberFormatException e){}
                                    try{
                                        int c = Integer.parseInt(String.valueOf(arrayLists.get(arrayLists.size() - 1).get(1).charAt(2)));
                                        digits = 3;

                                    }catch (IndexOutOfBoundsException | NumberFormatException e){}



                                    switch (digits){
                                        case 1:

                                            line.set(1, (String.valueOf(arrayLists.get(arrayLists.size() - 1).get(1).charAt(0)) + " - " + line.get(1) + "%"));

                                            break;
                                        case 2:

                                            line.set(1, (String.valueOf(arrayLists.get(arrayLists.size() - 1).get(1).charAt(0)) +
                                                        String.valueOf(arrayLists.get(arrayLists.size() - 1).get(1).charAt(1)) +
                                                        " - " + line.get(1) + "%"));

                                            break;
                                        case 3:

                                            line.set(1, (String.valueOf(arrayLists.get(arrayLists.size() - 1).get(1).charAt(0)) +
                                                        String.valueOf(arrayLists.get(arrayLists.size() - 1).get(1).charAt(1)) +
                                                        String.valueOf(arrayLists.get(arrayLists.size() - 1).get(1).charAt(2)) +
                                                        " - " + line.get(1) + "%"));

                                            break;
                                    }

                                    Log.d("debugBB", line.toString());

                                    break;

                                case 2:

                                    if (arrayLists.get(arrayLists.size() - 1).get(1).length() == 1) {

                                        line.set(1, (arrayLists.get(arrayLists.size() - 1).get(1) + "/" + line.get(1)));
                                    }else {

                                        if (arrayLists.get(arrayLists.size() - 1).get(1).charAt(1) == '+' ||
                                                arrayLists.get(arrayLists.size() - 1).get(1).charAt(1) == '-') {

                                            line.set(1, (String.valueOf(arrayLists.get(arrayLists.size() - 1).get(1).charAt(0)) +
                                                    String.valueOf(arrayLists.get(arrayLists.size() - 1).get(1).charAt(1)) +
                                                    " to " + line.get(1)));

                                        } else {
                                            line.set(1, (arrayLists.get(arrayLists.size() - 1).get(1).charAt(0) + " to " + line.get(1)));
                                        }
                                    }
                            }

                            arrayLists.set(arrayLists.size() - 1, line);

                        } else {
                            arrayLists.add(line);
                        }
                    } catch (IndexOutOfBoundsException e) {
                        arrayLists.add(line);
                        Log.e("debug", e.toString());
                    }
                }
            }
        }


        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.Linear1);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        linearLayout.removeAllViews();

        for(int i = 0; i < arrayLists.size(); i++){

            View child = inflater.inflate(R.layout.prediction_lv_item, null);
            linearLayout.addView(child);

            ArrayList<String> line = arrayLists.get(i);

            View childAt = linearLayout.getChildAt(i);

            TextView t3 = (TextView) childAt.findViewById(R.id.getOr_text_view);
            TextView t4 = (TextView) childAt.findViewById(R.id.grade_to_get_text_view);
            TextView t5 = (TextView) childAt.findViewById(R.id.from_text_view);
            TextView t6 = (TextView) childAt.findViewById(R.id.grade_category_text_view);
            t3.setText("");
            t4.setText("");
            t5.setText("");
            t6.setText("");

            if (i != 0){
                line.set(0, "or");
            }

            if (!prefs.getBoolean("useCategories", false)) {

                try {
                    t3.setText(line.get(0));
                    t4.setText(line.get(1));
                    t5.setText("");
                    t6.setText("");
                } catch (IndexOutOfBoundsException e) {
                    linearLayout.removeView(child);
                    arrayLists.remove(i);
                    Log.e("debug", e.toString());
                }

            }
            else {

                try {
                    t3.setText(line.get(0));
                    t4.setText(line.get(1));
                    t5.setText(line.get(2));
                    t6.setText(line.get(3));
                } catch (IndexOutOfBoundsException e) {
                    linearLayout.removeView(child);
                    arrayLists.remove(i);
                    Log.e("debug", e.toString());
                }
            }
        }

        try {
            View childAt = linearLayout.getChildAt(0);
            TextView t3 = (TextView) childAt.findViewById(R.id.getOr_text_view);
        }catch (NullPointerException e){

            View child = inflater.inflate(R.layout.prediction_lv_item, null);
            linearLayout.addView(child);

            View childAt = linearLayout.getChildAt(0);
            TextView t3 = (TextView) childAt.findViewById(R.id.getOr_text_view);
            TextView t4 = (TextView) childAt.findViewById(R.id.grade_to_get_text_view);
            TextView t5 = (TextView) childAt.findViewById(R.id.from_text_view);
            TextView t6 = (TextView) childAt.findViewById(R.id.grade_category_text_view);
            t3.setText("");
            t4.setText("");
            t5.setText("");
            t6.setText("");

            String text;

            /*if ((position + 1) == currentGrade){                  Keby chcem napisat ze ktoru znamku teraz mas
                text = "You are Here";

                t3.setText(text);

                t3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            }
            else{*/

                text = "Not Possible";

                t3.setText(text);

                t3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            //}
        }


        return convertView;
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
            return String.valueOf(letter);
        }
    }

    public double alphabeticGradeRound(int pos){

        double rounded = 433;
        for (int j = pos; j > 0; j--) {

            if (rounded == 67.0) {
                rounded = 0;
            } else if (String.valueOf(rounded).endsWith("67.0")) {
                rounded -= 34;
            } else {
                rounded -= 33;
            }
        }
        rounded /= 100;

        return rounded;
    }
}
