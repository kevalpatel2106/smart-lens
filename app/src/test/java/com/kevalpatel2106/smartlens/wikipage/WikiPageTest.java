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

package com.kevalpatel2106.smartlens.wikipage;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Keval on 25-Jul-17.
 */
public class WikiPageTest {
    private static final String MOCK_LABEL = "mock_label";
    private static final String MOCK_IMAGE_URL = "mock_image_url";
    private static final String MOCK_SUMMARY = "mock_summary";

    @SuppressWarnings("ConstantConditions")
    @Test
    public void getSummaryMessage() throws Exception {
        WikiPage wikiPage = new WikiPage(MOCK_LABEL);
        wikiPage.setSummaryMessage(MOCK_SUMMARY);
        assertEquals(wikiPage.getSummaryMessage(), MOCK_SUMMARY);

        try {
            wikiPage.setSummaryMessage(null);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }
    }

    @Test
    public void checkImageUrl() throws Exception {
        WikiPage wikiPage = new WikiPage(MOCK_LABEL);
        wikiPage.setImageUrl(MOCK_IMAGE_URL);
        assertEquals(wikiPage.getImageUrl(), MOCK_IMAGE_URL);

        try {
            wikiPage.setImageUrl(null);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkLLabel() throws Exception {
        try {
            new WikiPage(null);
            fail();
        } catch (IllegalArgumentException e) {
            //Pass
        }

        WikiPage wikiPage = new WikiPage(MOCK_LABEL);
        assertEquals(wikiPage.getLabel(), MOCK_LABEL);
    }
}