package com.xiaoma.shop.business.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.business.model.FlowBean;
import com.xiaoma.shop.business.model.FlowItemForCash;
import com.xiaoma.shop.business.model.ScoreProductBean;
import com.xiaoma.shop.business.model.ScoreProductBean.ProductInfoBean.ChildProductBean;
import com.xiaoma.shop.business.repository.FlowRepository;
import com.xiaoma.shop.common.callback.HandleCallback;

import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2019/01/24
 *     desc   :
 * </pre>
 */
public class FlowVm extends BaseViewModel {

    private MutableLiveData<XmResource<List<ChildProductBean>>> mChildProductBeans;
    private MutableLiveData<XmResource<FlowBean>> mFlowMarginBean;
    private MutableLiveData<ScoreProductBean> mScoreProductBean;
    private MutableLiveData<XmResource<Object>> mQrCodeBean;

    private MutableLiveData<XmResource<List<FlowItemForCash>>> mFlowItemForCachLiveData;


    private FlowRepository mRepository;

    public FlowVm(@NonNull Application application) {
        super(application);
        mRepository = new FlowRepository();
    }

    public MutableLiveData<ScoreProductBean> getScoreProductBean() {
        if (mScoreProductBean == null) {
            mScoreProductBean = new MutableLiveData<>();

        }
        return mScoreProductBean;
    }

    public MutableLiveData<XmResource<List<ChildProductBean>>> getTrafficMall() {
        if (mChildProductBeans == null) {
            mChildProductBeans = new MutableLiveData<>();
        }
        return mChildProductBeans;
    }

    public MutableLiveData<XmResource<Object>> getFlowByCarCoin() {
        if (mQrCodeBean == null) {
            mQrCodeBean = new MutableLiveData<>();
        }
        return mQrCodeBean;
    }


    public MutableLiveData<XmResource<FlowBean>> getFlowMarginBean() {
        if (mFlowMarginBean == null) {
            mFlowMarginBean = new MutableLiveData<>();
        }
        return mFlowMarginBean;
    }

    public MutableLiveData<XmResource<List<FlowItemForCash>>> getFlowItemForCash() {
        if (mFlowItemForCachLiveData == null) {
            mFlowItemForCachLiveData = new MutableLiveData<>();
        }
        return mFlowItemForCachLiveData;
    }

    public void fetchTrafficMall() {
        mRepository.fetchTrafficMall( new HandleCallback<XmResource<List<ChildProductBean>>>() {
            @Override
            public void handleResult(XmResource<List<ChildProductBean>> listXmResource) {
                getTrafficMall().setValue(listXmResource);
            }
        }, new HandleCallback<ScoreProductBean>() {
            @Override
            public void handleResult(ScoreProductBean bean) {
                getScoreProductBean().setValue(bean);
            }
        });
    }

    public void fetchFlowMarginBean(String vin) {
        mRepository.fetchFlowMarginBean(vin,  new HandleCallback<XmResource<FlowBean>>() {
            @Override
            public void handleResult(XmResource<FlowBean> flowBeanXmResource) {
                getFlowMarginBean().setValue(flowBeanXmResource);
            }
        });
    }

    public void fetchTrafficMallFromUnicom() {
        mRepository.fetchTrafficMallFromUnicom( new HandleCallback<XmResource<List<FlowItemForCash>>>() {
            @Override
            public void handleResult(XmResource<List<FlowItemForCash>> listXmResource) {
                getFlowItemForCash().setValue(listXmResource);
            }
        });
    }
}
