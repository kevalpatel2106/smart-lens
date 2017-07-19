package com.kevalpatel2106.smartlens.utils;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Keval on 19-Jul-17.
 */
public class FileUtilsTest extends BaseTestClass {
    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
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