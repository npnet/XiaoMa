package com.xiaoma.xting.player.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;

import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerAction;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.SubscribeInfo;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.player.model.FMChannel;
import com.xiaoma.xting.player.vm.OnlineFunctionVM;

import java.util.ArrayList;
import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/11/13
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_PLAYER_STORE)
public class OnlineStoreFragment extends AbsPlayerRelatedFragment {

    private OnlineFunctionVM mViewModel;
    private List<SubscribeInfo> mSubscribeInfoList;

    public static OnlineStoreFragment newInstance() {
        return new OnlineStoreFragment();
    }

    @Override
    protected void cancelData() {

    }

    @Override
    protected void dealObserver() {
        mViewModel = ViewModelProviders.of(this).get(OnlineFunctionVM.class);
        mViewModel.getSubscribeListLiveData().observe(this, new Observer<List<SubscribeInfo>>() {
            @Override
            public void onChanged(@Nullable List<SubscribeInfo> infoList) {
                dismissLoading();
                mSubscribeInfoList = infoList;
                if (infoList == null || infoList.isEmpty()) {
                    showEmptyView();
                } else {
                    showContentView();
                    List<FMChannel> list = new ArrayList<>();
                    FMChannel bean;
                    for (SubscribeInfo info : mSubscribeInfoList) {
                        bean = new FMChannel();
                        bean.setName(info.getAlbumName());
                        bean.setCoverUrl(info.getImgUrl());
                        list.add(bean);
                    }
                    mAdapter.setNewData(list);
                }
            }
        });
    }

    @Override
    protected void fetchRelatedInfo() {
        showLoading();
        mViewModel.fetchSubscribes();
    }

    @Override
    protected void dispatchItemClick(int position) {
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        PlayerInfo toPlayerInfo = BeanConverter.toPlayerInfo(mSubscribeInfoList.get(position));
        if (toPlayerInfo.equals(playerInfo)) {
            PlayerInfoImpl.newSingleton().onProgramSubscribeChanged(true);
            getActivity().onBackPressed();
        } else {
            PlayerSourceFacade.newSingleton().setSourceType(toPlayerInfo.getType());
            toPlayerInfo.setAction(PlayerAction.PLAY_LIST);
            PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(toPlayerInfo, new IFetchListener() {
                @Override
                public void onLoading() {
                    showLoading();
                }

                @Override
                public void onSuccess(Object t) {
                    getActivity().onBackPressed();
                    PlayerInfoImpl.newSingleton().onProgramSubscribeChanged(true);
                    dismissLoading();
                }

                @Override
                public void onFail() {
                    dismissLoading();
                    XMToast.showToast(mContext, R.string.error_by_data_source);
                }

                @Override
                public void onError(int code, String msg) {
                    dismissLoading();
                    XMToast.showToast(mContext, R.string.error_by_net);
                }
            });
        }
    }

}