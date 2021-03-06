package com.thejuanandonly.gradeday;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Daniel on 10/31/2015.
 */

public class NotesFragment extends Fragment {

    android.support.v7.widget.Toolbar toolbar;
    static View v;


    //Notes
    ArrayList<String> notesGroupNames;
    ArrayList<String> notesAbout;
    ArrayList<Integer> colors;

    String[] namesArray;
    String[] aboutArray;
    Integer[] colorsArray;


    //JSON for loading
    JSONArray aboutJSONload, namesJSONload, colorsJSONload;

    //Dialog
    CustomDialog dialog;
    int color = Color.parseColor("#e74c3c");

    //Custom SharedPreferences class
    SaveSharedPreferences customSharedPreferences = new SaveSharedPreferences();

    //ListVIew Adapter and the ListView
    ListViewAdapter adapter;
    ListView listView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notes_layout, null);
        v = rootView;

        loadArrFromSP();

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4648715887566496~3996876969");
        AdView mAdView = (AdView) rootView.findViewById(R.id.adViewBannerNotes);

        if(hasNetworkConnection()) {
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else  {
            mAdView.setVisibility(View.GONE);
        }

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-4648715887566496~3996876969");

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getActivity());

        listView = (ListView) v.findViewById(R.id.listView);
        adapter = new ListViewAdapter(NotesFragment.this.getContext(), namesArray, aboutArray, colorsArray, 0);
        listView.setAdapter(adapter);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        toolbar.setBackgroundColor(Color.parseColor("#2c3e50"));
        toolbar.setPadding(3, 5, 3, 0);

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

    public void loadArrFromSP() {

        try {
            aboutJSONload = (new JSONArray(customSharedPreferences.getStringSP(NotesFragment.this.getActivity(), "JSON", "about")));
            ArrayList<String> helper = new ArrayList<>();
            for (int i = 0; i < aboutJSONload.length(); i++) {
                helper.add(aboutJSONload.get(i).toString());
            }
            aboutArray = new String[helper.size()];
            aboutArray = helper.toArray(aboutArray);
        } catch (JSONException | NullPointerException e) {
            aboutArray = new String[0];
        }

        try {
            namesJSONload = (new JSONArray(customSharedPreferences.getStringSP(NotesFragment.this.getActivity(), "JSON", "names")));
            ArrayList<String> helper = new ArrayList<>();
            for (int i = 0; i < namesJSONload.length(); i++) {
                helper.add(namesJSONload.get(i).toString());
            }
            namesArray = new String[helper.size()];
            namesArray = helper.toArray(namesArray);
        } catch (JSONException | NullPointerException e) {
            namesArray = new String[0];
        }

        try {

            String string;
            SharedPreferences preferences = this.getContext().getSharedPreferences("JSON", Context.MODE_PRIVATE);
            string = preferences.getString("colors", null);

            colorsJSONload = (new JSONArray(string));
            ArrayList<Integer> helper = new ArrayList<>();
            for (int i = 0; i < colorsJSONload.length(); i++) {
                helper.add(Integer.parseInt(colorsJSONload.get(i).toString()));
            }
            colorsArray = new Integer[helper.size()];
            colorsArray = helper.toArray(colorsArray);
        } catch (JSONException | NullPointerException e) {
            colorsArray = new Integer[0];
        }

        notesGroupNames = new ArrayList<>(Arrays.asList(namesArray));
        notesAbout = new ArrayList<>(Arrays.asList(aboutArray));
        colors = new ArrayList<>(Arrays.asList(colorsArray));

    }

    public void openDialog() {

        //Open Dialog to add NoteGroups
        dialog = new CustomDialog();
        dialog.showDialog(this.getActivity(), "ADD", "CANCEL");
        dialog.positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadArrFromSP();

                String nameFromET;
                nameFromET = dialog.name.getText().toString();
                notesGroupNames.add(nameFromET);

                String aboutFromET;
                aboutFromET = dialog.about.getText().toString();
                notesAbout.add(aboutFromET);


                if (dialog.redBool) {
                    color = Color.parseColor("#395165");
                    colors.add(color);
                } else if (dialog.greenBool) {
                    color = Color.parseColor("#395165");
                    colors.add(color);
                } else if (dialog.orangeBool) {
                    color = Color.parseColor("#395165");
                    colors.add(color);
                } else if (dialog.purpleBool) {
                    color = Color.parseColor("#395165");
                    colors.add(color);
                } else {
                    color = Color.parseColor("#395165");
                    colors.add(color);
                }


                if (namesArray.length == 0) {
                    namesArray = new String[notesGroupNames.size()];
                } else {
                }

                if (aboutArray.length == 0) {
                    aboutArray = new String[notesAbout.size()];
                } else {
                }
                if (colorsArray.length == 0) {
                    colorsArray = new Integer[colors.size()];
                } else {
                }

                namesArray = notesGroupNames.toArray(namesArray);
                aboutArray = notesAbout.toArray(aboutArray);
                colorsArray = colors.toArray(colorsArray);

                JSONArray aboutJSON = new JSONArray(Arrays.asList(aboutArray));
                JSONArray namesJSON = new JSONArray(Arrays.asList(namesArray));
                JSONArray colorsJSON = new JSONArray(Arrays.asList(colorsArray));

                customSharedPreferences.saveToSharedPreferences(NotesFragment.this.getActivity(), "JSON", "about", aboutJSON.toString());
                customSharedPreferences.saveToSharedPreferences(NotesFragment.this.getActivity(), "JSON", "names", namesJSON.toString());
                customSharedPreferences.saveToSharedPreferences(NotesFragment.this.getActivity(), "JSON", "colors", colorsJSON.toString());

                adapter = new ListViewAdapter(NotesFragment.this.getContext(), namesArray, aboutArray, colorsArray, 0);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                dialog.dialog.dismiss();
            }
        });
    }

    public void notifyAdapter() {
        adapter = new ListViewAdapter(NotesFragment.this.getContext(), namesArray, aboutArray, colorsArray, 0);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void deleteGroup(String[] aboutArray, String[] namesArray, Integer[] colorsArray) {

        this.aboutArray = aboutArray;
        this.namesArray = namesArray;
        this.colorsArray = colorsArray;

        JSONArray aboutJSON = new JSONArray(Arrays.asList(this.aboutArray));
        JSONArray namesJSON = new JSONArray(Arrays.asList(this.namesArray));
        JSONArray colorsJSON = new JSONArray(Arrays.asList(this.colorsArray));

        customSharedPreferences.saveToSharedPreferences(NotesFragment.this.getActivity(), "JSON", "about", aboutJSON.toString());
        customSharedPreferences.saveToSharedPreferences(NotesFragment.this.getActivity(), "JSON", "names", namesJSON.toString());
        customSharedPreferences.saveToSharedPreferences(NotesFragment.this.getActivity(), "JSON", "colors", colorsJSON.toString());

        adapter = new ListViewAdapter(NotesFragment.this.getContext(), namesArray, aboutArray, colorsArray, 0);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

}


class CustomDialog extends SaveSharedPreferences {

    Dialog dialog;
    Button positive, negative;
    ImageView red, green, blue, purple, orange;
    ViewGroup.LayoutParams params;

    AppCompatEditText name, about;
    String nameString, aboutString;
    int color;

    public boolean redBool, greenBool, orangeBool, purpleBool;


    public void showDialog(Activity activity, String positiveButton, String negativeButton) {
        dialog = new Dialog(activity);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        positive = (Button) dialog.findViewById(R.id.dialogPositive);
        negative = (Button) dialog.findViewById(R.id.dialogNegative);

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });


        name = (AppCompatEditText) dialog.findViewById(R.id.Notes_GroupName);
        nameString = name.getText().toString();
        about = (AppCompatEditText) dialog.findViewById(R.id.Notes_About);
        aboutString = about.getText().toString();


        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.getWindow().setLayout((6 * width) / 7, LinearLayout.LayoutParams.WRAP_CONTENT);


        dialog.show();

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}


class ListViewAdapter extends BaseAdapter {

    Context context;

    String[] namesArray, aboutArray;
    Integer[] colorsArray;
    ArrayList<String> namesArrayList, aboutArraylist;
    ArrayList<Integer> colorsArrayList;

    public TextView groupName;
    public TextView about;
    public HorizontalScrollView scrollView;
    public TextView timeDue;
    public ImageView addPictures;
    private ImageView settings;

    public LinearLayout coloredBar;
    public LinearLayout linearInSV, linearWithPicture;

    //size of images in the scrollView
    int imageWidth = 0;

    SaveSharedPreferences saveToSP = new SaveSharedPreferences();

    public ArrayList<ArrayList<String>> arrayOfArrays;

    JSONArray aboutJSONload, namesJSONload, colorsJSONload;

    private static LayoutInflater inflater = null;

    public ListViewAdapter(Context context, String[] namesArray, String[] aboutArray, Integer[] colorsArray, int code) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width = display.getWidth();
        imageWidth = width / 4;

        this.context = context;
        final int REQUEST_CODE_SHARE_TO_MESSENGER = 9;

        if (code == 1) {

            try {
                aboutJSONload = (new JSONArray(saveToSP.getStringSP(((Activity) context), "JSON", "about")));
                ArrayList<String> helper = new ArrayList<>();
                for (int i = 0; i < aboutJSONload.length(); i++) {
                    helper.add(aboutJSONload.get(i).toString());
                }
                this.aboutArray = new String[helper.size()];
                this.aboutArray = helper.toArray(aboutArray);
            } catch (JSONException | NullPointerException e) {
                this.aboutArray = new String[0];
            }

            try {
                namesJSONload = (new JSONArray(saveToSP.getStringSP(((Activity) context), "JSON", "names")));
                ArrayList<String> helper = new ArrayList<>();
                for (int i = 0; i < namesJSONload.length(); i++) {
                    helper.add(namesJSONload.get(i).toString());
                }
                this.namesArray = new String[helper.size()];
                this.namesArray = helper.toArray(namesArray);
            } catch (JSONException | NullPointerException e) {
                this.namesArray = new String[0];
            }

            try {

                String string;
                SharedPreferences preferences = context.getSharedPreferences("JSON", Context.MODE_PRIVATE);
                string = preferences.getString("colors", null);

                colorsJSONload = (new JSONArray(string));
                ArrayList<Integer> helper = new ArrayList<>();
                for (int i = 0; i < colorsJSONload.length(); i++) {
                    helper.add(Integer.parseInt(colorsJSONload.get(i).toString()));
                }
                this.colorsArray = new Integer[helper.size()];
                this.colorsArray = helper.toArray(colorsArray);
            } catch (JSONException | NullPointerException e) {
                this.colorsArray = new Integer[0];
            }


        } else {

            this.namesArray = new String[namesArray.length];
            this.aboutArray = new String[aboutArray.length];
            this.colorsArray = new Integer[colorsArray.length];

            this.namesArray = namesArray;
            this.aboutArray = aboutArray;
            this.colorsArray = colorsArray;

        }


        String arrayOfArraysString = saveToSP.getStringSP(((Activity) context), "NOTES ARRAY", "arrayofarrays");
        arrayOfArrays = new ArrayList<>();

        try {
            String temp = "";
            ArrayList<String> tempArray = new ArrayList<>();
            char[] charArray = arrayOfArraysString.toCharArray();
            for (int i = 0; i < charArray.length; i++) {
                if (charArray[i] == '`') {
                    tempArray.add(temp);
                    temp = "";
                } else if (charArray[i] == '~') {
                    arrayOfArrays.add(tempArray);
                    tempArray = new ArrayList<>();
                } else {
                    temp += charArray[i];
                }
            }
        } catch (NullPointerException e) {

        }

    }

    @Override
    public int getCount() {
        return namesArray.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        LayoutInflater inflater = LayoutInflater.from(context);
        if (vi == null) {
            vi = inflater.inflate(R.layout.recylcerview_notes_item, null);
        } else {
            vi = inflater.inflate(R.layout.recylcerview_notes_item, null);
        }

        Typeface robotoRegular = Typeface.createFromAsset(context.getAssets(), "fonts/roboto_regular.ttf");

        groupName = (TextView) vi.findViewById(R.id.itemName);
        groupName.setTypeface(robotoRegular);
        about = (TextView) vi.findViewById(R.id.itemAbout);
        about.setTypeface(robotoRegular);
        scrollView = (HorizontalScrollView) vi.findViewById(R.id.viewPager);
        linearInSV = (LinearLayout) vi.findViewById(R.id.linearScrollView);
        coloredBar = (LinearLayout) vi.findViewById(R.id.colorBar);
        settings = (ImageView) vi.findViewById(R.id.settingsItem);


        groupName.setText(String.valueOf(namesArray[position]));
        about.setText(String.valueOf(aboutArray[position]));
        coloredBar.setBackgroundColor(colorsArray[position]);

        try {
            for (int i = 0; i < arrayOfArrays.get(position).size(); i++) {


                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth, LinearLayout.LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 2, 0);
                final ImageView imageView = new ImageView(context);
                imageView.setTag(arrayOfArrays.get(position).get(i) + "`" + position);
                imageView.setLayoutParams(params);
                loadBitmap(arrayOfArrays.get(position).get(i), imageView, true);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String tag = imageView.getTag().toString();

                        int position = Integer.parseInt(tag.substring(tag.indexOf("`") + 1, tag.length()));
                        String uri = tag.substring(0, tag.indexOf("`"));

                        Intent intent = new Intent(context, NotesDetailActivity.class);
                        intent.putExtra("position", position);
                        intent.putExtra("uri", uri);
                        context.startActivity(intent);

                    }
                });

                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        String tag = imageView.getTag().toString();

                        final int position = Integer.parseInt(tag.substring(tag.indexOf("`") + 1, tag.length()));
                        String uri = tag.substring(0, tag.indexOf("`"));

                        int tempInt = 0;
                        for (int i = 0; i < arrayOfArrays.get(position).size(); i++) {
                            String temp = arrayOfArrays.get(position).get(i);
                            if (temp.equals(uri)) {
                                break;
                            } else {
                                tempInt++;
                            }
                        }

                        PopupMenu papap = new PopupMenu(context, imageView);
                        papap.getMenuInflater().inflate(R.menu.image_menu, papap.getMenu());
                        final int finalTempInt = tempInt;
                        papap.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                String title = item.getTitle().toString();
                                switch (title) {
                                    case "Delete":

                                        arrayOfArrays.get(position).remove(finalTempInt);

                                        String save = "";
                                        for (int i = 0; i < arrayOfArrays.size(); i++) {
                                            for (int j = 0; j < arrayOfArrays.get(i).size(); j++) {
                                                save += arrayOfArrays.get(i).get(j) + "`";
                                            }
                                            save += "~";
                                        }

                                        saveToSP.saveToSharedPreferences(((Activity) context), "NOTES ARRAY", "arrayofarrays", save);

                                        notifyDataSetChanged();

                                        break;

                                    case "Share to Messenger":

                                        Uri contentUri = null;
                                        String imageUri = arrayOfArrays.get(position).get(finalTempInt);
                                        String mimeType = "image/jpeg";
                                        File file = new File(imageUri);

                                        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                            Toast.makeText(context, "This feature is not yet available on android 7.1+ phones", Toast.LENGTH_SHORT).show();
                                        } else {
                                            contentUri = Uri.fromFile(new File(imageUri));
                                            // setting the image uri so that it can be sent
                                            ShareToMessengerParams shareToMessengerParams = ShareToMessengerParams.newBuilder(contentUri, mimeType).build();
                                            // sending
                                            MessengerUtils.shareToMessenger((Activity) context, 1, shareToMessengerParams);
                                        }

                                        break;
                                }

                                return false;
                            }
                        });

                        papap.show();

                        return false;
                    }
                });

                linearInSV.addView(imageView);


            }

        } catch (Exception e) {

        }


        final PopupMenu popup = new PopupMenu(context, settings);
        popup.getMenuInflater().inflate(R.menu.notes_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                String itemTitle = item.getTitle().toString();

                switch (itemTitle) {

                    case "Add Pictures":
                        ((MainActivity) context).addingImages(position, getCount());
                        break;

                    case "Delete":

                        final AlertDialog.Builder builder = new AlertDialog.Builder((Activity) context)
                                .setTitle("Delete Group")
                                .setMessage("Are you sure you want to delete this group?")
                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        aboutArraylist = new ArrayList<String>();
                                        aboutArraylist.addAll(Arrays.asList(aboutArray));
                                        namesArrayList = new ArrayList<String>();
                                        namesArrayList.addAll(Arrays.asList(namesArray));
                                        colorsArrayList = new ArrayList<Integer>();
                                        colorsArrayList.addAll(Arrays.asList(colorsArray));

                                        aboutArraylist.remove(position);
                                        namesArrayList.remove(position);
                                        colorsArrayList.remove(position);

                                        aboutArray = new String[aboutArraylist.size()];
                                        aboutArray = aboutArraylist.toArray(aboutArray);

                                        namesArray = new String[namesArrayList.size()];
                                        namesArray = namesArrayList.toArray(namesArray);

                                        colorsArray = new Integer[colorsArrayList.size()];
                                        colorsArray = colorsArrayList.toArray(colorsArray);

                                        try {
                                            arrayOfArrays.remove(position);
                                        } catch (IndexOutOfBoundsException e) {

                                        }

                                        String save = "";
                                        for (int a = 0; a < arrayOfArrays.size(); a++) {
                                            for (int j = 0; j < arrayOfArrays.get(a).size(); j++) {
                                                save += arrayOfArrays.get(a).get(j) + "`";
                                            }
                                            save += "~";
                                        }

                                        saveToSP.saveToSharedPreferences(((Activity) context), "NOTES ARRAY", "arrayofarrays", save);

                                        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                                        NotesFragment fragment = (NotesFragment) fm.findFragmentByTag("NotesFragment");
                                        fragment.deleteGroup(aboutArray, namesArray, colorsArray);

                                    }
                                });
                        builder.show();



                        break;

                    case "Share":

//                        Toast.makeText(context, "Comming soon", Toast.LENGTH_SHORT).show();

                        if (hasNetworkConnection()) {
                            AdDialog dialog = new AdDialog();
                            dialog.dialog(((Activity) context));
                        } else {
                            Toast.makeText(context, "Comming soon", Toast.LENGTH_SHORT).show();
                        }

                }

                return true;
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popup.show(); //showing popup menu

            }
        });

        return vi;
    }

    public void loadBitmap(String uri, ImageView imageView, boolean resize) {
        ImageLoader task = new ImageLoader(imageView, uri, resize, context);
        task.execute(uri);
    }

    private boolean hasNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public int getItemViewType(int position) {
        return position;
    }

}