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
            Log.d(TAG, "Thread interrupted");
            return;
        }

        final int gradeType = subjectData.getGradeType();

        List<String> gradesToBeAdded;
        List<String> gradesToAdapter;
        switch (gradeType){
            default:
                gradesToAdapter = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
                gradesToBeAdded = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
                break;

            case SubjectDetailActivity.PERCENTAGE:
                gradesToAdapter = new ArrayList<>(Arrays.asList("90", "75", "50", "30", "0"));

                gradesToBeAdded = new ArrayList<>(101);
                for (int i = 0; i < 101; i++){
                    gradesToBeAdded.add(String.valueOf(i));
                }
                break;

            case SubjectDetailActivity.ALPHABETIC:
                gradesToAdapter = new ArrayList<>(Arrays.asList("4", "3", "2", "1", "0"));

                gradesToBeAdded = new ArrayList<>(13);
                for (int i = 433; i >= 0;){
                    gradesToBeAdded.add(SubjectDetailActivity.numberToLetter(((double) i) / 100.0));

                    if (i == 67) {
                        i = 0;
                    } else if (String.valueOf(i).endsWith("67")) {
                        i -= 34;
                    } else {
                        i -= 33;
                    }
                }
                break;

            case SubjectDetailActivity.TEN_GRADE:
                gradesToAdapter = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
                gradesToBeAdded = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
                break;
        }

        if (Thread.interrupted()){
            Log.d(TAG, "Thread interrupted");
            return;
        }

        List<List<List<String>>> prediction = new ArrayList<>(gradesToAdapter.size());
        for (int i = 0; i < gradesToAdapter.size(); i++){
            prediction.add(new ArrayList<List<String>>());
            prediction.get(i).add(new ArrayList<String>());
        }

        for (int i = 0; i < gradesInCategories.size(); i++){
            for (int j = 0; j < gradesToBeAdded.size(); j++){
                gradesInCategories.get(i).add(gradesToBeAdded.get(j));

                double avgAfter = countAverage(gradesInCategories, subjectData);

                gradesInCategories.get(i).remove(gradesInCategories.get(i).size() - 1);

                int gradeAfter = indexToGrade(avgAfter);

                try {
                    prediction.get(gradeAfter - 1).get(i).add(gradesToBeAdded.get(j));
                } catch (IndexOutOfBoundsException e) {
                    prediction.get(gradeAfter - 1).add(new ArrayList<String>());
                    prediction.get(gradeAfter - 1).get(i).add(gradesToBeAdded.get(j));
                }
            }
        }

        Log.d(TAG, prediction.toString());

        if (Thread.interrupted()){
            Log.d(TAG, "Thread interrupted");
            return;
        }

        setAdapter(gradesToAdapter, prediction);
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
                throw new RuntimeException("Not implemented yet");

            case SubjectDetailActivity.TEN_GRADE:
                return (int) Math.round(index);
        }
    }

    private void setAdapter(final List<String> gradesToAdapter, final List<List<List<String>>> prediction){
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(new PredictionAdapter(context, gradesToAdapter, prediction, subjectData));

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
        private List<List<List<String>>> prediction;
        private SubjectData subjectData;

        public PredictionAdapter(Context context, List<String> resource, List<List<List<String>>> prediction, SubjectData subjectData) {
            super(context, 0, resource);

            this.resources = resource;
            this.prediction = prediction;
            this.subjectData = subjectData;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.prediction_listview, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.grade_text_view)).setText(resources.get(position));

            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.Linear1);

            LayoutInflater inflater = LayoutInflater.from(getContext());
            layout.removeAllViews();

            boolean added = false;

            for (int i = 0; i < prediction.get(position).size(); i++){

                String category = subjectData.getArrayOfCategories().get(i);

                View child = inflater.inflate(R.layout.prediction_lv_item, null);
                layout.addView(child);

                TextView getOrTv = (TextView) child.findViewById(R.id.getOr_text_view);
                TextView gradeToGetTv = (TextView) child.findViewById(R.id.grade_to_get_text_view);
                TextView fromTv = (TextView) child.findViewById(R.id.from_text_view);
                TextView gradeCategoryTv = (TextView) child.findViewById(R.id.grade_category_text_view);

                if (position == resources.indexOf(String.valueOf((int) Math.round(Double.parseDouble(subjectData.getAverage()))))) {
                    gradeCategoryTv.setText(R.string.you_are_here);

                    getOrTv.setText("");
                    gradeToGetTv.setText("");
                    fromTv.setText("");
                }
                else if (prediction.get(position).get(i).size() == 0) {
                    if (!added && i == prediction.get(position).size() - 1) {

                        gradeCategoryTv.setText(R.string.no_way);

                        getOrTv.setText("");
                        gradeToGetTv.setText("");
                        fromTv.setText("");
                    }
                    else {
                        layout.removeViewAt(i);
                    }
                }
                else {
                    String gradeToGet;
                    if (prediction.get(position).get(i).size() == 1){
                        gradeToGet = prediction.get(position).get(i).get(0);

                    }else {
                        gradeToGet = prediction.get(position).get(i).get(0) + " - "
                                + prediction.get(position).get(i).get(prediction.get(position).get(i).size() - 1);
                    }

                    if (added) getOrTv.setText(R.string.or);
                    else getOrTv.setText(R.string.get);

                    gradeToGetTv.setText(gradeToGet);

                    if (subjectData.isUseCategories()) {
                        gradeCategoryTv.setText(category);
                        fromTv.setText(R.string.from);
                    } else {
                        gradeCategoryTv.setText("");
                        fromTv.setText("");
                    }

                    added = true;
                }
            }

            hideLoading();

            return convertView;
        }
    }
}