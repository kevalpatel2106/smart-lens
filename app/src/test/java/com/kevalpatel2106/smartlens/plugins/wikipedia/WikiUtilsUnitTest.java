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

package com.kevalpatel2106.smartlens.plugins.wikipedia;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval Patel on 25/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public class WikiUtilsUnitTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkSummaryValid() {
        assertFalse(WikiUtils.isValidSummary("xyz", null));
        assertFalse(WikiUtils.isValidSummary(null, null));
        assertFalse(WikiUtils.isValidSummary(null, "abcdefghijklmnopqrstuvwxyz1234567890"));
        assertFalse(WikiUtils.isValidSummary("xyz", ""));
        assertFalse(WikiUtils.isValidSummary("xyz", "xyz may refer to:"));
        assertFalse(WikiUtils.isValidSummary("xyz", "xyz may refer to: "));
        assertTrue(WikiUtils.isValidSummary("xyz", "abcdefghijklmnopqrstuvwxyz1234567890"));
    }

    @Test
    public void checkPossibleLabel() throws Exception {
        String[] strings = WikiUtils.generatePossibleWikiLabel("Computer keyboard");
        assertEquals(strings.length, 2);
        assertEquals(strings[0], "Computer");
        assertEquals(strings[1], "keyboard");

        strings = WikiUtils.generatePossibleWikiLabel("Computer");
        assertEquals(strings.length, 0);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkWikiLabel() {
        assertFalse(WikiUtils.generateWikiLabel("computer keyboard").contains("\\s"));
        assertFalse(WikiUtils.generateWikiLabel("computer keyboard").equals("Computer_keyboard"));

        try {
            WikiUtils.generateWikiLabel(null);
            fail();
        } catch (IllegalArgumentException e) {
            //Success
        }
    }

}