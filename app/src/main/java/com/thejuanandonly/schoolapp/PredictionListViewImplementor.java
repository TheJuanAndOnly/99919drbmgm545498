package com.thejuanandonly.schoolapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Robo on 28-Oct-16.
 */
public class PredictionListViewImplementor implements Runnable {

    private static final String TAG = "GD PredictionListView";

    private Context context;
    private SubjectData subjectData;
    private ListView listView;

    private ProgressBar progress;

    public PredictionListViewImplementor(Context context, SubjectData subjectData, ListView listView) {
        this.context = context;
        this.subjectData = subjectData;
        this.listView = listView;

        progress = (ProgressBar) ((Activity) context).findViewById(R.id.prediction_loading);
    }

    @Override
    public void run() {
        showLoading();

        final List<List<String>> gradesInCategories;

        if (subjectData.isUseCategories()){
            gradesInCategories = subjectData.getGrades();
        }
        else {
            gradesInCategories = new ArrayList<>(1);
            gradesInCategories.add(new ArrayList<String>());
            List<List<String>> temp = subjectData.getGrades();

            for (List<String> list : temp){
                for (String s : list){
                    gradesInCategories.get(0).add(s);
                }
            }
        }

        if (Thread.interrupted()){
            Log.i(TAG, "Thread interrupted");
            return;
        }

        final int gradeType = subjectData.getGradeType();

        List<String> gradesToAdapter;
        switch (gradeType){
            default:
                gradesToAdapter = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
                break;

            case SubjectDetailActivity.PERCENTAGE:
                gradesToAdapter = new ArrayList<>(
                        Arrays.asList(
                                subjectData.getPercentageConversion().get(0).toString(),
                                subjectData.getPercentageConversion().get(1).toString(),
                                subjectData.getPercentageConversion().get(2).toString(),
                                subjectData.getPercentageConversion().get(3).toString(),
                                subjectData.getPercentageConversion().get(4).toString()
                        )
                );

                break;

            case SubjectDetailActivity.ALPHABETIC:
                gradesToAdapter = new ArrayList<>(Arrays.asList("4", "3", "2", "1", "0"));

                break;

            case SubjectDetailActivity.TEN_GRADE:
                gradesToAdapter = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
                break;
        }

        List<List<String>> gradesToBeAdded =
                new ArrayList<>(
                        combine(gradesToAdapter, subjectData.getTestsToWrite(), new ArrayList<String>(), 0, new ArrayList<List<String>>())
                );

        if (Thread.interrupted()){
            Log.i(TAG, "Thread interrupted");
            return;
        }

        List<List<List<String>>> prediction = new ArrayList<>(gradesToAdapter.size());
        List<List<List<String>>> output = new ArrayList<>(gradesToAdapter.size());
        for (int i = 0; i < gradesToAdapter.size(); i++){
            prediction.add(new ArrayList<List<String>>());
        }

        //counting averages
        for (int i = 0; i < gradesInCategories.size(); i++){
            for (int j = 0; j < gradesToBeAdded.size(); j++){
                gradesInCategories.get(i).addAll(gradesToBeAdded.get(j));

                double avgAfter = countAverage(gradesInCategories, subjectData);

                for (int k = 0; k < gradesInCategories.get(i).size(); k++){
                    gradesInCategories.get(i).remove(gradesInCategories.get(i).size() - 1);
                }

                int gradeAfter = indexToGrade(avgAfter);

                prediction.get(gradeAfter - 1).add(gradesToBeAdded.get(j));
            }
        }

        //saving into the array passed to adapter
        for (int grade = 0; grade < prediction.size(); grade++){
            output.add(new ArrayList<List<String>>());
            for (int cat = 0; cat < prediction.get(grade).size(); cat++){
                output.get(grade).add(new ArrayList<String>());

                if (prediction.get(grade).get(cat).size() == 0){
                    output.get(grade).set(0, Arrays.asList("", "", "There's no way", ""));
                    break;
                }

                if (cat == 0) {
                    output.get(grade).get(cat).add(context.getString(R.string.get));
                }
                else {
                    output.get(grade).get(cat).add(context.getString(R.string.or));
                }

                String gradeToGet = "";
                if (subjectData.getTestsToWrite() == 1){
                    gradeToGet = prediction.get(grade).get(cat).get(0);
                    if (prediction.get(grade).get(cat).size() > 1) {
                        if (subjectData.getGradeType() == SubjectDetailActivity.ALPHABETIC) {
                            gradeToGet += " to ";
                        } else {
                            gradeToGet += " - ";
                        }
                        gradeToGet += prediction.get(grade).get(cat).get(prediction.get(grade).get(cat).size() - 1);
                    }

                }
                else {
                    for (int i = 0; i < prediction.get(grade).get(cat).size(); i++){
                        gradeToGet += prediction.get(grade).get(cat).get(i);

                        if (i != prediction.get(grade).get(cat).size() - 1) gradeToGet += ", ";
                    }
                }
                output.get(grade).get(cat).add(gradeToGet);

                if (subjectData.isUseCategories()){
                    output.get(grade).get(cat).add(context.getString(R.string.from));
                    output.get(grade).get(cat).add(subjectData.getArrayOfCategories().get(cat));
                }
                else {
                    output.get(grade).get(cat).add("");
                    output.get(grade).get(cat).add("");
                }
            }
        }

        Log.d(TAG, prediction.toString());
        Log.d(TAG, output.toString());

        if (Thread.interrupted()){
            Log.i(TAG, "Thread interrupted");
            return;
        }

        setAdapter(gradesToAdapter, output);
    }

    private double countAverage(List<List<String>> gradesInCategories, SubjectData subjectData){

        List<Double> averages = new ArrayList<>(gradesInCategories.size());

        for (int i = 0; i < gradesInCategories.size(); i++) {
            List<String> grades = gradesInCategories.get(i);

            for (String grade : grades) {
                if (subjectData.getGradeType() == SubjectDetailActivity.ALPHABETIC) {

                    try {
                        averages.set(i, averages.get(i) + SubjectDetailActivity.letterToNumber(grade));
                    }catch (IndexOutOfBoundsException e){
                        averages.add(SubjectDetailActivity.letterToNumber(grade));
                    }
                    continue;
                }

                try {
                    averages.set(i, averages.get(i) + Double.parseDouble(grade));
                }catch (IndexOutOfBoundsException e){
                    try{
                        averages.add(Double.parseDouble(grade));
                    }catch (NumberFormatException ignored){}
                }catch (NumberFormatException ignored){}
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

    private List<List<String>> combine(List<String> grades, int ttw, List<String> branch, int numElem, List<List<String>> result){

        if (numElem == ttw){

            List<String> list = new ArrayList<>();
            list.addAll(branch);

            result.add(list);

            return result;
        }

        int i = numElem == 0 ? 0 : Integer.valueOf(branch.get(numElem -1)) - 1;
        for (; i < grades.size(); i++){

            try {
                branch.set(numElem++, grades.get(i));
            } catch (IndexOutOfBoundsException e){
                branch.add(grades.get(i));
            }

            combine(grades, ttw, branch, numElem, result);

            numElem--;
        }

        return result;
    }

    private int indexToGrade(double index){
        switch (subjectData.getGradeType()){
            default:
                return (int) Math.round(index);

            case SubjectDetailActivity.PERCENTAGE:
                for (int i = 0; i < subjectData.getPercentageConversion().size(); i++){
                    if (index >= subjectData.getPercentageConversion().get(i)) {
                        return i + 1;
                    }
                }
                return 5;

            case SubjectDetailActivity.ALPHABETIC:
                return 5 - (int) Math.round(index);

            case SubjectDetailActivity.TEN_GRADE:
                return (int) Math.round(index);
        }
    }

    private void setAdapter(final List<String> gradesToAdapter, final List<List<List<String>>> prediction){
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new PredictionAdapter(context, gradesToAdapter, prediction));

                setTotalHeightOfListView(listView);
            }
        });
    }

    private void showLoading(){
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideLoading(){
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
            }
        });
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

    private class PredictionAdapter extends ArrayAdapter<String> {

        private List<String> resources;
        private List<List<List<String>>> output;

        public PredictionAdapter(Context context, List<String> resource, List<List<List<String>>> output) {
            super(context, 0, resource);

            this.resources = resource;
            this.output = output;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.prediction_listview, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.grade_text_view)).setText(resources.get(position));

            if (subjectData.getGradeType() == SubjectDetailActivity.PERCENTAGE){
                convertView.findViewById(R.id.grade_plus_text_view).setVisibility(View.VISIBLE);
            } else {
                convertView.findViewById(R.id.grade_plus_text_view).setVisibility(View.GONE);
            }

            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.Linear1);

            LayoutInflater inflater = LayoutInflater.from(getContext());
            layout.removeAllViews();

            for (int i = 0; i < output.get(position).size(); i++){

                View child = inflater.inflate(R.layout.prediction_lv_item, null);
                layout.addView(child);

                TextView getOrTv = (TextView) child.findViewById(R.id.getOr_text_view);
                TextView gradeToGetTv = (TextView) child.findViewById(R.id.grade_to_get_text_view);
                TextView fromTv = (TextView) child.findViewById(R.id.from_text_view);
                TextView gradeCategoryTv = (TextView) child.findViewById(R.id.grade_category_text_view);


                List<String> strings = output.get(position).get(i);

                getOrTv.setText(strings.get(0));
                gradeToGetTv.setText(strings.get(1));
                fromTv.setText(strings.get(2));
                gradeCategoryTv.setText(strings.get(3));
            }

            hideLoading();

            return convertView;
        }
    }
}