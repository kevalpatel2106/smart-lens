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

import android.content.Context;
import android.support.test.espresso.intent.Intents;

import com.kevalpatel2106.smartlens.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Keval on 02-Feb-17.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public final class IntentTestUtils {

    /**
     * Check if the activity is changed when any view is clicked.
     *
     * @param context          instance of the caller.
     * @param viewId           id of the view.
     * @param nextActivityName Name of the next activity.
     */
    public static void checkActivityOnViewClicked(Context context,
                                                  int viewId,
                                                  String nextActivityName) {
        Intents.init();

        //set intent package
        intending(toPackage(context.getString(R.string.package_name)));

        // Perform click operation on FAB
        onView(withId(viewId)).perform(click());

        // Assert that if the opened activity is Quiz activity.
        intended(hasComponent(nextActivityName));

        Intents.release();
    }
}
