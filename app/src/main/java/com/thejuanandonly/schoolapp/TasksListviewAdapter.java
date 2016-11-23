package com.thejuanandonly.schoolapp;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TasksListviewAdapter extends BaseAdapter {

    Context context;
    String[] data;
    private boolean todo;
    private static LayoutInflater inflater = null;

    public TasksListviewAdapter(Context context, String[] data, boolean todo) {
        this.context = context;
        this.data = data;
        this.todo = todo;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_listview_tasks, null);
        }

        RelativeLayout rlTime = (RelativeLayout) view.findViewById(R.id.rl_time);
        RelativeLayout rlItem = (RelativeLayout) view.findViewById(R.id.rl_item);
        final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(R.id.swipe_layout);
        if (!todo) {
            swipeLayout.setSwipeEnabled(false);
        }

        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);

        final TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        final TextView tvBody = (TextView) view.findViewById(R.id.tv_body);
        ImageButton ibInfo = (ImageButton) view.findViewById(R.id.ib_info);
        View viewColor = (View) view.findViewById(R.id.view_color);

        ImageButton ibEdit = (ImageButton) view.findViewById(R.id.ib_edit);
        ImageButton ibDone = (ImageButton) view.findViewById(R.id.ib_done);

        final String row = data[position];
        final String name = row.substring(0, row.indexOf("|name|"));
        final String body = row.substring(row.indexOf("|name|") + 6, row.indexOf("|body|"));
        final Date loadedDate;

        if (row.contains("@new")) {
            loadedDate = new Date(Long.parseLong(row.substring(row.indexOf("|body|") + 6, row.length()-4)));

            rlTime.setVisibility(View.VISIBLE);

            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", loadedDate);//Thursday
            String stringMonth = (String) android.text.format.DateFormat.format("MMM", loadedDate); //Jun
            String intMonth = (String) android.text.format.DateFormat.format("MM", loadedDate); //06
            String year = (String) android.text.format.DateFormat.format("yyyy", loadedDate); //2013
            String day = (String) android.text.format.DateFormat.format("dd", loadedDate); //20

            if (Integer.parseInt(day) == (int) calendar.get(Calendar.DAY_OF_MONTH)) {
                tvTime.setText("Today");
            } else if (Integer.parseInt(day) == (int) calendar.get(Calendar.DAY_OF_MONTH)+1) {
                tvTime.setText("Tomorrow");
            } else if (Integer.parseInt(day) == (int) calendar.get(Calendar.DAY_OF_MONTH)-1) {
                tvTime.setText("Yesterday");
            } else {
                tvTime.setText(dayOfTheWeek + " " + day + "." + intMonth + "." + year);
            }
        } else {
            loadedDate = new Date(Long.parseLong(row.substring(row.indexOf("|body|") + 6, row.length())));

            rlTime.setVisibility(View.GONE);
        }

        tvName.setText(name);
        tvBody.setText(body);

        if (todo) {
            ibInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_task_details);

                    TextView tvName = (TextView) dialog.findViewById(R.id.tv_name);
                    TextView tvBody = (TextView) dialog.findViewById(R.id.tv_body);
                    TextView tvTimeDate = (TextView) dialog.findViewById(R.id.tv_time_date);

                    String timeDate = (String) android.text.format.DateFormat.format("HH:mm EEEE, dd.MM.yyyy", loadedDate);

                    tvName.setText(name);
                    tvBody.setText(body);
                    tvTimeDate.setText(timeDate);

                    dialog.show();
                }
            });
        } else {
            ibInfo.setBackgroundResource(R.drawable.ic_done_white_24dp);
        }

//        rlItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (swipeLayout.getOpenStatus() == SwipeLayout.Status.Open) {
//                    swipeLayout.close();
//                } else {
//                    swipeLayout.open();
//                }
//            }
//        });

        ibEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
                JSONArray arrayName, arrayWhat, arrayTime;
                String name, what;
                long time;
                try {
                    arrayName = new JSONArray(prefs.getString("TaskName", null));
                    arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
                    arrayTime = new JSONArray(prefs.getString("TaskTime", null));
                } catch (Exception e) {
                    arrayName = new JSONArray();
                    arrayWhat = new JSONArray();
                    arrayTime = new JSONArray();
                }

                name = row.substring(0, row.indexOf("|name|"));
                what = row.substring(row.indexOf("|name|") + 6, row.indexOf("|body|"));
                time = loadedDate.getTime();

                int pos = 0;
                for (int a = 0; a < arrayName.length(); a++) {
                    try {
                        if (arrayName.getString(a).equals(name)) {
                            if (arrayWhat.getString(a).equals(what)) {
                                if (arrayTime.getLong(a) == time) {
                                    pos = a;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                swipeLayout.close();

                FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                TasksFragment fragment = (TasksFragment) fm.findFragmentById(R.id.containerView);
                fragment.editTask(pos);
            }
        });

        ibDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = context.getSharedPreferences("ListOfTasks", Context.MODE_PRIVATE);
                JSONArray arrayName, arrayWhat, arrayTime;
                String name, what;
                long time;
                try {
                    arrayName = new JSONArray(prefs.getString("TaskName", null));
                    arrayWhat = new JSONArray(prefs.getString("TaskWhat", null));
                    arrayTime = new JSONArray(prefs.getString("TaskTime", null));
                } catch (Exception e) {
                    arrayName = new JSONArray();
                    arrayWhat = new JSONArray();
                    arrayTime = new JSONArray();
                }

                name = row.substring(0, row.indexOf("|name|"));
                what = row.substring(row.indexOf("|name|") + 6, row.indexOf("|body|"));
                time = loadedDate.getTime();

                int pos = 0;
                for (int a = 0; a < arrayName.length(); a++) {
                    try {
                        if (arrayName.getString(a).equals(name)) {
                            if (arrayWhat.getString(a).equals(what)) {
                                if (arrayTime.getLong(a) == time) {
                                    pos = a;
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }

                FragmentManager fm = ((MainActivity) context).getSupportFragmentManager();
                TasksFragment fragment = (TasksFragment) fm.findFragmentById(R.id.containerView);
                fragment.doneTask(pos);
            }
        });

        return view;
    }
}
