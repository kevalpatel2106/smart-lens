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

import android.content.Context;

import com.kevalpatel2106.smartlens.BuildConfig;

import io.reactivex.annotations.NonNull;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Keval on 20-Dec-16.
 * Build the client for api service.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

@SuppressWarnings("WeakerAccess")
public class RetrofitBuilder {
    @SuppressWarnings("unused")
    private static final String TAG = "RetrofitBuilder";

    /**
     * Get the instance of the retrofit {@link APIService}. The api request made using this won't
     * get cache.
     *
     * @param context Instance of caller.
     * @return {@link APIService}
     */
    public static APIService getApiService(@NonNull Context context) {
        return getApiService(context, false, 0);
    }

    /**
     * Get the instance of the retrofit {@link APIService}.
     *
     * @param context Instance of caller.
     * @param isCacheEnable    True if you want to cache the request for 30 seconds.
     * @param cacheTimeSeconds Cache expiration time in seconds
     * @return {@link APIService}
     */
    @SuppressWarnings("SameParameterValue")
    public static APIService getApiService(@NonNull Context context,
                                           boolean isCacheEnable,
                                           int cacheTimeSeconds) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ?
                HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new CacheInterceptor(isCacheEnable, cacheTimeSeconds))
                .cache(new Cache(context.getCacheDir(), CacheInterceptor.CACHE_SIZE))
                .build();

        //Building retrofit
        final Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(APIService.BASE_URL)
                .client(client)
                .build();

        return retrofit.create(APIService.class);
    }
}
