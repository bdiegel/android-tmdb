package com.honu.tmdb;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Application settings stored in default shared preferences file
 */
public class AppPreferences {

    //private static final String LOG_TAG = AppPreferences.class.getSimpleName();

    public static int getCurrentSortMethod(Context context) {
        String key = context.getString(R.string.key_sort_method_current);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getInt(key, SortOption.POPULARITY);
    }

    public static int getPreferredSortMethod(Context context) {
        String key = context.getString(R.string.key_sort_method_pref);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sharedPreferences.getString(key, "0"));
    }

    public static void setCurrentSortMethod(Context context, int sortMethod) {
        String key = context.getString(R.string.key_sort_method_current);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, sortMethod).commit();
    }

    public static void setPreferredSortMethod(Context context, int sortMethod) {
        String key = context.getString(R.string.key_sort_method_pref);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, "" + sortMethod).commit();
    }
}
