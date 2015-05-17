package com.github.zzn01.phone;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by zzn on 5/17/15.
 */

public class OperationDialog extends DialogFragment {
    private static final String TAG="OperationDialog";
    private static final int[] operations = {
            R.string.op_call, R.string.op_detail,
            R.string.op_block, R.string.op_delete
    };
    private static final String CURRENTLOGENTRY = "OperationDialog.Entry";

    Common.CallLogEntry entry;

    public  static OperationDialog newInstance(Common.CallLogEntry entry){
        Bundle bundle = new Bundle();

        bundle.putParcelable(CURRENTLOGENTRY, new Common.ParcelableCallLogEntry(entry));

        OperationDialog dialog= new OperationDialog();
        dialog.setArguments(bundle);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        entry = ((Common.ParcelableCallLogEntry) getArguments().getParcelable(CURRENTLOGENTRY)).getEntry();

        String title = entry.name;
        if (title==null || title.equals("")){
            title = entry.number;
        }
        getDialog().setTitle(title);

        View custom = inflater.inflate(R.layout.dialog, container, false);

        GridView gridview = (GridView) custom.findViewById(R.id.dialog_grid);

        gridview.setAdapter(new OperationAdapter(inflater.getContext()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Log.i(TAG, "operation " + position);

                switch (operations[position]) {
                    case R.string.op_call:
                        op_call(entry);
                        break;
                    case R.string.op_detail:
                        op_detail(entry);
                        break;
                    case R.string.op_block:
                        op_block(entry);
                        break;
                    case R.string.op_delete:
                        op_delete(entry);
                        break;
                }
            }
        });

        return custom;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    private void op_block(Common.CallLogEntry entry){

    }

    private void op_delete(Common.CallLogEntry entry){

    }

    private void op_call(Common.CallLogEntry entry){
        String phoneNumber = entry.number;
        assert (phoneNumber != null && !phoneNumber.equals(""));
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    public void op_detail(Common.CallLogEntry callLogEntry) {
        startActivity(CallLogDetailActivity.newIntent(getActivity(), callLogEntry));
    }


    public class OperationAdapter extends BaseAdapter {
        private Context mContext;

        public OperationAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return operations.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        private View newView(ViewGroup parent){
            LinearLayout layout;
            LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = (LinearLayout) inf.inflate(R.layout.operation, parent, false);
            layout.setLayoutParams(new GridView.LayoutParams(100, GridView.LayoutParams.WRAP_CONTENT));
            return layout;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;

            if (convertView == null) {
                convertView = newView(parent);
            }
            tv = (TextView) convertView.findViewById(R.id.action);
            tv.setText(operations[position]);

            return convertView;
        }
    }


}


