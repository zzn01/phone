package com.github.zzn01.phone;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.LinearLayout;

import java.io.InputStream;
import java.util.ArrayList;

import utils.Pair;

/**
 * Created by zzn on 4/1/15.
 */
public class ContactHelper {

    private static final String[] projection = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI,
            ContactsContract.Contacts.SORT_KEY_PRIMARY
    };
    // Defines the text expression
    private static final String SELECTION = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" ;

    // Defines a variable for the search string
    private String mSearchString;
    // Defines the array to hold values that replace the ?
    private String[] mSelectionArgs = { mSearchString };

    public static Bitmap queryContactImage(ContentResolver cr, int imageDataRow) {

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Photo.PHOTO
        };

        String[] data = new String[]{
                Integer.toString(imageDataRow)
        };

        Cursor c = cr.query(ContactsContract.Data.CONTENT_URI, projection,
                            ContactsContract.Data._ID + "=?", data, null);
        byte[] imageBytes = null;
        if (c != null) {
            if (c.moveToFirst()) {
                imageBytes = c.getBlob(0);
            }
            c.close();
        }

        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return null;
        }
    }

    public static Cursor execute(ContentResolver cr, String select, String[] args) {
        return execute(cr, select, args, projection);
    }

    public static Cursor execute(ContentResolver cr, String select, String[] args, String[] _projection) {
        if (_projection == null) {
            _projection = projection;
        }

        return cr.query(ContactsContract.Contacts.CONTENT_URI, _projection, select, args, ContactsContract.Contacts.SORT_KEY_PRIMARY);
    }

    public static class ContactInfo {
        String name;
        ArrayList<Common.Phone> phone;
        ArrayList<Common.Email> email;
        ArrayList<String> addr;
        ArrayList<String> org;

        ContactInfo() {
            phone = new ArrayList<>();
            email = new ArrayList<>();
            addr = new ArrayList<>();
            org = new ArrayList<>();
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            out.append("name:"+name);
            for (Common.Phone p : phone) {
                out.append("," + p.Type + ":" + p.Number);
            }

            for (Common.Email p : email) {
                out.append("," + p.Type + ":" + p.Addr);
            }

            for (String s : addr) {
                out.append(",addr:"+s);
            }
            for (String s : org) {
                out.append(",org:"+s);
            }

            return out.toString();
        }
    }

    public static ArrayList<Common.Phone> getPhone(ContentResolver resolver, int id){
        ArrayList<Common.Phone> phone = new ArrayList<>();

        String[] projection = {
                ContactsContract.Data.DATA1,
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.DATA2,
                ContactsContract.Data.IS_PRIMARY
        };
        String selectPhone = ContactsContract.Data.MIMETYPE + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "'";

        Uri uri = Uri.parse("content://com.android.contacts/contacts/"+id+"/data");

        Cursor cursor = resolver.query(uri, projection, selectPhone, null, null);
        while(cursor.moveToNext()){
            phone.add(new Common.Phone(cursor.getString(0), cursor.getString(2), cursor.getInt(3)));
        }
        cursor.close();
        return phone;
    }


    public static ContactInfo getContact(ContentResolver resolver, int id){
        ContactInfo contat = new ContactInfo();
        String[] projection = {
                ContactsContract.Data.DATA1,
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.DATA2,
                ContactsContract.Data.IS_SUPER_PRIMARY
        };

        Uri uri = Uri.parse("content://com.android.contacts/contacts/"+id+"/data");
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        while(cursor.moveToNext()){
            String data = cursor.getString(cursor.getColumnIndex("data1"));
            if(cursor.getString(cursor.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/name")){       //如果是名字
                contat.name = data;
            }
            else if(cursor.getString(cursor.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/phone_v2")){  //如果是电话

                contat.phone.add(new Common.Phone(data, cursor.getString(2), cursor.getInt(3)));
            }
            else if(cursor.getString(cursor.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/email_v2")){  //如果是email
                contat.email.add(new Common.Email(data, cursor.getString(2)));
            }
            else if(cursor.getString(cursor.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/postal-address_v2")){ //如果是地址
                contat.addr.add(data);
            }
            else if(cursor.getString(cursor.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/organization")){  //如果是组织
                contat.org.add(data);
            }
        }
        cursor.close();
        return contat;
    }

    //读取通讯录的全部的联系人
    //需要先在raw_contact表中遍历id，并根据id到data表中获取数据
    public static void testReadAll(ContentResolver resolver){
        //uri = content://com.android.contacts/contacts
        Uri uri = Uri.parse("content://com.android.contacts/contacts"); //访问raw_contacts表

        uri = ContactsContract.Contacts.CONTENT_FREQUENT_URI;
        //获得_id属性
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data._ID}, null, null, null);
        while(cursor.moveToNext()){
            StringBuilder buf = new StringBuilder();
            //获得id并且在data中寻找数据
            int id = cursor.getInt(0);
            Log.i("Contacts", String.valueOf(getContact(resolver, id)));
        }
    }

    public static Uri getPhotoUri(Long photoId){
        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, photoId);
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

//    public static final String FREQUENT_ORDER_BY = ContactsContract.DataUsageStatColumns.TIMES_USED + " DESC," + ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static ArrayList<Common.ContactShow> getFrequentContacts(ContentResolver resolver) {
        String[] projection = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.PHOTO_ID
        };

        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_FREQUENT_URI,
                projection, null, null, null);

        ArrayList<Common.ContactShow> frequentContacts = new ArrayList<>();

        while(cursor.moveToNext()){
            //获得id并且在data中寻找数据
            int id = cursor.getInt(0);
            Log.i("list",String.valueOf(cursor.getLong(2)));

            frequentContacts.add(new Common.ContactShow(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)));
        }
        cursor.close();

        return frequentContacts;
    }
}
