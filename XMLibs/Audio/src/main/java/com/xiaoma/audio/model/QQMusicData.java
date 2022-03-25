package com.xiaoma.audio.model;

import android.text.TextUtils;
import java.util.LinkedHashMap;

public class QQMusicData {

    public String module;
    public QQMusicCommand command;

    public boolean isPlayModule() {
        if (TextUtils.isEmpty(module)) {
            return false;
        }
        return module.equals("play");
    }

    public class QQMusicCommand {
        public String method;
        public QQMusicCommandData data;

        public boolean isUpdateStatus() {
            if (TextUtils.isEmpty(method)) {
                return false;
            }
            return method.equals("update_state");
        }

        public boolean isBuffering() {
            if (data == null) {
                return false;
            }
            return data.state == 1;
        }

        public boolean isPlaying() {
            if (data == null) {
                return false;
            }
            return data.state == 2;
        }

        public boolean isPause() {
            if (data == null) {
                return false;
            }
            return data.state == 3;
        }

        public boolean isStop() {
            if (data == null) {
                return false;
            }
            return data.state == 4;
        }
    }

    public class QQMusicCommandData {
        public String key_title;
        public LinkedHashMap<String, String> key_artist;
        public String key_album;
        public int state = 0;
        public int isForeground = 0;
    }

}
