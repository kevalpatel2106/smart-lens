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

package com.kevalpatel2106.smartlens.wikipedia;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;

import com.kevalpatel2106.smartlens.infopage.InfoCallbacks;
import com.kevalpatel2106.smartlens.infopage.InfoModel;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 28-Jul-17.
 */
public class WikiRetrofitHelperTest extends BaseTestClass {
    private WikiRetrofitHelper mWikiRetrofitHelper;
    private InfoCallbacks mInfoCallbacks = new InfoCallbacks() {
        @Override
        public void onSuccess(InfoModel infoModel) {
            //Do nothing
        }

        @Override
        public void onRecomandedLoaded(InfoModel[] recommended) {
            //Do nothing
        }

        @Override
        public void onError(String message) {
            //Do nothing
        }
    };

    @Before
    public void setUp() {
        mWikiRetrofitHelper = new WikiRetrofitHelper(InstrumentationRegistry.getTargetContext(),
                mInfoCallbacks);
    }

    @Test
    public void canInit() throws Exception {
        try {
            new WikiRetrofitHelper(null, mInfoCallbacks);
            new WikiRetrofitHelper(InstrumentationRegistry.getTargetContext(), null);
            fail();
        } catch (IllegalArgumentException e) {
            //Success
        }
    }

    @Test
    public void getBaseUrl() throws Exception {
        assertNotNull(mWikiRetrofitHelper.getBaseUrl());
//        assertTrue(mWikiRetrofitHelper.getBaseUrl().contains("wikipedia"));
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}