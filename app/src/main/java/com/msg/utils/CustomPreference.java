package com.msg.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CustomPreference {
    private static SharedPreferences mUserPreferences;

    public static void ensureIntializePreference(Context context,String prename) {
        if (mUserPreferences != null) {
            return;
        }
        mUserPreferences = context.getSharedPreferences(prename, 0);
    }

    public static void save(String key, String value) {
        Editor editor = mUserPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void save(String key, int value) {
        Editor editor = mUserPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void save(String key, boolean value) {
        Editor editor = mUserPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void save(String key, long value) {
        Editor editor = mUserPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }
    
    public static void clearAll(){
    	Editor edior = mUserPreferences.edit();
    	edior.clear();
    	edior.commit();
    }
    
    public static String read(String key, String defaultvalue) {
        return mUserPreferences.getString(key, defaultvalue);
    }

    public static int read(String key, int defaultvalue) {
        return mUserPreferences.getInt(key, defaultvalue);
    }

    public static long read(String key, long defaultvalue) {
        return mUserPreferences.getLong(key, defaultvalue);
    }

    public static boolean read(String key, boolean defaultvalue) {
        return mUserPreferences.getBoolean(key, defaultvalue);
    }

}
