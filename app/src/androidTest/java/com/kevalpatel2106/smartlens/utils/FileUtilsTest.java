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
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 19-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public final class FileUtilsTest extends BaseTestClass {
    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void canInitiate() throws Exception {
        try {
            Class<?> c = Class.forName("FileUtils");
            Constructor constructor = c.getDeclaredConstructors()[0];
            constructor.newInstance();
            fail("Should have thrown Arithmetic exception");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCacheDir() throws Exception {
        //Check if the output is not null.
        assertNotNull(FileUtils.getCacheDir(mContext));

        String cachePath = FileUtils.getCacheDir(mContext).getAbsolutePath();
        assertTrue(cachePath.equals(mContext.getExternalCacheDir() != null ?
                mContext.getExternalCacheDir().getAbsolutePath()
                : mContext.getCacheDir().getAbsolutePath()));
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}