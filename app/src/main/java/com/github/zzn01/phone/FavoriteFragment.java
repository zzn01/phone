package com.github.zzn01.phone;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import utils.Pair;

/**
 * Created by zzn on 5/1/15.
 */
public class FavoriteFragment extends Fragment {

    private ArrayList<Pair<String, Long>> contacts;

    public static FavoriteFragment newInstance() {
        final FavoriteFragment fragment = new FavoriteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        contacts = ContactHelper.getFrequentContacts(inflater.getContext().getContentResolver());

        View custom = inflater.inflate(R.layout.activity_favorite, container, false);


        GridView gridview = (GridView) custom.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(inflater.getContext(), contacts));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                call("1234567");
            }
        });

        return custom;
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        ArrayList<Pair<String,Long>> contacts;

        public ImageAdapter(Context c, ArrayList<Pair<String, Long>> data) {
            mContext = c;
            contacts = data;
        }

        public int getCount() {
            return contacts.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                tv = (TextView) inf.inflate(R.layout.favorite, null);
                tv.setLayoutParams(new GridView.LayoutParams(380, 320));
            //    tv.setPadding(1, 1, 1, 1);
            } else {
                tv = (TextView) convertView;
            }

            Pair<String, Long> p = contacts.get(position);
            int photoId = p.second().intValue();
            if (photoId>0) {
                BitmapDrawable a = new BitmapDrawable(ContactHelper.queryContactImage(mContext.getContentResolver(), photoId));
                tv.setBackground(a);
            }else{
                tv.setBackgroundResource(defaultImg[position % defaultImg.length]);
            }
            tv.setText(p.first());

            return tv;
        }
    }

    private int[] defaultImg = {
            R.drawable.contact1,
            R.drawable.contact2,
            R.drawable.contact3,
            R.drawable.contact4,
            R.drawable.contact5,
            R.drawable.contact6,
            R.drawable.contact7,
    };

    public void call(String phoneNumber) {
        assert (phoneNumber != null && !phoneNumber.equals(""));
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
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
