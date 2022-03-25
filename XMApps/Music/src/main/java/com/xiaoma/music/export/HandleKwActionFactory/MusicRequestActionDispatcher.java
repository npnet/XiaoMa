package com.xiaoma.music.export.HandleKwActionFactory;

import android.content.Context;

import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.player.AudioConstants;

/**
 * Created by ZYao.
 * Date ：2019/3/12 0012
 */
public class MusicRequestActionDispatcher {

    public static MusicRequestActionDispatcher getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final MusicRequestActionDispatcher instance = new MusicRequestActionDispatcher();
    }

    public BaseRequestInterceptHandler dispatcher(Context context, int action, ClientCallback callback) {
        BaseRequestInterceptHandler handler = null;
        switch (action) {
            case AudioConstants.SearchAction.DEFAULT:
                handler = new DefaultRequestHandler(context, callback);
                break;
            case AudioConstants.SearchAction.CURRENT:
                handler = new CurrentRequestHandler(context, callback);
                break;
            case AudioConstants.SearchAction.FAVORITE:
                handler = new FavoriteRequestHandler(context, callback);
                break;
            case AudioConstants.SearchAction.PAGE_LIST:
                handler = new PageListRequestHandler(context, callback);
                break;
            case AudioConstants.SearchAction.FETCH_IMAGE:
                handler = new FetchImageRequestHandler(context, callback);
                break;
            case AudioConstants.SearchAction.SEARCH_RESULT:
                handler = new ResultRequestHandler(context, callback);
                break;
            case AudioConstants.SearchAction.HAVE_HISTORY:
                handler = new HaveHistoryRequestHandler(context, callback);
                break;
            case AudioConstants.SearchAction.SEARCH_MUSIC_BY_NAME:
                handler = new SearchRequestHandler(context, callback);
                break;
            default:
                handler = new DefaultRequestHandler(context, callback);
                break;

        }
        return handler;
    }

    public BaseRequestInterceptHandler dispatcherAssistantAction(Context context, int action, ClientCallback callback) {
        BaseRequestInterceptHandler handler = null;
        switch (action) {
            case AudioConstants.Action.GET_AUDIO_SOURCE_TYPE:
                handler = new AudioSourceRequestHandler(context, callback);
                break;
            case AudioConstants.Action.SEARCH_MUSIC_BY_ALBUM:
                handler = new SearchAlbumHandler(context, callback);
                break;
            case AudioConstants.Action.SEARCH_MUSIC_BY_MUSIC_TYPE:
                handler = new SearchByCategoryHandler(context, callback);
                break;
            case AudioConstants.Action.SEARCH_MUSIC_BY_RANKING_LIST_TYPE:
                handler = new SearchByBillboardHandler(context, callback);
                break;
            case AudioConstants.Action.SEARCH_MUSIC_BY_NAME_AND_SINGER:
                handler = new SearchByMusicNameHandler(context, callback);
                break;
            case AudioConstants.Action.SEARCH_MUSIC_BY_SINGER_AND_CHORUS:
                handler = new SearchByDoubleArtistHandler(context, callback);
                break;
            default:
                handler = new DefaultRequestHandler(context, callback);
                break;
        }
        return handler;
    }

}
