package com.thejuanandonly.schoolapp;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



/**
 * Created by Daniel on 1/10/2016.
 */
public class SupportFragment extends android.support.v4.app.Fragment {

    Toolbar toolbar;
    Button bugReport, feedback;
    ImageView bug;
    TextView longText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Support");
        toolbar.setBackgroundColor(getResources().getColor(R.color.mainblue));

        ImageView img = (ImageView) getActivity().findViewById(R.id.overviewImg);
        img.setVisibility(View.GONE);

        TextView quote = (TextView) getActivity().findViewById(R.id.quote);
        quote.setVisibility(View.GONE);

        TextView author = (TextView) getActivity().findViewById(R.id.author);
        author.setVisibility(View.GONE);

        View rootView = inflater.inflate(R.layout.support_fragment_layout, null);

        bug = (ImageView) rootView.findViewById(R.id.HugeBug);
        longText = (TextView) rootView.findViewById(R.id.longText);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;


        if (height <= 853) {
            bug.getLayoutParams().width = 180;
            bug.getLayoutParams().height = 180;
            bug.requestLayout();

            longText.getLayoutParams().width = 340;
            longText.requestLayout();
        }


        bugReport = (Button) rootView.findViewById(R.id.bug_report);
        feedback = (Button) rootView.findViewById(R.id.feedback);

        Display display = getActivity().getWindowManager().getDefaultDisplay();

        String s = "Hardware info:";
        s += "\n    Model: " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";
        s += "\n    API Level: " + android.os.Build.VERSION.SDK_INT;
        s += "\n    Screen: " + display.getWidth() + "x" + display.getHeight();
        s += "\n";
        s += "\n --------YOUR MESSAGE HERE--------";

        final String finalS = s;

        bugReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","gradeday.team@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "BUG REPORT");
                emailIntent.putExtra(Intent.EXTRA_TEXT, finalS);
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","gradeday.team@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FEEDBACK");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));

            }
        });

        return rootView;
    }
}
