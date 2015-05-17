package com.github.zzn01.phone;

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
}
