package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.thejuanandonly.schoolapp.R;import org.json.JSONArray;
import org.json.JSONException;

import java.lang.Exception;import java.lang.NullPointerException;import java.lang.Override;import java.lang.String;import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Daniel on 10/31/2015.
 */
public class NotesFragment extends Fragment {

    ListView lv_notes;
    ArrayAdapter<String> m_adapter_notes;
    android.support.v7.widget.Toolbar toolbar;
    static View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notes_layout, null);
        v = rootView;

        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Notes");


        lv_notes = (ListView) rootView.findViewById(R.id.SubjectListView_Notes);


        lv_notes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                SharedPreferences arrayPrefs_notes = getActivity().getSharedPreferences("ListOfSubjectsNotes", Context.MODE_PRIVATE);
                JSONArray NotesArray = new JSONArray();
                String selected = "";
                try {
                    NotesArray = new JSONArray(arrayPrefs_notes.getString("ListNotes", null));
                    selected = NotesArray.getString(position);
                } catch (Exception e) {
                }

                Intent intent = new Intent(getActivity(), NotesDetailActivity.class);

                intent.putExtra("note", selected);
                intent.putExtra("position", position);

                try {
                    intent.putExtra("subjectNotes", NotesArray.getString(position));
                } catch (JSONException e) {
                }
                intent.putExtra("positionNotes", position);


                startActivity(intent);


            }
        });

        return rootView;
    }

    @Override
    public void onResume() {

        SharedPreferences arrayPrefs_notes = getActivity().getSharedPreferences("ListOfSubjectsNotes", Context.MODE_PRIVATE);
        JSONArray set_notes = new JSONArray();
        ArrayList<String> arrayList = new ArrayList<String>();

        try {
            try {
                set_notes = new JSONArray(arrayPrefs_notes.getString("ListNotes", null));
            } catch (Exception e) {}

            for (int i = 0; i < set_notes.length(); i++) {
                try {
                    arrayList.add(set_notes.getString(i));
                } catch (JSONException e) {}
            }

            lv_notes = (ListView) getView().findViewById(R.id.SubjectListView_Notes);
            m_adapter_notes = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.subjectadder_textview_layout, arrayList);
            lv_notes = (ListView) getView().findViewById(R.id.SubjectListView_Notes);
            lv_notes.setAdapter(m_adapter_notes);
            m_adapter_notes.notifyDataSetChanged();


        } catch (Exception e) {
            Toast.makeText(getContext(), "To add a note click on the plus button", Toast.LENGTH_SHORT).show();
        }

        super.onResume();
    }

    public static void reset(Context context){
        SharedPreferences arrayPrefs_notes = context.getSharedPreferences("ListOfSubjectsNotes", Context.MODE_PRIVATE);
        JSONArray set_notes = new JSONArray();
        ArrayList<String> arrayList = new ArrayList<String>();

        try {
            try {
                set_notes = new JSONArray(arrayPrefs_notes.getString("ListNotes", null));
            } catch (Exception e) {}

            for (int i = 0; i < set_notes.length(); i++) {
                try {
                    arrayList.add(set_notes.getString(i));
                } catch (JSONException e) {}
            }

            ListView lv_notes = (ListView) v.findViewById(R.id.SubjectListView_Notes)
                    ;
            ArrayAdapter m_adapter_notes = new ArrayAdapter<String>(context, R.layout.subjectadder_textview_layout, arrayList);

            lv_notes.setAdapter(m_adapter_notes);
            m_adapter_notes.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(context, "To add a note click on the plus button", Toast.LENGTH_SHORT).show();
        }
    }
}