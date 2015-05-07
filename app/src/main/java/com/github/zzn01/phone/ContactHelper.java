package com.github.zzn01.phone;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

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
        ArrayList<String> phone;
        ArrayList<String> email;
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
            for (String s : phone) {
                out.append(",phone:"+s);
            }

            for (String s : email) {
                out.append(",email:"+s);
            }

            for (String s : addr) {
                out.append(",addr:"+s);
            }

            for (String s : phone) {
                out.append(",org:"+s);
            }
            return out.toString();
        }
    }

    public static ContactInfo getContact(ContentResolver resolver, int id){
        ContactInfo contat = new ContactInfo();

        Uri uri = Uri.parse("content://com.android.contacts/contacts/"+id+"/data");
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data.DATA1, ContactsContract.Data.MIMETYPE}, null, null, null);
        while(cursor.moveToNext()){
            String data = cursor.getString(cursor.getColumnIndex("data1"));
            if(cursor.getString(cursor.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/name")){       //如果是名字
                contat.name = data;
            }
            else if(cursor.getString(cursor.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/phone_v2")){  //如果是电话
                contat.phone.add(data);
            }
            else if(cursor.getString(cursor.getColumnIndex("mimetype")).equals("vnd.android.cursor.item/email_v2")){  //如果是email
                contat.email.add(data);
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
        //获得_id属性
        Cursor cursor = resolver.query(uri, new String[]{ContactsContract.Data._ID}, null, null, null);
        while(cursor.moveToNext()){
            StringBuilder buf = new StringBuilder();
            //获得id并且在data中寻找数据
            int id = cursor.getInt(0);
            Log.i("Contacts", String.valueOf(getContact(resolver, id)));
        }
    }
}
