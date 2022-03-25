package com.xiaoma.smarthome.common.model;

import java.util.List;

public class XiaoMiTtsBean {

    private List<TtsBean> tts;

    public List<TtsBean> getTts() {
        return tts;
    }

    public void setTts(List<TtsBean> tts) {
        this.tts = tts;
    }

    public static class TtsBean {
        /**
         * url : cid:b810fefd-7499-478d-9cac-756c8bb9ca0c
         * text : 你好像没有智能设备，先去购买一个吧。
         * remoteUrl : http://file.ai.xiaomi.com/v2/anonymous/file/speech_tts_mercury_93024c8159407b4cbd30680218c3f2b6_5c83cab436dc97eef0d7d48929f688b5.mp3
         */

        private String url;
        private String text;
        private String remoteUrl;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getRemoteUrl() {
            return remoteUrl;
        }

        public void setRemoteUrl(String remoteUrl) {
            this.remoteUrl = remoteUrl;
        }
    }
}
