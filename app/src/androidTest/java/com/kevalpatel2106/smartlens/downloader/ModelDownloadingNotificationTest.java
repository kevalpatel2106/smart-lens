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

package com.kevalpatel2106.smartlens.downloader;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.testUtils.BaseTestClass;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by Keval Patel on 05/08/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
@RunWith(AndroidJUnit4.class)
public class ModelDownloadingNotificationTest extends BaseTestClass {

    private UiDevice mDevice;

    @Before
    public void init() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        //Open notification drawer
        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), 10000);
    }

    /**
     * Check if the model download notifications displayed?
     *
     * @see <a href="https://stackoverflow.com/a/33515200/4690731">https://stackoverflow.com/a/33515200/4690731</a>
     */
    @Test
    public void checkNotificationDisplayed() throws UiObjectNotFoundException {
        //Fire the notification
        ModelDownloadingNotification.notify(InstrumentationRegistry.getTargetContext(),
                0, true);

        /*
         * access Notification Center through resource id, package name, class name.
         * if you want to check resource id, package name or class name of the specific view
         * in the screen, run 'uiautomatorviewer' from command.
         */
        UiSelector notificationStackScroller = new UiSelector()
                .packageName("com.android.systemui")
                .className("android.view.ViewGroup")
                .resourceId("com.android.systemui:id/notification_stack_scroller");
        UiObject notificationStackScrollerUiObject = mDevice.findObject(notificationStackScroller);
        assertTrue(notificationStackScrollerUiObject.exists());

        /*
         * access top notification in the center through parent
         */
        UiObject notiSelectorUiObject = notificationStackScrollerUiObject.getChild(new UiSelector()
                .text(InstrumentationRegistry
                        .getTargetContext()
                        .getString(R.string.model_downloading_notification_title)));
        assertTrue(notiSelectorUiObject.exists());
    }


    /**
     * Check if we can cancel the cancel the notification? For that we will first create the notification
     * using {@link #checkNotificationDisplayed()}. Than we will cancel the notification.
     *
     * @see <a href="https://stackoverflow.com/a/33515200/4690731">https://stackoverflow.com/a/33515200/4690731</a>
     */
    @Test
    public void checkIfNotificationCanceled() throws UiObjectNotFoundException {
        //Fire the notification
        ModelDownloadingNotification.notify(InstrumentationRegistry.getTargetContext(),
                0, true);

        /*
         * access Notification Center through resource id, package name, class name.
         * if you want to check resource id, package name or class name of the specific view
         * in the screen, run 'uiautomatorviewer' from command.
         */
        UiSelector notificationStackScroller = new UiSelector()
                .packageName("com.android.systemui")
                .className("android.view.ViewGroup")
                .resourceId("com.android.systemui:id/notification_stack_scroller");
        UiObject notificationStackScrollerUiObject = mDevice.findObject(notificationStackScroller);
        assertTrue(notificationStackScrollerUiObject.exists());

        /*
         * access top notification in the center through parent
         */
        UiObject notiSelectorUiObject = notificationStackScrollerUiObject.getChild(new UiSelector()
                .text(InstrumentationRegistry
                        .getTargetContext()
                        .getString(R.string.model_downloading_notification_title)));
        assertTrue(notiSelectorUiObject.exists());

        //Now lets cancel the notification
        ModelDownloadingNotification.cancel(InstrumentationRegistry.getTargetContext());

        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), 10000);

        notiSelectorUiObject = notificationStackScrollerUiObject.getChild(new UiSelector()
                .text(InstrumentationRegistry
                        .getTargetContext()
                        .getString(R.string.model_downloading_notification_title)));
        assertFalse(notiSelectorUiObject.exists());
    }

    @Override
    public Activity getActivity() {
        return null;
    }
}