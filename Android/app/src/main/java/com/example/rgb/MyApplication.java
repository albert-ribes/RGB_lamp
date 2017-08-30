package com.example.rgb;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by albert on 29/12/2016.
 */

public class MyApplication extends Application {


    /*Context Management*/
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    /*Preferences Managament*/
    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        System.out.println("Storing preferences: " + key + "=" + value);
        editor.commit();
    }

    public static String getDefaults(String key, Context context) {
        String value;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        value = preferences.getString(key, null);
        System.out.println("Getting preferences: " + key + "=" + value);
        return value;
    }

}