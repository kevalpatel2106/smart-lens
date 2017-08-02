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


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.base.BaseActivity;
import com.kevalpatel2106.smartlens.base.BaseImageView;
import com.kevalpatel2106.smartlens.base.BaseTextView;
import com.kevalpatel2106.smartlens.plugins.wikipedia.WikiHelper;
import com.kevalpatel2106.smartlens.utils.Utils;

import java.util.ArrayList;

import butterknife.BindView;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class InfoActivity extends BaseActivity implements InfoCallbacks {
    static final String ARA_RECOGNITION_LIST = "arg_recognition_list";
    @SuppressWarnings("unused")
    private static final String TAG = "InfoActivity";

    @BindView(R.id.root_flipper)
    ViewFlipper mViewFlipper;
    @BindView(R.id.wiki_page_iv)
    BaseImageView mWikiImage;
    @BindView(R.id.wiki_page_tv)
    BaseTextView mSummaryTv;
    @BindView(R.id.suggestions_heading)
    BaseTextView mSuggestionHeading;
    @BindView(R.id.error_text_view)
    BaseTextView mErrorTv;
    @BindView(R.id.recommended_items_list)
    RecyclerView mRecyclerView;
    WikiHelper mWikiRetrofitHelper;
    private RecommendedItemAdapter mAdapter;
    private ArrayList<InfoModel> mRecommendedInfoModels;    //This array holds the list of all the recommended labels for given one.

    public InfoActivity() {
        // Required empty public constructor
    }

    public static void launch(@NonNull Activity activity,
                              @NonNull ArrayList<String> labels,
                              @Nullable View transitionView) {
        Intent launchIntent = new Intent(activity, InfoActivity.class);
        launchIntent.putStringArrayListExtra(ARA_RECOGNITION_LIST, labels);

        if (transitionView != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(activity,
                            transitionView,
                            activity.getString(R.string.info_activity_transition_name));
            activity.startActivity(launchIntent, options.toBundle());
            activity.overridePendingTransition(0, 0);
        } else {
            activity.startActivity(launchIntent);
        }
    }

    public static void launch(@NonNull Activity activity,
                              @NonNull String label,
                              @Nullable View transitionView) {
        ArrayList<String> labelsArray = new ArrayList<>();
        labelsArray.add(label);
        launch(activity, labelsArray, transitionView);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mWikiRetrofitHelper = new WikiHelper(this, this);

        //Validate the info
        //noinspection unchecked
        ArrayList<String> recognitions = getIntent().getStringArrayListExtra(ARA_RECOGNITION_LIST);
        if (recognitions == null || recognitions.isEmpty())
            throw new IllegalStateException(getString(R.string.info_activity_error_label));
        getIntent().removeExtra(ARA_RECOGNITION_LIST);

        //Set the toolbar
        setToolbar(R.id.toolbar, Utils.getCamelCaseText(recognitions.get(0)), true);

        //Set the recycler view
        mRecommendedInfoModels = new ArrayList<>();
        mAdapter = new RecommendedItemAdapter(this, mRecommendedInfoModels);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mAdapter);

        //Hide suggestions at initialization
        hideSuggestion();

        //Start loading the info.
        mViewFlipper.setDisplayedChild(0);
        mWikiRetrofitHelper.getLabelDetail(recognitions);
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    public void onSuccess(@NonNull InfoModel infoModel) {
        //Hide the previous suggestions
        hideSuggestion();

        //Change the title of the toolbar
        getSupportActionBar().setTitle(Utils.getCamelCaseText(infoModel.getLabel()));

        //Set the info for the label
        mSummaryTv.setText(infoModel.getInfo());
        Glide.with(getApplicationContext())
                .load(infoModel.getImageUrl())
                .into(mWikiImage);

        mViewFlipper.setDisplayedChild(1);
    }

    @Override
    public void onRecommendedLoaded(InfoModel recommended) {
        Timber.d(recommended.getLabel());
        //Make suggestions list visible.
        mSuggestionHeading.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

        //Add and update list
        mRecommendedInfoModels.add(recommended);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Hide the suggestions from the screen.
     */
    private void hideSuggestion() {
        mSuggestionHeading.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);

        mRecommendedInfoModels.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(@NonNull String message) {
        mErrorTv.setText(message);
        mViewFlipper.setDisplayedChild(2);
    }
}
