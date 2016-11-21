package com.example.filipmatracki.shoppinglist;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Set;

/**
 * Created by Filip Matracki on 9/9/2016.
 */
public class PrefSingleton {
    private static PrefSingleton mInstance;
    public static final String DEFAULT_STRING_VALUE = "";
    public static final int DEFAULT_INT_VALUE = 0;
   // public static final String TO_DO_DATES = "TO_DO_DATES";
    //public static final String TO_DO_TASKS = "TO_DO_TASKS";
   // public static final String TO_DO_IMAGE_PATHS = "TO_DO_IMAGE_PATHS";
      public static final String TO_DO_ELEMENTS = "TO_DO_ELEMENTS";
    private Context mContext;
    private SharedPreferences mMyPreferences;

    private PrefSingleton() {
    }

    public static PrefSingleton getInstance() {
        if (mInstance == null)
            mInstance = new PrefSingleton();
        return mInstance;
    }

    public void initialize(Context ctxt) {
        mContext = ctxt;
        mMyPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

    }

    public void writeStringToPref(String key, String value) {
        SharedPreferences.Editor e = mMyPreferences.edit();
        e.putString(key, value);
        e.commit();
    }
    public void writeStringSetToPref(String key, Set<String> values){
        SharedPreferences.Editor e = mMyPreferences.edit();
        e.putStringSet(key,values);
        e.commit();
    }

    public String getStringFromPref(String key) {
        return mMyPreferences.getString(key, DEFAULT_STRING_VALUE);
    }
    public int getIntFromPref(String key){
        return mMyPreferences.getInt(key,DEFAULT_INT_VALUE);
    }
    public Set<String> getStringSetFromPref(String key){
        return mMyPreferences.getStringSet(key,null);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mMyPreferences.getBoolean(key, defaultValue);
    }

    public boolean getBooleanFromResId(int resId) {
        return mMyPreferences.getBoolean(mContext.getString(resId, false), false);
    }

    public int getBackroundColorAsInt(){

        int hexColor = getIntFromPref(mContext.getString(R.string.pref_background_color));
        return hexColor;
    }
    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mMyPreferences.registerOnSharedPreferenceChangeListener(listener);
    }
    public Set<String> getSetOfElementsFromSharedPref(){
        return getStringSetFromPref(TO_DO_ELEMENTS);
    }
    public void writeSetOfElementsToSharedPref(Set<String> elements){
        writeStringSetToPref(TO_DO_ELEMENTS,elements);
    }
    public boolean isClearButtonEnabled(){
        return mMyPreferences.getBoolean(mContext.getString(R.string.pref_clear_button),false);
    }


    public void clearAllSharedPrefs() {
        mMyPreferences.edit().clear().commit();
    }
}
