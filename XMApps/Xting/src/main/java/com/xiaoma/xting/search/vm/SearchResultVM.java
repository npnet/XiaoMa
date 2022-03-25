package com.xiaoma.xting.search.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaoma.model.XmResource;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.bean.XMDataCallback;
import com.xiaoma.xting.sdk.bean.XMRadioList;
import com.xiaoma.xting.sdk.bean.XMSearchAlbumList;
import com.xiaoma.xting.sdk.bean.XMSearchTrackList;
import com.xiaoma.xting.sdk.bean.XMTrackList;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/09
 *     desc   :
 * </pre>
 */
public class SearchResultVM extends AndroidViewModel {

    private MutableLiveData<XmResource<XMSearchAlbumList>> mAlbumList;
    private MutableLiveData<XmResource<XMTrackList>> mTrackList;
    private MutableLiveData<XmResource<XMSearchTrackList>> mSearchTracksList;
    private MutableLiveData<XmResource<XMRadioList>> mRadioList;

    public SearchResultVM(@NonNull Application application) {
        super(application);
    }

    public void fetchAlbumList(String keyword) {
        mAlbumList.setValue(XmResource.loading());
        OnlineFMFactory.getInstance().getSDK().getSearchedAlbums(keyword, 0, 4, 1, 30, new XMDataCallback<XMSearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable XMSearchAlbumList data) {
                if (data != null && !ListUtils.isEmpty(data.getAlbums())) {
                    getAlbumList().setValue(XmResource.response(data));
                } else {
                    getAlbumList().setValue(XmResource.failure(XtingConstants.EMPTY_DATA));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getAlbumList().setValue(XmResource.error(code, msg));
            }
        });
    }

    public MutableLiveData<XmResource<XMSearchAlbumList>> getAlbumList() {
        if (mAlbumList == null) {
            mAlbumList = new MutableLiveData<>();
        }
        return mAlbumList;
    }


    public MutableLiveData<XmResource<XMTrackList>> getTrackList() {
        if (mTrackList == null) {
            mTrackList = new MutableLiveData<>();
        }
        return mTrackList;
    }

    public void fetchSearchTracksList(String keyword) {
        mSearchTracksList.setValue(XmResource.loading());
        OnlineFMFactory.getInstance().getSDK().getSearchedTracks(keyword, 0, 4, 1, 30, new XMDataCallback<XMSearchTrackList>() {
            @Override
            public void onSuccess(@Nullable XMSearchTrackList data) {
                if (data != null
                        && !ListUtils.isEmpty(data.getTracks())) {
                    getSearchTracksList().setValue(XmResource.response(data));
                } else {
                    getSearchTracksList().setValue(XmResource.failure(XtingConstants.EMPTY_DATA));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getSearchTracksList().setValue(XmResource.error(code, msg));
            }
        });
    }

    public MutableLiveData<XmResource<XMSearchTrackList>> getSearchTracksList() {
        if (mSearchTracksList == null) {
            mSearchTracksList = new MutableLiveData<>();
        }
        return mSearchTracksList;
    }

    public void fetchRadioList(String keyword) {
        mRadioList.setValue(XmResource.loading());
        OnlineFMFactory.getInstance().getSDK().getSearchedRadios(keyword, 4, 1, 30, new XMDataCallback<XMRadioList>() {
            @Override
            public void onSuccess(@Nullable XMRadioList data) {
                if (data != null && !ListUtils.isEmpty(data.getRadios())) {
                    getRadioList().setValue(XmResource.response(data));
                } else {
                    getRadioList().setValue(XmResource.failure(XtingConstants.EMPTY_DATA));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getRadioList().setValue(XmResource.error(code, msg));
            }
        });
    }

    public MutableLiveData<XmResource<XMRadioList>> getRadioList() {
        if (mRadioList == null) {
            mRadioList = new MutableLiveData<>();
        }
        return mRadioList;
    }

}
