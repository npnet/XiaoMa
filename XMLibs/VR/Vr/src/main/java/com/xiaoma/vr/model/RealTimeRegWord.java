package com.xiaoma.vr.model;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/28
 * Desc:实时转写bean
 */
public class RealTimeRegWord {

    /**
     * audioTag : 0
     * text : {"bg":0,"ed":0,"ls":false,"pgs":"rpl","rg":[1,1],"sn":2,"ws":[{"bg":0,"cw":[{"sc":0,"w":"深圳"}]},{"bg":0,"cw":[{"sc":0,"w":"今天"}]}]}
     */

    private int audioTag;
    private TextBean text;

    public int getAudioTag() {
        return audioTag;
    }

    public void setAudioTag(int audioTag) {
        this.audioTag = audioTag;
    }

    public TextBean getText() {
        return text;
    }

    public void setText(TextBean text) {
        this.text = text;
    }

    public static class TextBean {
        /**
         * bg : 0
         * ed : 0
         * ls : false
         * pgs : rpl
         * rg : [1,1]
         * sn : 2
         * ws : [{"bg":0,"cw":[{"sc":0,"w":"深圳"}]},{"bg":0,"cw":[{"sc":0,"w":"今天"}]}]
         */

        private int bg;
        private int ed;
        private boolean ls;
        private String pgs;
        private int sn;
        private List<Integer> rg;
        private List<WsBean> ws;

        public int getBg() {
            return bg;
        }

        public void setBg(int bg) {
            this.bg = bg;
        }

        public int getEd() {
            return ed;
        }

        public void setEd(int ed) {
            this.ed = ed;
        }

        public boolean isLs() {
            return ls;
        }

        public void setLs(boolean ls) {
            this.ls = ls;
        }

        public String getPgs() {
            return pgs;
        }

        public void setPgs(String pgs) {
            this.pgs = pgs;
        }

        public int getSn() {
            return sn;
        }

        public void setSn(int sn) {
            this.sn = sn;
        }

        public List<Integer> getRg() {
            return rg;
        }

        public void setRg(List<Integer> rg) {
            this.rg = rg;
        }

        public List<WsBean> getWs() {
            return ws;
        }

        public void setWs(List<WsBean> ws) {
            this.ws = ws;
        }

        public static class WsBean {
            /**
             * bg : 0
             * cw : [{"sc":0,"w":"深圳"}]
             */

            private int bg;
            private List<CwBean> cw;

            public int getBg() {
                return bg;
            }

            public void setBg(int bg) {
                this.bg = bg;
            }

            public List<CwBean> getCw() {
                return cw;
            }

            public void setCw(List<CwBean> cw) {
                this.cw = cw;
            }

            public static class CwBean {
                /**
                 * sc : 0
                 * w : 深圳
                 */

                private int sc;
                private String w;

                public int getSc() {
                    return sc;
                }

                public void setSc(int sc) {
                    this.sc = sc;
                }

                public String getW() {
                    return w;
                }

                public void setW(String w) {
                    this.w = w;
                }
            }
        }
    }
}
