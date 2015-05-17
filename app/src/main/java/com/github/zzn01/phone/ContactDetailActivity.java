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

    private class ContactAdapter<T> extends ArrayAdapter<T> {
        private ArrayList<T> data;
        private LayoutInflater inflater;
//        private static final int VIEW_HOLDER=1;

        protected class ViewHolder{
            TextView Info;
            TextView Type;
            ImageButton Action;
            View Row;
        }

        private View newView(ViewGroup parent){
            ViewHolder vh = new ViewHolder();

            View row = inflater.inflate(R.layout.contact_item, parent, false);

            vh.Info= (TextView) row.findViewById(R.id.info);
            vh.Type = (TextView) row.findViewById(R.id.type);
            vh.Action = (ImageButton) row.findViewById(R.id.action);

            vh.Row = row;

            row.setTag(vh);
            return row;
        }

        public ContactAdapter(Context context, int resource, ArrayList<T> l) {
            super(context, resource, l);

            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            data = l;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder vh;
            if (convertView==null) {
                convertView = newView(parent);
            }

            vh = (ViewHolder)convertView.getTag();

            setRow(vh, data.get(position));

            vh.Action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionClick(v);
                }
            });

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowClick(v);
                }
            });

            return convertView;
        }

        protected void rowClick(View v){

        }

        protected void actionClick(View v){

        }

        protected void setRow(ViewHolder vh, T t){

        }
    }

    private class PhoneAdapter extends ContactAdapter<Common.Phone> {
        public PhoneAdapter(Context context, int resource, ArrayList<Common.Phone> l) {
            super(context, resource, l);
        }

        @Override
        protected void setRow(ViewHolder vh, Common.Phone v){
            vh.Action.setBackgroundResource(R.drawable.ic_message_24dp);

            vh.Info.setText(v.Number);
            vh.Type.setText(v.Type);

            vh.Action.setTag(vh.Action.getId(), v.Number);

            vh.Row.setTag(vh.Row.getId(), v.Number);
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
            String phone_number = (String) v.getTag(v.getId());
            assert (phone_number != null && !phone_number.equals(""));

            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number)));
        }

        private void sendSMS(View v) {
            Log.i(TAG, "send message" + v.getTag(v.getId()));
            String phone_number = (String) v.getTag(v.getId());
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phone_number)));
        }
    }

    private class EmailAdapter extends ContactAdapter<Common.Email> {
        public EmailAdapter(Context context, int resource, ArrayList<Common.Email> l) {
            super(context, resource, l);
        }

        @Override
        protected void rowClick(View v){
            sendEmail(v);
        }

        private void sendEmail(View v) {
            Log.i(TAG, "send email message");
            String email = (String) v.getTag(v.getId());
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));

            startActivity(intent);
        }
        @Override
        protected void setRow(ViewHolder vh, Common.Email v){
            vh.Action.setVisibility(View.INVISIBLE);

            vh.Info.setText(v.Addr);
            vh.Type.setText(v.Type);

            vh.Action.setTag(vh.Action.getId(), v.Addr);

            vh.Row.setTag(vh.Row.getId(), v.Addr);
        }
    }
}

