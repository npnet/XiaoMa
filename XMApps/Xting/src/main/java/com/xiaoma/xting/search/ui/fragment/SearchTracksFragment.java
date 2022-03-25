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
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.EventConstants;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.common.playerSource.contract.PlayerSourceType;
import com.xiaoma.xting.common.playerSource.info.BeanConverter;
import com.xiaoma.xting.sdk.bean.XMSearchTrackList;
import com.xiaoma.xting.sdk.bean.XMTrack;
import com.xiaoma.xting.search.ui.SearchResultActivity;
import com.xiaoma.xting.utils.Transformations;

import java.util.List;

import static com.xiaoma.xting.common.EventConstants.NormalClick.programItem;
import static com.xiaoma.xting.common.EventConstants.NormalClick.radioItem;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/10
 *     desc   :
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.programSearchResult)
public class SearchTracksFragment extends AbsSearchResultFragment {

    private List<XMTrack> mTracks;

    public static SearchTracksFragment newInstance(String from) {
        SearchTracksFragment fragment = new SearchTracksFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SearchResultActivity.INTENT_KEY_WORD, from);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new TrackAdapter();
    }

    @Override
    protected void execute() {
        super.execute();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!NetworkUtils.isConnected(getContext())) {
                    XMToast.showToast(getContext(), getString(R.string.net_not_connect));
                    return;
                }
                XMTrack xmTrack = mTracks.get(position);
                PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
                ((SearchResultActivity) getActivity()).handlePlay(BeanConverter.toPlayerInfo(xmTrack));
                XmAutoTracker.getInstance().onEvent(programItem, xmTrack.getRadioName(), "SearchTracksFragment",
                        EventConstants.PageDescribe.programSearchResult);
            }
        });
    }

    @Override
    protected void subscriberList() {
        mSearchResultVM.getSearchTracksList().observe(this, new Observer<XmResource<XMSearchTrackList>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMSearchTrackList> listXmResource) {
                listXmResource.handle(new OnCallback<XMSearchTrackList>() {
                    @Override
                    public void onSuccess(XMSearchTrackList data) {
                        mTracks = data.getTracks();
                        setManualScroll();
                        mAdapter.setNewData(data.getTracks());
                    }

                    @Override
                    public void onFailure(String msg) {
                        dismissProgress();
                        if (isManualScroll()) {
                            mAdapter.setEmptyView(R.layout.state_empty_view, (ViewGroup) mRecyclerView.getParent());
                        } else {
                            notifyEmptyView(2);
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (NetworkUtils.isConnected(mContext)) {
                            if (isManualScroll()) {
                                mAdapter.setEmptyView(R.layout.state_empty_view, (ViewGroup) mRecyclerView.getParent());
                            } else {
                                notifyEmptyView(2);
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
        mSearchResultVM.fetchSearchTracksList(mKeyword);
    }

    private class TrackAdapter extends XMBaseAbstractBQAdapter<XMTrack, BaseViewHolder> {

        TrackAdapter() {
            super(R.layout.item_search_result, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, XMTrack item) {
            ImageLoader.with(SearchTracksFragment.this)
                    .load(item.getValidCover())
                    .placeholder(R.drawable.fm_default_cover)
                    .transform(Transformations.getRoundedCorners())
                    .into((ImageView) helper.getView(R.id.iv_cover));
            helper.setText(R.id.title, item.getTrackTitle());
        }

        @Override
        public ItemEvent returnPositionEventMsg(int position) {
            return new ItemEvent(getData().get(position).getTrackTitle(), getData().get(position).getDataId() + "");
        }
    }

}
