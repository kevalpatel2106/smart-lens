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
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.imageClassifier.ImageClassifiedEvent;
import com.kevalpatel2106.smartlens.imageClassifier.Recognition;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.testUtils.CustomMatchers;
import com.kevalpatel2106.smartlens.testUtils.Delay;
import com.kevalpatel2106.smartlens.testUtils.FragmentTestRule;
import com.kevalpatel2106.smartlens.testUtils.TestConfig;
import com.kevalpatel2106.smartlens.utils.rxBus.Event;
import com.kevalpatel2106.smartlens.utils.rxBus.RxBus;
import com.kevalpatel2106.smartlens.wikipedia.WikiHelper;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
public class InfoFragmentTest extends BaseTestClass {
    @SuppressWarnings("unused")
    private static final String TAG = "InfoFragmentTest";
    private static final String MOCK_LABEL = "electric locomotive";

    @Rule
    public FragmentTestRule<InfoFragment> mWikiFragmentFragmentTestRule =
            new FragmentTestRule<>(InfoFragment.class);

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

    @Before
    public void init() {
        mWikiFragmentFragmentTestRule.launchActivity(null);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkNewInstance() throws Exception {
        assertTrue(InfoFragment.getNewInstance() instanceof InfoFragment);
    }

    @Test
    public void checkRealApiResponse() throws Exception {
        WikiHelper.BASE_WIKI_URL = "https://en.wikipedia.org/w/";
        //Wait for the rx bus to get register after view creation
        Delay.startDelay(1000);
        onView(withId(R.id.wiki_page_tv));

        //Publish it with RxBus
        RxBus.getDefault().post(generateMockLabelsEvent());

        //Wait for the api call
        Delay.startDelay(TestConfig.DELAY_FOR_REAL_API);

        //Check if there are text?
        onView(withId(R.id.wiki_page_title_tv)).check(ViewAssertions.matches(CustomMatchers.hasText()));
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

        //Wait for the rx bus to get register after view creation
        Delay.startDelay(1000);
        onView(withId(R.id.wiki_page_tv));

        //Publish it with RxBus
        RxBus.getDefault().post(generateMockLabelsEvent());

        //Wait for mock api
        Delay.startDelay(TestConfig.DELAY_FOR_REAL_API);

        //Check if there are text?
        onView(withId(R.id.wiki_page_title_tv)).check(ViewAssertions.matches(CustomMatchers.hasText()));
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

        //Wait for the rx bus to get register after view creation
        Delay.startDelay(1000);
        onView(withId(R.id.wiki_page_tv));

        //Publish it with RxBus
        RxBus.getDefault().post(generateMockLabelsEvent());

        //Wait for mock api
        Delay.startDelay(TestConfig.DELAY_FOR_MOCK_API);

        //Check if there are text?
        onView(withId(R.id.wiki_page_title_tv)).check(ViewAssertions.matches(not(CustomMatchers.hasText())));
        onView(withId(R.id.wiki_page_tv)).check(ViewAssertions.matches(not(CustomMatchers.hasText())));
        onView(withId(R.id.wiki_page_iv)).check(ViewAssertions.matches(not(CustomMatchers.hasImage())));

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

        //Wait for the rx bus to get register after view creation
        Delay.startDelay(1000);
        onView(withId(R.id.wiki_page_tv));

        //Publish it with RxBus
        RxBus.getDefault().post(generateMockLabelsEvent());

        //Wait for mock api
        Delay.startDelay(TestConfig.DELAY_FOR_MOCK_API);

        //Check if there are text?
        onView(withId(R.id.wiki_page_title_tv)).check(ViewAssertions.matches(not(CustomMatchers.hasText())));
        onView(withId(R.id.wiki_page_tv)).check(ViewAssertions.matches(not(CustomMatchers.hasText())));
        onView(withId(R.id.wiki_page_iv)).check(ViewAssertions.matches(not(CustomMatchers.hasImage())));

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

        //Wait for the rx bus to get register after view creation
        Delay.startDelay(1000);
        onView(withId(R.id.wiki_page_tv));

        //Publish it with RxBus
        RxBus.getDefault().post(generateMockLabelsEvent());

        //Wait for mock api
        Delay.startDelay(TestConfig.DELAY_FOR_MOCK_API);

        //Check if there are text?
        onView(withId(R.id.wiki_page_title_tv)).check(ViewAssertions.matches(CustomMatchers.hasText()));
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
            fail("Failed to start mock server.");
            throw new RuntimeException("Failed to start mock server.");
        }
    }

    private String getStringFromFile(Context context, int filename) {
        return getStringFromInputStream(context.getResources().openRawResource(filename));
    }

    @NonNull
    private Event generateMockLabelsEvent() {
        List<Recognition> recognitions = new ArrayList<>();
        Recognition mockRecognition = new Recognition("1", MOCK_LABEL,
                0.56f, null);
        recognitions.add(mockRecognition);
        return new Event(new ImageClassifiedEvent(recognitions));
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