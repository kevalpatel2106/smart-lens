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

package com.kevalpatel2106.smartlens.base;

import android.app.Activity;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;

import com.kevalpatel2106.smartlens.TestActivity;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.is;

/**
 * Created by Keval on 26-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public class BaseActivityTest extends BaseTestClass {
    private static final String ACTIVITY_TITLE = "Test Activity";
    @Rule
    public ActivityTestRule<TestActivity> mActivityTestRule = new ActivityTestRule<>(TestActivity.class);
    private BaseActivity mBaseActivity;

    private static ViewInteraction matchToolbarTitle(CharSequence title) {
        return Espresso.onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    private static Matcher<Object> withToolbarTitle(
            final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override
            public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

    @Before
    public void init() {
        mBaseActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void checkToolbar() throws Exception {
        mBaseActivity.setToolbar(ACTIVITY_TITLE, true);

        //Check if the title visible
        matchToolbarTitle(ACTIVITY_TITLE).check(matches(isDisplayed()));

        //Check if the home button visible
        Espresso.onView(withId(android.R.id.home)).check(matches(isDisplayed()));
    }

    @Override
    public Activity getActivity() {
        return mActivityTestRule.getActivity();
    }
}