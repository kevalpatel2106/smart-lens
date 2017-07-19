package com.kevalpatel2106.smartlens.base;

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
