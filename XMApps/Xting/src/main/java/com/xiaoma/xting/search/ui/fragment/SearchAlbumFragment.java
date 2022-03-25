package com.xiaoma.xting.search.ui.fragment;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.player.ui.FMPlayerActivity;
import com.xiaoma.xting.sdk.bean.XMAlbum;
import com.xiaoma.xting.sdk.bean.XMSearchAlbumList;
import com.xiaoma.xting.sdk.bean.XMTrackList;
import com.xiaoma.xting.search.ui.SearchResultActivity;
import com.xiaoma.xting.utils.Transformations;

import static com.xiaoma.xting.common.EventConstants.NormalClick.albumItem;
import static com.xiaoma.xting.common.EventConstants.NormalClick.programItem;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/10
 *     desc   :
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.albumSearchResult)
public class SearchAlbumFragment extends AbsSearchResultFragment {

    public static SearchAlbumFragment newInstance(String from) {
        SearchAlbumFragment fragment = new SearchAlbumFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SearchResultActivity.INTENT_KEY_WORD, from);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new AlbumAdapter();
    }

    @Override
    protected void execute() {
        super.execute();
        subscribeTrack();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!NetworkUtils.isConnected(getContext())) {
                    XMToast.showToast(getContext(), getString(R.string.net_not_connect));
                    return;
                }
                XMAlbum album = (XMAlbum) adapter.getItem(position);
                PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
                ((SearchResultActivity) getActivity()).handlePlay(BeanConverter.toPlayerInfo(album));
                XmAutoTracker.getInstance().onEvent(albumItem, album.getAlbumTitle(), "SearchAlbumFragment",
                        EventConstants.PageDescribe.albumSearchResult);
            }
        });
    }

    @Override
    protected void subscriberList() {
        //防止多个loading框
        mSearchResultVM.getAlbumList().observe(this, new Observer<XmResource<XMSearchAlbumList>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMSearchAlbumList> listXmResource) {
                listXmResource.handle(new OnCallback<XMSearchAlbumList>() {
                    @Override
                    public void onSuccess(XMSearchAlbumList data) {
                        setManualScroll();
                        mAdapter.setNewData(data.getAlbums());
                    }

                    @Override
                    public void onFailure(String msg) {
                        if (isManualScroll()) {
                            mAdapter.setEmptyView(R.layout.state_empty_view, (ViewGroup) mRecyclerView.getParent());
                        } else {
                            notifyEmptyView(1);
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (NetworkUtils.isConnected(mContext)) {
                            if (isManualScroll()) {
                                mAdapter.setEmptyView(R.layout.state_empty_view, (ViewGroup) mRecyclerView.getParent());
                            } else {
                                notifyEmptyView(1);
                            }
                        } else {
                            showNoNetView();
                        }

                    }
                });
            }
        });
    }

    @Override
    protected void requestNetData(String keyword) {
        mSearchResultVM.fetchAlbumList(mKeyword);
    }

    private void subscribeTrack() {
        mSearchResultVM.getTrackList().observe(this, new Observer<XmResource<XMTrackList>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMTrackList> xmTrackListXmResource) {
                xmTrackListXmResource.handle(new OnCallback<XMTrackList>() {
                    @Override
                    public void onSuccess(XMTrackList data) {
                        if (!ListUtils.isEmpty(data.getTracks())) {
                            FMPlayerActivity.launch(getActivity());
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                    }
                });
            }
        });
    }

    private class AlbumAdapter extends XMBaseAbstractBQAdapter<XMAlbum, BaseViewHolder> {

        AlbumAdapter() {
            super(R.layout.item_search_result, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, XMAlbum item) {
            ImageLoader.with(SearchAlbumFragment.this)
                    .load(item.getValidCover())
                    .placeholder(R.drawable.fm_default_cover)
                    .transform(Transformations.getRoundedCorners())
                    .into((ImageView) helper.getView(R.id.iv_cover));
            helper.setText(R.id.title, item.getAlbumTitle());
        }

        @Override
        public ItemEvent returnPositionEventMsg(int position) {
            return new ItemEvent(getData().get(position).getAlbumTitle(), getData().get(position).getId() + "");
        }
    }

}
