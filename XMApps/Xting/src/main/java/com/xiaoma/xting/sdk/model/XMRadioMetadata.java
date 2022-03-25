/*
 * Copyright (c) 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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

public class XMRadioMetadata {
    private String source;
    private String artist;
    private String title;

    /**
     * @param source 当前电台的节目源,一般是电台的信道或id.
     * @param artist 当前节目的艺术家.
     * @param title  当前节目的标题.
     */
    public XMRadioMetadata(String source, String artist, String title) {
        this.source = source;
        this.artist = artist;
        this.title = title;
    }

    @Nullable
    public String getSource() {
        return source;
    }

    @Nullable
    public String getArtist() {
        return artist;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "XMRadioMetadata: "
                + "[ " + source + " ]"
                + "[ " + artist + " ]"
                + "[ " + title + " ]";
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof XMRadioMetadata)) {
            return false;
        }
        XMRadioMetadata rds = (XMRadioMetadata) object;
        return Objects.equals(source, rds.getSource())
                && Objects.equals(artist, rds.getArtist())
                && Objects.equals(title, rds.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, artist, title);
    }

}
