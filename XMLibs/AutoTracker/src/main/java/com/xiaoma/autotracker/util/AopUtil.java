package com.xiaoma.autotracker.util;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2018/12/6 0006
 * 获取当前context所在的activity路径
 */

public class AopUtil {

    public static Object getAttachedHost(View target) {
        if (target == null) {
            return null;
        }
        final Context context = target.getContext();
        if (!(context instanceof Activity)) {
            return null;
        }
        if (context instanceof FragmentActivity) {
            final FragmentActivity fmtAct = (FragmentActivity) context;
            final List<Fragment> fragmentList = fmtAct.getSupportFragmentManager().getFragments();
            if (fragmentList != null && !fragmentList.isEmpty()) {
                for (final Fragment f : fragmentList) {
                    final Fragment find = traversalFragment(f, target);
                    if (find != null) {
                        return find;
                    }
                }
            }
        }
        return traversalActivity((Activity) context);
    }

    private static Fragment traversalFragment(@NonNull Fragment fragment, @NonNull View target) {
        final List<Fragment> fragmentList = fragment.getChildFragmentManager().getFragments();
        if (fragmentList != null && !fragmentList.isEmpty()) {
            for (Fragment f : fragmentList) {
                final Fragment find = traversalFragment(f, target);
                if (find != null) {
                    return find;
                }
            }
        }
        return traversalView(fragment.getView(), target) ? fragment : null;
    }

    private static Activity traversalActivity(@NonNull Activity act) {
        return act;
    }

    private static Activity traversalActivity(@NonNull Activity act, @NonNull View target) {
        final View contentView = act.getWindow().getDecorView().findViewById(android.R.id.content);
        return traversalView(contentView, target) ? act : null;
    }

    private static boolean traversalView(View parent, View target) {
        if (parent != null && parent == target)
            return true;
        ViewParent targetParent = target.getParent();
        while (targetParent != null) {
            if (targetParent == parent)
                return true;
            targetParent = targetParent.getParent();
        }
        return false;
    }

    public static String getActivityNameFromContext(Context context) {
        Activity activity = null;
        try {
            if (context != null) {
                if (context instanceof Activity) {
                    activity = (Activity) context;
                } else if (context instanceof ContextWrapper) {
                    while (!(context instanceof Activity) && context instanceof ContextWrapper) {
                        context = ((ContextWrapper) context).getBaseContext();
                    }
                    if (context instanceof Activity) {
                        activity = (Activity) context;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (activity == null) {
            return "";
        } else {
            return activity.getClass().getCanonicalName();
        }
    }

    public static FragmentActivity getActivityFromContext(Context context) {
        FragmentActivity activity = null;
        try {
            if (context != null) {
                if (context instanceof FragmentActivity) {
                    activity = (FragmentActivity) context;
                } else if (context instanceof ContextWrapper) {
                    while (!(context instanceof FragmentActivity) && context instanceof ContextWrapper) {
                        context = ((ContextWrapper) context).getBaseContext();
                    }
                    if (context instanceof FragmentActivity) {
                        activity = (FragmentActivity) context;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }

    private List<View> getAllChildViews(View view) {
        List<View> allChildren = new ArrayList<>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewChild = vp.getChildAt(i);
                allChildren.add(viewChild);
                allChildren.addAll(getAllChildViews(viewChild));
            }
        }
        return allChildren;
    }

    //此方法保留
    protected boolean checkPageDescFromClickView(View view) {
        boolean flag = false;
        //FragmentActivity activityFromContext = AopUtil.getActivityFromContext(context);
        //if (activityFromContext == null) {
        //    KLog.e(TAG, "activityFromContext is null...");
        //    return false;
        //}
        //List<Fragment> fragments = activityFromContext.getSupportFragmentManager().getFragments();
        //out:for (Fragment fragment : fragments) {
        //    View fragmentView = fragment.getView();
        //    if (fragmentView == null && fragment instanceof DialogFragment) {
        //        fragmentView = ((DialogFragment) fragment).getDialog().getWindow().getDecorView();
        //    }
        //    List<View> allChildViews = getAllChildViews(fragmentView);
        //    for (View allChildView : allChildViews) {
        //        if (allChildView == view) {
        //            Class<? extends Fragment> fragmentClass = fragment.getClass();
        //            pageUIPath = fragment.getClass().getCanonicalName();
        //            PageDescComponent pageDescComponent = fragmentClass.getAnnotation(PageDescComponent.class);
        //            if (pageDescComponent == null) {
        //                implementsPageDescComponentAnnotationError(pageUIPath);
        //                continue ;
        //            }
        //            String value = pageDescComponent.value();
        //            if (TextUtils.isEmpty(value)) {
        //                implementsPageDescComponentAnnotationError(pageUIPath);
        //                continue ;
        //            }
        //            pageUIPathDesc = value;
        //            flag = true;
        //            KLog.e("lmh fragment: " + pageUIPath + " -- " + pageUIPathDesc);
        //            break out;
        //        }
        //    }
        //}
        //if (!flag) {
        //    Class<? extends FragmentActivity> activityFromContextClass = activityFromContext.getClass();
        //    pageUIPath = activityFromContext.getClass().getCanonicalName();
        //    PageDescComponent pageDescComponent = activityFromContextClass.getAnnotation(PageDescComponent.class);
        //    if (pageDescComponent == null) {
        //        implementsPageDescComponentAnnotationError(pageUIPath);
        //        return false;
        //    }
        //    String value = pageDescComponent.value();
        //    if (TextUtils.isEmpty(value)) {
        //        implementsPageDescComponentAnnotationError(pageUIPath);
        //        return false;
        //    }
        //    pageUIPathDesc = value;
        //    flag = true;
        //    KLog.e("lmh activityFromContext: " + pageUIPath + " -- " + pageUIPathDesc);
        //}
        return flag;
    }

}
