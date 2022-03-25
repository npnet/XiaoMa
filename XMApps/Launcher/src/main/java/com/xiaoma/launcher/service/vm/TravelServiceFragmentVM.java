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
public class TravelServiceFragmentVM extends BaseViewModel {
    private MutableLiveData<XmResource<List<ServiceBean>>> mTravels;

    public TravelServiceFragmentVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<ServiceBean>>> getmTravels() {
        if (mTravels == null) {
            mTravels = new MutableLiveData<>();
        }
        return mTravels;
    }

    public void fetchTravels() {
        getmTravels().setValue(XmResource.<List<ServiceBean>>loading());
        initTravels();

    }

    private void initTravels() {

        //TODO 测试数据
        List<ServiceBean> serviceBeans = new ArrayList<>();

//        serviceBeans.add(new ServiceBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1547714714&di=3bb721d9a852679d4b0c10bec5a1ad2a&imgtype=jpg&er=1&src=http%3A%2F%2Fimg.article.pchome.net%2F00%2F23%2F22%2F32%2Fpic_lib%2Fs960x639%2F10s960x639.jpg", "美食"));
//        serviceBeans.add(new ServiceBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1547120054019&di=f1d82e0d431ffb552604abf15026509b&imgtype=0&src=http%3A%2F%2Fwww.21cbr.com%2Fuploads%2Fallimg%2F151125%2F35660-15112511033Y19.png", "酒店"));
//        serviceBeans.add(new ServiceBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1547120099052&di=4094663f730372f7cf8f3ec6514c3598&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fitbbs%2F1504%2F01%2Fc34%2F4747354_1427866186246_mthumb.jpg", "景点"));
//        serviceBeans.add(new ServiceBean("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1547714843&di=ef7784ba9002cea7bae0c4e805397a8a&imgtype=jpg&er=1&src=http%3A%2F%2Fimgsrc.baidu.com%2Fimage%2Fc0%253Dshijue1%252C0%252C0%252C294%252C40%2Fsign%3D9a0918d431c79f3d9becec73d2c8a764%2Ff9dcd100baa1cd1121bf2f0bb312c8fcc3ce2d8b.jpg", "电影"));
//        serviceBeans.add(new ServiceBean("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3835928042,3993558076&fm=26&gp=0.jpg", "停车"));
//
        getmTravels().setValue(XmResource.success(serviceBeans));

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mTravels = null;
    }
}
