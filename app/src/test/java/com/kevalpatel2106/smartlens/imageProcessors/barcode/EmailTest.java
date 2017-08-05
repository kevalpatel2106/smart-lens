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
public class EmailTest {
    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkConstructor() throws Exception {
        BarcodeInfo.Email email = new BarcodeInfo.Email("kevalpatel2106@gmail.com");
        assertEquals(email.getEmail(), "kevalpatel2106@gmail.com");
        assertNull(email.getType());

        email = new BarcodeInfo.Email("kevalonly111@gmail.com", "Work");
        assertEquals(email.getEmail(), "kevalonly111@gmail.com");
        assertEquals(email.getType(), "Work");

        try {
            new BarcodeInfo.Email(null, "Work");
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }

        try {
            new BarcodeInfo.Email(null);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }
    }
}