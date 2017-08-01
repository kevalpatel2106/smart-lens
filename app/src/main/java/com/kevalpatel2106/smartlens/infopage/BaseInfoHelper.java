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

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by Keval on 29-Jul-17.
 */

public abstract class BaseInfoHelper {

    protected final Context mContext;
    protected final InfoCallbacks mCallbacks;

    public BaseInfoHelper(Context context, InfoCallbacks callbacks) {
        mContext = context;
        mCallbacks = callbacks;

        if (mCallbacks == null)
            throw new IllegalArgumentException("Callback cannot be null.");

    }

    public abstract void getLabelDetail(@NonNull List<String> recognitions);

    public abstract void getRecommendedItems(@NonNull List<String> recognitions,
                                             @NonNull String mainLabel);
}
