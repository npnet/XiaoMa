package com.xiaoma.shop.business.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.XmResource;
import com.xiaoma.shop.business.model.BoughtVehicleSoundBean;
import com.xiaoma.shop.business.model.VehicleSoundBean;
import com.xiaoma.shop.common.constant.LoadMoreState;
import com.xiaoma.shop.common.util.VehicleSoundUtils;
import com.xiaoma.thread.ThreadDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/28
 * @Describe:
 */

public class BoughtVehicleSoundVm extends AndroidViewModel {

    private int mTotalCount = 20;//总数量
    private int mTotalPageNum = 10;//总页数
    private int mPageCount = 10;//每页请求数
    private int mCurPageNum;//当前页数

    private MutableLiveData<XmResource<List<BoughtVehicleSoundBean>>> mVehicleSoundBeanLiveDatas;
    private MutableLiveData<Integer> mLoadStatusLiveData;
    public BoughtVehicleSoundVm(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<XmResource<List<BoughtVehicleSoundBean>>> getVehicleSoundBeanLiveDatas() {
        if(mVehicleSoundBeanLiveDatas == null){
            mVehicleSoundBeanLiveDatas = new MutableLiveData<>();
        }
        return mVehicleSoundBeanLiveDatas;
    }


    public MutableLiveData<Integer> getLoadStatus() {
        if (mLoadStatusLiveData == null) {
            mLoadStatusLiveData = new MutableLiveData<>();
        }
        return mLoadStatusLiveData;
    }
    public void getBoughtDatas() {
        final List<BoughtVehicleSoundBean> soundBeans = new ArrayList<>();
        for (int i = 0; i < mPageCount; i++) {
            BoughtVehicleSoundBean bean = new BoughtVehicleSoundBean();
            bean.setAppName("app_name_xxxxxxxxxxxxxxxxx");
            bean.setName("name_xxxxxxxxxxx");
            bean.setUsedNum(10);
            bean.setDefaultShowNum(20);
            bean.setUrl("url" + i);
            switch (VehicleSoundUtils.getRandomNum()) {
                case 0: //都可购买
                    bean.setPrice(20d);
                    bean.setDiscountPrice(10d);
                    bean.setScorePrice(10);
                    bean.setDiscountScorePrice(5);
                    break;
                case 1:// 车币购买
                    bean.setScorePrice(10);
                    bean.setDiscountScorePrice(5);
                    break;
                case 2:// 现金购买
                    bean.setPrice(20d);
                    bean.setDiscountPrice(10d);
                    break;
                case 3: // 已经购买
                    bean.setDiscountScorePrice(5);
                    bean.setDiscountPrice(10d);
                    bean.setBuy(true);
                    break;

            }
            soundBeans.add(bean);
        }
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                getVehicleSoundBeanLiveDatas().postValue(XmResource.response(soundBeans));
                getLoadStatus().postValue(judgeLoadStatus());
            }
        }, 800);
    }
    /**
     * 判断加载状态
     * * @return {@link LoadMoreState}
     */
    private int judgeLoadStatus() {
        int result = 0;
        if (mTotalCount > (mCurPageNum + 1) * mPageCount) {//未加载完毕
            result = LoadMoreState.COMPLETE;
        } else { // 全部加载完
            result = LoadMoreState.END;
        }
        return result;
    }
}
