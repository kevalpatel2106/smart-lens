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
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.TestActivity;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.testUtils.CustomMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Observable;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;

/**
 * Created by Keval on 26-Jul-17.
 */
@RunWith(AndroidJUnit4.class)
public class BaseActivityTest extends BaseTestClass {
    private static final String ACTIVITY_TITLE = "Test Activity";

    @Rule
    public ActivityTestRule<TestActivity> mActivityTestRule = new ActivityTestRule<>(TestActivity.class);

    private TestActivity mTestActivity;

    @Before
    public void init() {
        mTestActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void checkToolbar() throws Exception {
        //Test with the string title
        mTestActivity.runOnUiThread(() -> mTestActivity.setToolbar(R.id.toolbar, ACTIVITY_TITLE, true));
        CustomMatchers.matchToolbarTitle(ACTIVITY_TITLE).check(matches(isDisplayed()));
        Espresso.onView(withContentDescription("Navigate up")).check(matches(isDisplayed()));

        //Test with the string resource title
        mTestActivity.runOnUiThread(() -> mTestActivity.setToolbar(R.id.toolbar, R.string.app_name, true));
        CustomMatchers.matchToolbarTitle(mTestActivity.getString(R.string.app_name)).check(matches(isDisplayed()));
        Espresso.onView(withContentDescription("Navigate up")).check(matches(isDisplayed()));

        //Hide home button
        mTestActivity.runOnUiThread(() -> mTestActivity.setToolbar(R.id.toolbar, ACTIVITY_TITLE, false));
        try {
            Espresso.onView(withContentDescription("Navigate up")).perform(click());
            Assert.fail();
        } catch (NoMatchingViewException e) {
            //Pass
        }
    }

    @Test
    public void checkAddDisposable() throws Exception {
        Assert.assertNotNull(mTestActivity.getDisposables());

        mTestActivity.addSubscription(null);
        Assert.assertEquals(mTestActivity.getDisposables().size(), 0);

        mTestActivity.addSubscription(Observable.just("1").subscribe());
        Assert.assertEquals(mTestActivity.getDisposables().size(), 1);
    }

//    @SuppressWarnings("ConstantConditions")
//    @Test
//    public void checkHomeButton() throws Exception {
//        // Add a view to focus
//        EditText editText = new EditText(mTestActivity);
//        editText.setText("This is test.");
//        editText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//
//        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
//            FrameLayout frameLayout = mTestActivity.findViewById(R.id.container);
//            frameLayout.addView(editText);
//        });
//        Espresso.onView(withText("This is test.")).perform(click());
//
//        //Allow keyboard to open.
//        Delay.startDelay(5000);
//        Espresso.onView(withContentDescription("Navigate up")).perform(click());
//        Delay.stopDelay();
//
//        //Check if the activity is destroying?
//        Assert.assertTrue(mTestActivity.isFinishing());
//    }

    @After
    public void tearUp() {
        mTestActivity.finish();
    }

    @Override
    public Activity getActivity() {
        return mActivityTestRule.getActivity();
    }
}