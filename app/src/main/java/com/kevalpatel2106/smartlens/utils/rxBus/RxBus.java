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


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Keval on 06-Jul-17.
 * This is rx sBus to pass messages between different layers of the application.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class RxBus {
    private static PublishSubject<Event> sBus = PublishSubject.create();
    private static RxBus sInstance = new RxBus();

    private RxBus() {
        //Do nothing
    }

    /**
     * Get the singleton instance of the bus
     *
     * @return {@link RxBus}
     */
    public static RxBus getDefault() {
        return sInstance;
    }

    /**
     * Post an event to the RxBus.
     *
     * @param event {@link Event} to post.
     */
    @SuppressWarnings("ConstantConditions")
    public void post(@android.support.annotation.NonNull Event event) {
        if (event == null) throw new IllegalArgumentException("Event cannot be null.");
        sBus.onNext(event);
    }

    public Observable<Event> register(@android.support.annotation.NonNull final String tag) {
        return register(new String[]{tag});
    }

    public Observable<Event> register(@android.support.annotation.NonNull final Class aClass) {
        return register(new Class[]{aClass});
    }

    public Observable<Event> register(@android.support.annotation.NonNull final String[] tags) {
        return sBus.filter(event -> {
            for (String t : tags) if (event.getTag().equals(t)) return true;
            return false;
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Get the observable of the RxBus.
     *
     * @param aClass Class to subscribe.
     * @return {@link Observable} of {@link Event}
     */
    public Observable<Event> register(@android.support.annotation.NonNull final Class[] aClass) {
        String[] tags = new String[aClass.length];
        for (int i = 0; i < aClass.length; i++) tags[i] = aClass[i].getSimpleName();

        return register(tags)
                .filter(event -> {
                    return event.getObject() != null;       //If the object is null.....don't pass.
                });
    }
}
