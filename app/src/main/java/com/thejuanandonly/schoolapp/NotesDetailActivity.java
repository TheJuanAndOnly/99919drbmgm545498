package com.thejuanandonly.schoolapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.ListMenuItemView;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Daniel on 10/31/2015.
 */
public class NotesDetailActivity extends AppCompatActivity {

    public static Context initialContext;
    public static String currentNote;
    android.support.v7.widget.Toolbar toolbar;
    private Menu menu;
    ListView listView;
    JSONArray arrayOfCategories, jsonArray;
    ArrayAdapter<String> arrayAdapter;
    public static ArrayList<String> arrayList;
    public static String subjectName, newSubCategoryName;
    String subCategoryName, update = "";
    String subCategory = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_detail_layout);
        initialContext = getApplicationContext();
        currentNote = getIntent().getExtras().getString("subjectNotes", null);

        theme();
        String note = getIntent().getExtras().getString("note", "SchoolApp");
        toolbar.setTitle(note);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences prefs = getSharedPreferences("SubjectGroupName" + getIntent().getExtras().getString("notes", null), Context.MODE_PRIVATE);
        try {
            arrayOfCategories = new JSONArray(prefs.getString("ListOfSubjectsNotes", null));
        } catch (Exception e) {
            arrayOfCategories = new JSONArray();
        }

        try {
            ArrayList<String> strings = new ArrayList<String>();
            for (int i = 0; i < arrayOfCategories.length(); i++) {
                try {
                    strings.add(arrayOfCategories.getString(i));
                } catch (JSONException e) {
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Add a category", Toast.LENGTH_SHORT).show();
        }

        setListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SharedPreferences prefs = getSharedPreferences("ListOfSubjectsGroupName", Context.MODE_PRIVATE);
                JSONArray jsonArray = new JSONArray();

                try {
                    jsonArray = new JSONArray(prefs.getString("ListGroupName", null));
                    subCategory = jsonArray.getString(position);
                } catch (Exception e) {
                }

                Intent intent = new Intent(getApplicationContext(), PictureGroupActivity.class);
                intent.putExtra("subNote", subCategory);
                intent.putExtra("position", position);
                try {
                    intent.putExtra("subjectDetailNotes", jsonArray.getString(position));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                intent.putExtra("positionNotesDetail", position);

                startActivity(intent);
            }
        });

        registerForContextMenu(listView);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int id = info.position;
        final TextView textView = (TextView) findViewById(android.R.id.text1);
        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayList<String> arrayListRename = new ArrayList<>();
        final SharedPreferences preferences = getSharedPreferences("ListOfSubjectsGroupName" + getIntent().getExtras().getString("note", null), Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        JSONArray array = new JSONArray();

        try {
            textView.getText();
        } catch (NullPointerException e) {}

        SharedPreferences GroupNamePrefs = getSharedPreferences("SubjectGroupName" + subjectName, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editorName = GroupNamePrefs.edit();

        switch (item.getItemId()) {
            case R.id.rename:
            final AlertDialog.Builder alert = new AlertDialog.Builder(NotesDetailActivity.this);
                alert.setTitle("Rename");
                final EditText editText = new EditText(getApplicationContext());
                editText.setTextColor(Color.BLACK);
                alert.setView(editText);

                jsonArray = new JSONArray();
                try {
                    jsonArray = new JSONArray(preferences.getString("ListGroupName", jsonArray.toString()));
                    subCategoryName = jsonArray.getString(info.position);
                } catch (Exception e) {
                }

                editText.setText(subCategoryName);

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        arrayListRename.add(jsonArray.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                newSubCategoryName = editText.getText().toString();
                                subCategoryName = newSubCategoryName;

                                arrayListRename.remove(info.position);
                                arrayListRename.add(info.position, newSubCategoryName);

                                Toast.makeText(NotesDetailActivity.this, "Updated text: " + subCategoryName, Toast.LENGTH_SHORT).show();

                                editor.putString("ListGroupName", arrayListRename.toString()).apply();
                                setListView();
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
                    array = new JSONArray(preferences.getString("ListGroupName", null));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < array.length(); i++) {
                    try {
                        arrayList.add(array.get(i).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                arrayList.remove(id);
                arrayAdapter.notifyDataSetChanged();

                editor.putString("ListGroupName", arrayList.toString()).apply();

                setListView();

                return true;

            case R.id.cancel:
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.menu_notes_detail, menu);
                this.menu = menu;
                return true;
            }

                    @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.addPictureFolder) {
                    pictureGroupNameDialog();
                } else if (id == android.R.id.home) {
                    onBackPressed();
                }
                return super.onOptionsItemSelected(item);
            }


        public void setListView() {

            listView = (ListView) findViewById(R.id.PictureGroupListView);

            SharedPreferences prefs = getSharedPreferences("ListOfSubjectsGroupName" + getIntent().getExtras().getString("note", null), Context.MODE_PRIVATE);
            jsonArray = new JSONArray();
            try {
                jsonArray = new JSONArray(prefs.getString("ListGroupName", null));
            } catch (Exception e) {
            }

            arrayList = new ArrayList<String>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        arrayList.add(jsonArray.getString(i));
                        } catch (Exception e) {
                    }
                }

                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.notesdetail_listview_item, R.id.textView1, arrayList);

                //noteAdapter = new CustomNoteAdapter(getApplicationContext(), arrayList);
                listView.setAdapter(arrayAdapter);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public void theme() {
                SharedPreferences prefs = getSharedPreferences("themeSave", Context.MODE_PRIVATE);
                int theme = prefs.getInt("theme", 0);

                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.notesDetailToolbar);

                switch (theme) {
                    default:

                        toolbar.setBackgroundColor(getResources().getColor(R.color.mainblue));

                        if (MainActivity.api >= android.os.Build.VERSION_CODES.LOLLIPOP)
                        window.setStatusBarColor(getResources().getColor(R.color.mainblue800));
                }
            }


        public void pictureGroupNameDialog() {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add a Picture Group");

            final EditText input = new EditText(this);
            input.setHint("Subject name");
            input.setPadding(50, 50, 50, 30);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String subjectInput = input.getText().toString();

                    savePictureGroupName(subjectInput);

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }

        public void savePictureGroupName(String subject) {

            SharedPreferences prefs = getSharedPreferences("ListOfSubjectsGroupName" + getIntent().getExtras().getString("note", null), Context.MODE_PRIVATE);
            JSONArray jsonArray = new JSONArray();
            try {
                jsonArray = new JSONArray(prefs.getString("ListGroupName", null));
            } catch (Exception e) {
            }


            if (subject != null && subject.length() > 0) {
                jsonArray.put(subject);
            } else {
                Toast.makeText(this, "Don't leave the space blank!", Toast.LENGTH_LONG).show();
                pictureGroupNameDialog();
            }

            //SP pre kazdy predmet
            SharedPreferences preferences = getSharedPreferences("SubjectGroupName" + subject, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("subjectGroupName", subject).apply();

            //Zoznam predmetov
            SharedPreferences arrayPrefs = getSharedPreferences("ListOfSubjectsGroupName" + getIntent().getExtras().getString("note", null), Context.MODE_PRIVATE);
            SharedPreferences.Editor arrayPrefsEditor = arrayPrefs.edit();
            arrayPrefsEditor.putString("ListGroupName", jsonArray.toString()).apply();

            subjectName = subject;

            setListView();
        }
    }