package com.prince.telephone;

import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
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


public class CallLogActivity extends ListActivity {

    private class CallLog {
        public String name;
        public String number;
        public String time;
        public String date;
        public String type;
    }

    ArrayList<CallLog> callLogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);

        callLogs = new ArrayList<CallLog>();

        Cursor curLog = CallLogHelper.getAllCallLogs(getContentResolver());

        setCallLogs(curLog);

        setListAdapter(new MyAdapter(this, android.R.layout.simple_list_item_1,
                R.id.tvNameMain, callLogs));

    }

    private class MyAdapter extends ArrayAdapter<CallLog> {

        public MyAdapter(Context context, int resource, int textViewResourceId,
                         ArrayList<CallLog> l) {
            super(context, resource, textViewResourceId, l);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = setList(position, parent);
            return row;
        }

        private View setList(int position, ViewGroup parent) {
            LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inf.inflate(R.layout.call_log_item_style, parent, false);

            TextView tvName = (TextView) row.findViewById(R.id.tvNameMain);
            TextView tvNumber = (TextView) row.findViewById(R.id.tvNumberMain);
            TextView tvTime = (TextView) row.findViewById(R.id.tvTime);
            TextView tvDate = (TextView) row.findViewById(R.id.tvDate);
            ImageView tvType = (ImageView) row.findViewById(R.id.type_img);

            CallLog item = callLogs.get(position);
            if (item.name == null) {
                tvName.setText(item.number);
                tvNumber.setText("");
            }else{
                tvName.setText(item.name);
                tvNumber.setText(item.number);
            }

               if (item.type.equals("1")) {
                    tvType.setImageDrawable(getDrawable(R.drawable.ic_action_forward));
               } else
                   tvType.setImageDrawable(getDrawable(R.drawable.ic_action_back));

            tvTime.setText("( " + item.time + "sec )");
            tvDate.setText(item.date);
            return row;
        }
    }

    private void setCallLogs(Cursor curLog) {
        while (curLog.moveToNext()) {

            CallLog item = new CallLog();

            item.number = curLog.getString(curLog
                    .getColumnIndex(android.provider.CallLog.Calls.NUMBER));
            item.name = curLog.getString(curLog
                            .getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));

            String callDate = curLog.getString(curLog
                    .getColumnIndex(android.provider.CallLog.Calls.DATE));
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM -dd HH:mm");

            item.date = formatter.format(new Date(Long
                    .parseLong(callDate)));
            item.type = curLog.getString(curLog
                    .getColumnIndex(android.provider.CallLog.Calls.TYPE));


            item.time = curLog.getString(curLog
                    .getColumnIndex(android.provider.CallLog.Calls.DURATION));

            callLogs.add(item);
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
