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

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.teamcanjica.settings.device.fragments.AdvancedFragmentActivity;
import com.teamcanjica.settings.device.fragments.AudioFragmentActivity;
import com.teamcanjica.settings.device.fragments.GPUFragmentActivity;
import com.teamcanjica.settings.device.fragments.IOFragmentActivity;
import com.teamcanjica.settings.device.fragments.NetworkFragmentActivity;
import com.teamcanjica.settings.device.fragments.ScreenFragmentActivity;
import com.teamcanjica.settings.device.fragments.SettingsFragmentActivity;
import com.teamcanjica.settings.device.fragments.PowerFragmentActivity;

public class ContainerActivity extends Activity {

	FrameLayout frameLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Change theme
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		Utils.changeTheme(this, sharedPrefs.getString(
				DeviceSettings.KEY_SWITCH_THEME, "Material Design"));
		
		setContentView(R.layout.container);
		frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
		
		Fragment fragment = new SettingsFragmentActivity();
		try {
		switch (getIntent().getExtras().getInt(DeviceSettings.SELECTION)) {
			case 0:
				// Network
				fragment = new NetworkFragmentActivity();
				break;
			case 1:
				// Power
				fragment = new PowerFragmentActivity();
				break;
			case 2:
				// Audio
				fragment = new AudioFragmentActivity();
				break;
			case 3:
				// Screen
				fragment = new ScreenFragmentActivity();
				break;
			case 4:
				// GPU
				fragment = new GPUFragmentActivity();
				break;
			case 5:
				// I/O
				fragment = new IOFragmentActivity();
				break;
			case 6:
				// Advanced
				fragment = new AdvancedFragmentActivity();
				break;
			default:
				break;
		}
		} catch (NullPointerException e) {
			getFragmentManager().beginTransaction().
				replace(R.id.frameLayout, fragment, "SETTINGS_FRAGMENT").commit();
		}

		getFragmentManager().beginTransaction().
			replace(R.id.frameLayout, fragment).commit();

		getActionBar().setDisplayHomeAsUpEnabled(true);

		super.onCreate(savedInstanceState);
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
	        	getFragmentManager().beginTransaction().
	        		replace(R.id.frameLayout, new SettingsFragmentActivity(), "SETTINGS_FRAGMENT").commit();
	        	return true;
	        case android.R.id.home:
	        	NavUtils.navigateUpFromSameTask(this);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		try {
			if (getFragmentManager().
					findFragmentByTag("SETTINGS_FRAGMENT").isVisible()) {
				menu.removeItem(R.id.action_settings);
			}
		} catch (NullPointerException e) {

		}
		return true;
	}
}
