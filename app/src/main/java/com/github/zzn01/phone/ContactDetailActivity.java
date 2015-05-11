package com.github.zzn01.phone;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class ContactDetailActivity extends Activity {
    private static final String TAG = "ContactDetail";

    private ContactHelper.ContactInfo contactInfo;
    private  int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        id = intent.getIntExtra(ContactFragment.CONTACT_INFO, -1);

        contactInfo = ContactHelper.getContact(getContentResolver(), id);


        setContentView(R.layout.contact_detail_activity);


        ArrayList phone = contactInfo.phone;

        phone.addAll(contactInfo.org);
        if (!phone.isEmpty()) {
            ListView lv = (ListView) findViewById(R.id.phone_list);
            lv.setAdapter(new PhoneAdapter(this, android.R.layout.simple_list_item_1,
                    R.id.info, phone, "phone"));
        }


        if (!contactInfo.email.isEmpty()) {
            ListView lv2 = (ListView) findViewById(R.id.email_list);
            lv2.setAdapter(new EmailAdapter(this, android.R.layout.simple_list_item_1,
                    R.id.info, contactInfo.email, "email"));
        }


        /*
        ContactView view = new ContactView(this);
        if (!contactInfo.phone.isEmpty()) {
            view.addView(contactInfo.phone, "phone");
        }

        if (!contactInfo.email.isEmpty()) {
            view.addView(contactInfo.email, "email");
        }
        if (!contactInfo.org.isEmpty()) {
            view.addView(contactInfo.org, "email");
        }
        view.setView();
        */
    }

    /*
    private class ContactView {
        private LayoutInflater inflater;
        private LinearLayout parent;
        private Context context;
        private ArrayList<View> views;

        public ContactView(Context c){
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            parent = (LinearLayout) inflater.inflate(R.layout.contact_detail_activity, null);
            context = c;

            views = new ArrayList<>();
        }

        public void addView(ArrayList<String> list, String id) {

            View custom = inflater.inflate(R.layout.contact_detail_fragment, null);
            ListView lv = (ListView) custom.findViewById(R.id.contact_list);
            lv.setAdapter(new PhoneAdapter(context, android.R.layout.simple_list_item_1,
                    R.id.info, list, id));

            parent.addView(custom);
            parent.addView(getDivider());
        }

        public void setView() {
            setContentView(parent);
        }

        private View getDivider() {
            ImageView divider = new ImageView(getApplicationContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 3);
            divider.setLayoutParams(lp);
            divider.setBackgroundColor(Color.DKGRAY);
            return divider;
        }
    }
    */


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

    private class PhoneAdapter extends ArrayAdapter<String> {
        private ArrayList<String> data;
        String dataType;

        public PhoneAdapter(Context context, int resource, int textViewResourceId,
                         ArrayList<String> l, String type) {
            super(context, resource, textViewResourceId, l);

            data = l;
            dataType = type;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return setList(position, parent);
        }

        private View setList(int position, ViewGroup parent) {
            LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inf.inflate(R.layout.contact_detail, parent, false);

            TextView info = (TextView) row.findViewById(R.id.info);
            TextView type = (TextView) row.findViewById(R.id.type);
            Button action = (Button) row.findViewById(R.id.action);

            String v = data.get(position);

            info.setText(v);
            type.setText(dataType);

            action.setTag(v);
            row.setTag(v);

            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i(TAG, "send message" + v.getTag());

                    String phone_number = (String)v.getTag();

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone_number)));
                }
            });

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "call message");
                    String phone_number = (String)v.getTag();
                    assert (phone_number != null && !phone_number.equals(""));

                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number)));
                }
            });

            return row;
        }
    }

    private class EmailAdapter extends ArrayAdapter<String> {
        private ArrayList<String> data;
        String dataType;

        public EmailAdapter(Context context, int resource, int textViewResourceId,
                         ArrayList<String> l, String type) {
            super(context, resource, textViewResourceId, l);

            data = l;
            dataType = type;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return setList(position, parent);
        }

        private View setList(int position, ViewGroup parent) {
            LayoutInflater inf = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inf.inflate(R.layout.contact_detail, parent, false);

            TextView info = (TextView) row.findViewById(R.id.info);
            TextView type = (TextView) row.findViewById(R.id.type);
            Button action = (Button) row.findViewById(R.id.action);

            String v = data.get(position);

            info.setText(v);
            type.setText(dataType);

            action.setTag(v);
            action.setVisibility(View.INVISIBLE);

            row.setTag(v);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = (String) v.getTag();
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));

                    startActivity(intent);

                    Log.i(TAG, "send email message");
                }
            });

            return row;
        }
    }
}

