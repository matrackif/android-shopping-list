package com.example.filipmatracki.shoppinglist;

import android.app.Fragment;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;



public class SettingsScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //we only create the fragment once, if state is restored then do nothing in order to retain state
        Fragment existingFragment = getFragmentManager().findFragmentById(android.R.id.content);
        if (existingFragment == null || !existingFragment.getClass().equals(SettingsFragment.class))
        {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .commit();
            Log.d("debug", "in onCreate() SettingsScreen.java- Creating a settings fragment");

        }

        Log.d("debug", "in onCreate() SettingsScreen.java- Settings fragment already created");

    }

    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

        }
    }
    }

