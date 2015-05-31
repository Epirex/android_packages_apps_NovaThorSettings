/*
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

package com.teamcanjica.settings.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.AttributeSet;

public class MasterEditTextPreference extends EditTextPreference implements OnPreferenceChangeListener {

	private static final String FILE_DT2W_TIMEOUT = "/sys/kernel/bt404/doubletap2wake";
	private static final String FILE_S2WTHRESH_CODINA = "/sys/kernel/bt404/sweep2wake";
	private static final String FILE_S2WTHRESH_JANICE = "/sys/kernel/mxt224e/sweep2wake";
	private static final String FILE_BOOST_DELAY = "/sys/kernel/mali/mali_boost_delay";
	private static final String FILE_BOOST_HIGH = "/sys/kernel/mali/mali_boost_high";
	private static final String FILE_BOOST_LOW = "/sys/kernel/mali/mali_boost_low";
	
	public MasterEditTextPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {

		String key = preference.getKey();

		if (key.equals(DeviceSettings.KEY_DT2W_TIMEOUT)) {
			Utils.writeValue(FILE_DT2W_TIMEOUT, "timeout=" + (String) newValue);
		} else if (key.equals(DeviceSettings.KEY_X_SWEEP2WAKE)) {
			Utils.writeValue((Utils.isCodina() ?
					FILE_S2WTHRESH_CODINA : FILE_S2WTHRESH_JANICE), "threshold_x=" + newValue);
		} else if (key.equals(DeviceSettings.KEY_Y_SWEEP2WAKE)) {
			Utils.writeValue((Utils.isCodina() ?
					FILE_S2WTHRESH_CODINA : FILE_S2WTHRESH_JANICE), "threshold_y=" + newValue);
		} else if (key.equals(DeviceSettings.KEY_BOOST_DELAY)) {
			Utils.writeValue(FILE_BOOST_DELAY, (String) newValue);
		} else if (key.equals(DeviceSettings.KEY_BOOST_HIGHTHRESH)) {
			Utils.writeValue(FILE_BOOST_HIGH, "threshold=" + (String) newValue);
		} else if (key.equals(DeviceSettings.KEY_BOOST_LOWTHRESH)) {
			Utils.writeValue(FILE_BOOST_LOW, "threshold=" + (String) newValue);
		}

		return true;
	}
	
	public static void restore(Context context) {

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		// 2Tap2Wake timeout
		Utils.writeValue(FILE_DT2W_TIMEOUT, sharedPrefs.getString(
				DeviceSettings.KEY_DT2W_TIMEOUT, "250"));

		// Sweep2wake x and y threshold
		if (Utils.isCodina()) {
		Utils.writeValue(FILE_S2WTHRESH_CODINA, "threshold_x=" + sharedPrefs.getString(
				DeviceSettings.KEY_X_SWEEP2WAKE, "120"));

		Utils.writeValue(FILE_S2WTHRESH_CODINA, "threshold_y=" + sharedPrefs.getString(
				DeviceSettings.KEY_Y_SWEEP2WAKE, "240"));
		} else {
			Utils.writeValue(FILE_S2WTHRESH_JANICE, "threshold_x=" + sharedPrefs.getString(
					DeviceSettings.KEY_X_SWEEP2WAKE, "120"));

			Utils.writeValue(FILE_S2WTHRESH_JANICE, "threshold_y=" + sharedPrefs.getString(
					DeviceSettings.KEY_Y_SWEEP2WAKE, "240"));
		}

		// Mali boost delay
		Utils.writeValue(FILE_BOOST_DELAY, sharedPrefs.getString(
				DeviceSettings.KEY_BOOST_DELAY, "200"));

		// Mali boost high threshold
		Utils.writeValue(FILE_BOOST_HIGH, "threshold=" + sharedPrefs.getString(
				DeviceSettings.KEY_BOOST_HIGHTHRESH, "233"));

		// Mali boost low threshold
		Utils.writeValue(FILE_BOOST_LOW, "threshold=" + sharedPrefs.getString(
				DeviceSettings.KEY_BOOST_LOWTHRESH, "64"));

	}

}
