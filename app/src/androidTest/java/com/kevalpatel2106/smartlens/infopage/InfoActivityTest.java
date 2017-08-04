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

package com.kevalpatel2106.smartlens.infopage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.plugins.wikipedia.WikiHelper;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.testUtils.CustomMatchers;
import com.kevalpatel2106.smartlens.testUtils.Delay;
import com.kevalpatel2106.smartlens.testUtils.TestConfig;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by Keval Patel on 23/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public class InfoActivityTest extends BaseTestClass {

    @SuppressWarnings("unused")
    private static final String TAG = "InfoActivityTest";
    private static final String MOCK_LABEL = "electric locomotive";
    private static int ERROR_VIEW = 2;
    private static int INFO_VIEW = 1;
    @Rule
    public ActivityTestRule<InfoActivity> mWikiFragmentFragmentTestRule =
            new ActivityTestRule<>(InfoActivity.class, false, false);

    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private void openActivity() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add(MOCK_LABEL);

        Intent i = new Intent();
        i.putExtra(InfoActivity.ARA_RECOGNITION_LIST, labels);
        mWikiFragmentFragmentTestRule.launchActivity(i);
    }

    @Test
    public void checkRealApiResponse() throws Exception {
        WikiHelper.BASE_WIKI_URL = "https://en.wikipedia.org/w/";
        openActivity();

        //Wait for the api call
        Delay.startDelay(TestConfig.DELAY_FOR_REAL_API);
        onView(withId(R.id.root_view)).perform(ViewActions.closeSoftKeyboard());

        //Check if there are text?
        assertTrue(mWikiFragmentFragmentTestRule.getActivity().mViewFlipper.getDisplayedChild() ==
                INFO_VIEW);
        onView(withId(R.id.wiki_page_tv)).check(ViewAssertions.matches(CustomMatchers.hasText()));
        onView(withId(R.id.wiki_page_iv)).check(ViewAssertions.matches(CustomMatchers.hasImage()));
        Delay.stopDelay();
    }

    @Test
    public void checkInfoFoundApiResponse() throws Exception {
        MockWebServer mockWebServer = startMockWebServer();
        //Success response for the info api
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getStringFromFile(InstrumentationRegistry.getInstrumentation().getContext(),
                        com.kevalpatel2106.smartlens.test.R.raw.wiki_info_success_response)));
        //Success response for the image api.
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getStringFromFile(InstrumentationRegistry.getInstrumentation().getContext(),
                        com.kevalpatel2106.smartlens.test.R.raw.wiki_image_success_response)));
        openActivity();

        //Wait for mock api
        Delay.startDelay(TestConfig.DELAY_FOR_REAL_API);
        onView(withId(R.id.root_view)).perform(ViewActions.closeSoftKeyboard());

        //Check if there are text?
        assertTrue(mWikiFragmentFragmentTestRule.getActivity().mViewFlipper.getDisplayedChild() ==
                INFO_VIEW);
        onView(withId(R.id.wiki_page_tv)).check(ViewAssertions.matches(CustomMatchers.hasText()));
        onView(withId(R.id.wiki_page_iv)).check(ViewAssertions.matches(CustomMatchers.hasImage()));

        Delay.stopDelay();
        mockWebServer.shutdown();
    }

    @Test
    public void checkInfoNotFoundApiResponse() throws Exception {
        MockWebServer mockWebServer = startMockWebServer();
        //Success response for the info api
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getStringFromFile(InstrumentationRegistry.getInstrumentation().getContext(),
                        com.kevalpatel2106.smartlens.test.R.raw.wiki_info_not_found_success_response)));
        //Success response for the image api.
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getStringFromFile(InstrumentationRegistry.getInstrumentation().getContext(),
                        com.kevalpatel2106.smartlens.test.R.raw.wiki_image_success_response)));
        openActivity();

        //Wait for mock api
        Delay.startDelay(TestConfig.DELAY_FOR_MOCK_API);
        onView(withId(R.id.root_view)).perform(ViewActions.closeSoftKeyboard());

        //Check if is error?
        assertTrue(mWikiFragmentFragmentTestRule.getActivity().mViewFlipper.getDisplayedChild() ==
                ERROR_VIEW);

        Delay.stopDelay();
        mockWebServer.shutdown();
    }

    @Test
    public void checkInfoApiResponseFail() throws Exception {
        MockWebServer mockWebServer = startMockWebServer();
        //Fail response for the info api.
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        //Success response for the image api.
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getStringFromFile(InstrumentationRegistry.getInstrumentation().getContext(),
                        com.kevalpatel2106.smartlens.test.R.raw.wiki_image_success_response)));
        openActivity();

        //Wait for mock api
        Delay.startDelay(TestConfig.DELAY_FOR_MOCK_API);
        onView(withId(R.id.root_view)).perform(ViewActions.closeSoftKeyboard());

        //Check if error occurred?
        assertTrue(mWikiFragmentFragmentTestRule.getActivity().mViewFlipper.getDisplayedChild() ==
                ERROR_VIEW);

        Delay.stopDelay();
        mockWebServer.shutdown();
    }

    @Test
    public void checkImageApiResponseFail() throws Exception {
        MockWebServer mockWebServer = startMockWebServer();
        //Success response for the info api
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(getStringFromFile(InstrumentationRegistry.getInstrumentation().getContext(),
                        com.kevalpatel2106.smartlens.test.R.raw.wiki_info_success_response)));
        //Fail response for the image api.
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));
        openActivity();

        //Wait for mock api
        Delay.startDelay(TestConfig.DELAY_FOR_MOCK_API);
        onView(withId(R.id.root_view)).perform(ViewActions.closeSoftKeyboard());

        //Check if there are text?
        assertTrue(mWikiFragmentFragmentTestRule.getActivity().mViewFlipper.getDisplayedChild() ==
                INFO_VIEW);
        onView(withId(R.id.wiki_page_tv)).check(ViewAssertions.matches(CustomMatchers.hasText()));
        onView(withId(R.id.wiki_page_iv)).check(ViewAssertions.matches(not(CustomMatchers.hasImage())));

        Delay.stopDelay();
        mockWebServer.shutdown();
    }

    /**
     * Start mock web server for the wikipedia api.
     *
     * @return {@link MockWebServer}
     */
    private MockWebServer startMockWebServer() {
        try {
            MockWebServer mockWebServer = new MockWebServer();
            mockWebServer.start();
            WikiHelper.BASE_WIKI_URL = mockWebServer.url("/").toString();
            return mockWebServer;
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to startTimeMills mock server.");
            throw new RuntimeException("Failed to startTimeMills mock server.");
        }
    }

    private String getStringFromFile(Context context, int filename) {
        return getStringFromInputStream(context.getResources().openRawResource(filename));
    }

    @After
    public void tearDown() {
        getActivity().finish();
    }

    @Override
    public Activity getActivity() {
        return mWikiFragmentFragmentTestRule.getActivity();
    }
}