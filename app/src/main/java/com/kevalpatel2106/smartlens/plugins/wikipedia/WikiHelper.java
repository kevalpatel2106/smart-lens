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

package com.kevalpatel2106.smartlens.plugins.wikipedia;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.infopage.BaseInfoHelper;
import com.kevalpatel2106.smartlens.infopage.InfoCallbacks;
import com.kevalpatel2106.smartlens.infopage.InfoModel;
import com.kevalpatel2106.smartlens.utils.RetrofitHelper;
import com.kevalpatel2106.smartlens.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by Keval Patel on 23/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class WikiHelper extends BaseInfoHelper {
    public static volatile String BASE_WIKI_URL = "https://en.wikipedia.org/w/";
    private final RetrofitHelper mRetrofitHelper;
    private int position = 0;

    public WikiHelper(@NonNull Context context, @NonNull InfoCallbacks infoCallbacks) {
        super(context, infoCallbacks);
        mRetrofitHelper = new RetrofitHelper(context);
    }

    @Override
    public void getLabelDetail(@NonNull List<String> labels) {
        //Convert first character to cap.
        final String wikiLabel = labels.get(position);
        Observable<ResponseBody> observable = getApiService()
                .getInfo("json", "query", "extracts", "", "",
                        WikiUtils.generateWikiLabel(wikiLabel));
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(responseBody -> {
                    String summaryText = WikiUtils.getSummaryFromJson(Utils
                            .getStringFromStream(responseBody.byteStream()));

                    if (WikiUtils.isValidSummary(wikiLabel, summaryText)) {
                        InfoModel infoModel = new InfoModel(wikiLabel);
                        infoModel.setInfo(summaryText);

                        //Get the image.
                        getWikiImage(infoModel);

                        //Load the recognition
                        getRecommendedItems(labels, wikiLabel);
                    } else {

                        // Try to get the next label
                        position++;
                        if (position < labels.size() - 1) {
                            //Get the info about the next possible item
                            getLabelDetail(labels);
                        } else {
                            //None of the label has info.
                            mCallbacks.onError(mContext.getString(R.string.wiki_helper_no_info_found));
                        }
                    }
                }, throwable -> {
                    mCallbacks.onError(RetrofitHelper.getErrorMessage(throwable));
                    Timber.d("accept: " + RetrofitHelper.getErrorMessage(throwable));
                });
    }

    private void getWikiImage(@NonNull InfoModel infoModel) {
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
                            mCallbacks.onSuccess(infoModel);
                        },
                        throwable -> {
                            mCallbacks.onSuccess(infoModel);
                            Timber.d("accept: " + RetrofitHelper.getErrorMessage(throwable));
                        });
    }

    /**
     * Get the api service.
     *
     * @return {@link WikiService}
     */
    private WikiService getApiService() {
        return mRetrofitHelper.getRetrofitBuilder(BASE_WIKI_URL,
                false, 0)
                .create(WikiService.class);
    }

    /**
     * Get the wiki image.
     *
     * @param labels {@link InfoModel}
     */
    @Override
    public void getRecommendedItems(@NonNull List<String> labels,
                                    @NonNull String mainLabel) {

        ArrayList<String> recommendedLabel = new ArrayList<>();
        for (String label : labels) {
            if (!mainLabel.equalsIgnoreCase(label)) recommendedLabel.add(label);
            Collections.addAll(recommendedLabel, WikiUtils.generatePossibleWikiLabel(label));
        }
        for (String label : recommendedLabel) {
            Observable<ResponseBody> observable = getApiService()
                    .getImage("json",
                            "query",
                            "pageimages",
                            "original",
                            WikiUtils.generateWikiLabel(label));
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

                                if (!imageUrl.isEmpty()) {
                                    InfoModel infoModel = new InfoModel(label);
                                    infoModel.setImageUrl(imageUrl);
                                    Timber.d("getWikiImage: " + imageUrl);
                                    mCallbacks.onRecommendedLoaded(infoModel);
                                }
                            },
                            throwable -> {
                                Timber.d("accept: " + RetrofitHelper.getErrorMessage(throwable));
                            });
        }
    }
}
