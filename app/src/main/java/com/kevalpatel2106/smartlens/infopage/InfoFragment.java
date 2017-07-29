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

package com.kevalpatel2106.smartlens.infopage;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.base.BaseFragment;
import com.kevalpatel2106.smartlens.base.BaseImageView;
import com.kevalpatel2106.smartlens.base.BaseTextView;
import com.kevalpatel2106.smartlens.imageClassifier.ImageClassifiedEvent;
import com.kevalpatel2106.smartlens.imageClassifier.Recognition;
import com.kevalpatel2106.smartlens.utils.rxBus.RxBus;
import com.kevalpatel2106.smartlens.wikipedia.WikiHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends BaseFragment implements InfoCallbacks {
    @SuppressWarnings("unused")
    private static final String TAG = "InfoFragment";

    @BindView(R.id.wiki_page_iv)
    BaseImageView mWikiImage;
    @BindView(R.id.wiki_page_title_tv)
    BaseTextView mLabelHeadingTv;
    @BindView(R.id.wiki_page_tv)
    BaseTextView mSummaryTv;
    @BindView(R.id.suggestions_heading)
    BaseTextView mSuggestionHeading;
    @BindView(R.id.recommended_items_list)
    RecyclerView mRecyclerView;
    WikiHelper mWikiRetrofitHelper;
    private RecommendedItemAdapter mAdapter;
    private ArrayList<InfoModel> mInfoModels;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment getNewInstance() {
        return new InfoFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mWikiRetrofitHelper = new WikiHelper(mContext, this);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wiki, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Register image classifier.
        RxBus.getDefault().register(new Class[]{ImageClassifiedEvent.class})
                .filter(event -> {
                    List<Recognition> recognitions =
                            ((ImageClassifiedEvent) event.getObject()).getRecognitions();
                    return recognitions != null && !recognitions.isEmpty();
                })
                .doOnSubscribe(this::addSubscription)
                .doOnNext(event -> mWikiRetrofitHelper.getLabelDetail(
                        ((ImageClassifiedEvent) event.getObject()).getRecognitions()))
                .subscribe();

        mInfoModels = new ArrayList<>();
        mAdapter = new RecommendedItemAdapter(mContext, mInfoModels);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onSuccess(@NonNull InfoModel infoModel) {
        hideSuggestion();

        //Set the info
        mLabelHeadingTv.setText(infoModel.getLabel());
        mSummaryTv.setText(infoModel.getInfo());
        Glide.with(this)
                .load(infoModel.getImageUrl())
                .into(mWikiImage);
    }

    @Override
    public void onRecommendedLoaded(InfoModel recommended) {
        Timber.d(recommended.getLabel());

        if (mInfoModels.isEmpty()) {
            mSuggestionHeading.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        mInfoModels.add(recommended);
        mAdapter.notifyDataSetChanged();
    }

    private void hideSuggestion() {
        mSuggestionHeading.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        mInfoModels.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(@NonNull String message) {
        Timber.i(message);
    }
}
