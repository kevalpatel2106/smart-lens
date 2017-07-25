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

import android.support.annotation.NonNull;

/**
 * Created by Keval on 25-Jul-17.
 */

class WikiPage {

    private final String label;

    private String summaryMessage;

    private String imageUrl;

    WikiPage(@NonNull String label) {
        //noinspection ConstantConditions
        if (label == null) throw new IllegalArgumentException("Label cannot be null.");
        this.label = label;
    }

    String getSummaryMessage() {
        return summaryMessage;
    }

    void setSummaryMessage(@NonNull String summaryMessage) {
        //noinspection ConstantConditions
        if (summaryMessage == null) throw new IllegalArgumentException("Summary cannot be null.");

        this.summaryMessage = summaryMessage;
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
