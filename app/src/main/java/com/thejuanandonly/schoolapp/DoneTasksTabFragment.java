package com.thejuanandonly.schoolapp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
        theme();
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void theme() {

        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP) window.setStatusBarColor(getResources().getColor(R.color.mainblue800));
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
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        expListView.setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(0));
    }

    public int GetPixelFromDips(float pixels) {
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
