package com.github.zzn01.phone;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

    public static final String CONTACT_NAME = "com.github.zzn01.telephone.CallLogActivity.name";

    ArrayList<CallLog> callLogs;
    private View currentRow;

    private static final String TAG = "CallLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);

        callLogs = new ArrayList<CallLog>();

        Cursor curLog = CallLogHelper.getAllCallLogs(getContentResolver());
        setCallLogs(curLog);
        curLog.close();

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
            } else {
                tvName.setText(item.name);
                tvNumber.setText(item.number);
            }

            if (item.type.equals("1")) {
                tvType.setImageDrawable(getDrawable(R.drawable.ic_action_forward));
            } else
                tvType.setImageDrawable(getDrawable(R.drawable.ic_action_back));

            tvTime.setText("( " + item.time + "sec )");
            tvDate.setText(item.date);

            LinearLayout op = (LinearLayout) row.findViewById(R.id.operation);
            op.setVisibility(View.GONE);

            return row;
        }
    }

    private void switchState(View row) {

        LinearLayout op = (LinearLayout) row.findViewById(R.id.operation);
        switch (op.getVisibility()) {
            case View.GONE:
                op.setVisibility(View.VISIBLE);
                break;
            case View.VISIBLE:
                op.setVisibility(View.GONE);
                break;
            default:
                op.setVisibility(View.GONE);
        }
        Log.d(TAG, "click it " + row.getId());
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
    public void onListItemClick(ListView l, View v, int position, long id) {

        if (currentRow != null) {
            switchState(currentRow);
        }

        switchState(v);
        currentRow = v;
    }

    public void call_it(View v) {

        Log.d("Button", "call it");

        TextView n = (TextView) currentRow.findViewById(R.id.tvNumberMain);

        String phone_number = n.getText().toString();
        assert (phone_number != null && !phone_number.equals(""));

        //封装一个拨打电话的intent，并且将电话号码包装成一个uri对象传入
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number));
        //这是最重要的一步，调用系统自带的拨号程序
        startActivity(intent);
    }

    public void call_detail(View v) {
        Intent intent = new Intent(this, CallLogDetailActivity.class);
        Log.d("Button", "call detail");

        TextView n = (TextView) currentRow.findViewById(R.id.tvNameMain);
        intent.putExtra(CONTACT_NAME, n.getText().toString());

        startActivity(intent);
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
