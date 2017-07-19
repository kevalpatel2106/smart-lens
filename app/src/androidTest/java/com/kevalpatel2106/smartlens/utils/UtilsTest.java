package com.kevalpatel2106.smartlens.utils;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Keval on 19-Jul-17.
 */
public class UtilsTest extends BaseTestClass {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getDeviceId() throws Exception {
        assertNotNull(Utils.getDeviceId(InstrumentationRegistry.getContext()));
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}