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

import android.graphics.Typeface;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Created by Keval on 20-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public class BaseButtonTest {
    private BaseButton mBaseButton;

    @Before
    public void setUp() throws Exception {
        mBaseButton = new BaseButton(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void testGetFont() throws Exception {
        //Test with null typeface
        mBaseButton.setTypeface(null, Typeface.NORMAL);
        assertEquals(mBaseButton.getFont(mBaseButton.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Regular.ttf"));

        //Test with bold typeface
        mBaseButton.setTypeface(null, Typeface.BOLD);
        assertEquals(mBaseButton.getFont(mBaseButton.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Bold.ttf"));

        //Test with italic typeface
        mBaseButton.setTypeface(null, Typeface.ITALIC);
        assertEquals(mBaseButton.getFont(mBaseButton.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Italic.ttf"));

        //Test with italic typeface
        mBaseButton.setTypeface(null, Typeface.BOLD_ITALIC);
        assertEquals(mBaseButton.getFont(mBaseButton.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-BoldItalic.ttf"));


        //check with null typeface
        mBaseButton.setTypeface(null);
        assertEquals(mBaseButton.getFont(mBaseButton.getTypeface()),
                String.format(Locale.US, "fonts/%s", "OpenSans-Regular.ttf"));
    }

}