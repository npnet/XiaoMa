package com.xiaoma.service.order.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.service.common.manager.RequestManager;
import com.xiaoma.service.order.model.CityBean;
import com.xiaoma.service.order.model.OrderTime;
import com.xiaoma.service.order.model.PinyinComparator;
import com.xiaoma.service.order.model.ProgramBean;
import com.xiaoma.service.order.model.ProvinceBean;
import com.xiaoma.service.order.model.ShopBean;
import com.xiaoma.utils.CharacterParseUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Thomas on 2018/11/9 0009
 */

public class OrderVM extends AndroidViewModel {

    //预约时间段
    private MutableLiveData<XmResource<List<OrderTime>>> mOrderDates;
    //获取城市列表
    private MutableLiveData<XmResource<List<CityBean>>> cityListDates;
    //4s店列表
    private MutableLiveData<XmResource<List<ShopBean>>> shopListDates;
    //保养项目列表
    private MutableLiveData<XmResource<List<ProgramBean>>> programDates;
    //提交预约订单
    private MutableLiveData<XmResource<String>> commitOrderDates;

    public OrderVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<OrderTime>>> getOrderDates() {
        if (mOrderDates == null) {
            mOrderDates = new MutableLiveData<>();
        }
        return mOrderDates;
    }

    public MutableLiveData<XmResource<List<CityBean>>> getCityListDates() {
        if (cityListDates == null) {
            cityListDates = new MutableLiveData<>();
        }
        return cityListDates;
    }

    public MutableLiveData<XmResource<List<ShopBean>>> getShopListDates() {
        if (shopListDates == null) {
            shopListDates = new MutableLiveData<>();
        }
        return shopListDates;
    }

    public MutableLiveData<XmResource<List<ProgramBean>>> getProgramDates() {
        if (programDates == null) {
            programDates = new MutableLiveData<>();
        }
        return programDates;
    }

    public MutableLiveData<XmResource<String>> commitOrderDates() {
        if (commitOrderDates == null) {
            commitOrderDates = new MutableLiveData<>();
        }
        return commitOrderDates;
    }

    /**
     * 获取可预约的时间段
     */
    public void fetchAppointmentTime() {
        getOrderDates().setValue(XmResource.<List<OrderTime>>loading());
        RequestManager.getInstance().getAppointmentTime(new ResultCallback<XMResult<List<OrderTime>>>() {
            @Override
            public void onSuccess(XMResult<List<OrderTime>> result) {
                getOrderDates().setValue(XmResource.response(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getOrderDates().setValue(XmResource.<List<OrderTime>>error(code, msg));
            }
        });
    }

    /**
     * 获取城市列表数据
     */
    public void fetchCityList() {
        getCityListDates().setValue(XmResource.<List<CityBean>>loading());
        RequestManager.getInstance().getProvinceAndCity(new ResultCallback<XMResult<List<ProvinceBean>>>() {
            @Override
            public void onSuccess(XMResult<List<ProvinceBean>> result) {
                List<ProvinceBean> provinceBeanList = result.getData();
                if (provinceBeanList == null || provinceBeanList.isEmpty()) {
                    getCityListDates().setValue(XmResource.<List<CityBean>>error(result.getResultCode(),
                            result.getResultMessage()));
                    return;
                }

                List<CityBean> list = new ArrayList<>();
                for (int i = 0; i < provinceBeanList.size(); i++) {
                    List<ProvinceBean.ChildrenBean> childrenBeanList = provinceBeanList.get(i).getChildren();
                    setAllProvinceCity(childrenBeanList, list);
                }
                Collections.sort(list, new PinyinComparator());
                getCityListDates().setValue(XmResource.response(list));
            }

            @Override
            public void onFailure(int code, String msg) {
                getCityListDates().setValue(XmResource.<List<CityBean>>error(code, msg));
            }
        });
    }

    private void setAllProvinceCity(List<ProvinceBean.ChildrenBean> childrenBeanList, List<CityBean> list) {
        //获取每个省下面的城市列表
        for (int i = 0; i < childrenBeanList.size(); i++) {
            CityBean cityBean = new CityBean();
            //name
            String cityName = childrenBeanList.get(i).getText();
            cityBean.setName(cityName);
            //汉字转换成拼音
            String pinyin = CharacterParseUtil.getInstance().getSelling(cityName);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                cityBean.setLetters(sortString.toUpperCase());

            } else {
                cityBean.setLetters("#");
            }
            //多音字"长"处理
            if ("长".equals(cityName.substring(0, 1))) {
                cityBean.setLetters("C");
            }
            list.add(cityBean);
        }
    }

    /**
     * 获取4s列表
     */
    public void fetchShopList(String city) {
        getShopListDates().setValue(XmResource.<List<ShopBean>>loading());
        RequestManager.getInstance().queryDealerByCity(city, new ResultCallback<XMResult<List<ShopBean>>>() {
            @Override
            public void onSuccess(XMResult<List<ShopBean>> result) {
                getShopListDates().setValue(XmResource.response(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getShopListDates().setValue(XmResource.<List<ShopBean>>error(code, msg));
            }
        });
    }

    /**
     * 提交预约单
     */
    public void commitOrder(String typeId, String vin,String province, String city, String vDealer, String vDate,
                            String timePhase, final String realName, String mobile) {
        commitOrderDates.setValue(XmResource.<String>loading());
        RequestManager.getInstance().submitAppointmentOrder(typeId,vin, province, city, vDealer, vDate,
                timePhase, realName, mobile, new ResultCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> result) {
                        commitOrderDates().setValue(XmResource.response(result));
                    }

                    @Override
                    public void onFailure(int code, String msg) {
                        commitOrderDates().setValue(XmResource.<String>error(code, msg));
                    }
                });
    }

    /**
     * 获取项目列表
     */
    public void fetchProgramList() {
        getProgramDates().setValue(XmResource.<List<ProgramBean>>loading());
        RequestManager.getInstance().getFoursUpkeepOptions(new ResultCallback<XMResult<List<ProgramBean>>>() {
            @Override
            public void onSuccess(XMResult<List<ProgramBean>> result) {
                getProgramDates().setValue(XmResource.response(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getProgramDates().setValue(XmResource.<List<ProgramBean>>error(code, msg));
            }
        });
    }
}