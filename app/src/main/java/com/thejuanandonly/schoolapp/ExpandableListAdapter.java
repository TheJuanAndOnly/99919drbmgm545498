package com.thejuanandonly.schoolapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> taskWhat;
    private List<String> taskName;
    private int numberOfDoneTask, numberOfTask;

    public ExpandableListAdapter(Activity context, List<String> taskName, Map<String, List<String>> taskWhat) {
        this.context = context;
        this.taskWhat = taskWhat;
        this.taskName = taskName;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return taskWhat.get(taskName.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String TaskWhat = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.taskWhat);

        item.setText(TaskWhat);

        ImageView imgDone = (ImageView) convertView.findViewById(R.id.imgDone);
        imgDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
                SharedPreferences prefsDone = context.getSharedPreferences("ListOfDoneTasks", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                SharedPreferences.Editor editorDone = prefsDone.edit();
                JSONArray arrayName, arrayWhat,arrayNameDone, arrayWhatDone;

                numberOfTask = prefs.getInt("NumberOfTask", 0);
                numberOfTask--;

                numberOfDoneTask = prefsDone.getInt("NumberOfDoneTask", 0);
                numberOfDoneTask++;

                try {
                    arrayName = new JSONArray(prefs.getString("TaskName", null));
                    arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
                } catch (Exception e) {
                    arrayName = new JSONArray();
                    arrayWhat = new JSONArray();
                }

                try {
                    arrayNameDone = new JSONArray(prefsDone.getString("DoneTaskName", null));
                    arrayWhatDone = new JSONArray(prefsDone.getString("DoneTaskWhat", null));
                } catch (Exception e) {
                    arrayNameDone = new JSONArray();
                    arrayWhatDone = new JSONArray();
                }

                String stringName = "";
                String[] stringWhat = {};

                try {
                    stringName = arrayName.getString(groupPosition);
                    stringWhat = new String[]{arrayWhat.getString(groupPosition)};
                } catch (Exception e) {
                }

                ArrayList<String> listName = new ArrayList<String>();
                for (int i = 0; i < arrayName.length(); i++){
                    try {
                        listName.add(arrayName.get(i).toString());
                    } catch (Exception e) {
                    }
                }

                listName.remove(groupPosition);
                arrayName = new JSONArray(listName);

                ArrayList<String> listWhat = new ArrayList<String>();
                if (arrayWhat != null) {
                    int len = arrayWhat.length();
                    for (int i = 0; i < len ; i++){
                        try {
                            listWhat.add(arrayWhat.get(i).toString());
                        } catch (Exception e) {
                        }
                    }
                }
                listWhat.remove(groupPosition);
                arrayWhat = new JSONArray(listWhat);

                String stringWhatDone = stringWhat[0];

                arrayNameDone.put(stringName);
                arrayWhatDone.put(stringWhatDone);

                editor.putString("TaskName", arrayName.toString()).apply();
                editor.putString("TaskWhat", arrayWhat.toString()).apply();
                editor.putInt("NumberOfTask", numberOfTask).apply();
                editor.commit();

                editorDone.putString("DoneTaskName", arrayNameDone.toString()).apply();
                editorDone.putString("DoneTaskWhat", arrayWhatDone.toString()).apply();
                editorDone.putInt("NumberOfDoneTask", numberOfDoneTask).apply();
                editorDone.commit();

                ((MainActivity) context).refresh();
            }
        });
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return taskWhat.get(taskName.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return taskName.get(groupPosition);
    }

    public int getGroupCount() {
        return taskName.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String TaskName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.taskName);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(TaskName);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}