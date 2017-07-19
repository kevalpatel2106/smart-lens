package com.kevalpatel2106.smartlens.utils;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Keval on 19-Jul-17.
 * Unit tests for {@link Utils}.
 */
public class UtilsTest {

    /**
     * Test for {@link Utils#getDeviceName()}.
     */
    @Test
    public void getDeviceName() throws Exception {
        assertNotNull(Utils.getDeviceName());
        assertTrue(Utils.getDeviceName().contains("-"));
    }
}