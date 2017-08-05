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
 * Created by Keval Patel on 05/08/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public class SmsTest {

    @Test
    public void checkConstructor() {
        BarcodeInfo.Sms sms = new BarcodeInfo.Sms("This is test message.", "1234567890");
        assertEquals(sms.getMessage(), "This is test message.");
        assertEquals(sms.getPhoneNumber(), "1234567890");

        sms = new BarcodeInfo.Sms("This is test message.", null);
        assertEquals(sms.getMessage(), "This is test message.");
        assertNull(sms.getPhoneNumber());

        try {
            new BarcodeInfo.Sms(null, "1234567890");
            new BarcodeInfo.Sms(null, null);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }
    }

}