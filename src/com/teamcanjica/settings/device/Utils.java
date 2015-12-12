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

package com.teamcanjica.settings.device;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.SyncFailedException;

import com.teamcanjica.settings.device.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;

public class Utils {

	private static final String TAG = "NovaThor_Settings_Utils";
	// private static final String TAG_READ =
	// "NovaThor_Settings_Utils_Read";
	private static final String TAG_WRITE = "NovaThor_Settings_Utils_Write";

	/**
	 * Write a string value to the specified file.
	 * 
	 * @param filename
	 *            The filename
	 * @param value
	 *            The value
	 */
	public static void writeValue(String filename, String value) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(filename), false);
			fos.write(value.getBytes());
			fos.flush();
			// fos.getFD().sync();
		} catch (FileNotFoundException ex) {
			Log.w(TAG, "file " + filename + " not found: " + ex);
		} catch (SyncFailedException ex) {
			Log.w(TAG, "file " + filename + " sync failed: " + ex);
		} catch (IOException ex) {
			Log.w(TAG, "IOException trying to sync " + filename + ": " + ex);
		} catch (RuntimeException ex) {
			Log.w(TAG, "exception while syncing file: ", ex);
		} finally {
			if (fos != null) {
				try {
					Log.w(TAG_WRITE, "file " + filename + ": " + value);
					fos.close();
				} catch (IOException ex) {
					Log.w(TAG, "IOException while closing synced file: ", ex);
				} catch (RuntimeException ex) {
					Log.w(TAG, "exception while closing file: ", ex);
				}
			}
		}

	}

	/**
	 * Write a string value to the specified file.
	 * 
	 * @param filename
	 *            The filename
	 * @param value
	 *            The value
	 */
	public static void writeValue(String filename, Boolean value) {
		FileOutputStream fos = null;
		String sEnvia;
		try {
			fos = new FileOutputStream(new File(filename), false);
			if (value)
				sEnvia = "1";
			else
				sEnvia = "0";
			fos.write(sEnvia.getBytes());
			fos.flush();
			// fos.getFD().sync();
		} catch (FileNotFoundException ex) {
			Log.w(TAG, "file " + filename + " not found: " + ex);
		} catch (SyncFailedException ex) {
			Log.w(TAG, "file " + filename + " sync failed: " + ex);
		} catch (IOException ex) {
			Log.w(TAG, "IOException trying to sync " + filename + ": " + ex);
		} catch (RuntimeException ex) {
			Log.w(TAG, "exception while syncing file: ", ex);
		} finally {
			if (fos != null) {
				try {
					Log.w(TAG_WRITE, "file " + filename + ": " + value);
					fos.close();
				} catch (IOException ex) {
					Log.w(TAG, "IOException while closing synced file: ", ex);
				} catch (RuntimeException ex) {
					Log.w(TAG, "exception while closing file: ", ex);
				}
			}
		}
	}

	/**
	 * Write the "color value" to the specified file. The value is scaled from
	 * an integer to an unsigned integer by multiplying by 2.
	 * 
	 * @param filename
	 *            The filename
	 * @param value
	 *            The value of max value Integer.MAX
	 */
	public static void writeColor(String filename, int value) {
		writeValue(filename, String.valueOf((long) value * 2));
	}

	public static void showDialog(Context ctx, String title, String message, boolean choice) {
		final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		if (!choice) {
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
					new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
					}
				});
		} else {
			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
						new Thread(new Runnable() {
						    @Override
						    public void run() {
						    	// Reboot
						    	try {
									Runtime.getRuntime().exec(new String[] {
											"su", "-c", "reboot"}).waitFor();
								} catch (IOException e) {
									e.printStackTrace();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
						    }
						}).start();
				}
			});
			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.dismiss();
				}
			});
		}
		alertDialog.show();
	}

	public static String readFile(String filename) {
		String value = null;
		BufferedReader buffread;
		try {
			buffread = new BufferedReader(new FileReader(filename));
			value = buffread.readLine();
			buffread.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * Check if the specified file exists.
	 * 
	 * @param filename
	 *            The filename
	 * @return Whether the file exists or not
	 */
	public static boolean fileExists(String filename) {
		return new File(filename).exists();
	}

	public static boolean isSupported(String FILE) {
		return Utils.fileExists(FILE);
	}

	public static boolean isCodina() {
		if (Build.HARDWARE.equals("samsungcodina") && (Build.DEVICE.equals("codina") || Build.DEVICE.equals("codinap") || Build.MODEL.equals("GT-I8160")
				|| Build.MODEL.equals("GT-I8160P") || Build.PRODUCT.equals("GT-I8160") || Build.PRODUCT.equals("GT-I8160P"))) {
		return true;
		}
		return false;
	}

	public static boolean isJanice() {
		if (Build.HARDWARE.equals("samsungjanice") && (Build.DEVICE.equals("janice") || Build.DEVICE.equals("janicep") || Build.MODEL.equals("GT-I9070") 
				|| Build.MODEL.equals("GT-I9070P") || Build.PRODUCT.equals("GT-I9070") || Build.PRODUCT.equals("GT-I9070P"))) {
		return true;
		}
		return false;
	}
	
	/**
     * Change the theme of an activity
     *
     * The activity whose theme is to be changed
     * @param activity
     *
     * The theme to change to
     * @param theme
     */
	public static void changeTheme(Activity activity, String theme) {
		if (theme.equals("Default")) {
			activity.setTheme(R.style.BaseAppTheme);
		} else if (theme.equals("Holo Light")) {
			activity.setTheme(R.style.HoloLightTheme);
		} else if (theme.equals("Holo Light w/ Dark Action Bar")) {
			activity.setTheme(R.style.HoloLightDarkActionBarTheme);
		} else if (theme.equals("Material Design")) {
			activity.setTheme(R.style.MaterialTheme);
		}
	}
}
