package com.xiaoma.xting.online.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.ResUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.VisibilityFragment;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.adapter.GalleryAdapter;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceSubType;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.info.model.PlayerInfo;
import com.xiaoma.xting.common.playerSource.info.model.RecordInfo;
import com.xiaoma.xting.common.view.XmDividerDecoration;
import com.xiaoma.xting.koala.KoalaPlayer;
import com.xiaoma.xting.launcher.XtingAudioClient;
import com.xiaoma.xting.online.vm.PlayWithPlayerInfoVM;
import com.xiaoma.xting.online.vm.RecommendVM;

import java.util.List;

/**
 * @author KY
 * @date 2018/10/9
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_FM_NET_RECOMMEND)
public class RecommendFragment extends VisibilityFragment {

    private RecyclerView mGallery;
    private RecommendVM recommendVM;
    private GalleryAdapter<PlayerInfo> mAdapter;
    private XmScrollBar mScrollBar;
    private boolean Loaded;
    private int mPlayIndex = -1;
    private boolean mClickToRunF = false;

    public static RecommendFragment newInstance() {
        return new RecommendFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_gallery, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initView();
        initData();
    }

    private void bindView(@NonNull View view) {
        mGallery = view.findViewById(R.id.gallery);
        mScrollBar = view.findViewById(R.id.scroll_bar);
    }

    private void initView() {
        mGallery.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(mContext, DividerItemDecoration.HORIZONTAL);
        int horizontal = mContext.getResources().getDimensionPixelOffset(R.dimen.size_gallery_item_horizontal_margin);
        int extra = mContext.getResources().getDimensionPixelOffset(R.dimen.size_gallery_item_extra_margin);
        decor.setRect(horizontal, 0, horizontal, 0);
        decor.setExtraMargin(extra);
        mGallery.addItemDecoration(decor);
    }

    private void initData() {
        final PlayWithPlayerInfoVM playVM = ViewModelProviders.of(getActivity()).get(PlayWithPlayerInfoVM.class);
        playVM.getState().observe(this, new Observer<XmResource<Object>>() {
            @Override
            public void onChanged(@Nullable XmResource<Object> objectXmResource) {
                objectXmResource.handle(new OnCallback<Object>() {

                    @Override
                    public void onLoading() {
                    }

                    @Override
                    public void onSuccess(Object data) {
                        dismissProgress();
                    }

                    @Override
                    public void onFailure(String msg) {
                        dismissProgress();

                    }

                    @Override
                    public void onError(int code, String message) {
                        dismissProgress();
                    }
                });
            }
        });
        mAdapter = new GalleryAdapter<PlayerInfo>(this) {

            @Override
            public boolean isMarquee(BaseViewHolder holder) {
                if (mClickToRunF) {
                    return super.isMarquee(holder);
                } else {
                    PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
                    int adapterPosition = holder.getAdapterPosition();
                    PlayerInfo curPlayerInfo = mAdapter.getItem(adapterPosition);
                    if (playerInfo != null) {
                        int sourceType = playerInfo.getSourceType();
                        if (sourceType == PlayerSourceType.KOALA && KoalaPlayer.newSingleton().isPGCRadio()) {
                            return adapterPosition == mPlayIndex;
                        } else {
                            return sourceType == curPlayerInfo.getSourceType()
                                    && playerInfo.getAlbumId() == curPlayerInfo.getAlbumId();
                        }
                    }
                    return false;
                }
            }

            @Override
            public ItemEvent returnPositionEventMsg(int position) {
                return new ItemEvent(getData().get(position).getTitleText(mContext).toString(), EventConstants.PageDescribe.TAG_RECOMMEND);
            }
        };
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (NetworkUtils.isConnected(getContext())) {

                    if (position == 0) {
                        XtingAudioClient.newSingleton(mContext).setLauncherCategoryId(2);
                    } else {
                        XtingAudioClient.newSingleton(mContext).restoreLauncherCategoryId();
                    }
                    PlayerInfo playerInfo = mAdapter.getData().get(position);
                    PlayerInfo curPlayerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
                    if (playerInfo.equals(curPlayerInfo)) {
                        if (PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()) {
                            PlayerSourceFacade.newSingleton().getPlayerControl().play();
                        } else {
                            showProgressDialog(R.string.loading_data);
                            playVM.play(curPlayerInfo);
                        }
                    } else {

                        //考拉PGC电台数据实时刷新,所以不能使用播放历史
                        if (curPlayerInfo == null || (curPlayerInfo.getSourceType() == PlayerSourceType.KOALA && curPlayerInfo.getSourceSubType() == PlayerSourceSubType.KOALA_PGC_RADIO)) {
                            showProgressDialog(R.string.loading_data);
                            mPlayIndex = position;
                            playVM.play(playerInfo);
                        } else {
                            mPlayIndex = position;
                            showProgressDialog(R.string.loading_data);
                            List<RecordInfo> recordInfos = XtingUtils.getRecordDao().selectProgramByAlbum(playerInfo.getType(), playerInfo.getAlbumId());
                            if (ListUtils.isEmpty(recordInfos)) {
                                playVM.play(playerInfo);
                            } else {
                                playVM.play(BeanConverter.toPlayerInfo(recordInfos.get(0)));
                            }
                        }

                    }
                } else {
                    XMToast.showToast(mContext, ResUtils.getString(getContext(), R.string.net_not_connect));
                }
            }
        });
        mAdapter.bindToRecyclerView(mGallery);
        mScrollBar.setRecyclerView(mGallery);

        recommendVM = ViewModelProviders.of(this).get(RecommendVM.class);
        recommendVM.getRecommends().removeObservers(this);
        recommendVM.getRecommends().observe(this, new Observer<XmResource<List<PlayerInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<PlayerInfo>> listXmResource) {
                listXmResource.handle(new OnCallback<List<PlayerInfo>>() {
                    @Override
                    public void onLoading() {
                        if (RecommendFragment.this.getUserVisibleHint()) {
                            super.onLoading();
                        }
                    }

                    @Override
                    public void onSuccess(List<PlayerInfo> data) {
                        if (CollectionUtil.isListEmpty(data)) {
                            if (ListUtils.isEmpty(mAdapter.getData())) {
                                showEmptyView();
                            }
                        } else {
                            Loaded = true;
                            mAdapter.setClickToRun(false);
                            mAdapter.setNewData(data);
                            showContentView();
//                            marqueePlayingAlbum();
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        if (ListUtils.isEmpty(mAdapter.getData())) {
                            super.onFailure(msg);
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (ListUtils.isEmpty(mAdapter.getData())) {
                            super.onError(code, message);
                        }
                    }
                });
            }
        });
    }

    private void checkNetWork() {
        if (NetworkUtils.isConnected(getContext())) {
            recommendVM.fetchREC();
        } else {
            showNoNetView();
        }
    }

    @Override
    protected void noNetworkOnRetry() {
        if (checkVisible()) checkNetWork();
    }

    @Override
    protected void emptyOnRetry() {
        if (checkVisible()) checkNetWork();
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible) {
            recommendVM.fetchREC();
//            checkNetWork();
//            if (!Loaded || ListUtils.isEmpty(mAdapter.getData())) {
//                checkNetWork();
//            } else {
//                marqueePlayingAlbum();
//            }
        } else {
            mAdapter.stopMarquee();
        }
    }

    private void marqueePlayingAlbum() {
        PlayerInfo playerInfo = PlayerInfoImpl.newSingleton().getPlayerInfo();
        if (mPlayIndex < 0
                || playerInfo == null || playerInfo.getType() == PlayerSourceType.RADIO_YQ
                || playerInfo.getSourceSubType() == PlayerSourceSubType.RADIO
                || (playerInfo.getType() == PlayerSourceType.KOALA
                && !KoalaPlayer.newSingleton().isPGCRadio()
                && playerInfo.getSourceSubType() == PlayerSourceSubType.KOALA_ALBUM)) {
            mAdapter.stopMarquee();
            return;
        }
        List<PlayerInfo> recommendPlayerInfoList = mAdapter.getData();
        int size = recommendPlayerInfoList.size();
        if (mPlayIndex >= 0) {
            for (int i = 0; i < size; i++)
                if (playerInfo.getType() == PlayerSourceType.KOALA && KoalaPlayer.newSingleton().isPGCRadio()) {
                    if (i == mPlayIndex) {
                        mAdapter.updatePlayIndex(i);
                        return;
                    }
                } else {
                    if (recommendPlayerInfoList.get(i).getAlbumId() == playerInfo.getAlbumId()) {
                        mAdapter.updatePlayIndex(i);
                        return;
                    }
                }
        }

//                String piecesId = playerInfo.getPiecesId();
//                for (int i = 0; i < size; i++) {
//                    if (!piecesId.isEmpty() && playerInfo.getType() == PlayerSourceType.KOALA && KoalaPlayer.newSingleton().isPGCRadio()) {
//                        if (recommendPlayerInfoList.get(i).getPiecesId().equals(piecesId)) {
//                            mAdapter.updatePlayIndex(i);
//                            return;
//                        }
//                    } else {
//                        if (recommendPlayerInfoList.get(i).getAlbumId() == playerInfo.getAlbumId()) {
//                            mAdapter.updatePlayIndex(i);
//                            return;
//                        }
//                    }
//                }
        mAdapter.stopMarquee();
    }
}
