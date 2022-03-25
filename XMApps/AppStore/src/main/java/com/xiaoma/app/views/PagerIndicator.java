package com.xiaoma.app.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaoma.app.R;

import java.util.Stack;

/**
 * Created by LKF on 2017/2/8 0008.
 */

public class PagerIndicator extends LinearLayout {
    public static final int DEFAULT_ICON_SEL = R.drawable.icon_page_selected;
    public static final int DEFAULT_ICON_UNSEL = R.drawable.icon_page_normal;
    private Stack<View> viewCache = new Stack<>();
    private ViewPager pager;
    private ViewPager.OnPageChangeListener pageChangeListener;

    private Drawable selectedIcon;
    private Drawable unselectedIcon;
    private int indicatorSize = 20;
    private int lastPagerPos = -1;
    private boolean touchToChangeItem = true;
    private boolean smoothChangeItem = false;

    public PagerIndicator(Context context) {
        super(context);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setIndicatorIcons(int selectedIcon, int unselectedIcon) {
        Resources res = getResources();
        if (res != null)
            setIndicatorIcons(res.getDrawable(selectedIcon), res.getDrawable(unselectedIcon));
    }

    public void setIndicatorIcons(Drawable selectedIcon, Drawable unselectedIcon) {
        this.selectedIcon = selectedIcon;
        this.unselectedIcon = unselectedIcon;
    }

    public Drawable getUnselectedIcon() {
        if (unselectedIcon == null)
            unselectedIcon = getResources().getDrawable(DEFAULT_ICON_UNSEL);
        return unselectedIcon;
    }

    public Drawable getSelectedIcon() {
        if (selectedIcon == null)
            selectedIcon = getResources().getDrawable(DEFAULT_ICON_SEL);
        return selectedIcon;
    }

    public void setIndicatorSize(int indicatorSize) {
        this.indicatorSize = indicatorSize;
    }

    public void setTouchToChangeItem(boolean touchToChangeItem) {
        this.touchToChangeItem = touchToChangeItem;
    }

    public void setTouchToChangeItem(boolean touchToChangeItem, boolean smoothChangeItem) {
        setTouchToChangeItem(touchToChangeItem);
        this.smoothChangeItem = smoothChangeItem;
    }

    public void updateIndicator() {
        boolean layoutChanged = true;
        int pageCount = 0;
        do {
            PagerAdapter adapter = null;
            if (pager == null || (adapter = pager.getAdapter()) == null) {
                detachAllViewsFromParent();
                break;
            }
            pageCount = adapter.getCount();
            if (pageCount <= 1) {
                detachAllViewsFromParent();
                break;
            }
            if (getChildCount() == pageCount) {
                layoutChanged = false;
            } else {
                //大部分情况下,页数不会变化,所以循环不会每次都进入,主要为了避免每次更新都创建新的实例和添加新的布局
                while (getChildCount() != pageCount) {
                    if (getChildCount() < pageCount) {
                        LayoutParams params = new LayoutParams(indicatorSize, indicatorSize);  //dot的宽高
                        if (!viewCache.isEmpty()) {
                            attachViewToParent(viewCache.pop(), -1, params);
                        } else {
                            ImageView image = new ImageView(getContext());
                            addViewInLayout(image, -1, params);
                        }
                    } else {
                        View child = getChildAt(getChildCount() - 1);
                        detachViewFromParent(child);
                        viewCache.push(child);
                    }
                }
            }
            int curItem = pager.getCurrentItem();
            for (int i = 0; i < pageCount; i++) {
                ImageView iv = (ImageView) getChildAt(i);
                iv.setImageDrawable(curItem != i ? getUnselectedIcon() : getSelectedIcon());
            }
        } while (false);

        if (layoutChanged)
            requestLayout();
        invalidate();
    }

    /**
     * 正常切换page的时候,无需遍历,只交换其中两个的状态
     *
     * @param oldPos
     * @param curPos
     */
    private void swapPagerIndicator(int oldPos, int curPos) {
        if (oldPos == curPos)
            return;
        int childCount = getChildCount();
        if (oldPos >= 0 && oldPos < childCount && curPos >= 0 && curPos < childCount) {
            ((ImageView) getChildAt(oldPos)).setImageDrawable(getUnselectedIcon());
            ((ImageView) getChildAt(curPos)).setImageDrawable(getSelectedIcon());
        } else {
            updateIndicator();
        }
    }

    public void setupViewPager(ViewPager viewPager) {
        //移除上一个监听器
        if (pager != null && pageChangeListener != null) {
            pager.removeOnPageChangeListener(pageChangeListener);
        }
        pager = viewPager;
        lastPagerPos = -1;
        if (pageChangeListener == null) {
            pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    if (lastPagerPos >= 0) {
                        swapPagerIndicator(lastPagerPos, position);
                    } else {
                        updateIndicator();
                    }
                    lastPagerPos = position;
                }
            };
        }
        viewPager.removeOnPageChangeListener(pageChangeListener);
        viewPager.addOnPageChangeListener(pageChangeListener);
        updateIndicator();
    }

    private void handleTouch(MotionEvent e) {
        if (pager != null) {
            try {
                int beginX = (int) (e.getX() - getPaddingLeft());
                if (beginX < 0)
                    beginX = 0;
                int childIndex = (int) ((float) beginX / indicatorSize);
                if (childIndex >= getChildCount())
                    childIndex = getChildCount() - 1;
                pager.setCurrentItem(childIndex, smoothChangeItem);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (MotionEvent.ACTION_DOWN == ev.getAction() && getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchToChangeItem) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    handleTouch(event);
                    return true;
            }
        }
        return super.onTouchEvent(event);
    }
}
