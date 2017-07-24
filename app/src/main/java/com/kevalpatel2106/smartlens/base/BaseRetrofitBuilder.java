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

import android.content.Context;
import android.support.annotation.NonNull;

import com.kevalpatel2106.smartlens.BuildConfig;
import com.kevalpatel2106.smartlens.utils.FileUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Keval Patel on 23/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public abstract class BaseRetrofitBuilder {

    private final Context mContext;

    public BaseRetrofitBuilder(@NonNull Context context) {
        //noinspection ConstantConditions
        if (context == null)
            throw new IllegalArgumentException("Cannot be null.");

        mContext = context;
    }

    private OkHttpClient buildHttpClient(@NonNull Context context,
                                         boolean isCacheEnable,
                                         int cacheTimeSeconds) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ?
                HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new CacheInterceptor(isCacheEnable, cacheTimeSeconds))
                .cache(new Cache(FileUtils.getCacheDir(context), CacheInterceptor.CACHE_SIZE))
                .build();
    }

    /**
     * Get the instance of the retrofit.
     *
     * @param isCacheEnable    True if you want to cache the request for 30 seconds.
     * @param cacheTimeSeconds Cache expiration time in seconds
     * @return {@link Retrofit.Builder}
     */
    @SuppressWarnings("SameParameterValue")
    protected Retrofit getRetrofitBuilder(boolean isCacheEnable,
                                          int cacheTimeSeconds) {
        //Building retrofit
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getBaseUrl())
                .client(buildHttpClient(mContext, isCacheEnable, cacheTimeSeconds))
                .build();
    }

    protected abstract String getBaseUrl();

    private class CacheInterceptor implements Interceptor {
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
}
