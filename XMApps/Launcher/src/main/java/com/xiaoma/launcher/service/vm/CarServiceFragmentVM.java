package com.xiaoma.launcher.service.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.launcher.service.model.ServiceBean;
import com.xiaoma.model.XmResource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taojin
 * @date 2019/1/10
 */
public class CarServiceFragmentVM extends BaseViewModel {
    private MutableLiveData<XmResource<List<ServiceBean>>> mCars;

    public CarServiceFragmentVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<ServiceBean>>> getmTravels() {
        if (mCars == null) {
            mCars = new MutableLiveData<>();
        }
        return mCars;
    }

    public void fetchCars() {
        getmTravels().setValue(XmResource.<List<ServiceBean>>loading());
        initCars();

    }

    private void initCars() {

        //TODO 测试数据
        List<ServiceBean> serviceBeans = new ArrayList<>();

//        serviceBeans.add(new ServiceBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1547121338743&di=d4fadeb6842ee5a1ac5d545ddf0c705c&imgtype=0&src=http%3A%2F%2Fbpic.ooopic.com%2F15%2F13%2F49%2F15134980-2492fe788f05aee8d8705078371ea9a2-1.jpg", "维修"));
//        serviceBeans.add(new ServiceBean("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2496287420,2800527968&fm=26&gp=0.jpg", "保养"));
//        serviceBeans.add(new ServiceBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1547121496624&di=29ee7ba37b7ceb691c2ef0dec550764e&imgtype=0&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170703%2F0b8a3745388e48cbb69b17fc58e33fea_th.jpg", "加油"));
        getmTravels().setValue(XmResource.success(serviceBeans));

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCars = null;
    }
}
