package com.github.zzn01.phone;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CallLogDetailActivity extends ListActivity {
    private class CallEntry {
        public String number;
        public String time;
        public String date;
        public int type;
    }

    private String name;

    private ArrayList<CallEntry> callEntries;

    private static final String TAG = "CallLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log_detail);

        Intent intent = getIntent();
        name = intent.getStringExtra(CallLogActivity.CONTACT_NAME);

        callEntries = new ArrayList<CallEntry>();

        Cursor curLog = CallLogHelper.getCallLogsByName(getContentResolver(), name);
        setCallEntries(curLog);
        curLog.close();

        TextView header = new TextView(this);
        header.setTextSize(15);
        header.setText(name);
        ListView lv = getListView();
        lv.addHeaderView(header);

        setListAdapter(new MyAdapter(this, android.R.layout.simple_list_item_1,
                R.id.tvNameMain, callEntries));
    }

    private class MyAdapter extends ArrayAdapter<CallEntry> {

        public MyAdapter(Context context, int resource, int textViewResourceId,
                         ArrayList<CallEntry> l) {
            super(context, resource, textViewResourceId, l);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = setList(position, parent);
            return row;
        }

        private View setList(int position, ViewGroup parent) {
            LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inf.inflate(R.layout.call_log_detail_item, parent, false);

            TextView tvNumber = (TextView) row.findViewById(R.id.number);
            TextView tvType = (TextView) row.findViewById(R.id.type);

            TextView tvDate = (TextView) row.findViewById(R.id.date);
            TextView tvTime = (TextView) row.findViewById(R.id.duration);

            CallEntry item = callEntries.get(position);

            tvNumber.setText(item.number);
            switch (item.type) {
                case android.provider.CallLog.Calls.OUTGOING_TYPE:
                    tvType.setText(R.string.type_outgoing_call);
                    break;
                case android.provider.CallLog.Calls.MISSED_TYPE:
                    tvType.setText(R.string.type_missed_call);
                    break;
                case android.provider.CallLog.Calls.INCOMING_TYPE:
                    tvType.setText(R.string.type_incoming_call);
                    break;
            }

            tvTime.setText("( " + item.time + "sec )");
            tvDate.setText(item.date);

            row.setClickable(false);
            return row;
        }
    }

    private void setCallEntries(Cursor curLog) {
        while (curLog.moveToNext()) {

            CallEntry item = new CallEntry();

            item.number = curLog.getString(curLog
                    .getColumnIndex(android.provider.CallLog.Calls.NUMBER));

            String callDate = curLog.getString(curLog
                    .getColumnIndex(android.provider.CallLog.Calls.DATE));
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM -dd HH:mm");

            item.date = formatter.format(new Date(Long
                    .parseLong(callDate)));
            item.type = curLog.getInt(curLog.getColumnIndex(android.provider.CallLog.Calls.TYPE));


            item.time = curLog.getString(curLog
                    .getColumnIndex(android.provider.CallLog.Calls.DURATION));

            callEntries.add(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

