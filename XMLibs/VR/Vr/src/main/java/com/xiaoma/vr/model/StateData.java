package com.xiaoma.vr.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qiuboxiang on 2019/7/24 21:39
 * Desc:
 */
public class StateData {

    /**
     * HotWords : [""]
     * UserData : UserData : {"mapU::navi":{"activeStatus":"bg","data":{"dataInfo":{"avoid_poi":{},"end_poi":{},"reference_poi":{},"start_poi":{},"via_poi":{}}},"sceneStatus":"noNavi"},"musicX::default":{"activeStatus":"bg","data":{"dataInfo":{"artist":"","moreArtist":"","song":"","songId":""}},"sceneStatus":"paused"},"radio::default":{"activeStatus":"fg","data":{},"sceneStatus":"paused"},"weixin::default":{"activeStatus":"bg","sceneStatus":"default"},"internetRadio::default":{"activeStatus":"fg","data":{"dataInfo":{"song":"【世界杯球星】C 罗：独一无二的纪录粉碎机","presenter":"喜马王牌自制"}},"sceneStatus":"playing"}}
     */

    public StateData() {
        HotWords.add("");
    }

    private UserDataBean UserData = new UserDataBean();
    private List<String> HotWords = new ArrayList<>();

    public UserDataBean getUserData() {
        return UserData;
    }

    public void setUserData(UserDataBean UserData) {
        this.UserData = UserData;
    }

    public List<String> getHotWords() {
        return HotWords;
    }

    public void setHotWords(List<String> HotWords) {
        this.HotWords = HotWords;
    }

    public static class UserDataBean {
        /**
         * radio__default : {"data":{},"activeStatus":"bg","sceneStatus":"paused"}
         * musicX__default : {"data":{"dataInfo":{"songId":"","song":"","moreArtist":"","artist":""}},"activeStatus":"bg","sceneStatus":"playing"}
         * mapU__navi : {"activeStatus":"bg","data":{"dataInfo":{"avoid_poi":{},"end_poi":{},"reference_poi":{},"start_poi":{},"via_poi":{}}},"sceneStatus":"navigation"}
         * internetRadio__default : { "internetRadio::default": { "activeStatus": "fg", "data": { "dataInfo": { "song": "", "presenter": "" } }, "sceneStatus": "playing" } }
         */

        private RadioDefaultBean radio__default = new RadioDefaultBean();
        private MusicXDefaultBean musicX__default = new MusicXDefaultBean();
        private MapUNaviBean mapU__navi = new MapUNaviBean();
        private WeixinBean weixin__default = new WeixinBean();
        private InternetRadioBean internetRadio__default = new InternetRadioBean();

        public RadioDefaultBean getRadio__default() {
            return radio__default;
        }

        public void setRadio__default(RadioDefaultBean radio__default) {
            this.radio__default = radio__default;
        }

        public MusicXDefaultBean getMusicX__default() {
            return musicX__default;
        }

        public void setMusicX__default(MusicXDefaultBean musicX__default) {
            this.musicX__default = musicX__default;
        }

        public MapUNaviBean getMapU__navi() {
            return mapU__navi;
        }

        public void setMapU__navi(MapUNaviBean mapU__navi) {
            this.mapU__navi = mapU__navi;
        }

        public WeixinBean getWeixin__default() {
            return weixin__default;
        }

        public void setWeixin__default(WeixinBean weixin__default) {
            this.weixin__default = weixin__default;
        }

        public InternetRadioBean getInternetRadio__default() {
            return internetRadio__default;
        }

        public void setInternetRadio__default(InternetRadioBean internetRadio__default) {
            this.internetRadio__default = internetRadio__default;
        }

        public static class RadioDefaultBean {
            /**
             * data : {}
             * activeStatus : bg
             * sceneStatus : paused
             */

            private DataBean data = new DataBean();
            private String activeStatus = "bg";
            private String sceneStatus = "paused";

            public DataBean getData() {
                return data;
            }

            public void setData(DataBean data) {
                this.data = data;
            }

            public String getActiveStatus() {
                return activeStatus;
            }

            public void setActiveStatus(String activeStatus) {
                this.activeStatus = activeStatus;
            }

            public String getSceneStatus() {
                return sceneStatus;
            }

            public void setSceneStatus(String sceneStatus) {
                this.sceneStatus = sceneStatus;
            }

            public static class DataBean {
            }
        }

        public static class MusicXDefaultBean {
            /**
             * data : {"dataInfo":{"songId":"","song":"","moreArtist":"","artist":""}}
             * activeStatus : bg
             * sceneStatus : playing
             */

            private DataBeanX data = new DataBeanX();
            private String activeStatus = "bg";
            private String sceneStatus = "playing";

            public DataBeanX getData() {
                return data;
            }

            public void setData(DataBeanX data) {
                this.data = data;
            }

            public String getActiveStatus() {
                return activeStatus;
            }

            public void setActiveStatus(String activeStatus) {
                this.activeStatus = activeStatus;
            }

            public String getSceneStatus() {
                return sceneStatus;
            }

            public void setSceneStatus(String sceneStatus) {
                this.sceneStatus = sceneStatus;
            }

            public static class DataBeanX {
//                /**
//                 * dataInfo : {"songId":"","song":"","moreArtist":"","artist":""}
//                 */
//
//                private DataInfoBean dataInfo = new DataInfoBean();
//
//                public DataInfoBean getDataInfo() {
//                    return dataInfo;
//                }
//
//                public void setDataInfo(DataInfoBean dataInfo) {
//                    this.dataInfo = dataInfo;
//                }
//
//                public static class DataInfoBean {
//                    /**
//                     * songId :
//                     * song :
//                     * moreArtist :
//                     * artist :
//                     */
//
//                    private String songId = "";
//                    private String song = "";
//                    private String moreArtist = "";
//                    private String artist = "";
//
//                    public String getSongId() {
//                        return songId;
//                    }
//
//                    public void setSongId(String songId) {
//                        this.songId = songId;
//                    }
//
//                    public String getSong() {
//                        return song;
//                    }
//
//                    public void setSong(String song) {
//                        this.song = song;
//                    }
//
//                    public String getMoreArtist() {
//                        return moreArtist;
//                    }
//
//                    public void setMoreArtist(String moreArtist) {
//                        this.moreArtist = moreArtist;
//                    }
//
//                    public String getArtist() {
//                        return artist;
//                    }
//
//                    public void setArtist(String artist) {
//                        this.artist = artist;
//                    }
//                }
            }
        }

        public static class MapUNaviBean {
            /**
             * activeStatus : bg
             * data : {"dataInfo":{"avoid_poi":{},"end_poi":{},"reference_poi":{},"start_poi":{},"via_poi":{}}}
             * sceneStatus : navigation
             */

            private String activeStatus = "bg";
            private DataBeanXX data = new DataBeanXX();
            private String sceneStatus = "navigation";

            public String getActiveStatus() {
                return activeStatus;
            }

            public void setActiveStatus(String activeStatus) {
                this.activeStatus = activeStatus;
            }

            public DataBeanXX getData() {
                return data;
            }

            public void setData(DataBeanXX data) {
                this.data = data;
            }

            public String getSceneStatus() {
                return sceneStatus;
            }

            public void setSceneStatus(String sceneStatus) {
                this.sceneStatus = sceneStatus;
            }

            public static class DataBeanXX {
//                /**
//                 * dataInfo : {"avoid_poi":{},"end_poi":{},"reference_poi":{},"start_poi":{},"via_poi":{}}
//                 */
//
//                private DataInfoBeanX dataInfo = new DataInfoBeanX();
//
//                public DataInfoBeanX getDataInfo() {
//                    return dataInfo;
//                }
//
//                public void setDataInfo(DataInfoBeanX dataInfo) {
//                    this.dataInfo = dataInfo;
//                }
//
//                public static class DataInfoBeanX {
//                    /**
//                     * avoid_poi : {}
//                     * end_poi : {}
//                     * reference_poi : {}
//                     * start_poi : {}
//                     * via_poi : {}
//                     */
//
//                    private AvoidPoiBean avoid_poi = new AvoidPoiBean();
//                    private EndPoiBean end_poi = new EndPoiBean();
//                    private ReferencePoiBean reference_poi = new ReferencePoiBean();
//                    private StartPoiBean start_poi = new StartPoiBean();
//                    private ViaPoiBean via_poi = new ViaPoiBean();
//
//                    public AvoidPoiBean getAvoid_poi() {
//                        return avoid_poi;
//                    }
//
//                    public void setAvoid_poi(AvoidPoiBean avoid_poi) {
//                        this.avoid_poi = avoid_poi;
//                    }
//
//                    public EndPoiBean getEnd_poi() {
//                        return end_poi;
//                    }
//
//                    public void setEnd_poi(EndPoiBean end_poi) {
//                        this.end_poi = end_poi;
//                    }
//
//                    public ReferencePoiBean getReference_poi() {
//                        return reference_poi;
//                    }
//
//                    public void setReference_poi(ReferencePoiBean reference_poi) {
//                        this.reference_poi = reference_poi;
//                    }
//
//                    public StartPoiBean getStart_poi() {
//                        return start_poi;
//                    }
//
//                    public void setStart_poi(StartPoiBean start_poi) {
//                        this.start_poi = start_poi;
//                    }
//
//                    public ViaPoiBean getVia_poi() {
//                        return via_poi;
//                    }
//
//                    public void setVia_poi(ViaPoiBean via_poi) {
//                        this.via_poi = via_poi;
//                    }
//
//                    public static class AvoidPoiBean {
//                    }
//
//                    public static class EndPoiBean {
//                    }
//
//                    public static class ReferencePoiBean {
//                    }
//
//                    public static class StartPoiBean {
//                    }
//
//                    public static class ViaPoiBean {
//                    }
//                }
            }
        }

        public static class WeixinBean {

            /**
             * activeStatus : fg
             * sceneStatus : default
             */

            private String activeStatus = "bg";
            private String sceneStatus = "default";

            public String getActiveStatus() {
                return activeStatus;
            }

            public void setActiveStatus(String activeStatus) {
                this.activeStatus = activeStatus;
            }

            public String getSceneStatus() {
                return sceneStatus;
            }

            public void setSceneStatus(String sceneStatus) {
                this.sceneStatus = sceneStatus;
            }
        }

        public static class InternetRadioBean {

            /**
             * activeStatus : fg
             * data : {"dataInfo":{"song":"【世界杯球星】C 罗：独一无二的纪录粉碎机","presenter":"喜马王牌自制"}}
             * sceneStatus : playing
             */

            private String activeStatus;
            private DataBeanXXX data = new DataBeanXXX();
            private String sceneStatus;

            public String getActiveStatus() {
                return activeStatus;
            }

            public void setActiveStatus(String activeStatus) {
                this.activeStatus = activeStatus;
            }

            public DataBeanXXX getData() {
                return data;
            }

            public void setData(DataBeanXXX data) {
                this.data = data;
            }

            public String getSceneStatus() {
                return sceneStatus;
            }

            public void setSceneStatus(String sceneStatus) {
                this.sceneStatus = sceneStatus;
            }
        }

        public static class DataBeanXXX {
//            /**
//             * dataInfo : {"song":"【世界杯球星】C 罗：独一无二的纪录粉碎机","presenter":"喜马王牌自制"}
//             */
//
//            private DataInfoBeanXX dataInfo = new DataInfoBeanXX();
//
//            public DataInfoBeanXX getDataInfo() {
//                return dataInfo;
//            }
//
//            public void setDataInfo(DataInfoBeanXX dataInfo) {
//                this.dataInfo = dataInfo;
//            }
//
//            public static class DataInfoBeanXX {
//                /**
//                 * song : 【世界杯球星】C 罗：独一无二的纪录粉碎机
//                 * presenter : 喜马王牌自制
//                 */
//
//                private String song = "";
//                private String presenter = "";
//
//                public String getSong() {
//                    return song;
//                }
//
//                public void setSong(String song) {
//                    this.song = song;
//                }
//
//                public String getPresenter() {
//                    return presenter;
//                }
//
//                public void setPresenter(String presenter) {
//                    this.presenter = presenter;
//                }
//            }
        }
    }

}
