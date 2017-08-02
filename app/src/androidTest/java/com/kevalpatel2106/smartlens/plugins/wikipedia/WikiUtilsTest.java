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

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Keval on 29-Jul-17.
 */
public class WikiUtilsTest {
    private static final String DETAIL_LIST = "An electric locomotive is a locomotive powered by electricity from overhead lines, a third rail or on-board energy storage such as a battery or fuel cell. Electric locomotives with on-board fuelled prime movers, such as diesel engines or gas turbines, are classed as diesel-electric or gas turbine-electric locomotives because the electric generator/motor combination serves only as a power transmission system. Electricity is used to eliminate smoke and take advantage of the high efficiency of electric motors, but the cost of electrification means that usually only heavily used lines can be electrified.";

    private static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    @Test
    public void getSummaryFromJson() throws Exception {
        String summary = WikiUtils.getSummaryFromJson(getStringFromFile(InstrumentationRegistry
                        .getInstrumentation().getContext(),
                com.kevalpatel2106.smartlens.test.R.raw.wiki_info_success_response));
        assertEquals(summary, DETAIL_LIST);
        assertNull(WikiUtils.getSummaryFromJson(null));
    }

    private String getStringFromFile(Context context, int filename) {
        return getStringFromInputStream(context.getResources().openRawResource(filename));
    }


}