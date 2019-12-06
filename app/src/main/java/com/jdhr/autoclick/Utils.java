package com.jdhr.autoclick;

import android.os.Build;

public class Utils {

    public static boolean isVersionAbove(int version) {
        return Build.VERSION.SDK_INT >= version;
    }
}
