package com.xiaoma.assistant.view;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IHardwareEventListener;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.model.ImageBean;
import com.xiaoma.assistant.model.TrainDetailBean;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/26
 * Desc:二级界面
 */
public class MultiPageView extends RelativeLayout implements View.OnClickListener, IHardwareEventListener {

    private final int[] keyCode = new int[]{WheelKeyEvent.KEYCODE_WHEEL_LEFT, WheelKeyEvent.KEYCODE_WHEEL_RIGHT,
            WheelKeyEvent.KEYCODE_WHEEL_OK};
    public static final int PAGE_SIZE = 4;
    private static final String TAG = MultiPageView.class.getSimpleName();
    private BaseMultiPageAdapter mAdapter;
    private RecyclerView rlMoreResult;
    private FrameLayout flDetailPage;
    private TrainDetailView trainDetailView;
    private ImageDetailView imageDetailView;
    private int firstPosition;
    private int lastPosition;
    private boolean repeatSwitch = true;
    private TextView page;
    private View detailView;
    private LinearLayoutManager manager;
    private int curPage;
    private int totalPage;
    private OnWheelKeyListener.Stub carLibListener;

    public MultiPageView(Context context) {
        super(context);
        initView();
    }

    public MultiPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MultiPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_assistant_multipage, this);
        rlMoreResult = findViewById(R.id.rl_more_result);
        flDetailPage = findViewById(R.id.fl_multiple_page_detail);
        page = findViewById(R.id.page);
        manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rlMoreResult.setLayoutManager(manager);
        rlMoreResult.addItemDecoration(new HorizontalItemDecoration());
        rlMoreResult.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState != RecyclerView.SCROLL_STATE_IDLE) return;
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //判断是当前layoutManager是否为LinearLayoutManager
                // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //获取最后一个可见view的位置
                    lastPosition = linearManager.findLastVisibleItemPosition();
                    //获取第一个可见view的位置
                    firstPosition = linearManager.findFirstVisibleItemPosition();
                    KLog.d("zhs---" + lastPosition + "   " + firstPosition);

                    setPageNum();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            default:
                break;
        }
    }


    public boolean setData(BaseMultiPageAdapter adapter) {
        if (adapter == null) {
            return false;
        }
        this.mAdapter = adapter;
        rlMoreResult.setVisibility(VISIBLE);
        page.setVisibility(VISIBLE);
        displayDetailPage(false);
        rlMoreResult.setAdapter(adapter);
        //当设置新的数据上来的时候没有滚动不会有位置定位，需要设置为默认
        firstPosition = 0;
        lastPosition = PAGE_SIZE - 1;
        setPageNum();
        if (!ListUtils.isEmpty(adapter.getCurrentList())) {
            adapter.setSelectPosition(0);
        }
        return true;
    }


    public void setPage(int page) {
        if (mAdapter != null) {
            int size = PAGE_SIZE;
            if (page > 0) {
                /*if (lastPosition - firstPosition >= PAGE_SIZE) {
                    size = PAGE_SIZE - 1;
                }
                rlMoreResult.smoothScrollToPosition(lastPosition + size);*/
                if (curPage > 0) {
                    if (curPage + 1 < totalPage) {
                        scrollTo(curPage * PAGE_SIZE);
                    } else if (curPage + 1 == totalPage) {
                        scrollTo(mAdapter.getItemCount() - 1);
                    }
                }
            } else {
                /*if (lastPosition - firstPosition >= PAGE_SIZE) {
                    size = PAGE_SIZE - 1;
                }

                rlMoreResult.smoothScrollToPosition(firstPosition - size >= 0 ? firstPosition - size : 0);*/
                if (curPage - 1 > 0) {
                    scrollTo((curPage - 2) * PAGE_SIZE);
                }
            }
            displayDetailPage(false);
        }
    }

    public void setPageForIndex(int page) {
        if (page > 0 && page <= totalPage && page != curPage) {
            if (page == totalPage) {
                scrollTo(mAdapter.getItemCount() - 1);
            } else {
                scrollTo((page - 1) * PAGE_SIZE);
            }
        } else if (page == Integer.MAX_VALUE) {
            scrollTo(mAdapter.getItemCount() - 1);
        }
    }


    public boolean setDetailPage(int type, Object data) {
        if (data == null) {
            return false;
        }
        if (!AssistantManager.getInstance().inMultipleForChooseMode()) {
            AssistantManager.getInstance().showDetailView();
        }
        detailView = null;
        if (type == Constants.MultiplePageDetailType.TRAIN_DEATAIL) {
            TrainDetailBean detailBean = (TrainDetailBean) data;
            if (trainDetailView == null) {
                trainDetailView = new TrainDetailView(getContext());
            }
            trainDetailView.setData(detailBean);
            detailView = trainDetailView;
        } else if (type == Constants.MultiplePageDetailType.IMAGE_DEATAIL) {
            Bundle bundle = (Bundle) data;
            if (imageDetailView == null) {
                imageDetailView = new ImageDetailView(getContext());
            }
            List<ImageBean.ImagesBean> list = (List<ImageBean.ImagesBean>) bundle.getSerializable(Constants.BundleKey.LIST);
            int position = bundle.getInt(Constants.BundleKey.POSITION);
            imageDetailView.setData(list, position);
            detailView = imageDetailView;
        } else {
            return false;
        }
        flDetailPage.removeAllViews();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        flDetailPage.addView(detailView, layoutParams);
        rlMoreResult.setVisibility(GONE);
        page.setVisibility(GONE);
        displayDetailPage(true);
        return true;
    }

    private void displayDetailPage(boolean show) {
        flDetailPage.setVisibility(show ? VISIBLE : GONE);
        setMarginTop(!show);
    }

    private void setMarginTop(boolean isMarginTop) {
        RelativeLayout.LayoutParams layoutParams = (LayoutParams) getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, isMarginTop ? 55 : 0, layoutParams.rightMargin, layoutParams.bottomMargin);
        setLayoutParams(layoutParams);
    }


    public boolean isDetailPage() {
        return flDetailPage != null && flDetailPage.getVisibility() == VISIBLE;
    }


    public void hideDetailPage() {
        if (flDetailPage != null) {
            displayDetailPage(false);
            rlMoreResult.setVisibility(VISIBLE);
            page.setVisibility(VISIBLE);
        }
    }

    public boolean isFirstPage() {
        return firstPosition == 0 && lastPosition - firstPosition < PAGE_SIZE;
    }

    public boolean isLastPage() {
        return lastPosition == mAdapter.getItemCount() - 1 && lastPosition - firstPosition < PAGE_SIZE;
    }

    @Override
    public void onSelectNext() {
        //下一个
        setNextPosition();
    }

    @Override
    public void onSelectPre() {
        //上一个
        setPrePosition();
    }

    private synchronized void setPrePosition() {
        if (mAdapter != null && mAdapter.getAllList() != null) {
            if (mAdapter.getSelectPosition() <= 0) {
                return;
            }
            mAdapter.setSelectPosition(mAdapter.getSelectPosition() - 1);
            mAdapter.notifyDataSetChanged();
        }
    }

    private synchronized void setNextPosition() {
        if (mAdapter != null && mAdapter.getAllList() != null) {
            int size = mAdapter.getAllList().size();
            if (mAdapter.getSelectPosition() >= size) {
                return;
            }
            mAdapter.setSelectPosition(mAdapter.getSelectPosition() + 1);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSelect() {
        //方控选择
        if (mAdapter != null && mAdapter.getMultiPageListener() != null) {
            BaseMultiPageAdapter.OnMultiPageItemClickListener multiPageListener = mAdapter.getMultiPageListener();
            int selectPosition = mAdapter.getSelectPosition();
            if (selectPosition < 0 || selectPosition >= mAdapter.getItemCount()) {
                KLog.e(TAG, "IndexOutOfBoundsException,max num is " + mAdapter.getItemCount() + ",current selection is " + selectPosition);
                selectPosition = 0;
            }
            multiPageListener.onItemClick(selectPosition);
        }
    }

    public void setPageNum() {
        totalPage = getTotalPage();
        if (totalPage == 0) {
            totalPage = 1;
        }
        View lastVisibleItem = manager.findViewByPosition(lastPosition);
        if (lastVisibleItem != null && lastVisibleItem.getRight() > rlMoreResult.getWidth()) {
            lastPosition--;
        }
        if (lastPosition == mAdapter.getItemCount() - 1) {
            curPage = totalPage;
        } else {
            curPage = (lastPosition + 1) / PAGE_SIZE;
        }
        page.setText(curPage + "/" + totalPage);
    }

    private int getTotalPage() {
        return getTotalPage(mAdapter.getItemCount());
    }

    public static int getTotalPage(int size) {
        return size % PAGE_SIZE == 0 ? size / PAGE_SIZE : (size / PAGE_SIZE) + 1;
    }

    public View getDetailView() {
        return detailView;
    }

    public void scrollTo(int position) {
        TopSmoothScroller topSmoothScroller = new TopSmoothScroller(getContext());
        topSmoothScroller.setTargetPosition(position);
        manager.startSmoothScroll(topSmoothScroller);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            registerWheelListener();
        } else {
            unregisterWheelListener();
        }
    }

    public void unregisterWheelListener() {
        if (carLibListener != null) {
            XmWheelManager.getInstance().unregister(carLibListener);
        }
    }


    private void registerWheelListener() {
        if (carLibListener == null) {
            carLibListener = new OnWheelKeyListener.Stub() {
                @Override
                public boolean onKeyEvent(int keyAction, int keyCode) {
                    switch (keyCode) {
                        case WheelKeyEvent.KEYCODE_WHEEL_LEFT:
                            setPrePosition();
                            break;
                        case WheelKeyEvent.KEYCODE_WHEEL_RIGHT:
                            setNextPosition();
                            break;
                        case WheelKeyEvent.KEYCODE_WHEEL_OK:
                            onSelect();
                            break;
                    }

                    return true;
                }

                @Override
                public String getPackageName() throws RemoteException {
                    return getContext().getPackageName();
                }
            };
        }
        XmWheelManager.getInstance().register(carLibListener, keyCode);
    }

    public BaseMultiPageAdapter getAdapter() {
        return mAdapter;
    }

}
