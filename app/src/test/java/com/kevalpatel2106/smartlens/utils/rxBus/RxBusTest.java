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

package com.kevalpatel2106.smartlens.utils.rxBus;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Keval Patel on 24/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public class RxBusTest {
    private RxBus mRxBus;

    @Before
    public void setUp() {
        mRxBus = RxBus.getDefault();
    }

    @Test
    public void isSingleton() throws Exception {
        assertEquals(mRxBus, RxBus.getDefault());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void post() throws Exception {
        try {
            mRxBus.post(null);
            fail();
        } catch (IllegalArgumentException e) {
            //Success
        }
    }
}