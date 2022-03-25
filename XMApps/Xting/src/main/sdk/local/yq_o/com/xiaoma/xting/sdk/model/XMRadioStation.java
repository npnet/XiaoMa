/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.xiaoma.xting.sdk.model;

import android.support.annotation.Nullable;

import java.util.Objects;

/**
 * A representation of a radio station.
 */
public class XMRadioStation {
    private int channel;
    private int subChannel;
    private int radioBand;
    private boolean isValid;
    private XMRadioMetadata metadata;

    public XMRadioStation(int channel, int subChannel, int radioBand, boolean isValid, @Nullable XMRadioMetadata metadata) {
        this.channel = channel;
        this.subChannel = subChannel;
        this.radioBand = radioBand;
        this.isValid = isValid;
        this.metadata = metadata;
    }

    public int getChannel() {
        return channel;
    }

    public int getSubChannel() {
        return subChannel;
    }

    public int getRadioBand() {
        return radioBand;
    }

    public boolean isValid() {
        return isValid;
    }

    public XMRadioMetadata getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return String.format("XMRadioStation [channel: %s, subchannel: %s, band: %s, rds: %s]",
                channel, subChannel, radioBand, metadata);
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof XMRadioStation)) {
            return false;
        }

        XMRadioStation station = (XMRadioStation) object;
        return station.getChannel() == channel && station.getSubChannel() == subChannel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channel, subChannel);
    }

}
