package com.xiaoma.club.msg.chat.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.viewmodel.ViewState;
import com.xiaoma.club.msg.chat.controller.PoiItemCallback;
import com.xiaoma.club.msg.chat.vm.PoiSearchVM;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

/**
 * Created by LKF on 2018/10/11 0011.
 */
public class PoiSearchFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "PoiSearchFragment";
    private Button mBtnSearchPoi;
    private RecyclerView mRvPoiList;
    private XmScrollBar mRvScrollBar;
    private PoiSearchVM mPoiSearchVM;
    private ItemPoiSearchAdapter mPoiSearchAdapter;
    private boolean mFirstListChanged = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmt_poi_serach, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnSearchPoi = view.findViewById(R.id.btn_search_poi);
        mBtnSearchPoi.setOnClickListener(this);

        mRvPoiList = view.findViewById(R.id.rv_poi_list);
        mRvPoiList.setItemAnimator(null);
        mRvPoiList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvPoiList.setHasFixedSize(true);
        mRvPoiList.setAdapter(mPoiSearchAdapter = new ItemPoiSearchAdapter());

        mRvScrollBar = view.findViewById(R.id.scroll_bar);
        mRvScrollBar.setRecyclerView(mRvPoiList);

        // 当前选中index
        mPoiSearchVM = ViewModelProviders.of((FragmentActivity) view.getContext()).get(PoiSearchVM.class);
        mPoiSearchVM.getSelectPoiIndex().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                mPoiSearchAdapter.mSelectIndex = integer != null ? integer : 0;
                mPoiSearchAdapter.notifyDataSetChanged();
            }
        });
        // poi列表
        mPoiSearchVM.getPoiItemPageList().observe(this, new Observer<PagedList<PoiItem>>() {
            @Override
            public void onChanged(@Nullable final PagedList<PoiItem> poiItems) {
                LogUtil.logI(TAG, "PoiItemPageList # onChanged( poiItems: %s )", poiItems);
                mPoiSearchAdapter.setOnListChangedListener(new OnListChangedListener() {
                    @Override
                    public void onCurrentListChanged(@Nullable PagedList<PoiItem> currentList) {
                        mRvPoiList.scrollToPosition(0);
                        mPoiSearchVM.getSelectPoiIndex().setValue(0);
                        if (poiItems != null && !poiItems.isEmpty()) {
                            mPoiSearchVM.getViewState().setValue(ViewState.NORMAL);
                        } else if (NetworkUtils.isConnected(getContext())) {
                            if (mFirstListChanged) {
                                mFirstListChanged = false;
                            } else {
                                mPoiSearchVM.getViewState().setValue(ViewState.EMPTY_DATA);
                            }
                        } else {
                            mPoiSearchVM.getViewState().setValue(ViewState.NETWORK_ERROR);
                        }
                    }
                });
                mPoiSearchAdapter.submitList(poiItems);
            }
        });
        // 查询中心点和查询关键字
        final Observer<PoiSearchVM.LocationSearch> observer = new Observer<PoiSearchVM.LocationSearch>() {
            @Override
            public void onChanged(@Nullable PoiSearchVM.LocationSearch search) {
                final PagedList<PoiItem> poiItems = mPoiSearchVM.getPoiItemPageList().getValue();
                if (poiItems != null) {
                    poiItems.getDataSource().invalidate();
                }
                mPoiSearchVM.getViewState().setValue(ViewState.LOADING_INITIAL);
            }
        };
        mPoiSearchVM.getLocationSearch().observe(this, observer);
        // 加载状态
        mPoiSearchVM.getViewState().observe(this, new Observer<ViewState>() {
            @Override
            public void onChanged(@Nullable ViewState state) {
                if (state == null)
                    return;
                Fragment fmt = null;
                switch (state) {
                    case LOADING_INITIAL:
                        fmt = new StateLoadingFragment();
                        break;
                    case NORMAL:
                        break;
                    case EMPTY_DATA:
                        fmt = new StateDataEmptyFragment();
                        break;
                    case NETWORK_ERROR:
                        fmt = new StateNoNetworkFragment();
                        ((StateNoNetworkFragment) fmt).setCallback(new StateNoNetworkFragment.Callback() {
                            @Override
                            public void onReload() {
                                observer.onChanged(null);
                            }
                        });
                        break;
                }
                final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                if (fmt != null) {
                    mRvPoiList.setVisibility(View.GONE);
                    transaction.replace(R.id.fmt_state, fmt);
                } else {
                    mRvPoiList.setVisibility(View.VISIBLE);
                    Fragment exist = getChildFragmentManager().findFragmentById(R.id.fmt_state);
                    if (exist != null) {
                        transaction.remove(exist);
                    }
                }
                transaction.commitNow();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search_poi:
                if (mCallback != null) {
                    mCallback.onSearchLocationClick();
                }
                break;
        }
    }

    private interface OnListChangedListener {
        void onCurrentListChanged(@Nullable PagedList<PoiItem> currentList);
    }

    private class ItemPoiSearchAdapter extends PagedListAdapter<PoiItem, ItemPoiSearchHolder> {
        private int mSelectIndex;
        private OnListChangedListener mOnListChangedListener;

        ItemPoiSearchAdapter() {
            super(new PoiItemCallback());
        }

        void setOnListChangedListener(OnListChangedListener onListChangedListener) {
            mOnListChangedListener = onListChangedListener;
        }

        @Override
        public void onCurrentListChanged(@Nullable PagedList<PoiItem> currentList) {
            LogUtil.logI(TAG, "ItemPoiSearchAdapter # onCurrentListChanged( currentList: %s )", currentList);
            if (mOnListChangedListener != null) {
                mOnListChangedListener.onCurrentListChanged(currentList);
            }
        }

        @NonNull
        @Override
        public ItemPoiSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ItemPoiSearchHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_poi_search, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ItemPoiSearchHolder holder, final int position) {
            final PoiItem poiItem = getItem(position);
            if (poiItem != null) {
                holder.mTvPoi.setText(poiItem.getTitle());
                holder.mTvAddress.setText(poiItem.getSnippet());
                final Button btnSendLocation = holder.mBtnSendLocation;
                String sendBtnTxt = null;
                if (mCallback != null) {
                    sendBtnTxt = mCallback.getSendBtnText();
                }
                btnSendLocation.setText(StringUtil.optString(sendBtnTxt, getString(R.string.send_location)));
                if (position == mSelectIndex) {
                    holder.mIconSelect.setVisibility(View.VISIBLE);
                    btnSendLocation.setVisibility(View.VISIBLE);
                    btnSendLocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mCallback != null) {
                                mCallback.onPoiSelect(poiItem);
                            }
                        }
                    });
                    holder.itemView.setBackgroundResource(R.drawable.chat_poi_search_item_bg);
                } else {
                    holder.mIconSelect.setVisibility(View.INVISIBLE);
                    btnSendLocation.setVisibility(View.GONE);
                    holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final LatLonPoint point = poiItem.getLatLonPoint();
                        if (point != null) {
                            // 更新选中Index
                            mPoiSearchVM.getSelectPoiIndex().setValue(holder.getLayoutPosition());
                        } else {
                            showToast("无法获取当前坐标点");
                        }
                    }
                });
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                holder.itemView.setOnClickListener(null);
                holder.mTvPoi.setText("加载中...");
                holder.mTvAddress.setText("加载中...");
            }
        }
    }

    private static class ItemPoiSearchHolder extends RecyclerView.ViewHolder {
        TextView mTvPoi;
        TextView mTvAddress;
        Button mBtnSendLocation;
        View mIconSelect;

        ItemPoiSearchHolder(View itemView) {
            //R.layout.item_poi_search
            super(itemView);
            mTvPoi = itemView.findViewById(R.id.tv_poi);
            mTvAddress = itemView.findViewById(R.id.tv_address);
            mBtnSendLocation = itemView.findViewById(R.id.btn_send_location);
            mIconSelect = itemView.findViewById(R.id.icon_select);
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onPoiSelect(PoiItem poiItem);

        void onSearchLocationClick();

        String getSendBtnText();
    }
}
