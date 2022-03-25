package com.xiaoma.club.msg.chat.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.ClubEventConstants;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.RepoObserver;
import com.xiaoma.club.common.ui.SlideInFragment;
import com.xiaoma.club.msg.chat.model.FaceQuickInfo;
import com.xiaoma.club.msg.chat.repo.FaceRepo;
import com.xiaoma.club.msg.chat.vm.SendFaceVM;
import com.xiaoma.guide.listener.GuideCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.view.XmScrollBar;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by LKF on 2018/10/11 0011.
 */
@PageDescComponent(ClubEventConstants.PageDescribe.sendFaceFragment)
public class SendFaceFragment extends SlideInFragment {
    private static final int SPAN_COUNT = 4;
    private RecyclerView mRvFaceList;
    private XmScrollBar mScrollBar;
    private FaceItemAdapter mAdapter;
    private List<String> faceList = new ArrayList<>();
    private SendFaceVM mSendFaceVM;
    private NewGuide newGuide;
    private final FaceRepo mFaceRepo = ClubRepo.getInstance().getFaceRepo();

    @Override
    protected View onCreateWrapView(View childView) {
        return LayoutInflater.from(childView.getContext()).inflate(R.layout.fmt_send_face, (ViewGroup) childView, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context context = view.getContext();
        mRvFaceList = view.findViewById(R.id.rv_face_list);
        mRvFaceList.setLayoutManager(new GridLayoutManager(context, SPAN_COUNT));
        mRvFaceList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(30, 0, 30, 60);
            }
        });
        mRvFaceList.setAdapter(mAdapter = new FaceItemAdapter());

        mScrollBar = view.findViewById(R.id.scroll_bar);
        mScrollBar.setRecyclerView(mRvFaceList);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
        initVM();
        showGuideWindow(((ViewGroup) view).getChildAt(0));
    }

    private List<String> makeFaceUrls(FaceQuickInfo[] faceQuickInfos) {
        sortFace(faceQuickInfos);
        final List<String> faceUrls = new ArrayList<>();
        if (faceQuickInfos != null && faceQuickInfos.length > 0) {
            for (final FaceQuickInfo info : faceQuickInfos) {
                faceUrls.add(info.getExpressionUrl());
            }
        }
        return faceUrls;
    }

    private void sortFace(FaceQuickInfo[] faceQuickInfos) {
        //按orderLevel排序
        Arrays.sort(faceQuickInfos, new Comparator<FaceQuickInfo>() {
            @Override
            public int compare(FaceQuickInfo o1, FaceQuickInfo o2) {
                return o1.getOrderLevel() - o2.getOrderLevel();
            }
        });
    }

    private RepoObserver mRepoObserver;

    private void initVM() {
        mSendFaceVM = ViewModelProviders.of(this).get(SendFaceVM.class);
        mSendFaceVM.getFaceUrls().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> faceUrls) {
                faceList.clear();
                if (faceUrls != null) {
                    faceList.addAll(faceUrls);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        mFaceRepo.addObserver(mRepoObserver = new RepoObserver() {
            @Override
            public void onChanged(String table) {
                if (isDestroy())
                    return;
                mSendFaceVM.getFaceUrls().postValue(makeFaceUrls(mFaceRepo.get()));
            }
        });
        // 先从缓存读取,再从网络加载
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                if (isDestroy())
                    return;
                mSendFaceVM.getFaceUrls().postValue(makeFaceUrls(mFaceRepo.get()));
                mFaceRepo.fetchFaceQuickList(null, Priority.HIGH);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFaceRepo.removeObserver(mRepoObserver);
    }

    @Override
    protected int getSlideViewId() {
        return R.id.face_list_parent;
    }

    private class FaceItemAdapter extends RecyclerView.Adapter<FaceItemHolder> {
        @NonNull
        @Override
        public FaceItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new FaceItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_send_face, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull FaceItemHolder holder, int position) {
            final String uri = faceList.get(position);
            try {
                Glide.with(SendFaceFragment.this)
                        .load(uri)
                        .dontAnimate()
                        .placeholder(R.drawable.default_placeholder)
                        .error(R.drawable.default_placeholder)
                        .into(holder.mIvFace);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.itemView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                @Override
                public ItemEvent returnPositionEventMsg(View view) {
                    return new ItemEvent(ClubEventConstants.NormalClick.sendFace, uri);
                }

                @Override
                @BusinessOnClick
                public void onClick(View v) {
                    if (mCallback != null)
                        mCallback.onImageSelect(uri);
                }
            });
        }

        @Override
        public int getItemCount() {
            return faceList.size();
        }
    }

    private static class FaceItemHolder extends RecyclerView.ViewHolder {
        ImageView mIvFace;

        FaceItemHolder(View itemView) {
            super(itemView);
            mIvFace = itemView.findViewById(R.id.iv_face);
        }
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onImageSelect(String uri);
    }

    private void showGuideWindow(View view) {
        try {
            if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.CLUB_SHOWED, GuideConstants.CLUB_GUIDE_FIRST, false))
                return;
//            ViewGroup naviBar = (ViewGroup) view;
//            final View targetView =  ((ViewGroup) naviBar.getChildAt(0)).getChildAt(1);
            final View targetView = getSlideNaviBar().getBackPreView();
            targetView.post(new Runnable() {
                @Override
                public void run() {
                    Rect viewRect = NewGuide.getViewRect(targetView);
                    Rect targetRect = new Rect(viewRect.left, viewRect.top + (viewRect.height() / 2 - 92), viewRect.right, viewRect.top + (viewRect.height() / 2 + 92));
                    newGuide = NewGuide.with(getActivity())
                            .setLebal(GuideConstants.CLUB_SHOWED)
                            .setTargetView(targetView)
                            .setHighLightRect(targetRect)
                            .setGuideLayoutId(R.layout.guide_view_back_face)
                            .setNeedShake(true)
                            .setNeedHande(true)
                            .needMoveUpALittle(true)
                            .setViewHandId(R.id.iv_gesture)
                            .setViewWaveIdOne(R.id.iv_wave_one)
                            .setViewWaveIdTwo(R.id.iv_wave_two)
                            .setViewWaveIdThree(R.id.iv_wave_three)
                            .setViewSkipId(R.id.tv_guide_skip)
                            .setCallBack(new GuideCallBack() {
                                @Override
                                public void onHighLightClicked() {
                                    dismissGuideWindow();
                                    if (GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.CLUB_SHOWED, GuideConstants.CLUB_GUIDE_FIRST, false))
                                        EventBus.getDefault().post("", "show_guide_group_name");
                                    getActivity().onBackPressed();
                                }
                            })
                            .build();
                    newGuide.showGuide();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissGuideWindow() {
        if (newGuide == null) return;
        newGuide.dismissGuideWindow();
        newGuide = null;
    }
}