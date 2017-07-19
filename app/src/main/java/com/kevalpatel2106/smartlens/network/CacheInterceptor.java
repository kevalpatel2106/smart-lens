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

package com.kevalpatel2106.smartlens.network;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;

/**
 * Created by Keval on 16-Jun-17.
 * Cache controller for apis.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */


class CacheInterceptor implements Interceptor {
    static final int CACHE_SIZE = 5242880;          //5 MB //Cache size.

    @SuppressWarnings("unused")
    private static final String TAG = "CacheInterceptor";
    private final boolean mEnableCache;                   //True to enable caching of request
    private final int mCacheTime;

    /**
     * Constructor.
     *
     * @param enableCache True if you want to enable caching.
     * @param cacheTime   Caching time in seconds.
     */
    CacheInterceptor(boolean enableCache, int cacheTime) {
        mEnableCache = enableCache;
        mCacheTime = cacheTime;
    }

    @Override
    public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        //Modify the response based on caching status
        if (!mEnableCache) {
            CacheControl cacheControl = new CacheControl.Builder()
                    .maxStale(mCacheTime, TimeUnit.SECONDS)
                    .build();
            request = request.newBuilder()
                    .header("Cache-Control", cacheControl.toString())   //Enable cache
                    .build();
        } else {
            request = request.newBuilder()
                    .removeHeader("Cache-Control")      //Remove caching header
                    .build();
        }
        return chain.proceed(request);
    }
}

