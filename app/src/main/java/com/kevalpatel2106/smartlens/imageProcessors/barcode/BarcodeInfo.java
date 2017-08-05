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

package com.kevalpatel2106.smartlens.imageProcessors.barcode;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by Keval Patel on 02/08/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class BarcodeInfo {
    //TODO add wifi info object
    private Phone mPhone;
    private Rect mBoundingBox;
    private Contact mContact;
    private GeoPoint mGeoPoint;
    private CalendarEvent mCalendarEvent;
    private Sms mSms;
    private UrlBookmark mUrlBookmark;
    private String mRawValue;

    Rect getBoundingBox() {
        return mBoundingBox;
    }

    public void setBoundingBox(Rect boundingBox) {
        mBoundingBox = boundingBox;
    }

    Contact getContact() {
        return mContact;
    }

    public void setContact(Contact contact) {
        mContact = contact;
    }

    UrlBookmark getUrlBookmark() {
        return mUrlBookmark;
    }

    public void setUrlBookmark(UrlBookmark urlBookmark) {
        mUrlBookmark = urlBookmark;
    }

    Sms getSms() {
        return mSms;
    }

    public void setSms(Sms sms) {
        mSms = sms;
    }

    GeoPoint getGeoPoint() {
        return mGeoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        mGeoPoint = geoPoint;
    }

    CalendarEvent getCalendarEvent() {
        return mCalendarEvent;
    }

    public void setCalendarEvent(CalendarEvent calendarEvent) {
        mCalendarEvent = calendarEvent;
    }

    Phone getPhone() {
        return mPhone;
    }

    public void setPhone(Phone phone) {
        mPhone = phone;
    }

    String getRawValue() {
        return mRawValue;
    }

    public void setRawValue(String rawValue) {
        this.mRawValue = rawValue;
    }


    /**
     * Class to hold the person contact information.
     */
    public static class Contact {
        private String mFirstName;
        private String mLastName;
        private String mOrganisationName;
        private String mTitle;
        private String[] mUrls;
        private ArrayList<Email> mEmails;
        private ArrayList<Phone> mPhoneNumbers;
        private ArrayList<Address> mAddresses;

        ArrayList<Email> getEmails() {
            return mEmails;
        }

        public void addEmails(Email email) {
            if (mEmails == null) mEmails = new ArrayList<>();
            mEmails.add(email);
        }

        ArrayList<Phone> getPhones() {
            return mPhoneNumbers;
        }

        public void addPhone(Phone phone) {
            if (mPhoneNumbers == null) mPhoneNumbers = new ArrayList<>();
            mPhoneNumbers.add(phone);
        }

        String getFirstName() {
            return mFirstName;
        }

        public void setFirstName(String firstName) {
            mFirstName = firstName;
        }

        String getLastName() {
            return mLastName;
        }

        public void setLastName(String lastName) {
            mLastName = lastName;
        }

        String getOrganisationName() {
            return mOrganisationName;
        }

        public void setOrganisationName(String organisationName) {
            mOrganisationName = organisationName;
        }

        String getTitle() {
            return mTitle;
        }

        public void setTitle(String title) {
            mTitle = title;
        }

        String[] getUrls() {
            return mUrls;
        }

        public void setUrls(String[] urls) {
            mUrls = urls;
        }

        ArrayList<Address> getAddresses() {
            return mAddresses;
        }

        public void addAddress(Address address) {
            if (mAddresses == null) mAddresses = new ArrayList<>();
            mAddresses.add(address);
        }
    }

    public static class CalendarEvent {
        private String summary;
        private String description;
        private String location;
        private String organizer;
        private String status;
        private long startTimeMills;
        private long endTimeMills;

        String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        String getOrganizer() {
            return organizer;
        }

        public void setOrganizer(String organizer) {
            this.organizer = organizer;
        }

        String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        long getStartTimeMills() {
            return startTimeMills;
        }

        public void setStartTimeMills(long startTimeMills) {
            this.startTimeMills = startTimeMills;
        }

        long getEndTimeMills() {
            return endTimeMills;
        }

        public void setEndTimeMills(long endTimeMills) {
            this.endTimeMills = endTimeMills;
        }
    }

    public static class UrlBookmark {
        private final String mTitle;
        private final String mUrl;

        public UrlBookmark(String title, String url) {
            this.mTitle = title;
            this.mUrl = url;
        }

        String getTitle() {
            return mTitle;
        }

        String getUrl() {
            return mUrl;
        }
    }

    public static class Sms {
        private String mMessage;
        private String mPhoneNumber;

        public Sms(String message, String phoneNumber) {
            this.mMessage = message;
            this.mPhoneNumber = phoneNumber;

            if (message == null) throw new IllegalArgumentException("Message cannot be null.");
        }

        public String getMessage() {
            return mMessage;
        }

        public String getPhoneNumber() {
            return mPhoneNumber;
        }
    }

    public static class GeoPoint {
        private double lat;
        private double lng;

        public GeoPoint(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }

    public static final class Email {
        @NonNull
        private final String mEmail;

        @Nullable
        private String mType;

        public Email(@NonNull String email, @Nullable String type) {

            //noinspection ConstantConditions
            if (email == null)
                throw new IllegalArgumentException("Email cannot be null.");

            mEmail = email;
            mType = type;
        }

        public Email(@NonNull String email) {

            //noinspection ConstantConditions
            if (email == null)
                throw new IllegalArgumentException("Email cannot be null.");

            mEmail = email;
        }

        @NonNull
        public String getEmail() {
            return mEmail;
        }

        @Nullable
        public String getType() {
            return mType;
        }
    }

    public static final class Address {
        @NonNull
        private final String mAddress;

        @Nullable
        private String mType;

        public Address(@NonNull String address, @Nullable String type) {

            //noinspection ConstantConditions
            if (address == null)
                throw new IllegalArgumentException("Address cannot be null.");

            mAddress = address;
            mType = type;
        }

        public Address(@NonNull String address) {

            //noinspection ConstantConditions
            if (address == null)
                throw new IllegalArgumentException("Address cannot be null.");

            mAddress = address;
        }

        @NonNull
        public String getAddress() {
            return mAddress;
        }

        @Nullable
        public String getType() {
            return mType;
        }
    }

    public static final class Phone {
        @NonNull
        private final String mPhone;

        @Nullable
        private String mType;

        public Phone(@NonNull String phoneNumber, @Nullable String type) {

            //noinspection ConstantConditions
            if (phoneNumber == null)
                throw new IllegalArgumentException("Phone cannot be null.");

            mPhone = phoneNumber;
            mType = type;
        }

        public Phone(@NonNull String phone) {

            //noinspection ConstantConditions
            if (phone == null)
                throw new IllegalArgumentException("Phone cannot be null.");

            mPhone = phone;
        }

        @NonNull
        public String getPhone() {
            return mPhone;
        }

        @Nullable
        public String getType() {
            return mType;
        }
    }
}
