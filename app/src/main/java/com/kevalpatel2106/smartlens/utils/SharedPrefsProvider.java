/*
 * Copyright 2017 Keval Patel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kevalpatel2106.smartlens.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Keval on 20-Aug-16.
 * Class contains all the helper functions to access shared prefs.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */
public class SharedPrefsProvider {

    /**
     * Name of the shared preference file.
     */
    private static final String PREF_FILE = "app_prefs";

    /**
     * shared preference object.
     */
    private final SharedPreferences mSharedPreference;

    /**
     * Public constructor.
     *
     * @param context instance of caller.
     */
    public SharedPrefsProvider(@NonNull Context context) {
        //noinspection ConstantConditions
        if (context == null) throw new RuntimeException("Context cannot be null.");

        mSharedPreference = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
    }

    /**
     * Remove and clear data from preferences for given field
     *
     * @param key key of preference field to remove
     */
    public void removePreferences(@NonNull final String key) {
        //Delete SharedPref
        SharedPreferences.Editor prefsEditor = mSharedPreference.edit();
        prefsEditor.remove(key);
        prefsEditor.apply();
    }

    /**
     * Save value to shared preference
     *
     * @param key   key of preference field
     * @param value value to store
     */
    public void savePreferences(@NonNull String key, @Nullable String value) {
        //Save to share prefs
        SharedPreferences.Editor prefsEditor = mSharedPreference.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    /**
     * Save value to shared preference
     *
     * @param key   key of preference field
     * @param value value to store
     */
    public void savePreferences(@NonNull String key, boolean value) {
        //Save to share prefs
        SharedPreferences.Editor prefsEditor = mSharedPreference.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    /**
     * Save value to shared preference
     *
     * @param key   key of preference field
     * @param value value to store in int
     */
    public void savePreferences(@NonNull String key, int value) {
        //Save to share prefs
        SharedPreferences.Editor prefsEditor = mSharedPreference.edit();
        prefsEditor.putInt(key, value);
        prefsEditor.apply();
    }


    /**
     * Save value to shared preference
     *
     * @param key   key of preference field
     * @param value value to store in long
     */
    public void savePreferences(@NonNull String key, long value) {
        //Save to share prefs
        SharedPreferences.Editor prefsEditor = mSharedPreference.edit();
        prefsEditor.putLong(key, value);
        prefsEditor.apply();
    }

    /**
     * Read string from shared preference file
     *
     * @param key : key of value field to read
     * @return string value for given key else null if key not found.
     */
    @Nullable
    public String getStringFromPreferences(@NonNull String key) {
        return mSharedPreference.getString(key, null);
    }

    /**
     * Read string from shared preference file
     *
     * @param key : key of value field to read
     * @return boolean value for given key else flase if key not found.
     */
    public boolean getBoolFromPreferences(@NonNull String key) {
        return mSharedPreference.getBoolean(key, false);
    }

    /**
     * Read string from shared preference file
     *
     * @param key : key of value field to read
     * @return long value for given key else -1 if key not found.
     */
    public long getLongFromPreference(@NonNull String key) {
        return mSharedPreference.getLong(key, (long) -1);
    }

    /**
     * Read string from shared preference file
     *
     * @param key : key of value field to read
     * @return int value for given key else -1 if key not found.
     */
    public long getIntFromPreference(@NonNull String key) {
        return mSharedPreference.getInt(key, -1);
    }

}
