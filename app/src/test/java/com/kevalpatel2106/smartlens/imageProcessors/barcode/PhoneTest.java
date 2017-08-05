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

package com.kevalpatel2106.smartlens.imageProcessors.barcode;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 04-Aug-17.
 */
public class PhoneTest {
    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkConstructor() throws Exception {
        BarcodeInfo.Phone phone = new BarcodeInfo.Phone("7894561230");
        assertEquals(phone.getPhone(), "7894561230");
        assertNull(phone.getType());

        phone = new BarcodeInfo.Phone("1234567890", "Work");
        assertEquals(phone.getPhone(), "1234567890");
        assertEquals(phone.getType(), "Work");

        phone = new BarcodeInfo.Phone("1234567890", null);
        assertEquals(phone.getPhone(), "1234567890");
        assertNull(phone.getType());

        try {
            new BarcodeInfo.Phone(null, "Work");
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }

        try {
            new BarcodeInfo.Phone(null);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }
    }
}