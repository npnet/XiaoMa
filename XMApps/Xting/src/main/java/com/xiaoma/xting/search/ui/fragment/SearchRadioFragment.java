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
import com.xiaoma.xting.sdk.bean.XMRadio;
import com.xiaoma.xting.sdk.bean.XMRadioList;
import com.xiaoma.xting.search.ui.SearchResultActivity;
import com.xiaoma.xting.utils.Transformations;

import static com.xiaoma.xting.common.EventConstants.NormalClick.radioItem;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/10
 *     desc   :
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.radioSearchResult)
public class SearchRadioFragment extends AbsSearchResultFragment {

    public static SearchRadioFragment newInstance(String from) {
        SearchRadioFragment fragment = new SearchRadioFragment();
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
                XMRadio xmRadio = (XMRadio) adapter.getItem(position);
                PlayerSourceFacade.newSingleton().setSourceType(PlayerSourceType.HIMALAYAN);
                ((SearchResultActivity) mActivity).handlePlay(BeanConverter.toPlayerInfo(xmRadio));
                XmAutoTracker.getInstance().onEvent(radioItem, xmRadio.getProgramName(), "SearchRadioFragment",
                        EventConstants.PageDescribe.radioSearchResult);
            }
        });
    }

    @Override
    protected void subscriberList() {
        mSearchResultVM.getRadioList().observe(this, new Observer<XmResource<XMRadioList>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMRadioList> listXmResource) {
                listXmResource.handle(new OnCallback<XMRadioList>() {
                    @Override
                    public void onSuccess(XMRadioList data) {
                        mAdapter.setNewData(data.getRadios());
                    }

                    @Override
                    public void onFailure(String msg) {
                        dismissProgress();
                        if (isManualScroll()) {
                            mAdapter.setEmptyView(R.layout.state_empty_view, (ViewGroup) mRecyclerView.getParent());
                        } else {
                            notifyEmptyView(0);
                            setManualScroll();
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        if (NetworkUtils.isConnected(mContext)) {
                            if (isManualScroll()) {
                                mAdapter.setEmptyView(R.layout.state_empty_view, (ViewGroup) mRecyclerView.getParent());
                            } else {
                                notifyEmptyView(0);
                                setManualScroll();
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
        mSearchResultVM.fetchRadioList(mKeyword);
    }

    private class TrackAdapter extends XMBaseAbstractBQAdapter<XMRadio, BaseViewHolder> {

        TrackAdapter() {
            super(R.layout.item_search_result, null);
        }

        @Override
        protected void convert(BaseViewHolder helper, XMRadio item) {
            ImageLoader.with(SearchRadioFragment.this)
                    .load(item.getCoverUrlLarge())
                    .placeholder(R.drawable.fm_default_cover)
                    .transform(Transformations.getRoundedCorners())
                    .into((ImageView) helper.getView(R.id.iv_cover));
            helper.setText(R.id.title, item.getRadioName());
        }

        @Override
        public ItemEvent returnPositionEventMsg(int position) {
            return new ItemEvent(getData().get(position).getRadioName(), getData().get(position).getScheduleID() + "");
        }
    }

}
