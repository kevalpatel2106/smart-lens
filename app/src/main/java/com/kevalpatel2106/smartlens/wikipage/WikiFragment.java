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


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevalpatel2106.smartlens.R;
import com.kevalpatel2106.smartlens.base.BaseFragment;
import com.kevalpatel2106.smartlens.base.BaseImageView;
import com.kevalpatel2106.smartlens.base.BaseTextView;
import com.kevalpatel2106.smartlens.imageClassifier.ImageClassifiedEvent;
import com.kevalpatel2106.smartlens.utils.rxBus.RxBus;

import butterknife.BindView;
import okhttp3.ResponseBody;
import rx.Observable;

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
        RxBus.getDefault().register(ImageClassifiedEvent.class)
                .filter(event -> !((ImageClassifiedEvent) event.getObject()).getRecognitions().isEmpty())
                .doOnSubscribe(this::addSubscription)
                .doOnNext(event -> getWikiPageDetail(((ImageClassifiedEvent) event.getObject())
                        .getRecognitions()
                        .get(0)
                        .getTitle()));
    }

    public void getWikiPageDetail(@NonNull String label) {
        Log.d(TAG, "getWikiPageDetail: " + label);
        Observable<ResponseBody> observable = new WikiRetrofitBuilder(mContext).getApiService()
                .getInfo("json", "query", "extracts", "", "", label);
    }
}
