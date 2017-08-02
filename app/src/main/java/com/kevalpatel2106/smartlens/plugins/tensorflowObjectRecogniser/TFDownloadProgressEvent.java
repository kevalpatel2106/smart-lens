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

package com.kevalpatel2106.smartlens.plugins.tensorflowObjectRecogniser;

/**
 * Created by Keval on 31-Jul-17.
 */

public final class TFDownloadProgressEvent {
    private boolean isDownloading;

    private int percent;

    private String errorMsg;

    TFDownloadProgressEvent(boolean isDownloading, int percent) {
        this.isDownloading = isDownloading;
        this.percent = percent;
    }

    TFDownloadProgressEvent(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getPercent() {
        if (percent < 0) return 0;
        else if (percent > 100) return 100;
        else return percent;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
