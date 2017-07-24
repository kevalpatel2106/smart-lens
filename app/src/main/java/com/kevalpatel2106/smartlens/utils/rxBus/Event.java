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

/**
 * Created by Keval on 06-Jul-17.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class Event {
    @android.support.annotation.NonNull
    private final String mTag;
    private Object mObject;

    @SuppressWarnings("ConstantConditions")
    public Event(@android.support.annotation.NonNull String tag) {
        mTag = tag;
        if (mTag == null) throw new IllegalArgumentException("Tag cannot be null.");
    }

    @SuppressWarnings("ConstantConditions")
    public Event(@android.support.annotation.NonNull Object o) {
        mTag = o.getClass().getSimpleName();
        mObject = o;
        if (mObject == null) throw new IllegalArgumentException("Object cannot be null.");
    }

    /**
     * @return Tag of the event
     */
    @android.support.annotation.NonNull
    public String getTag() {
        return mTag;
    }

    /**
     * @return Object passed through bus.
     */
    public Object getObject() {
        return mObject;
    }
}
