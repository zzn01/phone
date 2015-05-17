package com.github.zzn01.phone;

import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
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
    ArrayList<Common.CallLogEntry> callLogEntries;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.list_fragment, container, false);
        ListView lv = (ListView) view.findViewById(android.R.id.list);
        lv.setDivider(null);
//        lv.setDividerHeight(1);
        TextView empty = (TextView) view.findViewById(android.R.id.empty);
        empty.setText("not more call log");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        Cursor curLog = CallLogHelper.getAllCallLogs(getActivity().getContentResolver());
        setCallLogEntries(curLog);
        curLog.close();

        setListAdapter(new LogAdapter(getActivity(), R.layout.call_log_item_style, callLogEntries));

        getListView().setLongClickable(true);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                OperationDialog dialog = OperationDialog.newInstance(callLogEntries.get(position));
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                dialog.show(fragmentManager, "dialog");

                return  true;
            }
        });
    }

    private void setCallLogEntries(Cursor curLog) {
        Common.CallLogEntry item = null;
        while (curLog.moveToNext()) {
            String number = curLog.getString(curLog.getColumnIndex(android.provider.CallLog.Calls.NUMBER));

            int size = callLogEntries.size();
            if (size > 0) {
                item = callLogEntries.get(size - 1);
            }

            if (item == null || !number.equals(item.number)) {
                item = new Common.CallLogEntry();
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

    public void recall(Common.CallLogEntry callLogEntry) {

        Log.d("Button", "call it");
        String phone_number = callLogEntry.number;
        assert (phone_number != null && !phone_number.equals(""));

        //封装一个拨打电话的intent，并且将电话号码包装成一个uri对象传入
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number));
        //这是最重要的一步，调用系统自带的拨号程序
        startActivity(intent);
    }

    public void show_detail(Common.CallLogEntry callLogEntry) {
        startActivity(CallLogDetailActivity.newIntent(getActivity(), callLogEntry));
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


    private class LogAdapter extends ArrayAdapter<Common.CallLogEntry> {

        private class ViewHolder{
            TextView Name;
            TextView Number;
            TextView Date;
            TextView Type;
            TextView CallIt;
            TextView Detail;
            LinearLayout op;
        }

        public LogAdapter(Context context, int resource, ArrayList<Common.CallLogEntry> l) {

            super(context, resource, l);
        }


        private Spanned getType(ArrayList<Integer> types) {
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
            return Html.fromHtml(type + suffix);
        }

        private View newView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inf.inflate(R.layout.call_log_item_style, parent, false);

            viewHolder.Name = (TextView) row.findViewById(R.id.tvNameMain);
            viewHolder.Number = (TextView) row.findViewById(R.id.tvNumberMain);
            viewHolder.Date = (TextView) row.findViewById(R.id.tvDate);
            viewHolder.Type = (TextView) row.findViewById(R.id.type_img);
            viewHolder.op = (LinearLayout) row.findViewById(R.id.operation);
            viewHolder.CallIt = (TextView) row.findViewById(R.id.btn_call_it);
            viewHolder.Detail = (TextView) row.findViewById(R.id.btn_detail);

            row.setTag(viewHolder);
            return row;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder viewHolder;
            if (convertView==null){
                convertView = newView(position, convertView, parent);
            }
            viewHolder = (ViewHolder)convertView.getTag();

            Common.CallLogEntry item = callLogEntries.get(position);

            viewHolder.Name.setText(item.name);
            viewHolder.Number.setText(item.number);

            viewHolder.Type.setText(getType(item.types));

            viewHolder.Date.setText(item.lastDate);

            viewHolder.op.setVisibility(View.GONE);

            viewHolder.CallIt.setTag(position);
            viewHolder.CallIt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recall(callLogEntries.get((int)v.getTag()));
                }
            });

            viewHolder.Detail.setTag(position);
            viewHolder.Detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_detail(callLogEntries.get((int)v.getTag()));
                }
            });

            return convertView;
        }
    }
}

