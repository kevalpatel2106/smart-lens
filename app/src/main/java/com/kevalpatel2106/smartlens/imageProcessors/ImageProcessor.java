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

package com.kevalpatel2106.smartlens.imageProcessors;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kevalpatel2106.smartlens.imageProcessors.barcode.BaseBarcodeScanner;

/**
 * Created by Keval on 03-Aug-17.
 */

public class ImageProcessor {
    private Context mContext;

    /**
     * Public constructor.
     *
     * @param context Instance of the caller.
     */
    public ImageProcessor(@NonNull Context context) {
        mContext = context;
    }

    public void addBarcodeScanner(BaseBarcodeScanner baseBarcodeScanner) {

    }
}
