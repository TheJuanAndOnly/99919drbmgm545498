package com.thejuanandonly.schoolapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;

public class ExpandableListAdapterDone extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> taskWhat;
    private List<String> taskName;

    public ExpandableListAdapterDone(Activity context, List<String> taskName, Map<String, List<String>> taskWhat) {
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
            convertView = inflater.inflate(R.layout.child_item_done, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.taskWhat);

        item.setText(TaskWhat);

        ImageView imgDelete = (ImageView) convertView.findViewById(R.id.imgDelete);
        ImageView imgRestore = (ImageView) convertView.findViewById(R.id.imgRestore);

        imgDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefsDone = context.getSharedPreferences("ListOfDoneTasks", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorDone = prefsDone.edit();
                JSONArray arrayNameDone, arrayWhatDone;

                int numberOfDoneTask = prefsDone.getInt("NumberOfDoneTask", 0);
                numberOfDoneTask--;

                try {
                    arrayNameDone = new JSONArray(prefsDone.getString("DoneTaskName", null));
                    arrayWhatDone = new JSONArray(prefsDone.getString("DoneTaskWhat", null));
                } catch (Exception e) {
                    arrayNameDone = new JSONArray();
                    arrayWhatDone = new JSONArray();
                }

                ArrayList<String> listNameDone = new ArrayList<String>();
                for (int i = 0; i < arrayNameDone.length(); i++){
                    try {
                        listNameDone.add(arrayNameDone.get(i).toString());
                    } catch (Exception e) {
                    }
                }

                listNameDone.remove(groupPosition);
                arrayNameDone = new JSONArray(listNameDone);

                ArrayList<String> listWhatDone = new ArrayList<String>();
                if (arrayWhatDone != null) {
                    int len = arrayWhatDone.length();
                    for (int i = 0; i < len ; i++){
                        try {
                            listWhatDone.add(arrayWhatDone.get(i).toString());
                        } catch (Exception e) {
                        }
                    }
                }
                listWhatDone.remove(groupPosition);
                arrayWhatDone = new JSONArray(listWhatDone);

                editorDone.putString("DoneTaskName", arrayNameDone.toString()).apply();
                editorDone.putString("DoneTaskWhat", arrayWhatDone.toString()).apply();
                editorDone.putInt("NumberOfDoneTask", numberOfDoneTask).apply();
                editorDone.commit();

                ((MainActivity) context).updateListTasks();
            }
        });

        imgRestore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
                SharedPreferences prefsDone = context.getSharedPreferences("ListOfDoneTasks", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                SharedPreferences.Editor editorDone = prefsDone.edit();
                JSONArray arrayName, arrayWhat, arrayTime, arrayNameDone, arrayWhatDone, arrayTimeDone;

                int numberOfTask = prefs.getInt("NumberOfTask", 0);
                numberOfTask++;

                int numberOfDoneTask = prefsDone.getInt("NumberOfDoneTask", 0);
                numberOfDoneTask--;

                try {
                    arrayName = new JSONArray(prefs.getString("TaskName", null));
                    arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
                    arrayTime = new JSONArray(prefs.getString("TaskTime", null));
                } catch (Exception e) {
                    arrayName = new JSONArray();
                    arrayWhat = new JSONArray();
                    arrayTime = new JSONArray();
                }

                try {
                    arrayNameDone = new JSONArray(prefsDone.getString("DoneTaskName", null));
                    arrayWhatDone = new JSONArray(prefsDone.getString("DoneTaskWhat", null));
                    arrayTimeDone = new JSONArray(prefsDone.getString("DoneTaskTime", null));
                } catch (Exception e) {
                    arrayNameDone = new JSONArray();
                    arrayWhatDone = new JSONArray();
                    arrayTimeDone = new JSONArray();
                }

                String stringName = "";
                String[] stringWhat = {};
                long timing = 0;

                try {
                    stringName = arrayNameDone.getString(groupPosition);
                    stringWhat = new String[]{arrayWhatDone.getString(groupPosition)};
                    timing = arrayTimeDone.getLong(groupPosition);
                } catch (Exception e) {
                }

                ArrayList<String> listNameDone = new ArrayList<String>();
                for (int i = 0; i < arrayNameDone.length(); i++){
                    try {
                        listNameDone.add(arrayNameDone.get(i).toString());
                    } catch (Exception e) {
                    }
                }

                listNameDone.remove(groupPosition);
                arrayNameDone = new JSONArray(listNameDone);

                ArrayList<String> listWhatDone = new ArrayList<String>();
                if (arrayWhatDone != null) {
                    int len = arrayWhatDone.length();
                    for (int i = 0; i < len ; i++){
                        try {
                            listWhatDone.add(arrayWhatDone.get(i).toString());
                        } catch (Exception e) {
                        }
                    }
                }
                listWhatDone.remove(groupPosition);
                arrayWhatDone = new JSONArray(listWhatDone);

                ArrayList<Long> listTimeDone = new ArrayList<Long>();
                for (int i = 0; i < arrayTimeDone.length(); i++){
                    try {
                        listTimeDone.add(arrayTimeDone.getLong(i));
                    } catch (Exception e) {
                    }
                }
                listTimeDone.remove(groupPosition);
                arrayTimeDone = new JSONArray(listTimeDone);

                String stringWhatDone = stringWhat[0];

                arrayName.put(stringName);
                arrayWhat.put(stringWhatDone);
                arrayTime.put(timing);

                editor.putString("TaskName", arrayName.toString()).apply();
                editor.putString("TaskWhat", arrayWhat.toString()).apply();
                editor.putString("TaskTime", arrayTime.toString()).apply();
                editor.putInt("NumberOfTask", numberOfTask).apply();
                editor.commit();

                editorDone.putString("DoneTaskName", arrayNameDone.toString()).apply();
                editorDone.putString("DoneTaskWhat", arrayWhatDone.toString()).apply();
                editorDone.putString("DoneTaskTime", arrayTimeDone.toString()).apply();
                editorDone.putInt("NumberOfDoneTask", numberOfDoneTask).apply();
                editorDone.commit();

                ((MainActivity) context).updateListTasks();
                ((MainActivity) context).updateNotification();
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
            convertView = infalInflater.inflate(R.layout.group_item_done, null);
        }
        ImageView img = (ImageView) convertView.findViewById(R.id.imgIndicatorGroup);
        if (isExpanded == false) {
            img.setBackground(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_24dp));
        } else if (isExpanded == true) {
            img.setBackground(context.getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp));
        }

        TextView item = (TextView) convertView.findViewById(R.id.taskName);
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