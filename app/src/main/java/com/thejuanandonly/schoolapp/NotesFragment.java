package com.thejuanandonly.schoolapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
    android.support.v7.widget.Toolbar toolbar;
    static View v;
    static ArrayList<String> arrayList;
    ArrayAdapter m_adapter_notes;
    JSONArray jsonArray, jsonArrayName;
    String subCategoryName, newSubCategoryName, subCategoryNameRename;
    String selected = "";

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

                try {
                    NotesArray = new JSONArray(arrayPrefs_notes.getString("ListNotes", null));
                    selected = NotesArray.getString(position);
                } catch (Exception e) {
                }


                Intent intent = new Intent(getActivity(), PictureGroupActivity.class);

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

        registerForContextMenu(lv_notes);

        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int id = info.position;
        final TextView textView = (TextView) getView().findViewById(android.R.id.text1);
        final ArrayList<String> arrayListRename = new ArrayList<>();
        final ArrayList<String> arrayListItemName = new ArrayList<>();
        JSONArray array = new JSONArray();
        ArrayList<String> arrayList = new ArrayList<>();

        textView.getText();

        SharedPreferences preferences = getActivity().getSharedPreferences("ListOfSubjectsNotes", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        SharedPreferences preferences_notes = getActivity().getSharedPreferences("SubjectNotes", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editorNotes = preferences_notes.edit();

        switch (item.getItemId()) {

            case R.id.rename:


                final AlertDialog.Builder alert = new AlertDialog.Builder(getView().getContext());
                alert.setTitle("Rename");
                final EditText editText = new EditText(getActivity().getApplicationContext());
                editText.setTextColor(Color.BLACK);
                alert.setView(editText);

                jsonArray = new JSONArray();
                jsonArrayName = new JSONArray();

                try {

                    jsonArray = new JSONArray(preferences.getString("ListNotes", jsonArray.toString()));
                    subCategoryName = jsonArray.getString(id);

                    jsonArrayName = new JSONArray(preferences_notes.getString("subjectNotes", jsonArrayName.toString()));
                    subCategoryNameRename = jsonArrayName.getString(id);


                } catch (Exception e) { }

                editText.setText(subCategoryName);

                for(int i = 0; i < jsonArrayName.length(); i++) {
                    try {
                        arrayListItemName.add(jsonArrayName.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        arrayListRename.add(jsonArray.get(i).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        newSubCategoryName = editText.getText().toString();

                        subCategoryName = newSubCategoryName;
                        subCategoryNameRename = subCategoryName;

                        arrayListRename.remove(id);
                        arrayListRename.add(id, newSubCategoryName);

                        Toast.makeText(getActivity().getApplicationContext(), "Updated text: " + subCategoryName, Toast.LENGTH_SHORT).show();


                        editorNotes.putString("subjectNotes", subCategoryNameRename).apply();
                        editor.putString("ListNotes", arrayListRename.toString()).apply();
                        editor.apply();

                        m_adapter_notes.notifyDataSetChanged();
                        onResume();
                    }
                });

                alert.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alert.show();
                return true;


            case R.id.delete:

                try {
                    array = new JSONArray(preferences.getString("ListNotes", null));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                for(int i = 0; i < array.length(); i++ ) {

                    try {
                        arrayList.add(array.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                arrayList.remove(id);
                m_adapter_notes.notifyDataSetChanged();

                editor.putString("ListNotes", arrayList.toString()).apply();

                onResume();

                return true;

            case R.id.cancel:

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onResume() {

        SharedPreferences arrayPrefs_notes = getActivity().getSharedPreferences("ListOfSubjectsNotes", Context.MODE_PRIVATE);
        JSONArray set_notes = new JSONArray();
        arrayList = new ArrayList<String>();

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
        arrayList = new ArrayList<String>();

        try {
            try {
                set_notes = new JSONArray(arrayPrefs_notes.getString("ListNotes", null));
            } catch (Exception e) {}

            for (int i = 0; i < set_notes.length(); i++) {
                try {
                    arrayList.add(set_notes.getString(i));
                } catch (JSONException e) {}
            }

            ListView lv_notes = (ListView) v.findViewById(R.id.SubjectListView_Notes);
            ArrayAdapter m_adapter_notes = new ArrayAdapter<String>(context, R.layout.subjectadder_textview_layout, arrayList);

            lv_notes.setAdapter(m_adapter_notes);
            m_adapter_notes.notifyDataSetChanged();

        } catch (Exception e) {
            Toast.makeText(context, "To add a note click on the plus button", Toast.LENGTH_SHORT).show();
        }
    }
}