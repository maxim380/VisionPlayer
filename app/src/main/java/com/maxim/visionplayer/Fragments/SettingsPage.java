package com.maxim.visionplayer.Fragments;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.view.View;

import com.maxim.visionplayer.MainActivity;
import com.maxim.visionplayer.R;

public class SettingsPage extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ListPreference mListPreference;
    private MainActivity activity;

    public SettingsPage() {

    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager manager = getPreferenceManager();
        manager.setSharedPreferencesName("colorPrefs");

        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.fragment_preferences, rootKey);

        activity = (MainActivity)getActivity();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference preference = findPreference(key);

        if (preference != null) {
            if(preference.getTitle().toString().equals("App color")) {
                String colourString = sharedPreferences.getString("colorString", "");
                activity.changeColor(activity.getColor());
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}