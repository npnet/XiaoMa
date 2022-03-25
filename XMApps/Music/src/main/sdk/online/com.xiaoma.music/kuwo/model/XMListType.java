package com.xiaoma.music.kuwo.model;

import cn.kuwo.base.bean.ListType;



/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public enum XMListType {
    LIST_ERROR_TYPE {
        public String getTypeName() {
            return "error_type";
        }

        public String getTypeShowName() {
            return "错误类型";
        }

        public int getMusicLimit() {
            return 0;
        }
    },
    LIST_LOCAL_ALL {
        public String getTypeName() {
            return "local.all";
        }

        public String getTypeShowName() {
            return "本地歌曲";
        }

        public int getMusicLimit() {
            return 2147483647;
        }
    },
    LIST_DOWNLOAD_UNFINISHED {
        public String getTypeName() {
            return "download.unfinish";
        }

        public String getTypeShowName() {
            return "正在下载";
        }

        public int getMusicLimit() {
            return 2147483647;
        }
    },
    LIST_DOWNLOAD_FINISHED {
        public String getTypeName() {
            return "download.finish";
        }

        public String getTypeShowName() {
            return "已下载";
        }

        public int getMusicLimit() {
            return 2147483647;
        }
    },
    LIST_MY_FAVORITE {
        public String getTypeName() {
            return "我喜欢听";
        }

        public String getTypeShowName() {
            return "我喜欢听";
        }

        public int getMusicLimit() {
            return LIST_MUSIC_LIMIT;
        }
    },
    LIST_DEFAULT {
        public String getTypeName() {
            return "默认列表";
        }

        public String getTypeShowName() {
            return "默认列表";
        }

        public int getMusicLimit() {
            return LIST_MUSIC_LIMIT;
        }
    },
    LIST_PC_DEFAULT {
        public String getTypeName() {
            return "PC默认列表";
        }

        public String getTypeShowName() {
            return "PC默认列表";
        }

        public int getMusicLimit() {
            return LIST_MUSIC_LIMIT;
        }
    },
    LIST_RECENTLY_PLAY {
        public String getTypeName() {
            return "最近播放";
        }

        public String getTypeShowName() {
            return "最近播放";
        }

        public int getMusicLimit() {
            return LIST_MUSIC_LIMIT;
        }
    },
    LIST_USER_CREATE {
        public String getTypeName() {
            return "LIST_USER_CREATE";
        }

        public String getTypeShowName() {
            return "";
        }

        public int getMusicLimit() {
            return LIST_MUSIC_LIMIT;
        }
    },
    LIST_TEMPORARY {
        public String getTypeName() {
            return "list.temporary";
        }

        public String getTypeShowName() {
            return "播放列表";
        }

        public int getMusicLimit() {
            return LIST_MUSIC_LIMIT;
        }
    },
    LIST_RADIO {
        public String getTypeName() {
            return "电台";
        }

        public String getTypeShowName() {
            return "电台";
        }

        public int getMusicLimit() {
            return LIST_MUSIC_LIMIT;
        }
    },
    LIST_ORDER {
        public String getTypeName() {
            return "LIST_NAME_ORDER";
        }

        public String getTypeShowName() {
            return "LIST_NAME_ORDER";
        }

        public int getMusicLimit() {
            return LIST_MUSIC_LIMIT;
        }
    },
    LIST_DELETE_CACHE1 {
        public String getTypeName() {
            return "DeleteCache1";
        }

        public String getTypeShowName() {
            return "";
        }

        public int getMusicLimit() {
            return LIST_MUSIC_LIMIT;
        }
    },
    LIST_DELETE_CACHE2 {
        public String getTypeName() {
            return "DeleteCache2";
        }

        public String getTypeShowName() {
            return "";
        }

        public int getMusicLimit() {
            return LIST_MUSIC_LIMIT;
        }
    },
    LIST_TEMP {
        public String getTypeName() {
            return "";
        }

        public String getTypeShowName() {
            return "";
        }

        public int getMusicLimit() {
            return 2147483647;
        }
    },
    LIST_DOWNLOAD_MV {
        public String getTypeName() {
            return "downloadmv";
        }

        public String getTypeShowName() {
            return "我的MV";
        }

        public int getMusicLimit() {
            return 2147483647;
        }
    };
    static int LIST_MUSIC_LIMIT = 20000;

    public abstract String getTypeName();

    public abstract String getTypeShowName();

    public abstract int getMusicLimit();

    public static XMListType convertListType(ListType type) {
        switch (type) {
            case LIST_ERROR_TYPE:
                return XMListType.LIST_ERROR_TYPE;
            case LIST_LOCAL_ALL:
                return XMListType.LIST_LOCAL_ALL;
            case LIST_DOWNLOAD_UNFINISHED:
                return XMListType.LIST_DOWNLOAD_UNFINISHED;
            case LIST_DOWNLOAD_FINISHED:
                return XMListType.LIST_DOWNLOAD_FINISHED;
            case LIST_MY_FAVORITE:
                return XMListType.LIST_MY_FAVORITE;
            case LIST_DEFAULT:
                return XMListType.LIST_DEFAULT;
            case LIST_PC_DEFAULT:
                return XMListType.LIST_PC_DEFAULT;
            case LIST_RECENTLY_PLAY:
                return XMListType.LIST_RECENTLY_PLAY;
            case LIST_USER_CREATE:
                return XMListType.LIST_USER_CREATE;
            case LIST_TEMPORARY:
                return XMListType.LIST_TEMPORARY;
            case LIST_RADIO:
                return XMListType.LIST_RADIO;
            case LIST_ORDER:
                return XMListType.LIST_ORDER;
            case LIST_DELETE_CACHE1:
                return XMListType.LIST_DELETE_CACHE1;
            case LIST_DELETE_CACHE2:
                return XMListType.LIST_DELETE_CACHE2;
            case LIST_TEMP:
                return XMListType.LIST_TEMP;
            case LIST_DOWNLOAD_MV:
                return XMListType.LIST_DOWNLOAD_MV;
            default:
                return XMListType.LIST_TEMP;
        }
    }

    public static ListType recoveryXMListType(XMListType type) {
        switch (type) {
            case LIST_ERROR_TYPE:
                return ListType.LIST_ERROR_TYPE;
            case LIST_LOCAL_ALL:
                return ListType.LIST_LOCAL_ALL;
            case LIST_DOWNLOAD_UNFINISHED:
                return ListType.LIST_DOWNLOAD_UNFINISHED;
            case LIST_DOWNLOAD_FINISHED:
                return ListType.LIST_DOWNLOAD_FINISHED;
            case LIST_MY_FAVORITE:
                return ListType.LIST_MY_FAVORITE;
            case LIST_DEFAULT:
                return ListType.LIST_DEFAULT;
            case LIST_PC_DEFAULT:
                return ListType.LIST_PC_DEFAULT;
            case LIST_RECENTLY_PLAY:
                return ListType.LIST_RECENTLY_PLAY;
            case LIST_USER_CREATE:
                return ListType.LIST_USER_CREATE;
            case LIST_TEMPORARY:
                return ListType.LIST_TEMPORARY;
            case LIST_RADIO:
                return ListType.LIST_RADIO;
            case LIST_ORDER:
                return ListType.LIST_ORDER;
            case LIST_DELETE_CACHE1:
                return ListType.LIST_DELETE_CACHE1;
            case LIST_DELETE_CACHE2:
                return ListType.LIST_DELETE_CACHE2;
            case LIST_TEMP:
                return ListType.LIST_TEMP;
            case LIST_DOWNLOAD_MV:
                return ListType.LIST_DOWNLOAD_MV;
            default:
                return null;
        }
    }
}
