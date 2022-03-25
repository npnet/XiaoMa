package com.xiaoma.xting.player.ui.fragment;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.launcher.LocalFMOperateManager;
import com.xiaoma.xting.local.model.BaseChannelBean;

import java.util.List;

/**
 * <des>
 *
 * @author wutao
 * @date 2018/12/5
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_LOCAL_PLAYER_STORE)
public class LocalCollectFragment extends AbsLocalFragment {

    public static LocalCollectFragment newInstance() {
        return new LocalCollectFragment();
    }


    @Override
    protected void getData() {
        mVM.getLocalStoreFM();
        mVM.getDbRadioLiveDate().observe(this, new Observer<List<BaseChannelBean>>() {
            @Override
            public void onChanged(@Nullable List<BaseChannelBean> channelBeans) {
                if (ListUtils.isEmpty(channelBeans)) {
                    showEmptyView();
                } else {
                    showContentView();
                    mAdapter.setNewData(channelBeans);
                }
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PlayerInfo lastPlayerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
                BaseChannelBean bean = (BaseChannelBean) adapter.getItem(position);
                PlayerInfo newPlayerInfo = BeanConverter.toPlayerInfo(bean);
                if (lastPlayerInfo.equals(newPlayerInfo)) {
                    getActivity().onBackPressed();
                } else {
                    LocalFMOperateManager.newSingleton().playChannel(bean);
                    PlayerInfoImpl.newSingleton().onPlayerInfoChanged(newPlayerInfo);
                    PlayerInfoImpl.newSingleton().onProgramSubscribeChanged(true);
                    getActivity().onBackPressed();
                }
            }
        });
    }
}
