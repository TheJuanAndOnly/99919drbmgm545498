package com.thejuanandonly.schoolapp;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
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

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, List<String>> taskWhat;
    private List<String> taskName;

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