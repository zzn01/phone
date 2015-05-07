package com.github.zzn01.phone;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ContactFragment extends ListFragment {

    public static final String CONTACT_INFO="contact_info";

    private class SimpleContact{
        String name;
        int id;
    }

    ArrayList<SimpleContact> contacts;

    public static ContactFragment newInstance() {
        final ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contacts = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        //ContactHelper.testReadAll(inflater.getContext().getContentResolver());

        Cursor curLog = ContactHelper.execute(inflater.getContext().getContentResolver(), null, null);
        getContacts(curLog);
        curLog.close();


        setListAdapter(new ContactAdapter(inflater.getContext(), android.R.layout.simple_list_item_1,
                R.id.contact_name, contacts));

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    private void getContacts(Cursor curLog) {
        while (curLog.moveToNext()) {
            SimpleContact contact = new SimpleContact();
            contact.name = curLog.getString(curLog.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            contact.id = curLog.getInt(curLog.getColumnIndex(ContactsContract.Contacts._ID));
            contacts.add(contact);

//            Log.d("test", curLog.getString(curLog.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)));
        }
    }


    private class ContactAdapter extends ArrayAdapter<SimpleContact> {

        public ContactAdapter(Context context, int resource, int textViewResourceId,
                         ArrayList<SimpleContact> l) {

            super(context, resource, textViewResourceId, l);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            return setList(position, parent);
        }

        private View setList(int position, ViewGroup parent) {
            LayoutInflater inf = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = inf.inflate(R.layout.contact, parent, false);

            TextView contact = (TextView) row.findViewById(R.id.displayName);

            SimpleContact item = contacts.get(position);

            contact.setText(item.name);
            contact.setTag(R.id.displayName, item.id);

            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int id = (int)v.getTag(R.id.displayName);

                    Log.i("contacts:", "id:" + id);
                    show_detail(id);
                }
            });

            return row;
        }

        public void show_detail(int id) {
            Intent intent = new Intent(getActivity(), ContactDetailActivity.class);
            Log.d("Button", "call detail");

            intent.putExtra(CONTACT_INFO, id);

            startActivity(intent);
        }
    }

}
