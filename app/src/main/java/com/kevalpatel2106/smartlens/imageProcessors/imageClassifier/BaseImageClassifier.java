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

package com.kevalpatel2106.smartlens.imageProcessors.imageClassifier;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by Keval Patel on 19/04/17.
 * Generic interface for interacting with different recognition engines.
 *
 * @author 'https://github.com/androidthings/sample-tensorflow-imageclassifier/blob/master/app/src/main/java/com/example/androidthings/imageclassifier/classifier/Classifier.java'
 */
public abstract class BaseImageClassifier {
    private Context mContext;

    public BaseImageClassifier(Context context) {
        mContext = context;
    }

    public abstract List<Recognition> scan(Bitmap bitmap);

    public abstract void downloadModels();

    public abstract boolean isModelDownloaded();

    public abstract void init();

    public abstract boolean isSafeToStart();

    public abstract void close();

    protected Context getContext() {
        return mContext;
    }
}
