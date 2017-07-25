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

import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by Keval on 02-Feb-17.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public final class CustomMatchers {

    /**
     * Returns a matcher that matches {@link TextView}s based on text property value.
     *
     * @param stringMatcher {@link Matcher} of {@link String} with text to match
     */
    @NonNull
    public static Matcher<View> withErrorText(final Matcher<String> stringMatcher) {

        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            public void describeTo(final Description description) {
                description.appendText("with error text: ");
                stringMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(final TextView textView) {
                return stringMatcher.matches(textView.getError().toString());
            }
        };
    }

    /**
     * Returns a matcher that matches {@link TextView}s based on text property value.
     */
    @NonNull
    public static Matcher<View> hasError() {

        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            public void describeTo(final Description description) {
                description.appendText("Has error case failed.");
            }

            @Override
            public boolean matchesSafely(final TextView textView) {
                return textView.getError() != null;
            }
        };
    }

    /**
     * Returns a matcher that matches {@link TextView}s based on text property value.
     *
     * @param stringMatcher {@link Matcher} of {@link String} with text to match
     */
    @NonNull
    public static Matcher<View> withPasswordText(final Matcher<String> stringMatcher) {

        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            public void describeTo(final Description description) {
                description.appendText("with error text: ");
                stringMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(final TextView textView) {
                return stringMatcher.matches(textView.getText().toString());
            }
        };
    }

    /**
     * Returns a matcher that matches {@link TextView}s based on text property value.
     */
    @NonNull
    public static Matcher<View> checkTextLength(final int length) {

        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            public void describeTo(final Description description) {
                description.appendText("Text length is : " + description);
            }

            @Override
            public boolean matchesSafely(final TextView textView) {
                return textView.getText().length() == length;
            }
        };
    }

    /**
     * Returns a matcher that matches {@link TextView}s based on text property value.
     */
    @NonNull
    public static Matcher<View> hasText() {

        return new BoundedMatcher<View, TextView>(TextView.class) {

            @Override
            public void describeTo(final Description description) {
                description.appendText("Text length is : " + description);
            }

            @Override
            public boolean matchesSafely(final TextView textView) {
                return textView.getText().length() > 0;
            }
        };
    }

    @NonNull
    public static TypeSafeMatcher<View> hasImage() {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("No image found.");
            }

            @Override
            protected boolean matchesSafely(View actualImageView) {
                return ((ImageView) actualImageView).getDrawable() != null;
            }
        };
    }
}