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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Keval on 19-Jul-17.
 * Test class for {@link SharedPrefsProvider}.
 */
@RunWith(AndroidJUnit4.class)
public final class SharedPrefsProviderTest extends BaseTestClass {
    private static final String TEST_KEY = "test_key";

    private SharedPreferences mSharedPreferences;
    private SharedPrefsProvider mSharedPrefsProvider;

    @Before
    public void setUp() throws Exception {
        mSharedPrefsProvider = new SharedPrefsProvider(InstrumentationRegistry.getContext());

        mSharedPreferences = InstrumentationRegistry.getContext().getSharedPreferences("app_prefs",
                Context.MODE_PRIVATE);

        //Clear the preference.
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void canInitiate() throws Exception {
        assertNotNull(mSharedPreferences);
        try {
            new SharedPrefsProvider(null);
        } catch (RuntimeException e) {
            //Success
        }
    }

    @Test
    public void removePreferences() throws Exception {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(TEST_KEY, "String");
        editor.apply();

        mSharedPrefsProvider.removePreferences(TEST_KEY);
        assertTrue(mSharedPreferences.getString(TEST_KEY, null) == null);
    }

    @Test
    public void savePreferences() throws Exception {
        assertFalse(mSharedPreferences.getInt(TEST_KEY, -1) != -1);
        mSharedPrefsProvider.savePreferences(TEST_KEY, "String");
        assertTrue(mSharedPreferences.getString(TEST_KEY, null) != null);
    }

    @Test
    public void savePreferences1() throws Exception {
        assertFalse(mSharedPreferences.getInt(TEST_KEY, -1) != -1);
        mSharedPrefsProvider.savePreferences(TEST_KEY, 1);
        assertTrue(mSharedPreferences.getInt(TEST_KEY, -1) != -1);
    }

    @Test
    public void savePreferences2() throws Exception {
        assertFalse(mSharedPreferences.getLong(TEST_KEY, -1) != -1);
        mSharedPrefsProvider.savePreferences(TEST_KEY, 100000L);
        assertTrue(mSharedPreferences.getLong(TEST_KEY, -1) != -1);
    }

    @Test
    public void savePreferences3() throws Exception {
        assertFalse(mSharedPreferences.getBoolean(TEST_KEY, false));
        mSharedPrefsProvider.savePreferences(TEST_KEY, true);
        assertTrue(mSharedPreferences.getBoolean(TEST_KEY, false));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void getStringFromPreferences() throws Exception {
        String testVal = "String";

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(TEST_KEY, testVal);
        editor.apply();

        assertTrue(mSharedPrefsProvider.getStringFromPreferences(TEST_KEY).equals(testVal));
    }

    @Test
    public void getBoolFromPreferences() throws Exception {
        boolean testVal = true;

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(TEST_KEY, true);
        editor.apply();

        //noinspection ConstantConditions
        assertTrue(mSharedPrefsProvider.getBoolFromPreferences(TEST_KEY) == testVal);
    }

    @Test
    public void getLongFromPreference() throws Exception {
        long testVal = 100000L;

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putLong(TEST_KEY, testVal);
        editor.apply();

        assertTrue(mSharedPrefsProvider.getLongFromPreference(TEST_KEY) == testVal);
    }

    @Test
    public void getIntFromPreference() throws Exception {
        int testVal = 100;

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(TEST_KEY, testVal);
        editor.apply();

        assertTrue(mSharedPrefsProvider.getIntFromPreference(TEST_KEY) == testVal);
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}