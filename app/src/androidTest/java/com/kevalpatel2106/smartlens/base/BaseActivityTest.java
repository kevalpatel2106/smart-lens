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
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.TestActivity;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.testUtils.CustomMatchers;
import com.kevalpatel2106.smartlens.testUtils.Delay;

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

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkHomeButton() {
        //Open the keyboard.
        InputMethodManager imm = (InputMethodManager) mTestActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            // Add a view to focus
            EditText editText = new EditText(mTestActivity);
            editText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            FrameLayout frameLayout = mTestActivity.findViewById(R.id.container);
            frameLayout.addView(editText);

            editText.requestFocus();

            //Open keyboard forcefully
            imm.toggleSoftInputFromWindow(editText.getWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
        });

        //Allow keyboard to open.
        Delay.startDelay(1500);
        Espresso.onView(withContentDescription("Navigate up")).perform(click());
        Delay.stopDelay();

        //Check if keyboard is closed.
        Assert.assertFalse(imm.isAcceptingText());

        //Check if the activity is destroying?
        Assert.assertTrue(mTestActivity.isFinishing());
    }

    @After
    public void tearUp() {
        mTestActivity.finish();
    }

    @Override
    public Activity getActivity() {
        return mActivityTestRule.getActivity();
    }
}