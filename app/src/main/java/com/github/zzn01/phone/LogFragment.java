package com.github.zzn01.phone;

import android.app.Dialog;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by zzn on 5/1/15.
 */
public class LogFragment extends ListFragment {
    public static final String CONTACT_NAME = "com.github.zzn01.telephone.CallLogActivity.name";
    public static final String CONTACT_NUMBER = "com.github.zzn01.telephone.CallLogActivity.number";
    public static final String CONTACT_PHOTO = "com.github.zzn01.telephone.CallLogActivity.photo";
    public static final String OUTGOING_SYMBOL = "<font color=green>\u2197</font>";
    public static final String INCOMING_SYMBOL = "<font color=green>\u2199</font>";
    public static final String MISSED_SYMBOL = "<font color=red>\u2199</font>";
    private static final int MAX_COUNT = 3;
    private static final String TAG = "CallLog";
    ArrayList<CallLogEntry> callLogEntries;
    private View currentRow;

    public static LogFragment newInstance() {
        final LogFragment fragment = new LogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callLogEntries = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        Cursor curLog = CallLogHelper.getAllCallLogs(inflater.getContext().getContentResolver());
        setCallLogEntries(curLog);
        curLog.close();

        setListAdapter(new MyAdapter(inflater.getContext(), android.R.layout.simple_list_item_1,
                R.id.tvNameMain, callLogEntries));

        Log.v(TAG, "onCreateView");


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        getListView().setLongClickable(true);

        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Dialog dialog= new Dialog(view.getContext());
                CallLogEntry  callLogEntry = callLogEntries.get(position);
                dialog.setTitle(callLogEntry.name);
                dialog.setContentView(R.layout.operation);
                dialog.show();
                return true;
            }
        });
    }

    private void setCallLogEntries(Cursor curLog) {
        CallLogEntry item = null;
        while (curLog.moveToNext()) {
            String number = curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.NUMBER));

            int size = callLogEntries.size();
            if (size > 0) {
                item = callLogEntries.get(size - 1);
            }

            if (item == null || !number.equals(item.number)) {
                item = new CallLogEntry();
                item.number = number;
                callLogEntries.add(item);
                item.name = curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME));
                item.photoID = curLog.getInt(curLog.getColumnIndex(android.provider.CallLog.Calls.CACHED_PHOTO_ID));
            }

            String callDate = curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.DATE));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            item.lastDate = formatter.format(new Date(Long.parseLong(callDate)));
            int type = curLog.getInt(curLog.getColumnIndex(android.provider.CallLog.Calls.TYPE));

            item.types.add(type);
        }
    }

    public void recall(CallLogEntry callLogEntry) {

        Log.d("Button", "call it");
        String phone_number = callLogEntry.number;
        assert (phone_number != null && !phone_number.equals(""));

        //封装一个拨打电话的intent，并且将电话号码包装成一个uri对象传入
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number));
        //这是最重要的一步，调用系统自带的拨号程序
        startActivity(intent);
    }

    public void show_detail(CallLogEntry callLogEntry) {
        Intent intent = new Intent(getActivity(), CallLogDetailActivity.class);
        Log.d("Button", "call detail");

        intent.putExtra(CONTACT_NAME, callLogEntry.name);
        intent.putExtra(CONTACT_NUMBER, callLogEntry.number);
        intent.putExtra(CONTACT_PHOTO, callLogEntry.photoID);

        startActivity(intent);
    }

    @Override
    public void onListItemClick (ListView l, View row, int position, long id) {
        super.onListItemClick(l, row, position, id);

        if (currentRow!=null && !currentRow.equals(row)){
            LinearLayout op = (LinearLayout) currentRow.findViewById(R.id.operation);
            op.setVisibility(View.GONE);
        }
        currentRow = row;
        LinearLayout op = (LinearLayout) row.findViewById(R.id.operation);
        switch (op.getVisibility()) {
            case View.GONE:
                op.setVisibility(View.VISIBLE);
                break;
            case View.VISIBLE:
                Log.d(TAG,"none of");
                op.setVisibility(View.GONE);
                break;
            default:
                op.setVisibility(View.GONE);
        }

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

    private class CallLogEntry {
        public String name;
        public String number;
        public String lastDate;
        public int photoID;
        ArrayList<Integer> types;

        CallLogEntry() {
            types = new ArrayList<>();
        }

        @Override
        public String toString(){
            return "name:"+name+",number:"+number+", lastDate:"+lastDate;
        }
    }

    private class MyAdapter extends ArrayAdapter<CallLogEntry> {

        public MyAdapter(Context context, int resource, int textViewResourceId,
                         ArrayList<CallLogEntry> l) {

            super(context, resource, textViewResourceId, l);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return setList(position, parent);
        }

        private void setType(ArrayList<Integer> types, TextView t) {
            String type = "";
            String suffix = "";

            int count = types.size();
            if (count > MAX_COUNT) {
                suffix = "(" + count + ")";
                count = MAX_COUNT;
            }
            for (int i = 0; i < count; ++i) {
                switch (types.get(i)) {
                    case android.provider.CallLog.Calls.OUTGOING_TYPE:
                        type += OUTGOING_SYMBOL;
                        break;
                    case android.provider.CallLog.Calls.MISSED_TYPE:
                        type += MISSED_SYMBOL;
                        break;
                    case android.provider.CallLog.Calls.INCOMING_TYPE:
                        type += INCOMING_SYMBOL;
                        break;
                }
            }
            t.setText(Html.fromHtml(type + suffix));
        }

        private View setList(int position, ViewGroup parent) {
            LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inf.inflate(R.layout.call_log_item_style, parent, false);

            TextView tvName = (TextView) row.findViewById(R.id.tvNameMain);
            TextView tvNumber = (TextView) row.findViewById(R.id.tvNumberMain);
            TextView tvDate = (TextView) row.findViewById(R.id.tvDate);
            TextView tvType = (TextView) row.findViewById(R.id.type_img);

            CallLogEntry item = callLogEntries.get(position);

            tvName.setText(item.name);
            tvNumber.setText(item.number);

            setType(item.types, tvType);

            tvDate.setText(item.lastDate);

            LinearLayout op = (LinearLayout) row.findViewById(R.id.operation);
            op.setVisibility(View.GONE);

            TextView call_it = (TextView)op.findViewById(R.id.btn_call_it);
            call_it.setTag(position);
            call_it.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recall(callLogEntries.get((int)v.getTag()));
                }
            });

            TextView detail = (TextView)op.findViewById(R.id.btn_detail);
            detail.setTag(position);
            detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_detail(callLogEntries.get((int)v.getTag()));
                }
            });

            return row;
        }
    }
}

