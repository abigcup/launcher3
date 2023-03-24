/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.launcher3;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.android.hwyun.data.SettingSharedPreferencesManager;

/**
 * Settings activity for Launcher. Currently implements the following setting: Allow rotation
 */
public class SettingsActivity extends Activity {


    public SettingSharedPreferencesManager mSspm;
    public Switch mScreenLoopSwitch;

    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_setting);
        // Display the fragment as the main content.
//        getFragmentManager().beginTransaction()
//                .replace(android.R.id.content, new LauncherSettingsFragment())
//                .commit();
        mContext = this;
        mSspm = SettingSharedPreferencesManager.getInstance();
        initView();
    }

    public void initView(){
        mScreenLoopSwitch = findViewById(R.id.screen_loop_item_checkbox);
        if(mSspm.ismScreenIsLoop(getApplicationContext())){
            mScreenLoopSwitch.setChecked(true);
        }else{
            mScreenLoopSwitch.setChecked(false);
        }
        mScreenLoopSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mSspm.ismScreenIsLoop(getApplicationContext())){
                    Log.d("cym ismScreenIsLoop",mSspm.ismScreenIsLoop(getApplicationContext())+"");
                    mScreenLoopSwitch.setChecked(false);
                    mSspm.setmScreenIsLoop(false,getApplicationContext());
                    Log.d("cym ismScreenIsLoop",mSspm.ismScreenIsLoop(getApplicationContext())+"");
                }else{
                    Log.d("cym ismScreenIsLoop",mSspm.ismScreenIsLoop(getApplicationContext())+"");
                    mScreenLoopSwitch.setChecked(true);
                    mSspm.setmScreenIsLoop(true,getApplicationContext());
                    Log.d("cym ismScreenIsLoop",mSspm.ismScreenIsLoop(getApplicationContext())+"");
                }
            }
        });
    }




















    /**
     * This fragment shows the launcher preferences.
     */
    public static class LauncherSettingsFragment extends PreferenceFragment
            implements OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.launcher_preferences);

            SwitchPreference pref = (SwitchPreference) findPreference(
                    Utilities.ALLOW_ROTATION_PREFERENCE_KEY);
            pref.setPersistent(false);

            Bundle extras = new Bundle();
            extras.putBoolean(LauncherSettings.Settings.EXTRA_DEFAULT_VALUE, false);
            Bundle value = getActivity().getContentResolver().call(
                    LauncherSettings.Settings.CONTENT_URI,
                    LauncherSettings.Settings.METHOD_GET_BOOLEAN,
                    Utilities.ALLOW_ROTATION_PREFERENCE_KEY, extras);
            pref.setChecked(value.getBoolean(LauncherSettings.Settings.EXTRA_VALUE));

            pref.setOnPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Bundle extras = new Bundle();
            extras.putBoolean(LauncherSettings.Settings.EXTRA_VALUE, (Boolean) newValue);
            getActivity().getContentResolver().call(
                    LauncherSettings.Settings.CONTENT_URI,
                    LauncherSettings.Settings.METHOD_SET_BOOLEAN,
                    preference.getKey(), extras);
            return true;
        }
    }
}
