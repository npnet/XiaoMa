package com.xiaoma.xting.common;

import android.support.v4.app.FragmentManager;

import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.component.nodejump.ChildNode;
import com.xiaoma.utils.BackHandlerHelper;
import com.xiaoma.utils.FragmentUtils;

/**
 * @author KY
 * @date 12/21/2018
 */
public class VisibilityFragment extends VisibleFragment implements BackHandlerHelper.FragmentBackHandler, ChildNode {

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

    protected boolean popBack() {
        FragmentManager fm = getFragmentManager();
        if (fm != null) {
            FragmentUtils.pop(fm);
            return true;
        } else {
            return false;
        }
    }
}
