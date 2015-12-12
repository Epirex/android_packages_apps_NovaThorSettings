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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DeviceSettings extends Activity implements OnItemClickListener{

	// General
	public static final String SHARED_PREFERENCES_BASENAME = "com.teamcanjica.settings.device";
	public static final String ACTION_UPDATE_PREFERENCES = "com.teamcanjica.settings.device.UPDATE";
	public static final String KEY_SWITCH_THEME = "switch_theme";
	public static final String KEY_SEEKBARVAL = "seekbarvalue";
	public static final String KEY_DISABLE_RESTORE = "disable_restore";

	public static final String SELECTION = "selection";
	public static boolean disableRestore;
	public static final String SETTINGS = "settings";

	// Network
	public static final String KEY_HSPA = "hspa";
	public static final String KEY_TCP_CONTROL = "tcp_control";
	public static final String KEY_USE_WIFIPM_MAX = "use_wifipm_max";

	// Power
	public static final String KEY_USB_OTG_POWER = "usb_otg_power";
	public static final String KEY_FAST_CHARGE = "fast_charge";
	public static final String KEY_USE_CHARGER_CONTROL = "use_charger_control";
	public static final String KEY_CHARGER_CURRENCY = "charger_currency";
	public static final String KEY_USE_CYCLE_CHARGING = "use_cycle_charging";
	public static final String KEY_DISCHARGING_THRESHOLD = "discharging_threshold";
	public static final String KEY_RECHARGING_THRESHOLD = "recharging_threshold";
	public static final String KEY_EOC = "eoc_status";
	public static final String KEY_DEEPEST_SLEEP_STATE = "deepest_sleep_state";
	public static final String KEY_REFRESH_BATTERY_STATS = "refresh_battery_stats";

	// Audio
	public static final String KEY_ENABLE_ANAGAIN3 = "enable_anagain3";
	public static final String KEY_ANAGAIN3_CONTROL = "anagain3_control";
	public static final String KEY_ENABLE_HSLDIGGAIN = "enable_hsldiggain";
	public static final String KEY_HSLDIGGAIN_CONTROL = "hsldiggain_control";
	public static final String KEY_ENABLE_HSRDIGGAIN = "enable_hsrdiggain";
	public static final String KEY_HSRDIGGAIN_CONTROL = "hsrdiggain_control";
	public static final String KEY_ENABLE_HSLOWPOW = "enable_hslowpow";
	public static final String KEY_ENABLE_HSDACLOWPOW = "enable_hsdaclowpow";
	public static final String KEY_ENABLE_HSHPEN = "enable_hshpen";
	public static final String KEY_ENABLE_CLASSDHPG = "enable_classdhpg";
	public static final String KEY_CLASSDHPG_CONTROL = "classdhpg_control";
	public static final String KEY_ENABLE_CLASSDWG = "enable_classdwg";
	public static final String KEY_CLASSDWG_CONTROL = "classdwg_control";
	public static final String KEY_ENABLE_ADDIGGAIN2 = "enable_addiggain2";
	public static final String KEY_ADDIGGAIN2_CONTROL = "addiggain2_control";
	public static final String KEY_ENABLE_EARDIGGAIN = "enable_eardiggain";
	public static final String KEY_EARDIGGAIN_CONTROL = "eardiggain_control";
	public static final String KEY_ENABLE_LPA_MODE = "enable_lpa_mode";
	public static final String KEY_LPA_MODE_CONTROL = "lpa_mode_control";
	public static final String KEY_USE_CHARGEPUMP = "enable_chargepump";

	// Screen
	public static final String KEY_USE_2TAP2WAKE = "use_2tap2wake";
	public static final String KEY_DT2W_TIMEOUT = "dt2w_timeout";
	public static final String KEY_USE_SWEEP2WAKE = "use_sweep2wake";
	public static final String KEY_X_SWEEP2WAKE = "x_sweep2wake";
	public static final String KEY_Y_SWEEP2WAKE = "y_sweep2wake";
	public static final String KEY_TOUCHSCREEN = "touchscreen";
	public static final String KEY_TOUCHSCREEN_SENSITIVITY = "touchscreen_sensitivity";
	public static final String KEY_SCREEN_VISUAL = "screen_visual";
	public static final String KEY_PANEL_GAMMA = "panel_gamma";
	public static final String KEY_FSYNC_CAT = "fsync_cat";
	public static final String KEY_FSYNC_MODE = "fsync_mode";
	public static final String KEY_SCREEN_OFF = "screen_off";
	public static final String KEY_POWER_MENU = "power_menu";
	public static final String KEY_MIN_BRIGHTNESS = "min_brightness";

	// GPU
	public static final String KEY_DISABLE_AUTOBOOST = "disable_autoboost";
	public static final String KEY_SET_GPU_CLOCK = "set_gpu_clock";
	public static final String KEY_DISABLE_FULLSPEED = "disable_fullspeed";
	public static final String KEY_BOOST_DELAY = "boost_delay";
	public static final String KEY_GPU_VOLTAGE = "gpu_voltage";
	public static final String KEY_MALI_L2MR = "mali_l2_mr";
	public static final String KEY_MALI_PAM = "mali_pam";
	public static final String KEY_BOOST_HIGHTHRESH = "boost_highthresh";
	public static final String KEY_BOOST_LOWTHRESH = "boost_lowthresh";
	public static final String KEY_SET_HIGH_CLOCK = "set_high_clock";
	public static final String KEY_SET_LOW_CLOCK = "set_low_clock";

	// I/O
	public static final String KEY_USE_SPI_CRC = "use_spi_crc";
	public static final String KEY_SCHED_MC = "sched_mc";
	public static final String KEY_READAHEADKB = "readaheadkb";
	public static final String KEY_BOOTTIME = "boottime";

	// Advanced
	public static final String KEY_SWITCH_STORAGE = "switch_storage";
	public static final String KEY_BACKLIGHT = "backlight";
	public static final String KEY_DISABLE_BLN = "disable_bln";
	public static final String KEY_BURNING_LED = "burning_led";
	public static final String KEY_ENABLE_VOLTAGE = "enable_voltage";
	public static final String KEY_CPU_VOLTAGE = "cpu_voltage";
	public static final String KEY_USE_ACCELEROMETER_CALIBRATION = "use_accelerometer_calibration";
	public static final String KEY_CALIBRATE_ACCELEROMETER = "calibrate_accelerometer";

	// ListView
	public static final Integer[] images = { R.drawable.network,
        R.drawable.power, R.drawable.audio, R.drawable.screen,
		R.drawable.gpu, R.drawable.io, R.drawable.advanced };

	public String[] titles;

	ListView listView;
	List<RowItem> rowItems;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		titles = getResources().getStringArray(R.array.settings_entries);

		// Change theme
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		Utils.changeTheme(this, sharedPrefs.getString(
                DeviceSettings.KEY_SWITCH_THEME, "Default"));
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_settings);

		rowItems = new ArrayList<RowItem>();
		for (int i = 0; i < titles.length; i++) {
			RowItem item = new RowItem(images[i], titles[i]);
			rowItems.add(item);
		}

		listView = (ListView) findViewById(R.id.settingsList);
		CustomListViewAdapter adapter = new CustomListViewAdapter(this,
            R.layout.list_item, rowItems);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int index,
			long id) {
		Intent intent = new Intent(this, ContainerActivity.class);
		intent.putExtra(SELECTION, index);
		startActivity(intent);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.device_settings, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_about:
	        	startActivity(new Intent(this, AboutActivity.class));
	            return true;
	        case R.id.action_settings:
	        	startActivity(new Intent(this, ContainerActivity.class));
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
