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

package com.kevalpatel2106.smartlens.plugins.visionBarcodeScanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.kevalpatel2106.smartlens.imageProcessors.barcode.BarcodeInfo;
import com.kevalpatel2106.smartlens.imageProcessors.barcode.BaseBarcodeScanner;

import java.util.Calendar;

/**
 * Created by Keval Patel on 02/08/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

public class BarcodeScanner extends BaseBarcodeScanner {

    private BarcodeDetector mBarcodeDetector;

    public BarcodeScanner(Context context) {
        //Build the detector
        mBarcodeDetector = new BarcodeDetector.Builder(context.getApplicationContext())
                .build();
    }

    @SuppressWarnings("WeakerAccess")
    static BarcodeInfo.CalendarEvent parseCalenderEvent(@NonNull Barcode barcode) {
        if (barcode.calendarEvent != null) return null;

        BarcodeInfo.CalendarEvent calendarEvent = new BarcodeInfo.CalendarEvent();

        calendarEvent.setStatus(barcode.calendarEvent.status);
        calendarEvent.setDescription(barcode.calendarEvent.description);
        calendarEvent.setSummary(barcode.calendarEvent.summary);
        calendarEvent.setOrganizer(barcode.calendarEvent.organizer);
        calendarEvent.setLocation(barcode.calendarEvent.location);

        //Set start time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, barcode.calendarEvent.start.day);
        calendar.set(Calendar.MONTH, barcode.calendarEvent.start.month);
        calendar.set(Calendar.YEAR, barcode.calendarEvent.start.year);
        calendar.set(Calendar.HOUR_OF_DAY, barcode.calendarEvent.start.hours);
        calendar.set(Calendar.MINUTE, barcode.calendarEvent.start.minutes);
        calendar.set(Calendar.SECOND, barcode.calendarEvent.start.seconds);
        calendar.set(Calendar.MILLISECOND, 0);
        calendarEvent.setStartTimeMills(calendar.getTimeInMillis());

        //Set end time
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, barcode.calendarEvent.end.day);
        calendar.set(Calendar.MONTH, barcode.calendarEvent.end.month);
        calendar.set(Calendar.YEAR, barcode.calendarEvent.end.year);
        calendar.set(Calendar.HOUR_OF_DAY, barcode.calendarEvent.end.hours);
        calendar.set(Calendar.MINUTE, barcode.calendarEvent.end.minutes);
        calendar.set(Calendar.SECOND, barcode.calendarEvent.end.seconds);
        calendar.set(Calendar.MILLISECOND, 0);
        calendarEvent.setEndTimeMills(calendar.getTimeInMillis());

        return calendarEvent;
    }

    @SuppressWarnings("WeakerAccess")
    static BarcodeInfo.Contact parseContactInfo(@NonNull Barcode barcode) {
        if (barcode.contactInfo != null) return null;

        BarcodeInfo.Contact contactInfo = new BarcodeInfo.Contact();

        //Parse the name info
        contactInfo.setFirstName(barcode.contactInfo.name.first);
        contactInfo.setLastName(barcode.contactInfo.name.last);
        contactInfo.setUrls(barcode.contactInfo.urls);

        //Parse company info
        contactInfo.setOrganisationName(barcode.contactInfo.organization);
        contactInfo.setTitle(barcode.contactInfo.title);

        //Parse the email info
        for (Barcode.Email barcodeEmail : barcode.contactInfo.emails) {
            contactInfo.addEmails(new BarcodeInfo.Email(barcodeEmail.address,
                    BarcodeScannerUtils.getEmailTypeString(barcodeEmail.type)));
        }

        //Parse the phone info
        for (Barcode.Phone barcodePhone : barcode.contactInfo.phones) {
            contactInfo.addPhone(new BarcodeInfo.Phone(barcodePhone.number,
                    BarcodeScannerUtils.getPhoneTypeString(barcodePhone.type)));
        }

        //Parse the address info
        for (Barcode.Address barcodeAddress : barcode.contactInfo.addresses) {
            contactInfo.addAddress(new BarcodeInfo.Address(TextUtils.join("\n", barcodeAddress.addressLines),
                    BarcodeScannerUtils.getAddressTypeString(barcodeAddress.type)));
        }

        return contactInfo;
    }

    @Override
    public BarcodeInfo scan(Bitmap bitmap) {
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<Barcode> barcodes = mBarcodeDetector.detect(frame);
        if (barcodes.size() > 0) {
            Barcode barcode = barcodes.valueAt(0);

            //Convert to the barcode info object
            BarcodeInfo barcodeInfo = new BarcodeInfo();
            barcodeInfo.setBoundingBox(barcode.getBoundingBox());
            barcodeInfo.setRawValue(barcode.rawValue);

            //Parse the contact info
            barcodeInfo.setContact(parseContactInfo(barcode));

            //Parse the url bookmarks
            if (barcode.url != null)
                barcodeInfo.setUrlBookmark(new BarcodeInfo.UrlBookmark(
                        barcode.url.title, barcode.url.url));

            //Parse the Sms detail
            if (barcode.sms != null)
                barcodeInfo.setSms(new BarcodeInfo.Sms(barcode.sms.message, barcode.sms.phoneNumber));

            //Parse the location detail
            if (barcode.geoPoint != null)
                barcodeInfo.setGeoPoint(new BarcodeInfo.GeoPoint(barcode.geoPoint.lat,
                        barcode.geoPoint.lng));

            //Parse calender event
            barcodeInfo.setCalendarEvent(parseCalenderEvent(barcode));

            //Parse only number
            if (barcode.phone != null)
                barcodeInfo.setPhone(new BarcodeInfo.Phone(barcode.phone.number,
                        BarcodeScannerUtils.getPhoneTypeString(barcode.phone.type)));

            return barcodeInfo;
        }
        return null;
    }

    @Override
    public boolean isSafeToStart() {
        return mBarcodeDetector != null && mBarcodeDetector.isOperational();
    }

    @Override
    public void close() {
        mBarcodeDetector.release();
        mBarcodeDetector = null;
    }

}
