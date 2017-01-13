package com.thejuanandonly.schoolapp;

import android.app.Activity;
import android.content.Context;
import android.test.mock.MockContext;
import android.util.Log;
import android.util.Pair;
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
class PredictionListViewImplementor implements Runnable {

    private static final String TAG = "GD PredictionListView";

    private Context context;
    private SubjectData subjectData;
    private ListView listView;

    private ProgressBar progress;

    PredictionListViewImplementor(Context context, SubjectData subjectData, ListView listView) {
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

        Pair<List<String>, List<List<List<String>>>> result;

        if (subjectData.getGradeType() == SubjectData.PERCENTAGE && subjectData.getTestsToWrite() > 1){
            result = compactDisplay(gradesInCategories);
        }
        else {
            result = regularDisplay(gradesInCategories);
        }

        if (result == null) {
            return;
        }

        setAdapter(result.first, result.second);
    }

    private double countAverage(List<List<String>> gradesInCategories) {

        List<Double> averages = new ArrayList<>(gradesInCategories.size());

        for (int i = 0; i < gradesInCategories.size(); i++) {
            List<String> grades = gradesInCategories.get(i);
            averages.add(0.0);

            for (String grade : grades) {
                if (subjectData.getGradeType() == SubjectData.ALPHABETIC) {

                    try {
                        averages.set(i, averages.get(i) + SubjectData.letterToNumber(grade));
                    }catch (IndexOutOfBoundsException e){
                        averages.add(SubjectData.letterToNumber(grade));
                    }
                    continue;
                }

                try {
                    averages.set(i, averages.get(i) + Double.parseDouble(grade));
                }catch (IndexOutOfBoundsException e){
                    try{
                        averages.add(Double.parseDouble(grade));
                    }catch (NumberFormatException e1){
                        e1.printStackTrace();
                    }
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

        int i = numElem == 0 ? 0 : grades.indexOf(branch.get(numElem -1));
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

    private List<List<String>> contract(List<List<String>> prediction){

        List<List<String>> result = new ArrayList<>();
        result.add(new ArrayList<>(prediction.get(0)));

        for (int i = 1, predictionSize = prediction.size(); i < predictionSize; i++) {
            List<String> item = prediction.get(i);

            for (int j = 0, itemSize = item.size(); j < itemSize; j++) {
                String grade = item.get(j);

                if (grade.equals(result.get(result.size() - 1).get(j))) continue;

                if (j != itemSize - 1){
                    result.add(new ArrayList<>(item));
                    break;
                }

                result.get(result.size() - 1).add(grade);
            }
        }

        return result;
    }

    Pair<List<String>, List<List<List<String>>>> regularDisplay(List<List<String>> gradesInCategories){
        List<String> gradesToAdapter;
        List<String> gradesToPredict;
        switch (subjectData.getGradeType()){
            default:
                gradesToAdapter = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
                gradesToPredict = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
                break;

            case SubjectData.PERCENTAGE:
                gradesToAdapter = new ArrayList<>(
                        Arrays.asList(
                                subjectData.getPercentageConversion().get(0).toString(),
                                subjectData.getPercentageConversion().get(1).toString(),
                                subjectData.getPercentageConversion().get(2).toString(),
                                subjectData.getPercentageConversion().get(3).toString(),
                                subjectData.getPercentageConversion().get(4).toString()
                        )
                );

                gradesToPredict = new ArrayList<>(101);
                for (int i = 0; i < 101; i++){
                    gradesToPredict.add(String.valueOf(i));
                }

                break;

            case SubjectData.ALPHABETIC:
                gradesToAdapter = new ArrayList<>(Arrays.asList("4", "3", "2", "1", "0"));

                gradesToPredict = new ArrayList<>(13);
                for (int i = 433; i >= 0;){
                    gradesToPredict.add(SubjectData.numberToLetter(((double) i) / 100.0));

                    if (i == 67) {
                        i = 0;
                    } else if (String.valueOf(i).endsWith("67")) {
                        i -= 34;
                    } else {
                        i -= 33;
                    }
                }

                break;

            case SubjectData.TEN_GRADE:
                gradesToAdapter = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
                gradesToPredict = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
                break;
        }

        List<List<String>> gradesToBeAdded = new ArrayList<>(
                combine(gradesToPredict, subjectData.getTestsToWrite(), new ArrayList<String>(), 0, new ArrayList<List<String>>())
        );

        if (Thread.interrupted()){
            Log.i(TAG, "Thread interrupted");
            return null;
        }

        List<List<List<List<String>>>> prediction = new ArrayList<>(gradesToAdapter.size());
        List<List<List<String>>> output = new ArrayList<>(gradesToAdapter.size());
        for (int i = 0; i < gradesToAdapter.size(); i++){
            prediction.add(new ArrayList<List<List<String>>>());

            for (int j = 0; j < subjectData.getArrayOfCategories().size(); j++){
                prediction.get(i).add(new ArrayList<List<String>>());
            }
        }

        //counting averages
        for (int i = 0; i < gradesInCategories.size(); i++){
            for (int j = 0; j < gradesToBeAdded.size(); j++){
                gradesInCategories.get(i).addAll(gradesToBeAdded.get(j));

                double avgAfter = countAverage(gradesInCategories);

                Log.d(TAG, gradesToBeAdded.get(j) + "\n" + avgAfter);

                for (int k = 0; k < gradesToBeAdded.get(j).size(); k++){
                    gradesInCategories.get(i).remove(gradesInCategories.get(i).size() - 1);
                }

                int gradeAfter = indexToGrade(avgAfter);

                try {
                    prediction.get(gradeAfter - 1).get(i).add(gradesToBeAdded.get(j));
                } catch (IndexOutOfBoundsException e){
                    prediction.get(gradeAfter - 1).add(new ArrayList<List<String>>());
                    prediction.get(gradeAfter - 1).get(i).add(gradesToBeAdded.get(j));
                }
            }
        }

        //saving into the array passed to adapter
        for (int gradeIndex = 0; gradeIndex < prediction.size(); gradeIndex++){
            output.add(new ArrayList<List<String>>());

            for (int catIndex = 0; catIndex < prediction.get(gradeIndex).size(); catIndex++){

                List<List<String>> contractedGrades = new ArrayList<>(contract(prediction.get(gradeIndex).get(catIndex)));

                for (int optionIndex = 0, contractedGradesSize = contractedGrades.size(); optionIndex < contractedGradesSize; optionIndex++) {
                    output.get(gradeIndex).add(new ArrayList<String>());

                    if (prediction.get(gradeIndex).get(catIndex).size() == 0){
                        output.get(gradeIndex).set(0, Arrays.asList("", "", "There's no way", ""));
                        break;
                    }

                    if (catIndex == 0) {
                        output.get(gradeIndex).get(catIndex + optionIndex).add(context.getString(R.string.get));
                    }
                    else {
                        output.get(gradeIndex).get(catIndex + optionIndex).add(context.getString(R.string.or));
                    }

                    List<String> grades = contractedGrades.get(optionIndex);
                    String outputGrades = "";
                    for (int i = 0; i < grades.size(); i++) {

                        if (grades.size() == subjectData.getTestsToWrite()){
                            outputGrades += grades.get(i);

                            if (i != grades.size() - 1){
                                outputGrades += ", ";
                            }

                            continue;
                        }

                        if (i < subjectData.getTestsToWrite() - 1){
                            outputGrades += grades.get(i) + ", ";
                            continue;
                        }

                        outputGrades += grades.get(i);

                        outputGrades +=
                                subjectData.getGradeType() == SubjectData.ALPHABETIC ?
                                        " to " : " - ";

                        outputGrades += grades.get(grades.size() - 1);
                        break;
                    }

                    output.get(gradeIndex).get(catIndex + optionIndex).add(outputGrades);

                    if (subjectData.isUseCategories()){
                        output.get(gradeIndex).get(catIndex + optionIndex).add(context.getString(R.string.from));
                        output.get(gradeIndex).get(catIndex + optionIndex).add(subjectData.getArrayOfCategories().get(catIndex));
                    }
                    else {
                        output.get(gradeIndex).get(catIndex + optionIndex).add("");
                        output.get(gradeIndex).get(catIndex + optionIndex).add("");
                    }

                }
            }
        }


        if (Thread.interrupted()){
            Log.i(TAG, "Thread interrupted");
            return null;
        }

        return new Pair<>(gradesToAdapter, output);
    }

    Pair<List<String>, List<List<List<String>>>> compactDisplay(List<List<String>> gradesInCategories){
        List<List<String>> ranges = new ArrayList<>(5);

        int min = 0;
        final int max = subjectData.getTestsToWrite() * 100;
        int previous = max;

        for (int i = 0; i < subjectData.getPercentageConversion().size() - 1; i++) {
            ranges.add(new ArrayList<String>());

            for (int j = 0; j < gradesInCategories.size(); j++) {

                boolean valid = false;

                int perc;
                for (perc = min; perc <= max; perc += (int) ((double) max) * 0.1){

                    gradesInCategories.get(j).add(String.valueOf(perc));
                    for (int k = 1; k < subjectData.getTestsToWrite(); k++){
                        gradesInCategories.get(j).add("0");
                    }

                    double avg = countAverage(gradesInCategories);

                    for (int k = 0; k < subjectData.getTestsToWrite(); k++){
                        gradesInCategories.get(j).remove(gradesInCategories.get(j).size() - 1);
                    }

                    if ((int) Math.round(avg) >= subjectData.getPercentageConversion().get(i)){
                        valid = true;
                        break;
                    }
                }

                if (!valid){
                    ranges.get(i).add("");
                    break;
                }

                for (; perc >= 0; perc--){

                    gradesInCategories.get(j).add(String.valueOf(perc));
                    for (int k = 1; k < subjectData.getTestsToWrite(); k++){
                        gradesInCategories.get(j).add("0");
                    }

                    double avg = countAverage(gradesInCategories);

                    for (int k = 0; k < subjectData.getTestsToWrite(); k++){
                        gradesInCategories.get(j).remove(gradesInCategories.get(j).size() - 1);
                    }

                    if ((int) Math.round(avg) < subjectData.getPercentageConversion().get(i)) break;
                }

                min = perc + 1;

                if (previous == max) {
                    ranges.get(i).add(previous + " - " + min);
                }else {
                    ranges.get(i).add((previous - 1) + " - " + min);
                }

                previous = min;
            }
        }
        ranges.add(new ArrayList<String>());
        if (previous != 0) {
            ranges.get(ranges.size() - 1).add((previous - 1) + " - 0");
        }else {
            ranges.get(ranges.size() - 1).add("");
        }

        Log.d(TAG, ranges.toString());

        List<String> gradesToAdapter = new ArrayList<>(
                Arrays.asList(
                        subjectData.getPercentageConversion().get(0).toString(),
                        subjectData.getPercentageConversion().get(1).toString(),
                        subjectData.getPercentageConversion().get(2).toString(),
                        subjectData.getPercentageConversion().get(3).toString(),
                        subjectData.getPercentageConversion().get(4).toString()
                )
        );

        List<List<List<String>>> output = new ArrayList<>(subjectData.getPercentageConversion().size());

        if (Thread.interrupted()){
            Log.i(TAG, "Thread interrupted");
            return null;
        }

        //saving into the array passed to adapter
        for (int gradeIndex = 0; gradeIndex < ranges.size(); gradeIndex++){
            output.add(new ArrayList<List<String>>());

            for (int catIndex = 0; catIndex < ranges.get(gradeIndex).size(); catIndex++){

                output.get(gradeIndex).add(new ArrayList<String>());

                if (ranges.get(gradeIndex).get(catIndex).isEmpty()){
                    output.get(gradeIndex).set(0, Arrays.asList("", "", "There's no way", ""));
                    break;
                }

                output.get(gradeIndex).get(catIndex).add(context.getString(R.string.get_a_sum_of));

                output.get(gradeIndex).get(catIndex).add(ranges.get(gradeIndex).get(catIndex));

                if (subjectData.isUseCategories()){
                    output.get(gradeIndex).get(catIndex).add(context.getString(R.string.from));
                    output.get(gradeIndex).get(catIndex).add(subjectData.getArrayOfCategories().get(catIndex));
                }
                else {
                    output.get(gradeIndex).get(catIndex).add("");
                    output.get(gradeIndex).get(catIndex).add("");
                }
            }
        }

        if (Thread.interrupted()){
            Log.i(TAG, "Thread interrupted");
            return null;
        }

        Log.d(TAG, output.toString());

        return new Pair<>(gradesToAdapter, output);
    }

    private int indexToGrade(double index){
        switch (subjectData.getGradeType()){
            default:
                return (int) Math.round(index);

            case SubjectData.PERCENTAGE:
                for (int i = 0; i < subjectData.getPercentageConversion().size(); i++){
                    if (index >= subjectData.getPercentageConversion().get(i)) {
                        return i + 1;
                    }
                }
                return 5;

            case SubjectData.ALPHABETIC:
                return 5 - (int) Math.round(index);

            case SubjectData.TEN_GRADE:
                return (int) Math.round(index);
        }
    }

    private void setAdapter(final List<String> gradesToAdapter, final List<List<List<String>>> prediction){

        final PredictionAdapter predictionAdapter = new PredictionAdapter(context, gradesToAdapter, prediction);

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(predictionAdapter);

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

    private void setTotalHeightOfListView(ListView listView) {

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

        PredictionAdapter(Context context, List<String> resource, List<List<List<String>>> output) {
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

            if (subjectData.getGradeType() == SubjectData.PERCENTAGE){
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