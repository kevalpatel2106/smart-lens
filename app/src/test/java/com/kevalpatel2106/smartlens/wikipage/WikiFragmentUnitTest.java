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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval Patel on 25/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public class WikiFragmentUnitTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkSummaryValid() {
        assertFalse(WikiFragment.isValidSummary("xyz", null));
        assertFalse(WikiFragment.isValidSummary(null, null));
        assertFalse(WikiFragment.isValidSummary(null, "abcdefghijklmnopqrstuvwxyz1234567890"));
        assertFalse(WikiFragment.isValidSummary("xyz", ""));
        assertFalse(WikiFragment.isValidSummary("xyz", "xyz  may refer to:"));
        assertFalse(WikiFragment.isValidSummary("xyz", "xyz  may refer to: "));
        assertTrue(WikiFragment.isValidSummary("xyz", "abcdefghijklmnopqrstuvwxyz1234567890"));
    }


    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkWikiLabel() {
        assertFalse(WikiFragment.generateWikiLabel("computer keyboard").contains("\\s"));
        assertFalse(WikiFragment.generateWikiLabel("computer keyboard").equals("Computer keyboard"));
        assertFalse(WikiFragment.generateWikiLabel("computer keyboard").substring(0, 1).equals("C"));

        try {
            WikiFragment.generateWikiLabel(null);
            fail();
        } catch (IllegalArgumentException e) {
            //Success
        }
    }

}