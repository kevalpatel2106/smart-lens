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

package com.kevalpatel2106.smartlens.utils.rxBus;

import android.app.Activity;
import android.support.test.runner.AndroidJUnit4;

import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by Keval Patel on 24/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
@RunWith(AndroidJUnit4.class)
public class RxBusTest extends BaseTestClass {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkEventBusWithSingleClass() throws Exception {
        Long aLong = 234L;
        RxBus.getDefault().register(Long.class)
                .doOnSubscribe(Assert::assertNotNull)
                .doOnError(throwable -> fail())
                .doOnNext(event -> assertEquals(event.getObject(), aLong))
                .subscribe();
        RxBus.getDefault().post(new Event(aLong));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkEventBusWithSingleTag() throws Exception {
        String mockTag = "MOCK_TAG";
        RxBus.getDefault().register(mockTag)
                .doOnSubscribe(Assert::assertNotNull)
                .doOnError(throwable -> fail())
                .doOnNext(event -> {
                    assertEquals(event.getTag(), mockTag);
                    assertNull(event.getObject());
                })
                .subscribe();
        RxBus.getDefault().post(new Event(mockTag));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkEventBusWithMultipleClass() throws Exception {
        Long aLong = 234L;
        Integer integer = 34;
        RxBus.getDefault().register(new Class[]{Long.class, Integer.class})
                .doOnSubscribe(Assert::assertNotNull)
                .doOnError(throwable -> fail())
                .doOnNext(event -> {
                    if (event.getObject() instanceof Long) {
                        assertEquals(event.getObject(), aLong);
                    } else if (event.getObject() instanceof Integer) {
                        assertEquals(event.getObject(), integer);
                    }
                })
                .subscribe();
        RxBus.getDefault().post(new Event(aLong));
        RxBus.getDefault().post(new Event(integer));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void checkEventBusWithMultipleTags() throws Exception {
        final String mockTag1 = "MOCK_TAG_1";
        final String mockTag2 = "MOCK_TAG_2";

        RxBus.getDefault().register(new String[]{mockTag1, mockTag2})
                .doOnSubscribe(Assert::assertNotNull)
                .doOnError(throwable -> fail())
                .doOnNext(event -> {
                    assertNotNull(event.getTag());
                    switch (event.getTag()) {
                        case mockTag1:
                            //Pass
                            break;
                        case mockTag2:
                        default:
                            fail();
                    }
                })
                .subscribe();
        RxBus.getDefault().post(new Event(mockTag1));
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}