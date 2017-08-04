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

/**
 * Created by Keval on 04-Aug-17.
 */
public class CalendarEventTest {
    @Test
    public void setSummary() throws Exception {
        BarcodeInfo.CalendarEvent event = new BarcodeInfo.CalendarEvent();
        event.setSummary("Test summary.");
        assertEquals(event.getSummary(), "Test summary.");

        //Check for the null
        event.setSummary(null);
        assertNull(event.getSummary());
    }

    @Test
    public void setDescription() throws Exception {
        BarcodeInfo.CalendarEvent event = new BarcodeInfo.CalendarEvent();
        event.setDescription("Test description.");
        assertEquals(event.getDescription(), "Test description.");

        //Check for the null
        event.setDescription(null);
        assertNull(event.getDescription());
    }

    @Test
    public void setLocation() throws Exception {
        BarcodeInfo.CalendarEvent event = new BarcodeInfo.CalendarEvent();
        event.setLocation("201, KB Plaza");
        assertEquals(event.getLocation(), "201, KB Plaza");

        //Check for the null
        event.setLocation(null);
        assertNull(event.getLocation());
    }

    @Test
    public void setOrganizer() throws Exception {
        BarcodeInfo.CalendarEvent event = new BarcodeInfo.CalendarEvent();
        event.setOrganizer("James Bond");
        assertEquals(event.getOrganizer(), "James Bond");

        //Check for the null
        event.setOrganizer(null);
        assertNull(event.getOrganizer());
    }

    @Test
    public void setStatus() throws Exception {
        BarcodeInfo.CalendarEvent event = new BarcodeInfo.CalendarEvent();
        event.setStatus("Going");
        assertEquals(event.getStatus(), "Going");

        //Check for the null
        event.setStatus(null);
        assertNull(event.getStatus());
    }

    @Test
    public void setStartTimeMills() throws Exception {
        long time = System.currentTimeMillis() + 12000;
        BarcodeInfo.CalendarEvent event = new BarcodeInfo.CalendarEvent();
        event.setStartTimeMills(time);
        assertEquals(event.getStartTimeMills(), time);
    }

    @Test
    public void setEndTimeMills() throws Exception {
        long time = System.currentTimeMillis();
        BarcodeInfo.CalendarEvent event = new BarcodeInfo.CalendarEvent();
        event.setEndTimeMills(time);
        assertEquals(event.getEndTimeMills(), time);
    }
}