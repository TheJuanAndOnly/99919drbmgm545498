package com.thejuanandonly.schoolapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Chronometer;
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

        ImageView imgEdit = (ImageView) convertView.findViewById(R.id.imgEdit);
        ImageView imgDone = (ImageView) convertView.findViewById(R.id.imgDone);

        imgEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
                JSONArray arrayName, arrayWhat, arrayTime;

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
                long time = 0;

                try {
                    stringName = arrayName.getString(groupPosition);
                    stringWhat = new String[]{arrayWhat.getString(groupPosition)};
                    time = arrayTime.getLong(groupPosition);
                } catch (Exception e) {
                }

                Intent TaskAdderActivity = new Intent(context, TaskAdder.class);
                TaskAdderActivity.putExtra("EditTaskName", stringName);
                TaskAdderActivity.putExtra("EditTaskWhat", stringWhat[0]);
                TaskAdderActivity.putExtra("EditTaskTime", time);
                context.startActivity(TaskAdderActivity);
            }
        });

        imgDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
                SharedPreferences prefsDone = context.getSharedPreferences("ListOfDoneTasks", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                SharedPreferences.Editor editorDone = prefsDone.edit();
                JSONArray arrayName, arrayWhat, arrayTime, arrayNameDone, arrayWhatDone, arrayTimeDone;

                numberOfTask = prefs.getInt("NumberOfTask", 0);
                numberOfTask--;

                numberOfDoneTask = prefsDone.getInt("NumberOfDoneTask", 0);
                numberOfDoneTask++;

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
                    stringName = arrayName.getString(groupPosition);
                    stringWhat = new String[]{arrayWhat.getString(groupPosition)};
                    timing = arrayTime.getLong(groupPosition);
                } catch (Exception e) {
                }

                ArrayList<String> listName = new ArrayList<String>();
                for (int i = 0; i < arrayName.length(); i++){
                    try {
                        listName.add(arrayName.getString(i));
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
                            listWhat.add(arrayWhat.getString(i));
                        } catch (Exception e) {
                        }
                    }
                }
                listWhat.remove(groupPosition);
                arrayWhat = new JSONArray(listWhat);

                ArrayList<Long> listTime = new ArrayList<Long>();
                for (int i = 0; i < arrayTime.length(); i++){
                    try {
                        listTime.add(arrayTime.getLong(i));
                    } catch (Exception e) {
                    }
                }
                listTime.remove(groupPosition);
                arrayTime = new JSONArray(listTime);

                String stringWhatDone = stringWhat[0];

                arrayNameDone.put(stringName);
                arrayWhatDone.put(stringWhatDone);
                arrayTimeDone.put(timing);

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
            convertView = infalInflater.inflate(R.layout.group_item, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.taskName);
        item.setText(TaskName);

        final TextView txtTimer = (TextView) convertView.findViewById(R.id.txtTime);

        JSONArray arrayTime;
        SharedPreferences prefs = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
        try {
            arrayTime = new JSONArray(prefs.getString("TaskTime", null));
        } catch (Exception e) {
            arrayTime = new JSONArray();
        }

        long timerTime = 0;
        try {
            timerTime = arrayTime.getLong(groupPosition);
        } catch (Exception e) {
        }

        timerTime = timerTime - System.currentTimeMillis();

        int days = (int) (timerTime / (1000*60*60*24));
        int hours = (int) ((timerTime - (1000*60*60*24*days)) / (1000*60*60));
        int minutes = (int) (timerTime - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);

        new CountDownTimer(timerTime, 1000) {


            public void onTick(long millisUntilFinished) {
                int days = (int) (millisUntilFinished / (1000*60*60*24));
                int hours = (int) ((millisUntilFinished - (1000*60*60*24*days)) / (1000*60*60));
                int minutes = (int) (millisUntilFinished - (1000*60*60*24*days) - (1000*60*60*hours)) / (1000*60);

                if (millisUntilFinished > 60000) {
                    txtTimer.setText(days + "d " + hours + "h " + minutes + "m");
                } else {
                    txtTimer.setText(millisUntilFinished/1000 + "s");
                }
            }

            public void onFinish() {
                txtTimer.setText("Done!");
            }
        }.start();

        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}