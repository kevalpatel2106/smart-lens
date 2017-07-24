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

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Keval Patel on 23/07/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 * @see <a href='https://stackoverflow.com/a/28401782/4690731>https://stackoverflow.com/a/28401782/4690731</a>
 */

public interface WikiService {

    @GET("api.php")
    Observable<ResponseBody> getInfo(@Query("format") String format,
                                     @Query("action") String action,
                                     @Query("prop") String prp,
                                     @Query("exintro") String extraInfo,
                                     @Query("explaintext") String explainText,
                                     @Query("titles") String title);

    @GET("api.php")
    Observable<ResponseBody> getImage(@Query("action") String action,
                                      @Query("prop") String prp,
                                      @Query("exintro") String extraInfo,
                                      @Query("explaintext") String explainText,
                                      @Query("titles") String title);

}
