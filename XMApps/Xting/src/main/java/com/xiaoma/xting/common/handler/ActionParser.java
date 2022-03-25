package com.xiaoma.xting.common.handler;

import android.os.Bundle;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.player.AudioCategoryBean;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.xting.common.handler.impl.LauncherCategoryHandler;
import com.xiaoma.xting.common.handler.impl.OnlineNormalPlayerHandler;
import com.xiaoma.xting.common.handler.impl.PageListHandler;
import com.xiaoma.xting.common.handler.impl.PlayHistoryHandler;
import com.xiaoma.xting.common.handler.impl.XtingCurPlayListHandler;
import com.xiaoma.xting.common.handler.impl.XtingStoreAlbumPlayerHandler;
import com.xiaoma.xting.common.handler.impl.XtingStoreHandler;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/14
 */
public class ActionParser {

    private ActionParser() {
        throw new UnsupportedOperationException("not allowed");
    }

    public static AbsActionHandler parseLauncherSearchAction(Bundle bundle, ClientCallback callback) {
        AbsActionHandler handler = null;
        int searchAction = bundle.getInt(AudioConstants.BundleKey.SEARCH_ACTION);
        switch (searchAction) {
            case AudioConstants.SearchAction.SEARCH_RESULT://桌面点击categroy item的时候
                handler = new LauncherCategoryHandler(callback);
                break;
            case AudioConstants.SearchAction.FAVORITE: //桌面点击电台收藏的时候
                handler = new XtingStoreHandler(callback);
                break;
            case AudioConstants.SearchAction.CURRENT: //获取当前播放列表
                handler = new XtingCurPlayListHandler(callback);
                break;
            case AudioConstants.SearchAction.PAGE_LIST: //用于进行加载更多操作
                handler = new PageListHandler(callback);
                break;
        }

        return handler;
    }

    public static AbsActionHandler parseLauncherPlayAction(Bundle bundle, ClientCallback callback) {
        AbsActionHandler handler = null;

        bundle.setClassLoader(AudioCategoryBean.class.getClassLoader());
        AudioCategoryBean bean = bundle.getParcelable(AudioConstants.BundleKey.EXTRA);
        if (bean == null) {
            return null;
        }
        switch (bean.getAction()) {
            case AudioConstants.PlayAction.DEFAULT:
            case AudioConstants.PlayAction.PLAY_LIST_AT_INDEX:
                handler = new OnlineNormalPlayerHandler(callback);
                break;
            case AudioConstants.PlayAction.PLAY_ALBUM_AT_INDEX:
                handler = new XtingStoreAlbumPlayerHandler(callback);
                break;
            case AudioConstants.PlayAction.HISTORY:
                handler = new PlayHistoryHandler(callback);
                break;
        }

        return handler;
    }

}
