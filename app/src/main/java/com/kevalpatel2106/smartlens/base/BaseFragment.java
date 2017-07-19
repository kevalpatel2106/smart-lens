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
}
