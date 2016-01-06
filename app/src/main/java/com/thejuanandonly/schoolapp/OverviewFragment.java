package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class OverviewFragment extends Fragment {

    ListView lv;
    android.support.v7.widget.Toolbar toolbar;
    static View v;
    public static Context overviewFragmentContext;
    String username;
    String imageUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.overview_layout, null);
        v = rootView;
        overviewFragmentContext = getContext();

        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Overview");



        //TEST NA USERNAME A PHOTO Z LOGINU ↓ Môžeš mazať :D
        try {
            username = getActivity().getIntent().getExtras().getString("nickname");
            Toast.makeText(getActivity().getApplicationContext(), "the username is: " + username, Toast.LENGTH_SHORT).show();
        }catch (NullPointerException e) {
            Toast.makeText(getActivity().getApplicationContext(), "No username is selected", Toast.LENGTH_SHORT).show();
        }

        try {
            imageUri = getActivity().getIntent().getExtras().getString("avatar");
            Toast.makeText(getActivity().getApplicationContext(), "the imageUri is: " + imageUri, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(getActivity().getApplicationContext(), "No photo was chosen", Toast.LENGTH_SHORT).show();
        }




        lv = (ListView) rootView.findViewById(R.id.SubjectListView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {

                SharedPreferences arrayPrefs = getActivity().getSharedPreferences("ListOfSubjects", Context.MODE_PRIVATE);
                JSONArray arrayOfSubjects;
                String subject = "";
                try {
                    arrayOfSubjects = new JSONArray(arrayPrefs.getString("List", null));
                    subject = arrayOfSubjects.getString(position);
                }catch (Exception e) {arrayOfSubjects = new JSONArray();}

                Intent intent = new Intent(getActivity(), SubjectDetailActivity.class);
                intent.putExtra("subject", subject);
                intent.putExtra("position", position);
                startActivity(intent);

            }
        });

        return rootView;
    }

    @Override
    public void onResume() {

        ArrayList<Subject> arrayOfSubjects = Subject.getSubjects();

        CustomSubjectAdapter adapter = new CustomSubjectAdapter(getContext(), arrayOfSubjects);

        ListView listView = (ListView) getView().findViewById(R.id.SubjectListView);
        listView.setAdapter(adapter);

        super.onResume();
    }

    public static void reset(Context context){
        ArrayList<Subject> arrayOfSubjects = Subject.getSubjects();

        CustomSubjectAdapter adapter = new CustomSubjectAdapter(context, arrayOfSubjects);

        ListView listView = (ListView) v.findViewById(R.id.SubjectListView);
        listView.setAdapter(adapter);
    }
}