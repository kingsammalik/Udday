package com.samapps.udday;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.zip.CheckedOutputStream;


public class SharedPrefUtils {

    private static final String PREF_APP = "pref_app";
    private final static String isLogged = "isLogged";

    static void saveLogin(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).edit();
        editor.putBoolean(isLogged,true);
        editor.apply();
    }

    static boolean isLogged(Context context){
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE).getBoolean(isLogged,false);
    }
}
