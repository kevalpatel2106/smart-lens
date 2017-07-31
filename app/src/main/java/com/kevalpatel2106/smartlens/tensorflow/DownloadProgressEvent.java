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

package com.kevalpatel2106.smartlens.tensorflow;

/**
 * Created by Keval on 31-Jul-17.
 */

public final class DownloadProgressEvent {
    private boolean isDownloading;

    private int percent;

    DownloadProgressEvent(boolean isDownloading, int percent) {
        this.isDownloading = isDownloading;
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }

    public boolean isDownloading() {
        return isDownloading;
    }
}
