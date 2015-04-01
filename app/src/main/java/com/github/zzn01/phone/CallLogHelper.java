package com.github.zzn01.phone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.util.Log;

public class CallLogHelper {
    private static final String logOrder = android.provider.CallLog.Calls.DATE + " DESC";
    private static final String contentUri = "content://call_log/calls";
    private static final String[] projection = {
            CallLog.Calls.CACHED_NAME,
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DURATION,
            CallLog.Calls.DATE,
            CallLog.Calls.CACHED_PHOTO_ID,
    };


    public static Cursor execute(ContentResolver cr, String select, String[] args) {
        return execute(cr, select, args, projection);
    }

    public static Cursor execute(ContentResolver cr, String select, String[] args, String[] _projection) {
        Uri callUri = Uri.parse(contentUri);
        if (_projection == null) {
            _projection = projection;
        }

        return cr.query(callUri, _projection, select, args, logOrder);
    }

    public static Cursor getAllCallLogs(ContentResolver cr) {
        // reading all data in descending order according to DATE
        return execute(cr, null, null);
    }

    public static Cursor getCallLogsByNumber(ContentResolver cr, String number) {
        String select = CallLog.Calls.NUMBER + "=?";
        String[] args = {number};

        return execute(cr, select, args);
    }

    public static Cursor getCallLogsByName(ContentResolver cr, String name) {
        String select = android.provider.CallLog.Calls.CACHED_NAME + "=?";
        String[] args = {name};

        return execute(cr, select, args);
    }

    public static void insertPlaceholderCall(ContentResolver contentResolver,
                                             String name, String number) {
        ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, number);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        values.put(CallLog.Calls.DURATION, 0);
        values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
        values.put(CallLog.Calls.NEW, 1);
        values.put(CallLog.Calls.CACHED_NAME, name);
        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
        values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");
        Log.d("Call Log", "Inserting call log placeholder for " + number);
        contentResolver.insert(CallLog.Calls.CONTENT_URI, values);
    }


}
