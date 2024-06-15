package com.pluk.fiveballs.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pluk.fiveballs.R;

public class OptionsActivity extends PreferenceActivity {
	
	private static final String SOUND_ENABLED_PREFERENCE_KEY = "sound";
	private static final String TAG = "OptionsActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    addPreferencesFromResource(R.xml.preferences);
	    
	    String soundPreferenceKey = SOUND_ENABLED_PREFERENCE_KEY;
	    Preference soundPref = findPreference(soundPreferenceKey);
	    soundPref.setOnPreferenceClickListener(onPreferenceClickListener());
	}
	
	private OnPreferenceClickListener onPreferenceClickListener() {
		return new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(Preference preference) {
				String prefKey = preference.getKey();
				Log.d(TAG, "Se ha modificado la preferencia " + prefKey);;
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
				boolean valor = prefs.getBoolean(prefKey, true);
				
				prefs.edit().putBoolean(prefKey, valor);
				prefs.edit().commit();
				Log.d(TAG, "El resultado de la preferencia es: " + valor);
				return true;
			}
		};
	}
}
