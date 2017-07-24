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

import com.kevalpatel2106.smartlens.imageClassifier.ImageClassifierFragment;
import com.kevalpatel2106.smartlens.testUtils.FragmentTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by Keval Patel on 23/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public class WikiFragmentTest {

    @Rule
    public FragmentTestRule<WikiFragment> mWikiFragmentFragmentTestRule =
            new FragmentTestRule<>(WikiFragment.class);
    private WikiFragment mWikiFragment;

    @Before
    public void init() {
        mWikiFragmentFragmentTestRule.launchActivity(null);
        mWikiFragment = mWikiFragmentFragmentTestRule.getFragment();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkNewInstance() throws Exception {
        assertTrue(ImageClassifierFragment.getNewInstance() instanceof ImageClassifierFragment);
    }

}