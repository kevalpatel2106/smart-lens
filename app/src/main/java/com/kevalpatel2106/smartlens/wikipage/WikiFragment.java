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

package com.kevalpatel2106.smartlens.wikipage;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.base.BaseFragment;
import com.kevalpatel2106.smartlens.base.BaseImageView;
import com.kevalpatel2106.smartlens.base.BaseTextView;
import com.kevalpatel2106.smartlens.imageClassifier.ImageClassifiedEvent;
import com.kevalpatel2106.smartlens.utils.Utils;
import com.kevalpatel2106.smartlens.utils.rxBus.RxBus;
import com.kevalpatel2106.tensorflow.Classifier;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 */
public class WikiFragment extends BaseFragment {
    @SuppressWarnings("unused")
    private static final String TAG = "WikiFragment";

    @BindView(R.id.wiki_page_iv)
    BaseImageView mWikiImage;

    @BindView(R.id.wiki_page_tv)
    BaseTextView mWikiTextView;

    public WikiFragment() {
        // Required empty public constructor
    }

    public static WikiFragment getNewInstance() {
        return new WikiFragment();
    }

    /**
     * Check if the summary is valid.
     *
     * @param label   label of the summary
     * @param summary Summary to check
     * @return True if the summary is valid.
     */
    @SuppressWarnings("ConstantConditions")
    static boolean isValidSummary(@NonNull String label, @NonNull String summary) {
        if (label == null || summary == null) return false;
        int thresholdLength = (label + " may refer to: ").length();
        return summary.length() > thresholdLength;
    }

    /**
     * Convert the label received to the wikipedia compatible label. This will convert first latter
     * to the capital and replaces spaces with '_'.
     *
     * @param label label to convert
     * @return Converted label.
     */
    @SuppressWarnings("ConstantConditions")
    static String generateWikiLabel(@NonNull String label) {
        if (label == null) throw new IllegalArgumentException("Cannot be null.");

        //Convert first character to caps
        label = Character.toUpperCase(label.charAt(0)) + label.substring(1).toLowerCase();

        //Replace space with _.
        label = label.replace("\\s", "_");
        return label;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wiki, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Register image classifier.
        RxBus.getDefault().register(new Class[]{ImageClassifiedEvent.class})
                .filter(event -> {
                    List<Classifier.Recognition> recognitions =
                            ((ImageClassifiedEvent) event.getObject()).getRecognitions();
                    return recognitions != null && !recognitions.isEmpty();
                })
                .doOnSubscribe(this::addSubscription)
                .doOnNext(event -> {
                    Log.e(TAG, "onViewCreated: " + ((ImageClassifiedEvent) event.getObject())
                            .getRecognitions()
                            .get(0)
                            .getTitle());
                    getWikiPageDetail(mContext, ((ImageClassifiedEvent) event.getObject())
                            .getRecognitions()
                            .get(0)
                            .getTitle());
                })
                .subscribe();
    }

    void getWikiPageDetail(@NonNull Context context, @NonNull String label) {
        //Convert first character to cap.
        label = WikiFragment.generateWikiLabel(label);

        Log.d(TAG, "getWikiPageDetail: " + label);

        String finalLabel = label;

        WikiRetrofitBuilder wikiRetrofitBuilder = new WikiRetrofitBuilder(context);
        Observable<ResponseBody> observable = wikiRetrofitBuilder.getApiService()
                .getInfo("json", "query", "extracts", "", "", label);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(this::addSubscription)
                .subscribe(responseBody -> {
                    String responseStr = Utils.getStringFromStream(responseBody.byteStream());
                    JSONObject pagesObj = new JSONObject(responseStr)
                            .getJSONObject("query")
                            .getJSONObject("pages");
                    String summaryText = pagesObj
                            .getJSONObject(pagesObj.names().getString(0))
                            .getString("extract");

                    if (isValidSummary(finalLabel, summaryText)) {
                        WikiPage wikiPage = new WikiPage(finalLabel);
                        wikiPage.setSummaryMessage(summaryText);
                        mWikiTextView.setText(wikiPage.getSummaryMessage());

                        //Get the image.
                        getWikiImage(mContext, finalLabel);
                    } else {
                        //TODO Handle no result found
                    }
                }, throwable -> Log.d(TAG, "accept: " + wikiRetrofitBuilder.getErrorMessage(throwable)));
    }

    void getWikiImage(@NonNull Context context, @NonNull String label) {
        WikiRetrofitBuilder wikiRetrofitBuilder = new WikiRetrofitBuilder(context);
        Observable<ResponseBody> observable = wikiRetrofitBuilder.getApiService()
                .getImage("json", "query", "pageimages", "original", label);
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(this::addSubscription)
                .subscribe(responseBody -> {
                            String responseStr = Utils.getStringFromStream(responseBody.byteStream());
                            JSONObject pagesObj = new JSONObject(responseStr)
                                    .getJSONObject("query")
                                    .getJSONObject("pages");
                            String imageUrl = pagesObj
                                    .getJSONObject(pagesObj.names().getString(0))
                                    .getJSONObject("thumbnail")
                                    .getString("original");

                            Log.d(TAG, "getWikiImage: " + imageUrl);
                            Glide.with(WikiFragment.this)
                                    .load(imageUrl)
                                    .thumbnail(0.1f)
                                    .into(mWikiImage);

                        },
                        throwable -> {
                            Log.d(TAG, "accept: " + wikiRetrofitBuilder.getErrorMessage(throwable));
                        });
    }
}
