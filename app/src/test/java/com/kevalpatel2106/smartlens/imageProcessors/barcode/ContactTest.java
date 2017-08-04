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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Keval on 04-Aug-17.
 */
public class ContactTest {
    @Test
    public void addEmails() throws Exception {
        BarcodeInfo.Contact contact = new BarcodeInfo.Contact();
        BarcodeInfo.Email email = new BarcodeInfo.Email("kevalpatel2106@gmail.com");
        contact.addEmails(email);
        email = new BarcodeInfo.Email("kevalonly111@gmail.com", "Work");
        contact.addEmails(email);

        assertEquals(contact.getEmails().size(), 2);
        assertEquals(contact.getEmails().get(0).getEmail(), "kevalpatel2106@gmail.com");
        assertNull(contact.getEmails().get(0).getType());
        assertEquals(contact.getEmails().get(1).getEmail(), "kevalonly111@gmail.com");
        assertEquals(contact.getEmails().get(1).getType(), "Work");
    }

    @Test
    public void addPhone() throws Exception {
        BarcodeInfo.Contact contact = new BarcodeInfo.Contact();
        BarcodeInfo.Phone phone = new BarcodeInfo.Phone("7894561230");
        contact.addPhone(phone);
        phone = new BarcodeInfo.Phone("1234567890", "Work");
        contact.addPhone(phone);

        assertEquals(contact.getPhones().size(), 2);
        assertEquals(contact.getPhones().get(0).getPhone(), "7894561230");
        assertNull(contact.getPhones().get(0).getType());
        assertEquals(contact.getPhones().get(1).getPhone(), "1234567890");
        assertEquals(contact.getPhones().get(1).getType(), "Work");
    }

    @Test
    public void setFirstAndLastName() throws Exception {
        BarcodeInfo.Contact contact = new BarcodeInfo.Contact();

        //Check valid input
        contact.setFirstName("Keval");
        contact.setLastName("Patel");
        assertEquals(contact.getFirstName(), "Keval");
        assertEquals(contact.getLastName(), "Patel");

        //Check null input
        contact.setFirstName(null);
        contact.setLastName(null);
        assertNull(contact.getFirstName());
        assertNull(contact.getLastName());
    }

    @Test
    public void setOrganisationName() throws Exception {
        BarcodeInfo.Contact contact = new BarcodeInfo.Contact();

        //Check valid input
        contact.setOrganisationName("GitHub");
        assertEquals(contact.getOrganisationName(), "GitHub");

        //Check null input
        contact.setOrganisationName(null);
        assertNull(contact.getOrganisationName());
    }

    @Test
    public void setTitle() throws Exception {
        BarcodeInfo.Contact contact = new BarcodeInfo.Contact();

        //Check valid input
        contact.setTitle("GitHub");
        assertEquals(contact.getTitle(), "GitHub");

        //Check null input
        contact.setTitle(null);
        assertNull(contact.getTitle());
    }

    @Test
    public void setUrls() throws Exception {
        BarcodeInfo.Contact contact = new BarcodeInfo.Contact();

        //Check valid input
        String[] urls = new String[2];
        urls[0] = "www.google.com";
        urls[1] = "www.facebook.com";
        contact.setUrls(urls);
        assertEquals(contact.getUrls().length, 2);
        assertEquals(contact.getUrls()[0], "www.google.com");
        assertEquals(contact.getUrls()[1], "www.facebook.com");

        //Check null input
        contact.setUrls(null);
        assertNull(contact.getUrls());
    }

    @Test
    public void addAddress() throws Exception {
        BarcodeInfo.Contact contact = new BarcodeInfo.Contact();

        BarcodeInfo.Address address = new BarcodeInfo.Address("MIG-27, Street A, DC India.");
        contact.addAddress(address);
        address = new BarcodeInfo.Address("MIG-27, Street B India.", "Home");
        contact.addAddress(address);

        assertEquals(contact.getAddresses().size(), 2);
        assertEquals(contact.getAddresses().get(0).getAddress(), "MIG-27, Street A, DC India.");
        assertNull(contact.getAddresses().get(0).getType());
        assertEquals(contact.getAddresses().get(1).getAddress(), "MIG-27, Street B India.");
        assertEquals(contact.getAddresses().get(1).getType(), "Home");
    }

}