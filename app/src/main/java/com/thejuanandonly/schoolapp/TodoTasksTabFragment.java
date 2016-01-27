package com.thejuanandonly.schoolapp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;


import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;

public class TodoTasksTabFragment extends Fragment {

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> taskWhat;
    ExpandableListView expListView;

    public SharedPreferences prefs;
    public JSONArray arrayName, arrayWhat, arrayTime;
    public int numberOfTask;
    private Tracker mTracker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.todo_tasks_layout, null);

        createGroupList();
        createCollection();

        expListView = (ExpandableListView) view.findViewById(R.id.taskList);
        final ExpandableListAdapter expListAdapter = new ExpandableListAdapter(getActivity(), groupList, taskWhat);
        expListView.setAdapter(expListAdapter);

        return view;
    }

    private void createGroupList() {
        groupList = new ArrayList<String>();
    }

    private void createCollection() {
        taskWhat = new LinkedHashMap<String, List<String>>();
    }

    private void loadChild(String[] tasksWhat) {
        childList = new ArrayList<String>();
        for (String taskWhat : tasksWhat)
            childList.add(taskWhat);
    }

    @Override
    public void onResume() {
        prefs = getActivity().getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        numberOfTask = prefs.getInt("NumberOfTask", 0);

        try {
            arrayName = new JSONArray(prefs.getString("TaskName", null));
            arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
            arrayTime = new JSONArray(prefs.getString("TaskTime", null));
        } catch (Exception e) {
            arrayName = new JSONArray();
            arrayWhat = new JSONArray();
            arrayTime = new JSONArray();
        }

        String stringName = "";
        String[] stringWhat = {};

        groupList.clear();

        for (int taskToShow = 0; taskToShow < numberOfTask; taskToShow++) {
            try {
                stringName = arrayName.getString(taskToShow);
                stringWhat = new String[]{arrayWhat.getString(taskToShow)};
            } catch (Exception e) {
            }

            groupList.add(stringName);
            loadChild(stringWhat);
            taskWhat.put(stringName, childList);

        }

        super.onResume();
    }

}