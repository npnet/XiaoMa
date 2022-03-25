package com.xiaoma.shop.business.vm;

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.shop.business.model.AbsChildThemeBean;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/24
 *     desc   :
 * </pre>
 */
public class ChildThemeVm extends BaseViewModel {

    public ChildThemeVm(@NonNull Application application) {
        super(application);
    }

    public List<AbsChildThemeBean> getMockData() {
        List<AbsChildThemeBean> list = new ArrayList<>();
        list.add(new AbsChildThemeBean("1"));
        list.add(new AbsChildThemeBean("2"));
        list.add(new AbsChildThemeBean("3"));
        list.add(new AbsChildThemeBean("4"));
        list.add(new AbsChildThemeBean("5"));
        list.add(new AbsChildThemeBean("6"));
        list.add(new AbsChildThemeBean("7"));
        list.add(new AbsChildThemeBean("8"));
        return list;
    }
}
