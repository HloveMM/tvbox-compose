package com.github.tvbox.osc.util;

import android.util.Log;

public class LOG {
    private static final String TAG = "TVBox";
    public static void e(String msg) { Log.e(TAG, "" + msg); }
    public static void i(String msg) { Log.i(TAG, "" + msg); }
    public static void d(String msg) { Log.d(TAG, "" + msg); }
}
