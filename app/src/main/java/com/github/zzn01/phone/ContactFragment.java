package com.github.zzn01.phone;

import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class ContactFragment extends ListFragment {

    public static final String CONTACT_INFO="contact_info";

    private class SimpleContact{
        String name;
        int id;
        String thumbnail;
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

        return inflater.inflate(R.layout.list_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


        Cursor curLog = ContactHelper.execute(getActivity().getContentResolver(), null, null);
        getContacts(curLog);
        curLog.close();

        setListAdapter(new ContactAdapter(getActivity(), R.layout.contact, contacts));
    }

    private void getContacts(Cursor curLog) {
        while (curLog.moveToNext()) {
            SimpleContact contact = new SimpleContact();
            contact.name = curLog.getString(curLog.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
            contact.id = curLog.getInt(curLog.getColumnIndex(ContactsContract.Contacts._ID));
            contact.thumbnail = curLog.getString(curLog.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
            contacts.add(contact);

//            Log.d("test", curLog.getString(curLog.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)));
        }
    }

    private class ContactAdapter extends ArrayAdapter<SimpleContact> {

        LayoutInflater inflater ;

        public ContactAdapter(Context context, int resource,
                         ArrayList<SimpleContact> l) {

            super(context, resource, l);
            inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            if (convertView==null) {
                row = inflater.inflate(R.layout.contact, parent, false);
            }else{
                row = convertView;
            }

            TextView contact = (TextView) row.findViewById(R.id.displayName);
            ImageView thumbnail = (ImageView) row.findViewById(R.id.thumbnail);

            SimpleContact item = contacts.get(position);

            contact.setText(item.name);

            if (item.thumbnail!=null) {
                thumbnail.setImageURI(Uri.parse(item.thumbnail));
            }else{
                thumbnail.setImageResource(Common.getDefaultImg());
            }

            row.setTag(R.id.displayName, item.id);
            row.setOnClickListener(new View.OnClickListener() {
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
