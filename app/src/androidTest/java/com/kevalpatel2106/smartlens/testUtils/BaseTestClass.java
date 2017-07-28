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

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;
import android.view.WindowManager;

import org.junit.Before;

/**
 * Created by Keval on 10-Apr-17.
 */

public abstract class BaseTestClass {

//    /**
//     * Gran all the runtime permissions.
//     * Update it when you add new permission in manifest.
//     */
//    @BeforeClass
//    public static void grantAllRuntimePermission() {
//        // In M+, trying to call a number will trigger a runtime dialog. Make sure
//        // the permission is granted before running this test.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getInstrumentation().getUiAutomation().executeShellCommand("pm grant " + getTargetContext().getPackageName() + " android.permission.CAMERA");
//            getInstrumentation().getUiAutomation().executeShellCommand("pm grant " + getTargetContext().getPackageName() + " android.permission.SYSTEM_ALERT_WINDOW");
//            getInstrumentation().getUiAutomation().executeShellCommand("pm grant " + getTargetContext().getPackageName() + " android.permission.WRITE_EXTERNAL_STORAGE");
//            getInstrumentation().getUiAutomation().executeShellCommand("pm grant " + getTargetContext().getPackageName() + " android.permission.WRITE_SETTINGS");
//            getInstrumentation().getUiAutomation().executeShellCommand("pm grant " + getTargetContext().getPackageName() + " android.permission.ACCESS_COARSE_LOCATION");
//            getInstrumentation().getUiAutomation().executeShellCommand("pm grant " + getTargetContext().getPackageName() + " android.permission.ACCESS_FINE_LOCATION");
//            getInstrumentation().getUiAutomation().executeShellCommand("pm grant " + getTargetContext().getPackageName() + " android.permission.SEND_SMS");
//            getInstrumentation().getUiAutomation().executeShellCommand("pm grant " + getTargetContext().getPackageName() + " android.permission.READ_CONTACTS");
//            getInstrumentation().getUiAutomation().executeShellCommand("pm grant " + getTargetContext().getPackageName() + " android.permission.PROCESS_OUTGOING_CALLS");
//        }
//    }

    public abstract Activity getActivity();

    @Before
    public void setup() {
        // Unlock the screen if it's locked
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        try {
            device.wakeUp();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Set the flags on our activity so it'll appear regardless of lock screen state
        new Handler(Looper.getMainLooper()).post(() -> {
            if (getActivity() == null) return;
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        });
    }
}
