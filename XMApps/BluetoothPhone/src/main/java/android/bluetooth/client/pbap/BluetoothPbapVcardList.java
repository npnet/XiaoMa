/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.bluetooth.client.pbap;


import android.vcard.VCardEntry;
import android.vcard.VCardEntryConstructor;
import android.vcard.VCardEntryCounter;
import android.vcard.VCardEntryHandler;
import android.vcard.VCardParser;
import android.vcard.VCardParser_V21;
import android.vcard.VCardParser_V30;

import com.android.vcard.exception.VCardException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

class BluetoothPbapVcardList {

    private final ArrayList<VCardEntry> mCards = new ArrayList<VCardEntry>();

    class CardEntryHandler implements VCardEntryHandler {
        @Override
        public void onStart() {
        }

        @Override
        public void onEntryCreated(VCardEntry entry) {
            mCards.add(entry);
        }

        @Override
        public void onEnd() {
        }
    }

    public BluetoothPbapVcardList(InputStream in, byte format) throws IOException {
        parse(in, format);
    }

    private void parse(InputStream in, byte format) throws IOException {
        VCardParser parser;

        if (format == BluetoothPbapClient.VCARD_TYPE_30) {
            parser = new VCardParser_V30();
        } else {
            parser = new VCardParser_V21();
        }

        VCardEntryConstructor constructor = new VCardEntryConstructor();
        VCardEntryCounter counter = new VCardEntryCounter();
        CardEntryHandler handler = new CardEntryHandler();

        constructor.addEntryHandler(handler);

        parser.addInterpreter(constructor);
        parser.addInterpreter(counter);

        try {
            parser.parse(in);
        } catch (VCardException e) {
            e.printStackTrace();
        }
    }

    public int getCount() {
        return mCards.size();
    }

    public ArrayList<VCardEntry> getList() {
        return mCards;
    }

    public VCardEntry getFirst() {
        return mCards.get(0);
    }
}
