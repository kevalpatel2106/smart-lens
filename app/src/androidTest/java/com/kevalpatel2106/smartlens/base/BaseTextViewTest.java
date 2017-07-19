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
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

/**
 * Created by Keval on 19-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public class BaseTextViewTest extends BaseTestClass {
    private BaseTextView mBaseTextView;

    @Before
    public void setUp() throws Exception {
        mBaseTextView = new BaseTextView(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void getTrimmedText() throws Exception {
        String testVal = "123456789 ";
        mBaseTextView.setText(testVal);
        assertTrue(mBaseTextView.getTrimmedText().equals(testVal.trim()));
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}