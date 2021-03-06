package com.thejuanandonly.gradeday;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

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

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4648715887566496~3996876969");
        AdView mAdView = (AdView) rootView.findViewById(R.id.adViewBannerOverview);

        if (hasNetworkConnection()) {
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            mAdView.setVisibility(View.GONE);
        }

        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        toolbar = (android.support.v7.widget.Toolbar) rootView.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        toolbar.setTitle("Overview");
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(getActivity(), ((MainActivity) getActivity()).mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        ((MainActivity) getActivity()).mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        ImageView img = (ImageView) rootView.findViewById(R.id.overviewImg);
        img.setVisibility(View.VISIBLE);

        TextView quote = (TextView) rootView.findViewById(R.id.quote);
        quote.setVisibility(View.VISIBLE);

        TextView author = (TextView) rootView.findViewById(R.id.author);
        author.setVisibility(View.VISIBLE);


        setQuote(rootView);

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
                } catch (Exception e) {
                    arrayOfSubjects = new JSONArray();
                }

                Intent intent = new Intent(getActivity(), SubjectDetailActivity.class);
                intent.putExtra("subject", subject);
                intent.putExtra("position", position);
                startActivity(intent);

            }
        });

        return rootView;
    }

    private boolean hasNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onDestroy() {
        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(getActivity(), ((MainActivity) getActivity()).mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        ((MainActivity) getActivity()).mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        super.onDestroy();
    }

    @Override
    public void onResume() {

        ArrayList<Subject> arrayOfSubjects = Subject.getSubjects(getContext());

        CustomSubjectAdapter adapter = new CustomSubjectAdapter(getContext(), arrayOfSubjects);

        ListView listView = (ListView) getView().findViewById(R.id.SubjectListView);
        listView.setAdapter(adapter);

        super.onResume();
    }

    public static void reset(Context context) {
        ArrayList<Subject> arrayOfSubjects = Subject.getSubjects(context);

        CustomSubjectAdapter adapter = new CustomSubjectAdapter(context, arrayOfSubjects);

        ListView listView = (ListView) v.findViewById(R.id.SubjectListView);
        listView.setAdapter(adapter);
    }

    public void setQuote(final View view) {
        TextView quoteTv = (TextView) view.findViewById(R.id.quote);
        TextView authorTv = (TextView) view.findViewById(R.id.author);

        final String[] quotes = getResources().getStringArray(R.array.quotes);
        final String[] authors = getResources().getStringArray(R.array.authors);

        int rnd = (int) (Math.random() * 27);

        quoteTv.setText(quotes[rnd]);
        authorTv.setText(authors[rnd]);

        if (quotes[rnd].length() >= 120) {
            quoteTv.setTextSize(16);
            authorTv.setTextSize(14);
        } else {
            quoteTv.setTextSize(18);
            authorTv.setTextSize(16);
        }
    }
}