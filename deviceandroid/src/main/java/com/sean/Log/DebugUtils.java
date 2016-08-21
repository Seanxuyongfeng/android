package com.sean.Log;

import android.util.Log;

/**
 * Created by Sean on 2016/5/16.
 */
public class DebugUtils {
    public static final String TAG = "SXDEXLSOLLD";

    public static final boolean DEBUG = true;

    public static void i(String tag, String msg){
        Log.i(TAG, tag + " " + msg);
    }

    public static void v(String tag, String msg){
        Log.v(TAG, tag + " " + msg);
    }

    public static void w(String tag, String msg){
        Log.w(TAG, tag + " " + msg);
    }

    public static void e(String tag, String msg){
        Log.e(TAG, tag + " " + msg);
    }

}
