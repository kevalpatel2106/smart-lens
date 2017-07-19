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

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Keval on 31-May-17.
 * This class is to extend the functionality of {@link ViewPager}. Use this class instead
 * of {@link ViewPager} through out the application.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class BaseViewPager extends ViewPager {

    private boolean mIsSwipeGestureEnable = true;

    public BaseViewPager(Context context) {
        super(context);
    }

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.mIsSwipeGestureEnable && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.mIsSwipeGestureEnable && super.onInterceptTouchEvent(event);
    }

    /**
     * @param enabled True to enable the swipe gesture for the view pager to change the currently
     *                displaying page. By default this gesture is enabled.
     */
    public void setSwipeGestureEnable(boolean enabled) {
        this.mIsSwipeGestureEnable = enabled;
    }
}
