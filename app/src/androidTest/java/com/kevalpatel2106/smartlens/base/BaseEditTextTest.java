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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 19-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public final class BaseEditTextTest extends BaseTestClass {
    private BaseEditText mBaseEditText;

    @Before
    public void init() throws Exception {
        mBaseEditText = new BaseEditText(InstrumentationRegistry.getTargetContext());
        mBaseEditText.setText("");
    }

    @Test
    public void canInitialize() throws Exception {
        try {
            new BaseEditText(InstrumentationRegistry.getTargetContext());
            new BaseEditText(InstrumentationRegistry.getTargetContext(), null);
            new BaseEditText(InstrumentationRegistry.getTargetContext(), null, 1);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void getTrimmedText() throws Exception {
        String testVal = "123456789 ";

        //Check for the text
        mBaseEditText.setText(testVal);
        assertTrue(mBaseEditText.getTrimmedText().equals(testVal.trim()));

        //Check for the hint.
        mBaseEditText.setText("");
        mBaseEditText.setHint(testVal);
        assertTrue(mBaseEditText.getTrimmedText().length() == 0);
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void isEmpty() throws Exception {
        //Real empty
        mBaseEditText.setText("");
        assertTrue(mBaseEditText.isEmpty());

        //Spaces
        mBaseEditText.setText("     ");
        assertTrue(mBaseEditText.isEmpty());

        //Texts
        mBaseEditText.setText("72354");
        assertFalse(mBaseEditText.isEmpty());
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void clear() throws Exception {
        mBaseEditText.setText("123456789");
        mBaseEditText.clear();
        assertEquals(mBaseEditText.getText().length(), 0);
    }

    @Test
    public void testGetFont() throws Exception {
        //Test with null typeface
        mBaseEditText.setTypeface(null, Typeface.NORMAL);
        assertEquals(mBaseEditText.getFont(mBaseEditText.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Regular.ttf"));

        //Test with bold typeface
        mBaseEditText.setTypeface(null, Typeface.BOLD);
        assertEquals(mBaseEditText.getFont(mBaseEditText.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Bold.ttf"));

        //Test with italic typeface
        mBaseEditText.setTypeface(null, Typeface.ITALIC);
        assertEquals(mBaseEditText.getFont(mBaseEditText.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Italic.ttf"));

        //Test with italic typeface
        mBaseEditText.setTypeface(null, Typeface.BOLD_ITALIC);
        assertEquals(mBaseEditText.getFont(mBaseEditText.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-BoldItalic.ttf"));


        //check with null typeface
        mBaseEditText.setTypeface(null);
        assertEquals(mBaseEditText.getFont(mBaseEditText.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Regular.ttf"));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setError() throws Exception {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            String mockErrorText = "This is mock error.";
            mBaseEditText.setError(mockErrorText);
            assertTrue(mBaseEditText.isFocused());
            assertEquals(mBaseEditText.getError(), mockErrorText);
        });
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}