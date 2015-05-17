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
        // i==> (0, defaultImg.length]
        int i = Utils.randInt(1, defaultImg.length);
        return defaultImg[i - 1];
    }
}
