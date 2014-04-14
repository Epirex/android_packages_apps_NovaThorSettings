/*
 * Copyright (C) 2012 The CyanogenMod Project
 * Copyright (C) 2014 TeamCanjica https://github.com/TeamCanjica
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teamcanjica.settings.device.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import com.teamcanjica.settings.device.DeviceSettings;
import com.teamcanjica.settings.device.R;

public class SettingsFragmentActivity extends PreferenceFragment {
	
	private static final String TAG = "NovaThor_Settings_Settings";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings_preferences);

		getActivity().getActionBar().setTitle(getResources().getString(R.string.settings_name));
		getActivity().getActionBar().setIcon(getResources().getDrawable(R.drawable.advanced_icon));
	}
	
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {

		String key = preference.getKey();

		Log.w(TAG, "key: " + key);

		if (key.equals(DeviceSettings.KEY_DISABLE_RESTORE)){
			DeviceSettings.disableRestore = (((CheckBoxPreference) preference).
					isChecked() ? true : false);
		}

		return true;
	}

	public static void restore(Context context) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		DeviceSettings.disableRestore = sharedPrefs.getBoolean(
				DeviceSettings.KEY_DISABLE_RESTORE, false) ? true : false;

	}
}
