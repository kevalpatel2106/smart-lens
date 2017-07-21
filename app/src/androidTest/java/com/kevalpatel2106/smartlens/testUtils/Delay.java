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

package com.kevalpatel2106.smartlens.testUtils;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.TimeUnit;

/**
 * Created by Keval Patel on 05/04/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public final class Delay implements IdlingResource {
    private static Delay sTimeIdlingResource;

    private final long mStartTime;
    private final long mWaitingTime;
    private ResourceCallback mResourceCallback;

    /**
     * Private construction.
     *
     * @param waitingTime Wait time.
     */
    private Delay(long waitingTime) {
        this.mStartTime = System.currentTimeMillis();
        this.mWaitingTime = waitingTime;
    }

    /**
     * Register idling resource to delay for given time.
     *
     * @param waitTimeMills Wait time in millisecond.
     */
    public static void startDelay(long waitTimeMills) {
        // Make sure Espresso does not time out
        IdlingPolicies.setMasterPolicyTimeout(waitTimeMills * 2, TimeUnit.MILLISECONDS);
        IdlingPolicies.setIdlingResourceTimeout(waitTimeMills * 2, TimeUnit.MILLISECONDS);

        sTimeIdlingResource = new Delay(waitTimeMills);
        Espresso.registerIdlingResources(sTimeIdlingResource);
    }

    /**
     * Stop idling resource.
     */
    public static void stopDelay() {
        if (sTimeIdlingResource != null) Espresso.unregisterIdlingResources(sTimeIdlingResource);
        sTimeIdlingResource = null;
    }

    @Override
    public String getName() {
        return Delay.class.getName() + ":" + mWaitingTime;
    }

    @Override
    public boolean isIdleNow() {
        long elapsed = System.currentTimeMillis() - mStartTime;
        boolean idle = (elapsed >= mWaitingTime);
        if (idle) mResourceCallback.onTransitionToIdle();
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.mResourceCallback = resourceCallback;
    }
}
