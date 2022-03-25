package com.xiaoma.club.msg.chat.vm;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.v4.util.ArraySet;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.msg.chat.datasource.EMMessageDS;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.utils.StringUtil;

import java.util.Set;

/**
 * Created by LKF on 2018-12-27 0027.
 */
public class ChatMsgVM extends BaseViewModel {
    private static final String TAG = "ChatMsgVM";
    private static final int PAGE_SIZE = 50;

    private EMConversation mConversation;

    private final LiveData<PagedList<EMMessage>> mEMMessagePageList;
    private final Set<PagedList.BoundaryCallback<EMMessage>> mEMMessagePageListCallbacks = new ArraySet<>();

    public ChatMsgVM(@NonNull Application application) {
        super(application);
        final DataSource.Factory<String, EMMessage> factory = new DataSource.Factory<String, EMMessage>() {
            @Override
            public DataSource<String, EMMessage> create() {
                return new EMMessageDS(getApplication(), mConversation);
            }
        };
        final PagedList.Config pagedListCfg = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build();
        mEMMessagePageList = new LivePagedListBuilder<>(factory, pagedListCfg)
                .setInitialLoadKey(null)
                .setBoundaryCallback(new PagedList.BoundaryCallback<EMMessage>() {
                    @Override
                    public void onZeroItemsLoaded() {
                        LogUtil.logI(TAG, StringUtil.format("onZeroItemsLoaded()"));
                        Set<PagedList.BoundaryCallback<EMMessage>> callbacks = new ArraySet<>(mEMMessagePageListCallbacks);
                        for (PagedList.BoundaryCallback<EMMessage> callback : callbacks) {
                            callback.onZeroItemsLoaded();
                        }
                    }

                    @Override
                    public void onItemAtFrontLoaded(@NonNull EMMessage itemAtFront) {
                        LogUtil.logI(TAG, StringUtil.format("onItemAtFrontLoaded( itemAtFront: %s )", itemAtFront));
                        Set<PagedList.BoundaryCallback<EMMessage>> callbacks = new ArraySet<>(mEMMessagePageListCallbacks);
                        for (PagedList.BoundaryCallback<EMMessage> callback : callbacks) {
                            callback.onItemAtFrontLoaded(itemAtFront);
                        }
                    }

                    @Override
                    public void onItemAtEndLoaded(@NonNull EMMessage itemAtEnd) {
                        LogUtil.logI(TAG, StringUtil.format("onItemAtEndLoaded( itemAtEnd: %s )", itemAtEnd));
                        Set<PagedList.BoundaryCallback<EMMessage>> callbacks = new ArraySet<>(mEMMessagePageListCallbacks);
                        for (PagedList.BoundaryCallback<EMMessage> callback : callbacks) {
                            callback.onItemAtEndLoaded(itemAtEnd);
                        }
                    }
                }).build();
    }

    public void addEMMessagePageListCallback(PagedList.BoundaryCallback<EMMessage> callback) {
        if (callback != null)
            mEMMessagePageListCallbacks.add(callback);
    }

    public void removeEMMessagePageListCallback(PagedList.BoundaryCallback<EMMessage> callback) {
        if (callback != null)
            mEMMessagePageListCallbacks.remove(callback);
    }

    public void setConversation(EMConversation conversation) {
        mConversation = conversation;
        try {
            if (mEMMessagePageList.getValue() != null)
                mEMMessagePageList.getValue().getDataSource().invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public LiveData<PagedList<EMMessage>> getEMMessagePageList() {
        return mEMMessagePageList;
    }
}
