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

package com.kevalpatel2106.smartlens.plugins.wikipedia;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Keval on 28-Jul-17.
 */

class WikiUtils {

    /**
     * Convert the label received to the wikipedia compatible label. This will convert first latter
     * to the capital and replaces spaces with '_'.
     *
     * @param label label to convert
     * @return Converted label.
     */
    @SuppressWarnings("ConstantConditions")
    static String generateWikiLabel(@NonNull String label) {
        if (label == null) throw new IllegalArgumentException("Cannot be null.");

        //Convert first character to caps
        label = Character.toUpperCase(label.charAt(0)) + label.substring(1).toLowerCase();

        //Replace space with _.
        return label.replace("\\s", "_");
    }

    /**
     * Convert the label received to the wikipedia compatible label. This will convert first latter
     * to the capital and replaces spaces with '_'.
     *
     * @param label label to convert
     * @return Converted label.
     */
    @SuppressWarnings("ConstantConditions")
    static String[] generatePossibleWikiLabel(@NonNull String label) {
        if (label == null) throw new IllegalArgumentException("Cannot be null.");
        String[] labels = label.split("\\s");
        return labels.length == 1 ? new String[0] : labels;
    }

    /**
     * Check if the summary is valid.
     *
     * @param label   label of the summary
     * @param summary Summary to check
     * @return True if the summary is valid.
     */
    @SuppressWarnings("ConstantConditions")
    static boolean isValidSummary(@NonNull String label, @Nullable String summary) {
        if (label == null || summary == null) return false;

        //Redirection/Page moved or renamed.
        if (summary.contains("From a page move:")) return false;

        //Other page suggestions.
        int thresholdLength = (label + " may refer to: ").length();
        return summary.length() > thresholdLength;
    }

    @Nullable
    static String getSummaryFromJson(String responseStr) throws JSONException {
        if (responseStr == null) return null;
        JSONObject pagesObj = new JSONObject(responseStr)
                .getJSONObject("query")
                .getJSONObject("pages");
        return pagesObj
                .getJSONObject(pagesObj.names().getString(0))
                .getString("extract");

    }
}
