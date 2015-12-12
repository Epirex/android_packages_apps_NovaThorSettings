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
import com.teamcanjica.settings.device.Utils;

public class PowerFragmentActivity extends PreferenceFragment {

	private static final String TAG = "NovaThor_Settings_Power";
	private static final String FILE_FAST_CHARGE = "/sys/kernel/fast_charge/force_fast_charge";
	private static final String FILE_VOTG = "/sys/kernel/abb-regu/VOTG";
	private static final String FILE_CHARGER_CONTROL = "/sys/kernel/abb-charger/charger_curr";
	private static final String FILE_CYCLE_CHARGING_CONTROL = "/sys/kernel/abb-fg/fg_cyc";
	private static final String FILE_EOC = "/sys/kernel/abb-chargalg/eoc_status";
	private static final String FILE_REFRESH_BATTERY_STATS = "/sys/kernel/abb-fg/fg_refresh";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.power_preferences);

		getPreferenceScreen().findPreference(DeviceSettings.KEY_CHARGER_CURRENCY).setEnabled(
				((CheckBoxPreference) findPreference("use_charger_control")).isChecked());

		getPreferenceScreen().findPreference(DeviceSettings.KEY_DISCHARGING_THRESHOLD).setEnabled(
				((CheckBoxPreference) findPreference("use_cycle_charging")).isChecked());

		getPreferenceScreen().findPreference(DeviceSettings.KEY_RECHARGING_THRESHOLD).setEnabled(
				((CheckBoxPreference) findPreference("use_cycle_charging")).isChecked());

		getActivity().getActionBar().setTitle(getResources().getString(R.string.power_name));
		getActivity().getActionBar().setIcon(getResources().getDrawable(R.drawable.power_icon));

	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {

		String key = preference.getKey();

		Log.w(TAG, "key: " + key);

		if (key.equals(DeviceSettings.KEY_USB_OTG_POWER)) {
			Utils.writeValue(FILE_VOTG, ((CheckBoxPreference) preference).
					isChecked());
		} else if (key.equals(DeviceSettings.KEY_FAST_CHARGE)) {
			Utils.writeValue(FILE_FAST_CHARGE,
					((CheckBoxPreference) preference).isChecked() ? "on" : "off");
		} else if (key.equals(DeviceSettings.KEY_USE_CHARGER_CONTROL)) {
			Utils.writeValue(FILE_CHARGER_CONTROL,
					((CheckBoxPreference) preference).isChecked() ? "on" : "off");
			getPreferenceScreen().findPreference(DeviceSettings.KEY_CHARGER_CURRENCY).setEnabled(
					((CheckBoxPreference) preference).isChecked());
		} else if (key.equals(DeviceSettings.KEY_USE_CYCLE_CHARGING)) {
			Utils.writeValue(FILE_CYCLE_CHARGING_CONTROL,
					((CheckBoxPreference) preference).isChecked() ? "on" : "off");
			getPreferenceScreen().findPreference(DeviceSettings.KEY_DISCHARGING_THRESHOLD).setEnabled(
					((CheckBoxPreference) preference).isChecked());
			getPreferenceScreen().findPreference(DeviceSettings.KEY_RECHARGING_THRESHOLD).setEnabled(
					((CheckBoxPreference) preference).isChecked());
		} else if (key.equals(DeviceSettings.KEY_EOC)) {
			Utils.showDialog(getActivity(),
					getString(R.string.eoc_subcat_title),
					getString(R.string.eoc_dialog_message) + Utils.readFile(FILE_EOC),
					false);
		} else if (key.equals(DeviceSettings.KEY_REFRESH_BATTERY_STATS)) {
			Utils.writeValue(FILE_REFRESH_BATTERY_STATS, "0");
			Utils.showDialog(getActivity(),
					getString(R.string.refresh_bs_subcat_title),
					getString(R.string.refresh_bs_dialog_message),
					false);
		}

		return true;
	}

	public static void restore(Context context) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		Utils.writeValue(FILE_VOTG, sharedPrefs.getBoolean(
				DeviceSettings.KEY_USB_OTG_POWER, false) ? "1" : "0");

		Utils.writeValue(FILE_FAST_CHARGE, sharedPrefs.getBoolean(
				DeviceSettings.KEY_FAST_CHARGE, false) ? "1" : "0");

		Utils.writeValue(FILE_CHARGER_CONTROL, sharedPrefs.getBoolean(
				DeviceSettings.KEY_USE_CHARGER_CONTROL, false) ? "on" : "off");

		Utils.writeValue(FILE_CYCLE_CHARGING_CONTROL, sharedPrefs.getBoolean(
				DeviceSettings.KEY_USE_CYCLE_CHARGING, false) ? "on" : "off");

	}

}
