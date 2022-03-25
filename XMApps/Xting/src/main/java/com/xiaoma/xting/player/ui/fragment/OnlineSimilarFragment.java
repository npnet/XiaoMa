package com.xiaoma.xting.player.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.common.playerSource.info.impl.PlayerInfoImpl;
import com.xiaoma.xting.common.playerSource.loadmore.IFetchListener;
import com.xiaoma.xting.player.model.FMChannel;
import com.xiaoma.xting.player.vm.OnlineFunctionVM;
import com.xiaoma.xting.sdk.bean.XMAlbum;
import com.xiaoma.xting.sdk.bean.XMRelativeAlbums;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/11/6
 */
@PageDescComponent(EventConstants.PageDescribe.FRAGMENT_ONLINE_SIMILAR)
public class OnlineSimilarFragment extends AbsPlayerRelatedFragment {

    public static final String ARG_ID_SEARCH = "searchId";
    private OnlineFunctionVM mOnlineFunctionVM;
    private List<XMAlbum> mAlbumList;
    private long mSearchId;

    public static OnlineSimilarFragment newInstance(long searchId) {
        OnlineSimilarFragment fragment = new OnlineSimilarFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_ID_SEARCH, searchId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void dealObserver() {
        mSearchId = getArguments().getLong(ARG_ID_SEARCH);
        mOnlineFunctionVM = ViewModelProviders.of(this).get(OnlineFunctionVM.class);
        mOnlineFunctionVM.getRelativeAlbumsLiveData().observe(this, new Observer<XmResource<XMRelativeAlbums>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMRelativeAlbums> xmRelativeAlbumsXmResource) {
                xmRelativeAlbumsXmResource.handle(new OnCallback<XMRelativeAlbums>() {
                    @Override
                    public void onSuccess(XMRelativeAlbums data) {
                        List<FMChannel> fmChannels = FMChannel.similarAlbum2FMChannels(mAlbumList = data.getRelativeAlbumList());
                        mAdapter.setNewData(fmChannels);
                        if (ListUtils.isEmpty(fmChannels)) {
                            showEmptyView();
                        }
                    }

                    @Override
                    public void onCompleted() {
                        super.onCompleted();
                        mContentRV.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    @Override
    protected void fetchRelatedInfo() {
        mOnlineFunctionVM.fetchSimilar(mSearchId);
    }

    @Override
    protected void dispatchItemClick(int position) {
        XMAlbum xmAlbum = mAlbumList.get(position);
        if (xmAlbum.getCreatedAt() == 0) {
            XMToast.showToast(getContext(), R.string.error_by_data_source);
        } else {
            PlayerSourceFacade.newSingleton().getPlayerFetch().fetch(BeanConverter.toPlayerInfo(xmAlbum), new IFetchListener() {
                @Override
                public void onLoading() {
                    showProgressDialog(R.string.loading_data);
                }

                @Override
                public void onSuccess(Object t) {
                    dismissProgress();
                    PlayerInfoImpl.newSingleton().refreshSubscribe();
                    getActivity().onBackPressed();
                }

                @Override
                public void onFail() {
                    dismissProgress();
                    XMToast.showToast(getActivity(), R.string.error_by_data_source);
                }

                @Override
                public void onError(int code, String msg) {
                    dismissProgress();
                    XMToast.showToast(getActivity(), R.string.error_by_net);
                }
            });
        }
    }
}
