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
public class UrlBookmarkTest {
    @Test
    public void checkConstructor() throws Exception {
        BarcodeInfo.UrlBookmark urlBookmark = new BarcodeInfo.UrlBookmark("Google", "www.google.com");
        assertEquals(urlBookmark.getTitle(), "Google");
        assertEquals(urlBookmark.getUrl(), "www.google.com");

        urlBookmark = new BarcodeInfo.UrlBookmark("Google", null);
        assertEquals(urlBookmark.getTitle(), "Google");
        assertNull(urlBookmark.getUrl());

        urlBookmark = new BarcodeInfo.UrlBookmark(null, "www.google.com");
        assertNull(urlBookmark.getTitle());
        assertEquals(urlBookmark.getUrl(), "www.google.com");

        urlBookmark = new BarcodeInfo.UrlBookmark(null, null);
        assertNull(urlBookmark.getTitle());
        assertNull(urlBookmark.getUrl());
    }

}