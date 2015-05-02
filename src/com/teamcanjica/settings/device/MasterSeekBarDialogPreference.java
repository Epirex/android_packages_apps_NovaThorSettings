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
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.teamcanjica.settings.device.fragments.AudioFragmentActivity;

public class MasterSeekBarDialogPreference extends DialogPreference implements OnPreferenceChangeListener
{
    private static final int DEFAULT_MIN_PROGRESS = 0;
    private static final int DEFAULT_MAX_PROGRESS = 100;
    private static final int DEFAULT_PROGRESS = 0;

    private int mMinProgress;
    private int mMaxProgress;
    private int mProgress;
    private int stepSize = 0;
    private CharSequence mProgressTextSuffix;
    private TextView mProgressText;
    private SeekBar mSeekBar;
    private boolean isFloat = false;
	private static Context mCtx;

    private static final String FILE_READAHEADKB = "/sys/block/mmcblk0/queue/read_ahead_kb";
    private static final String FILE_CPU_VOLTAGE = "/sys/kernel/liveopp/arm_step";
    private static final String FILE_CYCLE_CHARGING = "/sys/kernel/abb-fg/fg_cyc";
    private static final String FILE_GPU_VOLTAGE = "/sys/kernel/mali/mali_dvfs_config";
    private static final String FILE_MIN_BRIGHTNESS = "/sys/module/ktd259_bl/parameters/min_brightness";

    private static final int defaultGPUVoltValues[] = {0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 
    	0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x26, 0x29, 0x29, 0x2B, 0x2B, 0x2C, 0x2C, 0x2D, 0x2D, 0x2E, 0x2E, 0x2F, 0x2F, 0x2F, 0x30, 0x30, 0x31, 0x31, 0x31, 0x32, 0x32, 0x32, 0x33, 0x33, 0x33, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x34, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F};
    private static final int defaultCPUVoltValues[] = {0x16, 0x17, 0x18, 0x19, 0x1A, 0x1E, 0x20, 0x22, 0x24, 0x29, 0x2F, 0x34, 0x36, 0x36, 0x37, 0x37};
    private static final double voltSteps[] = {0, 12.5, 25, 37.5, 50, 62.5, 75, 87.5, 100};

    public MasterSeekBarDialogPreference(Context context) {
        this(context, null);
    }

    public MasterSeekBarDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnPreferenceChangeListener(this);

        // Get attributes specified in XML
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MasterSeekBarDialogPreference, 0, 0);
        try {
            setMinProgress(a.getInteger(R.styleable.MasterSeekBarDialogPreference_min, DEFAULT_MIN_PROGRESS));
            setMaxProgress(a.getInteger(R.styleable.MasterSeekBarDialogPreference_android_max, DEFAULT_MAX_PROGRESS));
            setProgressTextSuffix(a.getString(R.styleable.MasterSeekBarDialogPreference_progressTextSuffix));
            stepSize = a.getInteger(R.styleable.MasterSeekBarDialogPreference_stepSize, 1);
            isFloat = a.getBoolean(R.styleable.MasterSeekBarDialogPreference_isFloat, false);
        }
        finally {
            a.recycle();
        }

        // Set layout
        setDialogLayoutResource(R.layout.preference_seek_bar_dialog);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setDialogIcon(null);
        
        mCtx = context;
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue) {
        setProgress(restore ? getPersistedInt(DEFAULT_PROGRESS) : (Integer) defaultValue);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, DEFAULT_PROGRESS);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        TextView dialogMessageText = (TextView) view.findViewById(R.id.text_dialog_message);
        dialogMessageText.setText(getDialogMessage());

        mProgressText = (TextView) view.findViewById(R.id.text_progress);

        mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update text that displays the current SeekBar progress value
                // Note: This does not persist the progress value. that is only ever done in setProgress()
            	String progressStr;
            	double mStepSize = stepSize;
            	if (isFloat) {
            		mStepSize = (double) stepSize / 10;
            	}
            	if (mStepSize >= 1) {
            		progressStr = String.valueOf(Math.round((progress + mMinProgress) / mStepSize) * mStepSize);
            		if (!isFloat) {
            			progressStr = progressStr.substring(0, progressStr.length()-2);
            		}
        		} else {
        			progressStr = String.valueOf(progress + mMinProgress);
        		}
            	mProgressText.setText(mProgressTextSuffix == null ? progressStr : 
            			progressStr.concat(mProgressTextSuffix.toString()));
            }
        });
        mSeekBar.setMax(mMaxProgress - mMinProgress);
        mSeekBar.setProgress(mProgress - mMinProgress);
       // mSeekBar.setKeyProgressIncrement(stepSize);
    }

    public int getMinProgress() {
        return mMinProgress;
    }

    public void setMinProgress(int minProgress) {
        mMinProgress = minProgress;
        setProgress(Math.max(mProgress, mMinProgress));
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        mMaxProgress = maxProgress;
        setProgress(Math.min(mProgress, mMaxProgress));
    }

    public int getProgress() {
        return mProgress;
    }

    public void setProgress(int progress) {
        progress = Math.max(Math.min(progress, mMaxProgress), mMinProgress);
        double mStepSize = stepSize;
        if (isFloat) {
        	mStepSize = (double) stepSize / 10;
        }
        if (progress != mProgress) {
        	if (mStepSize >= 1) {
        		progress = (int) (Math.round(progress / mStepSize) * mStepSize);
        	}
        	mProgress = progress;
        	persistInt(progress);
        	notifyChanged();
        }
    }

    public CharSequence getProgressTextSuffix() {
        return mProgressTextSuffix;
    }

    public void setProgressTextSuffix(CharSequence progressTextSuffix) {
        mProgressTextSuffix = progressTextSuffix;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
 
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            int seekBarProgress = mSeekBar.getProgress() + mMinProgress;
            if (callChangeListener(seekBarProgress)) {
                setProgress(seekBarProgress);
            }
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        // Save the instance state so that it will survive screen orientation changes and other events that may temporarily destroy it
        final Parcelable superState = super.onSaveInstanceState();
 
        // Set the state's value with the class member that holds current setting value
        final SavedState myState = new SavedState(superState);
        myState.minProgress = getMinProgress();
        myState.maxProgress = getMaxProgress();
        myState.progress = getProgress();

        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        // Check whether we saved the state in onSaveInstanceState()
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save the state, so call superclass
            super.onRestoreInstanceState(state);
            return;
        }

        // Restore the state
        SavedState myState = (SavedState) state;
        setMinProgress(myState.minProgress);
        setMaxProgress(myState.maxProgress);
        setProgress(myState.progress);

        super.onRestoreInstanceState(myState.getSuperState());
    }

    private static class SavedState extends BaseSavedState {
        int minProgress;
        int maxProgress;
        int progress;
 
        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
 
            minProgress = source.readInt();
            maxProgress = source.readInt();
            progress = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
 
            dest.writeInt(minProgress);
            dest.writeInt(maxProgress);
            dest.writeInt(progress);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    // PREFERENCE STUFF STARTS HERE
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {

		String key = preference.getKey();
		
		// CPU Voltage
		if (key.equals(DeviceSettings.KEY_CPU_VOLTAGE)) {
			double currentCPUVolt = Math.round((Integer) newValue / 12.5) * 12.5;
			int i;
			for (i = 0; voltSteps[i] != Math.abs(currentCPUVolt); i++) {
			}
			if (currentCPUVolt < 0) {
				i *= -1;
			}
			for (int j = 0; j <= defaultCPUVoltValues.length - 1; j++) {
			    Utils.writeValue(FILE_CPU_VOLTAGE + (j < 10 ? "0" + String.valueOf(j) : String.valueOf(j)), 
			    		"varm=0x" + Integer.toHexString(defaultCPUVoltValues[j] + i));
			}
		}
		
		// GPU Voltage
		else if (key.equals(DeviceSettings.KEY_GPU_VOLTAGE)) {
			double currentGPUVolt = Math.round((Integer) newValue / 12.5) * 12.5;
			int i;
			for (i = 0; voltSteps[i] != Math.abs(currentGPUVolt); i++) {
			}
			for (int j = 0; j <= defaultGPUVoltValues.length - 1; j++) {
			    Utils.writeValue(FILE_GPU_VOLTAGE, 
			    		j + " vape=0x" + Integer.toHexString(defaultGPUVoltValues[j] - i));
			}
		}	
		
		// Discharging Threshold
		else if (key.equals(DeviceSettings.KEY_DISCHARGING_THRESHOLD)) {
			// Check if discharging threshold value is less than or equal to recharging threshold
			if ((Integer) newValue <= PreferenceManager.getDefaultSharedPreferences(mCtx).
					getInt(DeviceSettings.KEY_RECHARGING_THRESHOLD, 5)) {
				Toast.makeText(mCtx, R.string.invalid_value, Toast.LENGTH_SHORT).show();
				return true;
			}
			Utils.writeValue(FILE_CYCLE_CHARGING, "dischar=" + String.valueOf((Integer) newValue));
		}
		
		// Recharging Threshold
		else if (key.equals(DeviceSettings.KEY_RECHARGING_THRESHOLD)) {
			// Check if recharging threshold value is greater than or equal to discharging threshold
			if ((Integer) newValue >= PreferenceManager.getDefaultSharedPreferences(mCtx).
					getInt(DeviceSettings.KEY_DISCHARGING_THRESHOLD, 100)) {
				Toast.makeText(mCtx, R.string.invalid_value, Toast.LENGTH_SHORT).show();
				return true;
			}
			Utils.writeValue(FILE_CYCLE_CHARGING, "rechar=" + String.valueOf((Integer) newValue));
		}

		// ReadAheadKB
		if (key.equals(DeviceSettings.KEY_READAHEADKB))
			Utils.writeValue(FILE_READAHEADKB, String.valueOf((Integer) (Math.round((Integer) newValue / 128 + 1) * 128)));

		// ABBamp Audio - ADDigGain2 Control
		else if (key.equals(DeviceSettings.KEY_ADDIGGAIN2_CONTROL))
			Utils.writeValue(AudioFragmentActivity.FILE_ADDIGGAIN2, "gain=" + String.valueOf((Integer) newValue));
		
		// ABBamp Audio - Anagain3 Control
		else if (key.equals(DeviceSettings.KEY_ANAGAIN3_CONTROL))
			Utils.writeValue(AudioFragmentActivity.FILE_ANAGAIN3, "gain=" + String.valueOf((Integer) newValue));
		
		// ABBamp Audio - ClassDHPG Control
		else if (key.equals(DeviceSettings.KEY_CLASSDHPG_CONTROL))
			Utils.writeValue(AudioFragmentActivity.FILE_CLASSDHPG, "gain=" + String.valueOf((Integer) newValue));
		
		// ABBamp Audio - ClassDWG Control
		else if (key.equals(DeviceSettings.KEY_CLASSDWG_CONTROL))
			Utils.writeValue(AudioFragmentActivity.FILE_CLASSDWG, "gain=" + String.valueOf((Integer) newValue));
		
		// ABBamp Audio - EarDigGain Control
		else if (key.equals(DeviceSettings.KEY_EARDIGGAIN_CONTROL))
			Utils.writeValue(AudioFragmentActivity.FILE_EARDIGGAIN, "gain=" + String.valueOf((Integer) newValue));
		
		// ABBamp Audio - HsLDigGain Control
		else if (key.equals(DeviceSettings.KEY_HSLDIGGAIN_CONTROL))
			Utils.writeValue(AudioFragmentActivity.FILE_HSLDIGGAIN, "gain=" + String.valueOf((Integer) newValue));
		
		// ABBamp Audio - HsRDigGain Control
		else if (key.equals(DeviceSettings.KEY_HSRDIGGAIN_CONTROL))
			Utils.writeValue(AudioFragmentActivity.FILE_HSRDIGGAIN, "gain=" + String.valueOf((Integer) newValue));
		
		// ABBamp Audio - LPA Mode Control
		else if (key.equals(DeviceSettings.KEY_LPA_MODE_CONTROL))
			Utils.writeValue(AudioFragmentActivity.FILE_LPA_MODE, "vape=0x" + Integer.toHexString((Integer) newValue));

		// Min Brightness
		else if (key.equals(DeviceSettings.KEY_MIN_BRIGHTNESS))
			Utils.writeValue(FILE_MIN_BRIGHTNESS, String.valueOf((Integer) newValue));

		return true;
	}

	public static void restore(Context context) {

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		// Readahead kB control
		Utils.writeValue(FILE_READAHEADKB,
				String.valueOf(sharedPrefs.
					getInt(DeviceSettings.KEY_READAHEADKB, 512)));

		// ABBamp Audio - ADDigGain2 Control
			Utils.writeValue(AudioFragmentActivity.FILE_ADDIGGAIN2,
				"gain=" + sharedPrefs.
					getInt(DeviceSettings.KEY_ADDIGGAIN2_CONTROL, 25));

		// ABBamp Audio - Anagain3 Control
			Utils.writeValue(AudioFragmentActivity.FILE_ANAGAIN3,
				"gain=" + sharedPrefs.
					getInt(DeviceSettings.KEY_ANAGAIN3_CONTROL, 0));

		// ABBamp Audio - ClassDHPG Control
			Utils.writeValue(AudioFragmentActivity.FILE_CLASSDHPG,
				"gain=" + sharedPrefs.
					getInt(DeviceSettings.KEY_CLASSDHPG_CONTROL, 10));

		// ABBamp Audio - ClassDWG Control
			Utils.writeValue(AudioFragmentActivity.FILE_CLASSDWG,
				"gain=" + sharedPrefs.
					getInt(DeviceSettings.KEY_CLASSDWG_CONTROL, 10));

		// ABBamp Audio - EarDigGain Control
			Utils.writeValue(AudioFragmentActivity.FILE_EARDIGGAIN,
				"gain=" + sharedPrefs.
					getInt(DeviceSettings.KEY_EARDIGGAIN_CONTROL, 4));

		// ABBamp Audio - HsLDigGain Control
			Utils.writeValue(AudioFragmentActivity.FILE_HSLDIGGAIN,
				"gain=" + sharedPrefs.
					getInt(DeviceSettings.KEY_HSLDIGGAIN_CONTROL, 4));

		// ABBamp Audio - HsRDigGain Control
			Utils.writeValue(AudioFragmentActivity.FILE_HSRDIGGAIN,
				"gain=" + sharedPrefs.
						getInt(DeviceSettings.KEY_HSRDIGGAIN_CONTROL, 4));
		
		// ABBamp Audio - LPA Mode Control
			Utils.writeValue(AudioFragmentActivity.FILE_LPA_MODE,
				"vape=0x" + sharedPrefs.
					getInt(DeviceSettings.KEY_LPA_MODE_CONTROL, 16));

		// Cycle Charging - Discharging threshold
		Utils.writeValue(FILE_CYCLE_CHARGING, 
				"dischar=" + sharedPrefs.
						getInt(DeviceSettings.KEY_DISCHARGING_THRESHOLD, 100));

		// Cycle Charging - Recharging threshold
		Utils.writeValue(FILE_CYCLE_CHARGING,
				"rechar=" + sharedPrefs.
						getInt(DeviceSettings.KEY_RECHARGING_THRESHOLD, 5));

		// Min Brightness
		Utils.writeValue(FILE_MIN_BRIGHTNESS,
				String.valueOf(sharedPrefs
						.getInt(DeviceSettings.KEY_MIN_BRIGHTNESS, 1)));

		int i;
		// CPU Voltage
		double currentCPUVolt = Math.round(sharedPrefs.
				getInt(DeviceSettings.KEY_CPU_VOLTAGE, 0) / 12.5) * 12.5;
		for (i = 0; voltSteps[i] != Math.abs(currentCPUVolt); i++) {
		}
		if (currentCPUVolt < 0) {
			i *= -1;
		}
		for (int j = 0; j <= defaultCPUVoltValues.length - 1; j++) {
		    Utils.writeValue(FILE_CPU_VOLTAGE + (j < 10 ? "0" + String.valueOf(j) : String.valueOf(j)), 
		    		"varm=0x" + Integer.toHexString(defaultCPUVoltValues[j] + i));
		}

		// GPU Voltage
		double currentGPUVolt = Math.round(sharedPrefs.
				getInt(DeviceSettings.KEY_GPU_VOLTAGE, 0) / 12.5) * 12.5;
		for (i = 0; voltSteps[i] != Math.abs(currentGPUVolt); i++) {
		}
		for (int j = 0; j <= defaultGPUVoltValues.length - 1; j++) {
		    Utils.writeValue(FILE_GPU_VOLTAGE, 
		    		j + " vape=0x" + Integer.toHexString(defaultGPUVoltValues[j] - i));
		}
	}
}
