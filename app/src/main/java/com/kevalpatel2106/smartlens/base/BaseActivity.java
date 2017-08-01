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

import android.annotation.SuppressLint;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.kevalpatel2106.smartlens.utils.Utils;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Keval on 17-Dec-16.
 * This is the root class for the activity that extends {@link AppCompatActivity}. Use this class instead
 * of {@link AppCompatActivity} through out the application.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        //Bind butter knife
        ButterKnife.bind(this);
    }

    /**
     * Set the toolbar of the activity.
     *
     * @param toolbarId    resource id of the toolbar
     * @param title        title of the activity
     * @param showUpButton true if toolbar should display up indicator.
     */
    protected void setToolbar(int toolbarId, @StringRes int title, boolean showUpButton) {
        Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);
        setToolbar(title, showUpButton);
    }

    /**
     * Set the toolbar of the activity.
     *
     * @param toolbarId    resource id of the toolbar
     * @param title        title of the activity
     * @param showUpButton true if toolbar should display up indicator.
     */
    protected void setToolbar(int toolbarId, @NonNull String title, boolean showUpButton) {
        Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);
        setToolbar(title, showUpButton);
    }

    /**
     * Set the toolbar.
     *
     * @param title        Activity title string resource
     * @param showUpButton true if toolbar should display up indicator.
     */
    protected void setToolbar(@StringRes int title, boolean showUpButton) {
        setToolbar(getString(title), showUpButton);
    }

    /**
     * Set the toolbar.
     *
     * @param title        Activity title string.
     * @param showUpButton true if toolbar should display up indicator.
     */
    @SuppressLint("RestrictedApi")
    @SuppressWarnings("ConstantConditions")
    protected void setToolbar(@NonNull String title, boolean showUpButton) {
        //set the title
        getSupportActionBar().setTitle(title);

        //Set the up indicator
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(showUpButton);
        getSupportActionBar().setHomeButtonEnabled(showUpButton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(showUpButton);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //Hide the keyboard if any view is currently in focus.
            if (getCurrentFocus() != null) Utils.hideKeyboard(getCurrentFocus());
            supportFinishAfterTransition();
            return false;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Add the subscription to the {@link CompositeDisposable}.
     *
     * @param disposable {@link Disposable}
     */
    protected void addSubscription(@Nullable Disposable disposable) {
        if (disposable == null) return;
        mCompositeDisposable.add(disposable);
    }

    @NonNull
    public CompositeDisposable getDisposables() {
        return mCompositeDisposable;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }
}
