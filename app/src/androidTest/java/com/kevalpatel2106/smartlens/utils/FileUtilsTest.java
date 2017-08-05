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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.plugins.tensorflowImageClassifier.TFUtils;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 19-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public final class FileUtilsTest extends BaseTestClass {

    @Rule
    public GrantPermissionRule mGrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

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
        } catch (ClassNotFoundException
                | IllegalAccessException
                | InstantiationException
                | InvocationTargetException e) {
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


    @Test
    public void checkCopyFile() throws Exception {
        File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/destination.txt");
        File source = TFUtils.getImageLabels(InstrumentationRegistry.getTargetContext());

        if (!destination.exists()) assertFalse(FileUtils.copyFile(destination, source));

        //Create destination file
        assertTrue(destination.createNewFile());
        assertTrue(FileUtils.copyFile(destination, source));
        assertTrue(destination.length() > 0);
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}