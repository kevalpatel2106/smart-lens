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

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Keval on 31-Jul-17.
 */

public class ModelDownloadService extends IntentService {

    private static final String TENSORFLOW_GRAPH_URL = "";
    private static final String TENSORFLOW_LABEL_URL = "";

    public ModelDownloadService() {
        super(ModelDownloadService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
