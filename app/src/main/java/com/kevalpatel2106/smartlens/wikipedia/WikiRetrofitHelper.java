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

package com.kevalpatel2106.smartlens.wikipedia;

import android.content.Context;

import com.kevalpatel2106.smartlens.base.BaseRetrofitBuilder;
import com.kevalpatel2106.smartlens.infopage.InfoCallbacks;
import com.kevalpatel2106.smartlens.infopage.InfoModel;
import com.kevalpatel2106.smartlens.utils.Utils;

import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by Keval Patel on 23/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class WikiRetrofitHelper extends BaseRetrofitBuilder {
    public static String BASE_WIKI_URL = "https://en.wikipedia.org/w/";
    private final InfoCallbacks mInfoCallbacks;

    public WikiRetrofitHelper(@NonNull Context context, @NonNull InfoCallbacks infoCallbacks) {
        super(context);

        if (infoCallbacks == null)
            throw new IllegalArgumentException("WikiCallback cannot be null.");

        mInfoCallbacks = infoCallbacks;
    }

    @NonNull
    @Override
    protected String getBaseUrl() {
        return BASE_WIKI_URL;
    }

    /**
     * Get the api service.
     *
     * @return {@link WikiService}
     */
    private WikiService getApiService() {
        return getRetrofitBuilder(false, 0)
                .create(WikiService.class);
    }

    public void getWikiPageDetail(@NonNull final String label) {
        //Convert first character to cap.
        final String wikiLabel = WikiUtils.generateWikiLabel(label);
        Observable<ResponseBody> observable = getApiService()
                .getInfo("json", "query", "extracts", "", "", wikiLabel);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(responseBody -> {
                    String responseStr = Utils.getStringFromStream(responseBody.byteStream());
                    JSONObject pagesObj = new JSONObject(responseStr)
                            .getJSONObject("query")
                            .getJSONObject("pages");
                    String summaryText = pagesObj
                            .getJSONObject(pagesObj.names().getString(0))
                            .getString("extract");

                    if (WikiUtils.isValidSummary(wikiLabel, summaryText)) {
                        InfoModel infoModel = new InfoModel(wikiLabel);
                        infoModel.setInfo(summaryText);

                        //Get the image.
                        getWikiImage(infoModel);
                    } else {
                        //TODO Handle no result found
                    }
                }, throwable -> {
                    mInfoCallbacks.onError(BaseRetrofitBuilder.getErrorMessage(throwable));
                    Timber.d("accept: " + BaseRetrofitBuilder.getErrorMessage(throwable));
                });
    }

    void getWikiImage(@NonNull InfoModel infoModel) {
        Observable<ResponseBody> observable = getApiService()
                .getImage("json", "query", "pageimages", "original",
                        infoModel.getLabel());
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(responseBody -> {
                            String responseStr = Utils.getStringFromStream(responseBody.byteStream());
                            JSONObject pagesObj = new JSONObject(responseStr)
                                    .getJSONObject("query")
                                    .getJSONObject("pages");
                            String imageUrl = pagesObj
                                    .getJSONObject(pagesObj.names().getString(0))
                                    .getJSONObject("thumbnail")
                                    .getString("original");

                            Timber.d("getWikiImage: " + imageUrl);

                            if (!imageUrl.isEmpty()) infoModel.setImageUrl(imageUrl);
                            mInfoCallbacks.onSuccess(infoModel);
                        },
                        throwable -> {
                            mInfoCallbacks.onSuccess(infoModel);
                            Timber.d("accept: " + BaseRetrofitBuilder.getErrorMessage(throwable));
                        });
    }
}
