package com.xiaoma.club.discovery.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.club.R;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.discovery.model.HotGroupResultInfo;
import com.xiaoma.club.discovery.repo.DiscoverRepo;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.model.XmResource;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.thread.Work;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

import java.util.List;

/**
 * Author: loren
 * Date: 2018/12/26 0026
 */

public class DiscoveryVM extends BaseViewModel {

    private static final int HOT_GROUP_PAGE_SIZE = Integer.MAX_VALUE;
    private static final int HOT_GROUP_PAGE = 1;
    private boolean hasCheckedGuide;

    public DiscoveryVM(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<XmResource<List<GroupCardInfo>>> groupList;

    public MutableLiveData<XmResource<List<GroupCardInfo>>> getGroupList() {
        if (groupList == null) {
            groupList = new MutableLiveData<>();
        }
        return groupList;
    }

    private DiscoverRepo mDiscoverRepo = ClubRepo.getInstance().getDiscoverRepo();

    public void requestHotGroup(final boolean isCreate) {
        if (isCreate) {
            //除首次创建、重新加载loading，之后都静默loading
            getGroupList().setValue(XmResource.<List<GroupCardInfo>>loading());
        }
        SeriesAsyncWorker.create().next(new Work(Priority.HIGH) {
            @Override
            public void doWork(Object lastResult) {
                List<GroupCardInfo> groupCardInfos = mDiscoverRepo.getDiscoverGroups();
                if (isCreate) {
                    // 先从缓存内读取
                    if (groupCardInfos != null && !groupCardInfos.isEmpty()) {
                        getGroupList().postValue(XmResource.response(groupCardInfos));
                    }
                }
                doNext(groupCardInfos);
            }
        }).next(new Work<List<GroupCardInfo>>(Priority.HIGH) {
            @Override
            public void doWork(final List<GroupCardInfo> fromCache) {
                ClubRequestManager.getHotGroupList(HOT_GROUP_PAGE, HOT_GROUP_PAGE_SIZE, new CallbackWrapper<List<GroupCardInfo>>() {
                            @Override
                            public List<GroupCardInfo> parse(String data) {
                                KLog.d("parse: " + data);
                                HotGroupResultInfo result = GsonHelper.fromJson(data, new TypeToken<HotGroupResultInfo>() {
                                }.getType());
                                if (result == null || !result.isSuccess()) {
                                    return null;
                                }
                                return result.getData().getDataList();
                            }

                            @Override
                            public void onSuccess(final List<GroupCardInfo> groupCardInfos) {
                                if (groupCardInfos == null || groupCardInfos.isEmpty()) {
                                    getGroupList().postValue(XmResource.<List<GroupCardInfo>>failure(getApplication().getString(R.string.data_empty_club)));
                                    KLog.e("onFailed: groupCardInfos result is empty");
                                    delayShowGuide();//新手引导
                                    return;
                                }
                                ClubRepo.getInstance().getGroupRepo().insertAll(groupCardInfos);
                                getGroupList().postValue(XmResource.response(groupCardInfos));
                                // 缓存群组数据
                                ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDiscoverRepo.saveDiscoverGroups(groupCardInfos);
                                    }
                                });
                                delayShowGuide();// 新手引导
                            }

                            @Override
                            public void onError(int code, String msg) {
                                super.onError(code, msg);
                                KLog.d("onError: " + msg);
                                if (fromCache == null || fromCache.isEmpty()) {
                                    getGroupList().postValue(XmResource.<List<GroupCardInfo>>error(msg));
                                } else {
                                    getGroupList().postValue(XmResource.response(fromCache));
                                }
                                delayShowGuide();// 新手引导
                            }
                        }
                );
            }
        }).start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        groupList = null;
    }

    private void delayShowGuide() {
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                boolean shouldShowGuide = GuideDataHelper.shouldShowGuide(getApplication(), GuideConstants.CLUB_SHOWED, GuideConstants.CLUB_GUIDE_FIRST, !hasCheckedGuide);
                hasCheckedGuide = true;
                XmResource<List<GroupCardInfo>> res = getGroupList().getValue();
                List<GroupCardInfo> groupCardInfos;
                if (res != null
                        && (groupCardInfos = res.data) != null
                        && groupCardInfos.size() >= 1
                        && shouldShowGuide) {
                    EventBus.getDefault().post(false, "club_data_update");
                } else {
                    // 未能成功展示新手引导时,告诉新手引导模块移除对话框,避免点击事件被拦截,导致页面卡死
                    EventBus.getDefault().post(true, "club_data_update");
                }
            }
        }, 300);
    }
}
