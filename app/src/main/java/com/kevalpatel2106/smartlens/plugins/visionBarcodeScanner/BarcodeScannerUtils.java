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

import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by Keval Patel on 02/08/17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */

class BarcodeScannerUtils {

    static String getEmailTypeString(int type) {
        switch (type) {
            case Barcode.Email.HOME:
                return "Home";
            case Barcode.Email.WORK:
                return "Work";
            default:
                return null;
        }
    }

    static String getAddressTypeString(int type) {
        switch (type) {
            case Barcode.Address.HOME:
                return "Home";
            case Barcode.Address.WORK:
                return "Work";
            default:
                return null;
        }
    }

    static String getPhoneTypeString(int type) {
        switch (type) {
            case Barcode.Phone.HOME:
                return "Home";
            case Barcode.Phone.WORK:
                return "Work";
            case Barcode.Phone.MOBILE:
                return "Mobile";
            case Barcode.Phone.FAX:
                return "Fax";
            default:
                return null;
        }
    }
}
