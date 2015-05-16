package com.github.zzn01.phone;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import utils.TimeDelta;


public class CallLogDetailActivity extends ListActivity {
    private static final String TAG = "CallLog";
    private String name;
    private int photoID;

    private ArrayList<CallEntry> callEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log_detail);

        callEntries = new ArrayList<>();
        Intent intent = getIntent();

        photoID = intent.getIntExtra(LogFragment.CONTACT_PHOTO, -1);

        name = intent.getStringExtra(LogFragment.CONTACT_NAME);
        Cursor curLog;
        if (name == null || name.equals("")) {
            name = intent.getStringExtra(LogFragment.CONTACT_NUMBER);
            curLog = CallLogHelper.getCallLogsByNumber(getContentResolver(), name);
        } else {
            curLog = CallLogHelper.getCallLogsByName(getContentResolver(), name);
        }

        //TODO: check error
        try {
            setCallEntries(curLog);
        } finally {
            curLog.close();
        }

        setHeader();

        setListAdapter(new MyAdapter(this, android.R.layout.simple_list_item_1,
                R.id.tvNameMain, callEntries));
    }

    private void setHeader() {
        TextView header = (TextView) findViewById(R.id.contact_name);
        header.setText(name);
        if (photoID != -1) {
            ImageView pic = (ImageView) findViewById(R.id.contact_pic);
            pic.setImageBitmap(ContactHelper.queryContactImage(getContentResolver(), photoID));
        }
    }

    private void setDuration(int duration, TextView t) {
        TimeDelta timeDelta = new TimeDelta(duration);
        String time = "";
        if (timeDelta.day != 0) {
            time = time + timeDelta.day + getString(R.string.day);
        }
        if (timeDelta.hour != 0) {
            time = time + timeDelta.hour + getString(R.string.hour);
        }
        time = time + timeDelta.min + getString(R.string.min) + timeDelta.sec + getString(R.string.sec);

        t.setText(time);
    }

    private void setCallEntries(Cursor curLog) {
        while (curLog.moveToNext()) {

            CallEntry item = new CallEntry();

            item.number = curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.NUMBER));

            String callDate = curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.DATE));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            item.date = formatter.format(new Date(Long.parseLong(callDate)));
            item.type = curLog.getInt(curLog.getColumnIndex(android.provider.CallLog.Calls.TYPE));

            item.time = curLog.getInt(curLog.getColumnIndex(android.provider.CallLog.Calls.DURATION));

            callEntries.add(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call_log_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.d(TAG, "option select");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class CallEntry {
        public String number;
        public int time;
        public String date;
        public int type;
    }

    private class MyAdapter extends ArrayAdapter<CallEntry> {

        public MyAdapter(Context context, int resource, int textViewResourceId,
                         ArrayList<CallEntry> l) {
            super(context, resource, textViewResourceId, l);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return setList(position, parent);
        }

        private View setList(int position, ViewGroup parent) {
            LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inf.inflate(R.layout.call_log_detail_item, parent, false);

            TextView tvNumber = (TextView) row.findViewById(R.id.number);
            TextView tvType = (TextView) row.findViewById(R.id.type);
            TextView symbol = (TextView) row.findViewById(R.id.type_symbol);

            TextView tvDate = (TextView) row.findViewById(R.id.date);
            TextView tvTime = (TextView) row.findViewById(R.id.duration);

            CallEntry item = callEntries.get(position);

            tvNumber.setText(item.number);
            switch (item.type) {
                case CallLog.Calls.OUTGOING_TYPE:
                    symbol.setText(Html.fromHtml(LogFragment.OUTGOING_SYMBOL));
                    tvType.setText(R.string.type_outgoing_call);
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    symbol.setText(Html.fromHtml(LogFragment.MISSED_SYMBOL));
                    tvType.setText(R.string.type_missed_call);
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    symbol.setText(Html.fromHtml(LogFragment.INCOMING_SYMBOL));
                    tvType.setText(R.string.type_incoming_call);
                    break;
            }
            tvDate.setText(item.date);

            setDuration(item.time, tvTime);

            row.setClickable(false);
            return row;
        }
    }
}

