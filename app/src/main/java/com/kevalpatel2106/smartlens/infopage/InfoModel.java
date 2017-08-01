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

import android.support.annotation.NonNull;

/**
 * Created by Keval on 25-Jul-17.
 */

@SuppressWarnings("WeakerAccess")
public class InfoModel {

    private final String label;

    private String info;

    private String imageUrl;

    public InfoModel(@NonNull String label) {
        //noinspection ConstantConditions
        if (label == null) throw new IllegalArgumentException("Label cannot be null.");
        this.label = label;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(@NonNull String info) {
        //noinspection ConstantConditions
        if (info == null) throw new IllegalArgumentException("Info cannot be null.");

        this.info = info;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        //noinspection ConstantConditions
        if (imageUrl == null) throw new IllegalArgumentException("Image url cannot be null.");

        this.imageUrl = imageUrl;
    }

    public String getLabel() {
        return label;
    }
}
