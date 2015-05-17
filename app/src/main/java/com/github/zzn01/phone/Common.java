package com.github.zzn01.phone;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import utils.Utils;

/**
 * Created by zzn on 5/17/15.
 */
public class Common {
    private final static int[] defaultImg = {
            R.drawable.contact1,
            R.drawable.contact2,
            R.drawable.contact3,
            R.drawable.contact4,
            R.drawable.contact5,
            R.drawable.contact6,
            R.drawable.contact7,
    };

    public static int getDefaultImg(){
        // i==> [1, defaultImg.length]
        int i = Utils.randInt(1, defaultImg.length);
        return defaultImg[i - 1];
    }

    public static int getDefaultImg(int id){
        return defaultImg[id % defaultImg.length];
    }

    public static int getDefaultImg(String name){
        int i = name.charAt(0) + name.charAt(name.length() - 1);
        return defaultImg[i % defaultImg.length];
    }

    public static class CallLogEntry {
        public String name;
        public String number;
        public String lastDate;
        public int photoID;
        ArrayList<Integer> types;

        CallLogEntry() {
            types = new ArrayList<>();
        }

        @Override
        public String toString(){

            return "name:"+name+",number:"+number+", lastDate:"+lastDate;
        }
    }

    public static class ParcelableCallLogEntry implements Parcelable {
        private CallLogEntry entry ;

        ParcelableCallLogEntry(CallLogEntry e){
            entry = e;
        }

        CallLogEntry getEntry(){
            return entry;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeString(entry.name);
            out.writeString(entry.number);
            out.writeString(entry.lastDate);
            out.writeInt(entry.photoID);
        }

        public static final Parcelable.Creator<ParcelableCallLogEntry> CREATOR
                = new Parcelable.Creator<ParcelableCallLogEntry>() {
            public ParcelableCallLogEntry createFromParcel(Parcel in) {
                return new ParcelableCallLogEntry(in);
            }

            public ParcelableCallLogEntry[] newArray(int size) {
                return new ParcelableCallLogEntry[size];
            }
        };

        private ParcelableCallLogEntry(Parcel in) {
            entry = new CallLogEntry();

            entry.name = in.readString();
            entry.number = in.readString();
            entry.lastDate = in.readString();
            entry.photoID = in.readInt();
        }
    }
}
