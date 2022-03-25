package com.xiaoma.music.kuwo.handler;

import cn.kuwo.base.bean.quku.BaseQukuItem;

/**
 * Created by ZYao.
 * Date ：2018/10/17 0017
 */
public class DispatchHandlerFactory {

    public static IHandler createHandler(String type) {
        IHandler handler;
        switch (type) {
            case BaseQukuItem.TYPE_BILLBOARD:
                handler = new BillboardHandler();
                break;
            case BaseQukuItem.TYPE_ARTIST:
                handler = new ArtistHandler();
                break;
            case BaseQukuItem.TYPE_MUSIC:
                handler = new MusicHandler();
                break;
            case BaseQukuItem.TYPE_ALBUM:
                handler = new AlbumHandler();
                break;
            case BaseQukuItem.TYPE_RADIO:
                handler = new DefaultHandler();
                break;
            case BaseQukuItem.TYPE_SONGLIST:
                handler = new SongListHandler();
                break;
            case BaseQukuItem.TYPE_RECOMMEND:
                handler = new DefaultHandler();
                break;
            case BaseQukuItem.TYPE_CATEGORY_SONGLIST:
                handler = new CategoryHandler();
                break;
            case BaseQukuItem.TYPE_ALBUMLIST:
                handler = new DefaultHandler();
                break;
            default:
                handler = new DefaultHandler();
                break;
        }
        return handler;
    }
}
