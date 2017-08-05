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

/**
 * Created by Keval Patel on 05/08/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public class BarcodeInfoTest {

    @Test
    public void getContact() throws Exception {
        BarcodeInfo.Contact contact = new BarcodeInfo.Contact();
        BarcodeInfo barcodeInfo = new BarcodeInfo();
        barcodeInfo.setContact(contact);

        assertEquals(barcodeInfo.getContact(), contact);
    }

    @Test
    public void getUrlBookmark() throws Exception {
        BarcodeInfo.UrlBookmark urlBookmark = new BarcodeInfo.UrlBookmark("Google", "www.google.com");
        BarcodeInfo barcodeInfo = new BarcodeInfo();
        barcodeInfo.setUrlBookmark(urlBookmark);

        assertEquals(barcodeInfo.getUrlBookmark(), urlBookmark);
    }

    @Test
    public void getSms() throws Exception {
        BarcodeInfo.Sms sms = new BarcodeInfo.Sms("This is test message.", "1234567890");
        BarcodeInfo info = new BarcodeInfo();
        info.setSms(sms);

        assertEquals(info.getSms(), sms);
    }

    @Test
    public void getGeoPoint() throws Exception {
        BarcodeInfo.GeoPoint geoPoint = new BarcodeInfo.GeoPoint(23.0225, 72.5714);
        BarcodeInfo info = new BarcodeInfo();
        info.setGeoPoint(geoPoint);

        assertEquals(info.getGeoPoint(), geoPoint);
    }

    @Test
    public void getCalendarEvent() throws Exception {
        BarcodeInfo.CalendarEvent calendarEvent = new BarcodeInfo.CalendarEvent();
        BarcodeInfo barcodeInfo = new BarcodeInfo();
        barcodeInfo.setCalendarEvent(calendarEvent);

        assertEquals(barcodeInfo.getCalendarEvent(), calendarEvent);
    }

    @Test
    public void getPhone() throws Exception {
        BarcodeInfo.Phone phone = new BarcodeInfo.Phone("7894561230");
        BarcodeInfo barcodeInfo = new BarcodeInfo();
        barcodeInfo.setPhone(phone);

        assertEquals(barcodeInfo.getPhone(), phone);
    }

    @Test
    public void getRawValue() throws Exception {
        BarcodeInfo barcodeInfo = new BarcodeInfo();
        barcodeInfo.setRawValue("Raw value");

        assertEquals(barcodeInfo.getRawValue(), "Raw value");

    }

}