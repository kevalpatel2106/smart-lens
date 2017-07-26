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

package com.kevalpatel2106.smartlens.base;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 19-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public final class BaseTextViewTest extends BaseTestClass {
    private BaseTextView mBaseTextView;

    @Before
    public void init() throws Exception {
        mBaseTextView = new BaseTextView(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void canInitialize() throws Exception {
        try {
            new BaseTextView(InstrumentationRegistry.getTargetContext());
            new BaseTextView(InstrumentationRegistry.getTargetContext(), null);
            new BaseTextView(InstrumentationRegistry.getTargetContext(), null, 1);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void getTrimmedText() throws Exception {
        String testVal = "123456789 ";
        mBaseTextView.setText(testVal);
        assertTrue(mBaseTextView.getTrimmedText().equals(testVal.trim()));
    }


    @Test
    public void testGetFont() throws Exception {
        //Test with null typeface
        mBaseTextView.setTypeface(null, Typeface.NORMAL);
        assertEquals(mBaseTextView.getFont(mBaseTextView.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Regular.ttf"));

        //Test with bold typeface
        mBaseTextView.setTypeface(null, Typeface.BOLD);
        assertEquals(mBaseTextView.getFont(mBaseTextView.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Bold.ttf"));

        //Test with italic typeface
        mBaseTextView.setTypeface(null, Typeface.ITALIC);
        assertEquals(mBaseTextView.getFont(mBaseTextView.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Italic.ttf"));

        //Test with italic typeface
        mBaseTextView.setTypeface(null, Typeface.BOLD_ITALIC);
        assertEquals(mBaseTextView.getFont(mBaseTextView.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-BoldItalic.ttf"));


        //check with null typeface
        mBaseTextView.setTypeface(null);
        assertEquals(mBaseTextView.getFont(mBaseTextView.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Regular.ttf"));
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}