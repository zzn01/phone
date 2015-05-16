package com.github.zzn01.phone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import utils.Pair;


public class ContactDetailActivity extends Activity {
    private static final String TAG = "ContactDetail";

    private ContactHelper.ContactInfo contactInfo;
    private  int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_detail_activity);

        Intent intent = getIntent();

        id = intent.getIntExtra(ContactFragment.CONTACT_INFO, -1);
        contactInfo = ContactHelper.getContact(getContentResolver(), id);

        Log.i(TAG, contactInfo.toString());

        initHeader();
        initContactView();
        initLogView();
    }

    private void initHeader(){
        TextView header = (TextView) findViewById(R.id.contact_name);
        header.setText(contactInfo.name);
    }

    private void initContactView(){
        ContactView view = new ContactView((LinearLayout)findViewById(R.id.contact_detail));
        if (!contactInfo.phone.isEmpty()) {
            view.addView(new PhoneAdapter(this, R.layout.contact_item, contactInfo.phone), "phone");
        }

        if (!contactInfo.email.isEmpty()) {
            view.addView(new EmailAdapter(this, R.layout.contact_item, contactInfo.email), "email");
        }
    }

    private void initLogView(){

    }

    private class ContactView {
        private LayoutInflater inflater;
        private LinearLayout parent;

        public ContactView(LinearLayout root){
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            parent = root;
        }

        public void addView(ContactAdapter adapter, String id) {
            View custom = inflater.inflate(R.layout.contact_fragment, null);

            ListView lv = (ListView) custom.findViewById(R.id.contact_list);
            lv.setAdapter(adapter);

            TextView symbol = (TextView) custom.findViewById(R.id.contact_symbol);
            symbol.setText(id);

            parent.addView(custom);
        }

        private View getDivider() {
            ImageView divider = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3);
            divider.setLayoutParams(lp);
            divider.setBackgroundColor(Color.DKGRAY);
            return divider;
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

    private class ContactAdapter extends ArrayAdapter<Pair<String,String>> {
        private ArrayList<Pair<String,String>> data;
        private LayoutInflater inflater;

        public ContactAdapter(Context context, int resource, ArrayList<Pair<String,String>> l) {
            super(context, resource, l);

            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            data = l;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;

            if (convertView==null) {
                row = inflater.inflate(R.layout.contact_item, parent, false);
            }else{
                row = convertView;
            }

            TextView info = (TextView) row.findViewById(R.id.info);
            TextView type = (TextView) row.findViewById(R.id.type);
            ImageButton action = (ImageButton) row.findViewById(R.id.action);

            Pair<String, String> v = data.get(position);

            info.setText(v.first());
            type.setText(v.second());

            action.setTag(v.first());
            row.setTag(v.first());

            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionClick(v);
                }
            });

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowClick(v);
                }
            });

            setRow(row);

            return row;
        }

        protected void rowClick(View v){

        }

        protected void actionClick(View v){

        }

        protected void setRow(View row){

        }
    }

    private class PhoneAdapter extends ContactAdapter {
        public PhoneAdapter(Context context, int resource, ArrayList<Pair<String, String>> l) {
            super(context, resource, l);
        }

        @Override
        protected void setRow(View row){
            ImageButton btn = (ImageButton)row.findViewById(R.id.action);
            btn.setBackgroundResource(R.drawable.ic_message_24dp);
        }
        @Override
        protected void rowClick(View v){
            call(v);
        }

        @Override
        protected void actionClick(View v){
            sendSMS(v);
        }

        private void call(View v) {
            Log.i(TAG, "call message");
            String phone_number = (String) v.getTag();
            assert (phone_number != null && !phone_number.equals(""));

            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number)));
        }

        private void sendSMS(View v) {
            Log.i(TAG, "send message" + v.getTag());
            String phone_number = (String) v.getTag();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone_number)));
        }
    }

    private class EmailAdapter extends ContactAdapter {
        public EmailAdapter(Context context, int resource, ArrayList<Pair<String, String>> l) {
            super(context, resource, l);
        }

        @Override
        protected void rowClick(View v){
            sendEmail(v);
        }

        private void sendEmail(View v) {
            Log.i(TAG, "send email message");
            String email = (String) v.getTag();
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));

            startActivity(intent);
        }

        @Override
        protected void setRow(View row){
            row.findViewById(R.id.action).setVisibility(View.INVISIBLE);
        }
    }
}

