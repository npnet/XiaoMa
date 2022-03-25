package com.xiaoma.autotracker.lifecycle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.xiaoma.autotracker.db.AutoTrackDBManager;
import com.xiaoma.autotracker.model.AppViewScreen;
import com.xiaoma.autotracker.model.AutoTrackInfo;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * @author taojin
 * @date 2018/12/6
 */
public class XmDataFragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks implements ITrackerFragmentVisible {
    private List<WeakReference<Fragment>> refs = new ArrayList<>();
    private WeakHashMap<Fragment, Boolean> trackedRefs = new WeakHashMap<>();


    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        if (f != null) {
            refs.add(new WeakReference<>(f));
        }

        if (f != null) {
            if (isAncestorVisible(f) && !isParentFragment(f) && isVisible(f)) {
                // 如果父Fragment可见
                // 并且本身不是父Fragment
                // 并且本身可见，则进行统计
                track(f);
            }
        }
    }

    @Override
    public void onFragmentVisibilityChanged(boolean visible, Fragment f) {
        if (visible) {
            if (f != null) {
                // 由于内嵌的Fragment不会触发onHiddenChange()和setUserVisibleHint()方法，故此处只能根据其父Fragment来判断
                List<Fragment> fragments = findVisibleChildren(f);
                for (Fragment fragment : fragments) {
                    track(fragment);
                }
            }
        } else {
            removeHiddenFragment(f);
            trackedRefs.remove(f);
        }
    }

    @Override
    public void onFragmentPaused(FragmentManager fm, Fragment f) {
        for (int i = 0; i < refs.size(); i++) {
            if (refs.get(i).get() == f) {
                refs.remove(f);
                break;
            }
        }
        trackedRefs.remove(f);
    }

    private void removeHiddenFragment(Fragment f) {
        if (!f.isAdded()) {
            return;
        }
        if (isParentFragment(f)) {
            List<Fragment> fragments = f.getChildFragmentManager().getFragments();
            if (fragments != null && fragments.size() > 0) {
                for (int i = 0; i < fragments.size(); i++) {
                    if (trackedRefs.get(fragments.get(i)) != null) {
                        trackedRefs.remove(fragments.get(i));
                        removeHiddenFragment(fragments.get(i));
                    }
                }
            }
        }

    }


    private void track(Fragment fragment) {
        if (trackedRefs.get(fragment) != null && trackedRefs.get(fragment) == true) {
            return;
        }

        AutoTrackInfo autoTrackInfo = new AutoTrackInfo();
        autoTrackInfo.setOt(AppViewScreen.APPPAGEFOREGROUND.getEventType().getEventValue());
        autoTrackInfo.setS(AppViewScreen.APPPAGEFOREGROUND.getAppStatus());
        AutoTrackDBManager.getInstance().saveFragmentViewScreenEvent(fragment, autoTrackInfo);


        trackedRefs.put(fragment, true);


    }

    /**
     * 根据一个Fragment，从[refs]中查找其所有的子Fragment/子孙Fragment
     *
     * @param parent 要查找的父/祖先Fragment
     * @return 查找到的Fragment，如果不存在Fragment，则返回的列表元素数量为0
     */
    private List<Fragment> findVisibleChildren(Fragment parent) {

        List<Fragment> children = new ArrayList<>();

        for (int i = 0; i < refs.size(); i++) {

            WeakReference<Fragment> weakReference = refs.get(i);
            Fragment child = weakReference.get();
            if (child != null && checkParent(child, parent)) {
                if (!child.isHidden() && child.getUserVisibleHint() && isAncestorVisible(child)) {
                    children.add(child);
                }
            }

        }

        if (children.isEmpty()) {
            children.add(parent);
        }

        return children;
    }

    /**
     * 判断一个Fragment是否可见
     *
     * @param f 要判断的Fragment
     * @return 在Fragment的[Fragment.isHidden]为false，并且[Fragment.getUserVisibleHint]为true时，才返回true；否则false
     */
    private boolean isVisible(Fragment f) {

        return !f.isHidden() && f.getUserVisibleHint();
    }

    /**
     * 判断父Fragment是否可见
     *
     * @return 父Fragment不存在时，直接返回true；父Fragment可见时返回true；其他情况时返回false
     */
    private boolean isParentVisible(Fragment f) {
        Fragment parent = f.getParentFragment();
        if (parent == null) {
            return true;
        } else {
            return !parent.isHidden() && parent.getUserVisibleHint();
        }
    }


    /**
     * 检查一个Fragment的祖先是否都可见
     *
     * @param f 要检查的Fragment
     * @return 如果祖先都可见则返回true；如果不存在祖先（其直接宿主为Activity），则返回true；否则返回false
     */
    private boolean isAncestorVisible(Fragment f) {
        Fragment parent = f.getParentFragment();

        if (parent == null) {
            return true;
        } else if (!parent.isHidden() && parent.getUserVisibleHint()) {
            return isAncestorVisible(parent);
        } else {
            return false;
        }
    }


    /**
     * 检查一个[parent]是否是[child]的父Fragment/祖先Fragment
     */
    private boolean checkParent(Fragment child, Fragment parent) {
        Fragment parentFragment = child.getParentFragment();
        if (parentFragment != null) {
            if (parentFragment == parent) {
                return true;
            } else {
                return checkParent(parentFragment, parent);
            }
        } else {
            return false;
        }
    }

    /**
     * 检查一个fragment是否是ParentFragment
     *
     * @param fragment
     * @return
     */
    private boolean isParentFragment(Fragment fragment) {
        List<Fragment> fragments = fragment.getChildFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            return true;
        }

        return false;
    }
}
