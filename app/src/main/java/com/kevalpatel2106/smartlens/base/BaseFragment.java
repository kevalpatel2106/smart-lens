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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Keval on 17-Dec-16.
 * Base fragment is base class for {@link Fragment}. This handles connection and disconnect of Rx bus.
 * Use this class instead of {@link AppCompatActivity} through out the application.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class BaseFragment extends Fragment {
    protected Context mContext;       //Instance of the caller

    /**
     * {@link CompositeDisposable} that holds all the subscriptions.
     */
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    /**
     * Add the subscription to the {@link CompositeDisposable}.
     *
     * @param disposable {@link Disposable}
     */
    protected void addSubscription(@Nullable Disposable disposable) {
        if (disposable == null) return;
        mCompositeDisposable.add(disposable);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Bind butter knife
        ButterKnife.bind(this, view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }

    protected void finish() {
        getActivity().finish();
    }
}
