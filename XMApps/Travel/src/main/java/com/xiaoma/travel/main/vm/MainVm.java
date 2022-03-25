package com.xiaoma.travel.main.vm;

import android.app.Application;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.travel.R;
import com.xiaoma.travel.main.model.MainTabBean;
import com.xiaoma.travel.view.citypick.model.City;
import com.xiaoma.travel.view.citypick.utils.DBManager;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/11/06
 *     desc   :
 * </pre>
 */
public class MainVm extends BaseViewModel {

    public MainVm(@NonNull Application application) {
        super(application);
    }

    public List<MainTabBean> getTabs() {
        List<MainTabBean> list = new ArrayList<>();
        list.add(new MainTabBean("酒店", R.drawable.test));
        list.add(new MainTabBean("美食", R.drawable.test));
        list.add(new MainTabBean("景点", R.drawable.test));
        list.add(new MainTabBean("电影", R.drawable.test));
        list.add(new MainTabBean("停车", R.drawable.test));
        return list;
    }

    public List<City> getCityList() {
        DBManager dbManager = new DBManager(getApplication());
        dbManager.copyDBFile();
        return dbManager.getAllCities();
    }
}
