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
import android.support.annotation.Nullable;

import com.kevalpatel2106.smartlens.BuildConfig;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Keval on 20-Dec-16.
 * Build the client for api service.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class RetrofitBuilder {
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

    /**
     * Make in api call on the separate thread.
     *
     * @param observable {@link Observable}
     * @param listener   {@link ResponseListener}.
     * @return {@link Disposable}
     * @see <a href="https://github.com/ReactiveX/RxJava/issues/4942">How to handle exceptions and errors?</a>
     */
    @Nullable
    public static Disposable makeApiCall(@NonNull final Observable observable,
                                         @NonNull final ResponseListener listener) {
        return makeApiCall(observable, listener, false);
    }

    /**
     * Make in api call on the separate thread.
     *
     * @param observable           {@link Observable}
     * @param listener             {@link ResponseListener}.
     * @param isRetryOnNetworkFail If set true, this method will try for the api call on network not
     *                             available situation for three times with the period of 10 seconds.
     * @return {@link Disposable}
     * @see <a href="https://github.com/ReactiveX/RxJava/issues/4942">How to handle exceptions and errors?</a>
     */
    @Nullable
    public static Disposable makeApiCall(@NonNull final Observable observable,
                                         @NonNull final ResponseListener listener,
                                         final boolean isRetryOnNetworkFail) {
        //noinspection unchecked
        return observable
                .retryWhen(new RetryWithDelay(isRetryOnNetworkFail ? 3 : 0, 10000))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<BaseResult>() {
                    @Override
                    public void accept(@NonNull BaseResult response) throws Exception {
                        listener.onSuccess(response);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        try {
                            if (throwable instanceof HttpException) { //Error frm the server.
                                listener.onError("Something went wrong.");
                            } else if (throwable instanceof IOException) {  //Internet not available.
                                listener.onError("Internet is not available. Please try again.");
                            }
                        } catch (Exception e1) {
                            listener.onError(throwable.getMessage());
                            e1.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Function to retry for the particular number of times with interval.
     */
    public static class RetryWithDelay implements Function<Observable<? extends Throwable>, Observable<?>> {
        private final int mMaxRetries;
        private final int mRetryInMills;
        private int mRetryCount;

        /**
         * Public constructor.
         *
         * @param maxRetries       Maximum number of retries.
         * @param retryDelayMillis Interval between
         */
        public RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
            mMaxRetries = maxRetries;
            mRetryInMills = retryDelayMillis;

            mRetryCount = 0;
        }

        @Override
        public Observable<?> apply(final Observable<? extends Throwable> attempts) {
            return attempts
                    .flatMap(new Function<Throwable, Observable<?>>() {
                        @Override
                        public Observable<?> apply(final Throwable throwable) {
                            if (throwable instanceof UnknownHostException && ++mRetryCount <= mMaxRetries) {
                                // When this Observable calls onNext, the original
                                // Observable will be retried (i.e. re-subscribed).
                                return Observable.timer(mRetryInMills, TimeUnit.MILLISECONDS);
                            }

                            // Max retries hit. Just pass the error along.
                            return Observable.error(throwable);
                        }
                    });
        }
    }
}
