package com.thejuanandonly.schoolapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class TasksFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_tabs = 2 ;
    android.support.v7.widget.Toolbar toolbar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.tasks_layout, null);

        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Tasks");
        toolbar.setBackgroundColor(getResources().getColor(R.color.mainblue));

        ImageView img = (ImageView) getActivity().findViewById(R.id.overviewImg);
        img.setVisibility(View.GONE);

        TextView quote = (TextView) getActivity().findViewById(R.id.quote);
        quote.setVisibility(View.GONE);

        TextView author = (TextView) getActivity().findViewById(R.id.author);
        author.setVisibility(View.GONE);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        theme();
        return view;
    }

    class PagerAdapter extends FragmentPagerAdapter{

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return new TodoTasksTabFragment();
                case 1 : return new DoneTasksTabFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_tabs;

        }


        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "To do";
                case 1 :
                    return "Done";
            }
            return null;
        }
    }

    public void theme() {
        SharedPreferences prefs = getActivity().getSharedPreferences("themeSave", Context.MODE_PRIVATE);
        int theme = prefs.getInt("theme", 0);

        switch (theme){
            default:

                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));
                tabLayout.setTabTextColors(getResources().getColor(R.color.lightbluebackground), getResources().getColor(R.color.white));
        }

    }
}

