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

import android.app.Activity;
import android.support.test.espresso.assertion.ViewAssertions;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.imageClassifier.ImageClassifiedEvent;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;
import com.kevalpatel2106.smartlens.testUtils.CustomMatchers;
import com.kevalpatel2106.smartlens.testUtils.Delay;
import com.kevalpatel2106.smartlens.testUtils.FragmentTestRule;
import com.kevalpatel2106.smartlens.testUtils.TestConfig;
import com.kevalpatel2106.smartlens.utils.rxBus.Event;
import com.kevalpatel2106.smartlens.utils.rxBus.RxBus;
import com.kevalpatel2106.tensorflow.Classifier;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

/**
 * Created by Keval Patel on 23/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
public class WikiFragmentTest extends BaseTestClass {
    @SuppressWarnings("unused")
    private static final String TAG = "WikiFragmentTest";
    private static final String MOCK_LABEL = "electric locomotive";

    @Rule
    public FragmentTestRule<WikiFragment> mWikiFragmentFragmentTestRule =
            new FragmentTestRule<>(WikiFragment.class);
    private MockWebServer mMockWebServer;

    @Before
    public void init() {
        mWikiFragmentFragmentTestRule.launchActivity(null);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkNewInstance() throws Exception {
        assertTrue(WikiFragment.getNewInstance() instanceof WikiFragment);
    }

    @Test
    public void checkApiResponse() throws Exception {
        //Generate the mock event
        List<Classifier.Recognition> recognitions = new ArrayList<>();
        Classifier.Recognition mockRecognition = new Classifier.Recognition("1", MOCK_LABEL,
                0.56f, null);
        recognitions.add(mockRecognition);
        Event event = new Event(new ImageClassifiedEvent(recognitions));

        //Wait for the rx bus to get register after view creation
        Delay.startDelay(1000);
        onView(withId(R.id.wiki_page_tv));

        //Publish it with RxBus
        RxBus.getDefault().post(event);

        //Wait for the api call
        Delay.startDelay(TestConfig.DELAY_FOR_API);

        //Check if there are text?
        onView(withId(R.id.wiki_page_tv)).check(ViewAssertions.matches(CustomMatchers.hasText()));
        onView(withId(R.id.wiki_page_iv)).check(ViewAssertions.matches(CustomMatchers.hasImage()));
        Delay.stopDelay();
    }

    @Test
    public void checkApiResponseFail() throws Exception {
        try {
            mMockWebServer = new MockWebServer();
            mMockWebServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        WikiRetrofitBuilder.BASE_WIKI_URL = mMockWebServer.url("/").toString();
        mMockWebServer.enqueue(new MockResponse().setResponseCode(500));

        //Generate the mock event
        List<Classifier.Recognition> recognitions = new ArrayList<>();
        Classifier.Recognition mockRecognition = new Classifier.Recognition("1", MOCK_LABEL,
                0.56f, null);
        recognitions.add(mockRecognition);
        Event event = new Event(new ImageClassifiedEvent(recognitions));

        //Wait for the rx bus to get register after view creation
        Delay.startDelay(2000);
        onView(withId(R.id.wiki_page_tv));
        Delay.stopDelay();

        //Publish it with RxBus
        RxBus.getDefault().post(event);

        //Check if there are text?
        onView(withId(R.id.wiki_page_tv)).check(ViewAssertions.matches(not(CustomMatchers.hasText())));
        onView(withId(R.id.wiki_page_iv)).check(ViewAssertions.matches(not(CustomMatchers.hasImage())));
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