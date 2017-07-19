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

package com.kevalpatel2106.smartlens.utils;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Keval on 24-Oct-16.
 * This calss manages user login session.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class UserSessionManager {
    //User preference keys.
    private static final String USER_ID = "USER_ID";        //User unique id
    private static final String USER_FIRST_NAME = "USER_FIRST_NAME";    //First name of the user
    private static final String USER_LAST_NAME = "USER_LAST_NAME";      //Last name of the user
    private static final String USER_EMAIL = "USER_EMAIL";              //Email address of the user
    private static final String TOKEN = "USER_TOKEN";                   //Authentication token

    private SharedPrefsProvider mSharedPrefsProvider;

    /**
     * Public constructor.
     *
     * @param context instance of the caller.
     */
    public UserSessionManager(@NonNull Context context) {
        mSharedPrefsProvider = new SharedPrefsProvider(context);
    }

    /**
     * Set user session detail.
     *
     * @param userId    unique id of the user
     * @param firstName first name of the user
     * @param lastName  last name of the user
     * @param email     email address of the user
     * @param token     authentication token
     */
    public void setNewSession(long userId,
                              @NonNull String firstName,
                              @NonNull String lastName,
                              @NonNull String email,
                              @NonNull String token) {
        //Save to the share prefs.
        mSharedPrefsProvider.savePreferences(USER_ID, userId);
        mSharedPrefsProvider.savePreferences(USER_FIRST_NAME, firstName);
        mSharedPrefsProvider.savePreferences(USER_LAST_NAME, lastName);
        mSharedPrefsProvider.savePreferences(USER_EMAIL, email);
        mSharedPrefsProvider.savePreferences(TOKEN, token);
    }

    /**
     * Get the user id of current user.
     *
     * @return user id.
     */
    public long getUserId() {
        return mSharedPrefsProvider.getLongFromPreference(USER_ID);
    }

    /**
     * First name of the user.
     *
     * @return first name
     */
    public String getFirstName() {
        return mSharedPrefsProvider.getStringFromPreferences(USER_FIRST_NAME);
    }

    /**
     * Get the last name of the user
     *
     * @return last name
     */
    public String getLastName() {
        return mSharedPrefsProvider.getStringFromPreferences(USER_LAST_NAME);
    }

    /**
     * Get current authentication token
     *
     * @return token
     */
    public String getToken() {
        return mSharedPrefsProvider.getStringFromPreferences(TOKEN);
    }

    /**
     * get the email of the user logged in.
     *
     * @return email address.
     */
    public String getEmail() {
        return mSharedPrefsProvider.getStringFromPreferences(USER_EMAIL);
    }

    /**
     * Clear user data.
     */
    public void clearUserSession() {
        mSharedPrefsProvider.removePreferences(USER_ID);
        mSharedPrefsProvider.removePreferences(USER_LAST_NAME);
        mSharedPrefsProvider.removePreferences(USER_FIRST_NAME);
        mSharedPrefsProvider.removePreferences(USER_EMAIL);
        mSharedPrefsProvider.removePreferences(TOKEN);
    }

    /**
     * Check if the user is currently logged in?
     *
     * @return true if the user is currently logged in.
     */
    public boolean isUserLoggedIn() {
        return mSharedPrefsProvider.getLongFromPreference(USER_ID) > 0
                && mSharedPrefsProvider.getStringFromPreferences(TOKEN) != null;
    }
}
