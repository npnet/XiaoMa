package com.xiaoma.shop.business.ui.hologram;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.HologramSkillAdapter;
import com.xiaoma.shop.business.model.HoloManInfo;
import com.xiaoma.shop.business.model.HologramSkill;
import com.xiaoma.shop.business.vm.HologramDetailVm;

import java.util.List;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 */
public class HologramSkillFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private HologramSkillAdapter mHologramSkillAdapter;
    private HologramDetailActivity mHoloActivity;
    private LinearLayoutManager mManager;
    private boolean mFragmentVisibleF;
    private int mRecordPosition;

    public static BaseFragment newInstance() {
        HologramSkillFragment fragment = new HologramSkillFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHoloActivity = (HologramDetailActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_hologram_detail, container, false);
        return super.onCreateWrapView(contentView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initVM();
    }

    private void initView(View view) {
        mFragmentVisibleF = false;
        mRecyclerView = view.findViewById(R.id.rv_hologram_detail);

        mHologramSkillAdapter = new HologramSkillAdapter();
        mManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int bottom = 40;
                int position = parent.getChildAdapterPosition(view);
                RecyclerView.Adapter adapter = parent.getAdapter();
                int itemCount = adapter.getItemCount();
                int countDivider = itemCount % 4;
                if (countDivider > 0) {
                    if (position == adapter.getItemCount() - 1) {
                        bottom = (4 - countDivider) * (65 + 40);
                    }
                }

                outRect.set(0, 0, 0, bottom);
            }
        });
        mRecyclerView.setAdapter(mHologramSkillAdapter);
        mHologramSkillAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mHologramSkillAdapter.notifyItemSelected(position);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int mDy;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstVisibleItemPosition = mManager.findFirstVisibleItemPosition();
                    RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(firstVisibleItemPosition);
                    if (viewHolder != null) {
                        int top = viewHolder.itemView.getTop();
                        Log.d("RV", "{onScrollStateChanged}-" + top);
                        int halfHeight = viewHolder.itemView.getHeight() >> 2;
                        if (mDy > 0 && top < 0 && Math.abs(top) > halfHeight) {
                            int firstCompletelyVisibleItemPosition = mManager.findFirstCompletelyVisibleItemPosition();
                            RecyclerView.ViewHolder completeTopViewHolder = mRecyclerView.findViewHolderForLayoutPosition(firstCompletelyVisibleItemPosition);
                            if (completeTopViewHolder != null) {
                                int completeTop = completeTopViewHolder.itemView.getTop();
                                if (completeTop > 4) {
                                    recyclerView.smoothScrollBy(0, completeTop);
                                }
                                Log.d("RV", "{0 . scroll to }-" + firstCompletelyVisibleItemPosition + " / " + completeTop);
                            } else {
                                int lastVisibleItemPosition = mManager.findLastVisibleItemPosition();
                                if (lastVisibleItemPosition == (mManager.getItemCount() - 1)) {
                                    RecyclerView.ViewHolder bottomViewHolder = mRecyclerView.findViewHolderForLayoutPosition(lastVisibleItemPosition);
                                    if (bottomViewHolder != null) {
                                        int completeTop = bottomViewHolder.itemView.getTop();
                                        recyclerView.smoothScrollBy(0, completeTop);
                                        Log.d("RV", "{1 . scroll to }-" + lastVisibleItemPosition);
                                    }
                                }
                            }
                        } else {
                            recyclerView.smoothScrollBy(0, top);

                            Log.d("RV", "{2 . scroll to }-" + firstVisibleItemPosition);
                            showArrow(firstVisibleItemPosition);

                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                Log.d("RV", "{onScrolled}-[] : " + dy);
                mDy = dy;
//                recyclerView.smoothScrollBy(dx,);
            }
        });
        setOnPreNextClick();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mFragmentVisibleF = isVisibleToUser;
        if (isVisibleToUser) {
            setOnPreNextClick();
        }
    }

    private void setOnPreNextClick() {
        if (mHoloActivity != null && mFragmentVisibleF) {
            if (mHologramSkillAdapter.getItemCount() >= 0) {
                showArrow(mRecordPosition);
            }
            mHoloActivity.setOnOnPreNextClickListener(new HologramDetailActivity.IOnPreNextClickListener() {
                @Override
                public void onPlayPre() {
                    int topCompletePosition = mManager.findFirstCompletelyVisibleItemPosition();
                    if (topCompletePosition < 0) {
                        topCompletePosition = mManager.findFirstVisibleItemPosition();
                    }
                    int toPosition = topCompletePosition - 4;
                    if (toPosition < 0) {
                        toPosition = 0;
                    }

                    mManager.scrollToPositionWithOffset(toPosition, 0);
                    showArrow(toPosition);
                }

                @Override
                public void onPlayNext() {
                    int completePosition = mManager.findFirstCompletelyVisibleItemPosition();
                    int toPosition = completePosition + 4;
                    mManager.scrollToPositionWithOffset(toPosition, 0);
                    showArrow(toPosition);
                }
            });
        }

    }

    private void initVM() {
        HologramDetailVm hologramDetailVm = ViewModelProviders.of(getActivity()).get(HologramDetailVm.class);
        hologramDetailVm.getHoloManInfoLiveData().observe(this, new Observer<XmResource<HoloManInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<HoloManInfo> holoManInfoXmResource) {
                holoManInfoXmResource.handle(new OnCallback<HoloManInfo>() {
                    @Override
                    public void onSuccess(HoloManInfo data) {
                        List<HologramSkill> holo_motion = data.getChildItems().getHolo_motion();
                        mHologramSkillAdapter.setNewData(holo_motion);
                        showArrow(0);
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                        showArrow(0);
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        showArrow(0);
                    }
                });
            }
        });

    }

    private void showArrow(final int curPosition) {
        int itemCount = mHologramSkillAdapter.getItemCount();
        if (itemCount <= 4) {
            mHoloActivity.showHideArrow(1, false, false);
        } else {
            mRecordPosition = curPosition;
            if (curPosition <= 0) {
                mHoloActivity.showHideArrow(1, false, true);
            } else if ((itemCount - curPosition) <= 4) {
                mHoloActivity.showHideArrow(1, true, false);
            } else {
                mHoloActivity.showHideArrow(1, true, true);
            }
        }
    }
}
