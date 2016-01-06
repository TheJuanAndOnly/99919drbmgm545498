package com.thejuanandonly.schoolapp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.json.JSONArray;

public class DoneTasksTabFragment extends Fragment {

    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> taskWhat;
    ExpandableListView expListView;
    public static ExpandableListAdapterDone expListAdapterDone;

    public SharedPreferences prefs;
    public JSONArray arrayName;
    public JSONArray arrayWhat;
    public int numberOfTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.done_tasks_layout, null);

        createGroupList();
        createCollection();

        expListView = (ExpandableListView) view.findViewById(R.id.taskList);
        expListAdapterDone = new ExpandableListAdapterDone(getActivity(), groupList, taskWhat);
        expListView.setAdapter(expListAdapterDone);

        setGroupIndicatorToRight();

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

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        expListView.setIndicatorBounds(width - getDipsFromPixel(35), width - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onResume() {
        prefs = getActivity().getSharedPreferences("ListOfDoneTasks", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        numberOfTask = prefs.getInt("NumberOfDoneTask", 0);

        try {
            arrayName = new JSONArray(prefs.getString("DoneTaskName", null));
            arrayWhat = new JSONArray(prefs.getString("DoneTaskWhat", null));
        } catch (Exception e) {
            arrayName = new JSONArray();
            arrayWhat = new JSONArray();
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
