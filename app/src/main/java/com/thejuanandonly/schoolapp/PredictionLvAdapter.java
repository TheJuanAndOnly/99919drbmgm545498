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


        String[] strings = {"1", "2", "3", "4", "5"};
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

        int currentGrade = prefs.getInt("currentGrade", 0);

        ArrayList<ArrayList<String>> arrayLists = new ArrayList<>();

        for (int k = 0; k < (avgsAfter.size() / 5); k++){


            for (int i = (5 * k); i < ((5 * k) + 5); i++){
                int prediction = (int) Math.round(avgsAfter.get(i));

                ArrayList<String> line = new ArrayList<>();
                line.add(0, "Get");
                line.add(1, "0");
                line.add(2, "from");
                line.add(3, "Cat");

                /*if (prediction == currentGrade) {                 Keby chcem napisat ze ktoru znamku teraz mas
                }else*/ if (prediction == (position + 1)){

                    Log.d("debug", String.valueOf(prediction) + ", " + String.valueOf(position + 1) + ", " + String.valueOf((i + 1) - (5 * k)));

                    try {

                        try {
                            line.set(1, String.valueOf((i + 1) - (5 * k)));
                        }catch (IndexOutOfBoundsException e){
                            line.add(1, String.valueOf((i + 1) - (5 * k)));
                        }

                        try {
                            line.set(2, "from");
                        }catch (IndexOutOfBoundsException e){
                            line.add(2, "from");
                        }

                        try{
                            line.set(3, arrayOfCategories.getString(k));
                        }catch (IndexOutOfBoundsException e){
                            line.add(3, arrayOfCategories.getString(k));
                        }

                    } catch (JSONException e){
                        Log.e("debug", e.toString());
                    }

                    try {
                        if (line.get(3).equals(arrayLists.get(arrayLists.size() - 1).get(3))){

                            if (arrayLists.get(arrayLists.size() - 1).get(1).length() == 1){

                                line.set(1, (arrayLists.get(arrayLists.size() - 1).get(1) + "/" + line.get(1)));
                            }
                            else {

                                line.set(1, (arrayLists.get(arrayLists.size() - 1).get(1).charAt(0) + " - " + line.get(1)));
                            }

                            arrayLists.set(arrayLists.size() - 1, line);

                        }else {
                            arrayLists.add(line);
                        }
                    }catch (IndexOutOfBoundsException e){
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

            /*if ((position + 1) == currentGrade){          Keby chcem napisat ze ktoru znamku teraz mas
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
}
