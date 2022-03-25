package com.xiaoma.shop.business.ui.hologram;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.carlib.store.HologramRepo;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.R;
import com.xiaoma.shop.business.adapter.HologramClothingAdapter;
import com.xiaoma.shop.business.hologram.HologramUsing;
import com.xiaoma.shop.business.model.HoloListModel;
import com.xiaoma.shop.business.model.HoloManInfo;
import com.xiaoma.shop.business.model.HologramDress;
import com.xiaoma.shop.business.vm.HologramDetailVm;
import com.xiaoma.utils.ListUtils;

import java.util.List;
import java.util.Objects;

/**
 * Created by Gillben
 * date: 2019/3/5 0005
 * <p>
 * 全息影像服装
 */
public class HologramClothingFragment extends BaseFragment {
    private static final String EXTRA_ROLE = "role";

    private RecyclerView mRecyclerView;
    private HologramClothingAdapter mHologramClothingAdapter;

    private HologramDetailActivity mHoloActivity;
    private GridLayoutManager mManager;
    private int mRecordPosition;
    private HoloListModel mRole;

    private BroadcastReceiver mBroadcastReceiver;

    public static BaseFragment newInstance(HoloListModel role) {
        HologramClothingFragment fragment = new HologramClothingFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ROLE, role);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHoloActivity = (HologramDetailActivity) getActivity();
        if (getArguments() != null)
            mRole = getArguments().getParcelable(EXTRA_ROLE);
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
        IntentFilter intent = new IntentFilter();
        intent.addAction(HologramUsing.ACTION_ROLE_CLOTH_USING);
        mContext.registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mHologramClothingAdapter == null) return;
                mHologramClothingAdapter.notifyDataSetChanged();
            }
        }, intent);
    }

    private void initVM() {
        HologramDetailVm hologramDetailVm = ViewModelProviders.of(getActivity()).get(HologramDetailVm.class);
        hologramDetailVm.getHoloManInfoLiveData().observe(this, new Observer<XmResource<HoloManInfo>>() {
            @Override
            public void onChanged(@Nullable XmResource<HoloManInfo> holoManInfoXmResource) {
                holoManInfoXmResource.handle(new OnCallback<HoloManInfo>() {
                    @Override
                    public void onSuccess(HoloManInfo data) {
                        List<HologramDress> holo_clothes = data.getChildItems().getHolo_clothes();
                        int usingRole = HologramRepo.getUsingRoleId(mContext);
                        if (!Objects.equals(data.getCode(), String.valueOf(usingRole))
                                && !ListUtils.isEmpty(holo_clothes)) {//不是使用状态默认选择第一个
                            holo_clothes.get(0).setSelected(true);
                        } else if (Objects.equals(data.getCode(), String.valueOf(usingRole))
                                && !ListUtils.isEmpty(holo_clothes)) {//使用状态
                            HologramDress hologramDress = searchUseCloth(holo_clothes);
                            if (hologramDress != null) {
                                hologramDress.setSelected(true);
                            } else {
                                holo_clothes.get(0).setSelected(true);
                            }
                        }
                        mHologramClothingAdapter.setNewData(holo_clothes);
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

    private HologramDress searchUseCloth(List<HologramDress> holo_clothes) {
        int id = HologramRepo.getUsingClothId(mActivity, HologramUsing.getRoleGoldWildId(mRole));
        for (HologramDress holo_clothe : holo_clothes) {
            if (HologramUsing.getClothGoldWildId(holo_clothe) == id) {
                return holo_clothe;
            }
        }
        return null;
    }

    private void initView(final View view) {
        mRecyclerView = view.findViewById(R.id.rv_hologram_detail);

        mHologramClothingAdapter = new HologramClothingAdapter(mRole);
        mManager = new GridLayoutManager(mContext, 5);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
               /* int top = 0;
                int right = 49;
                int bottom = 0;
                int position = parent.getChildAdapterPosition(view);
                bottom += 50;
                if (position % 4 == 0) {
                    right = 0;
                }

                int countDivider = mHologramClothingAdapter.getItemCount() % 10;
                if (countDivider <= 5) {
                    Log.d("Shop_Divider", "getItemOffsets: " + countDivider + " / " + position + " - " + (mHologramClothingAdapter.getItemCount() - countDivider));
                    if (position >= mHologramClothingAdapter.getItemCount() - countDivider) {
                        bottom += 196;
                    }
                }*/
                int position = parent.getChildAdapterPosition(view);
                int left = 0;
                if (position != 0) {
                    left = 90;
                }
                outRect.set(left, 0, 0, 0);
            }
        });
        mRecyclerView.setAdapter(mHologramClothingAdapter);

        mHologramClothingAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Context context = view.getContext();
                HologramDress cloth = mHologramClothingAdapter.getItem(position);
                if (cloth != null) {
                    if (HologramUsing.isRoleUsing(context, mRole)) {
                        // 使用中的角色才发切换衣服的信号
                        HologramUsing.useCloth(context, mRole, cloth);
                    } else {
                        // 非使用中的角色只记录衣服
                        HologramRepo.putUsingClothId(context,
                                HologramUsing.getRoleGoldWildId(mRole),
                                HologramUsing.getClothGoldWildId(cloth));
                    }
                    mHologramClothingAdapter.setSelected(cloth);
                }
                mHologramClothingAdapter.notifyDataSetChanged();
            }
        });
       /* mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int mDy;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstVisibleItemPosition = mManager.findFirstVisibleItemPosition();
                    RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForLayoutPosition(firstVisibleItemPosition);
                    if (viewHolder != null) {
                        int top = viewHolder.itemView.getTop();
                        Log.d("RV", "{onScrollStateChanged}-" + top);
                        int halfHeight = viewHolder.itemView.getHeight() >> 3;
                        if (mDy > 0 && top < 0 && Math.abs(top) > halfHeight) {
                            int firstCompletelyVisibleItemPosition = mManager.findFirstCompletelyVisibleItemPosition();
                            RecyclerView.ViewHolder completeTopViewHolder = mRecyclerView.findViewHolderForLayoutPosition(firstCompletelyVisibleItemPosition);
                            if (completeTopViewHolder != null) {
                                int completeTop = completeTopViewHolder.itemView.getTop();
                                recyclerView.smoothScrollBy(0, completeTop);
                                Log.d("RV", "{0 . scroll to }-" + firstCompletelyVisibleItemPosition);
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
        setOnPreNextClick();*/
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            setOnPreNextClick();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBroadcastReceiver != null) {
            mContext.unregisterReceiver(mBroadcastReceiver);
        }
    }

    private void setOnPreNextClick() {
        if (mHoloActivity != null) {
            if (mHologramClothingAdapter.getItemCount() > 0) {
                showArrow(mRecordPosition);
            }
            mHoloActivity.setOnOnPreNextClickListener(new HologramDetailActivity.IOnPreNextClickListener() {
                @Override
                public void onPlayPre() {
                    int topCompletePosition = mManager.findFirstCompletelyVisibleItemPosition();
                    if (topCompletePosition < 0) {
                        topCompletePosition = mManager.findFirstVisibleItemPosition();
                    }
                    int toPosition = topCompletePosition - 10;
                    if (toPosition < 0) {
                        toPosition = 0;
                    }

                    mManager.scrollToPositionWithOffset(toPosition, 0);
                    showArrow(toPosition);
                }

                @Override
                public void onPlayNext() {
                    int completePosition = mManager.findFirstCompletelyVisibleItemPosition();
                    int toPosition = completePosition + 10;
                    mManager.scrollToPositionWithOffset(toPosition, 0);
                    showArrow(toPosition);
                }
            });
        }
    }

    private void showArrow(final int curPosition) {
        /*
        int itemCount = mHologramClothingAdapter.getItemCount();
        if (itemCount <= 10) {
            mHoloActivity.showHideArrow(0, false, false);
        } else {
            mRecordPosition = curPosition;
            if (curPosition <= 0) {
                mHoloActivity.showHideArrow(0, false, true);
            } else if ((itemCount - curPosition) < 10) {
                mHoloActivity.showHideArrow(0, true, false);
            } else {
                mHoloActivity.showHideArrow(0, true, true);
            }
        }*/
    }

    public HologramDress searchSelectedCloth() {
        if (mHologramClothingAdapter == null) return null;
        return mHologramClothingAdapter.searchSelectedCloth();
    }
}
