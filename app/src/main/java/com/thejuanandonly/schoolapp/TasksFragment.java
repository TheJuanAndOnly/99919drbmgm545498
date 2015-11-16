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

public class TasksFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2 ;
    android.support.v7.widget.Toolbar toolbar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View x =  inflater.inflate(R.layout.tasks_layout, null);

        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Tasks");

        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });

        theme();
        return x;
    }

    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
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

            return int_items;

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
            case 1:

                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.orange));
                tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.orange));

                break;
            case 2:

                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.green));
                tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.green));

                break;
            case 3:

                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue));
                tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.blue));

                break;
            case 4:

                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.grey));
                tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.grey));

                break;
            case 5:

                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.teal));
                tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.teal));

                break;
            case 6:

                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.brown));
                tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.brown));

                break;
            default:

                tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.red));
                tabLayout.setTabTextColors(getResources().getColor(R.color.white), getResources().getColor(R.color.red));
        }

    }

}

