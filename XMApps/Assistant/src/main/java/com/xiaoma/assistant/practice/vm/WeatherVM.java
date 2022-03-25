package com.xiaoma.assistant.practice.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.assistant.manager.RequestManager;
import com.xiaoma.assistant.practice.util.PinyinComparator;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.pratice.CityBean;
import com.xiaoma.model.pratice.ProvinceBean;
import com.xiaoma.utils.CharacterParseUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author taojin
 * @date 2019/6/5
 */
public class WeatherVM extends BaseViewModel {

    //获取城市列表
    private MutableLiveData<XmResource<List<CityBean>>> cityListDatas;

    public WeatherVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<CityBean>>> getCityListDatas() {

        if (cityListDatas == null) {
            cityListDatas = new MutableLiveData<>();
        }

        return cityListDatas;
    }


    /**
     * 获取城市列表数据
     */
    public void fetchCityList() {
        getCityListDatas().setValue(XmResource.<List<CityBean>>loading());
        RequestManager.newSingleton().getProvinceAndCity(new ResultCallback<XMResult<List<ProvinceBean>>>() {
            @Override
            public void onSuccess(XMResult<List<ProvinceBean>> result) {
                List<ProvinceBean> provinceBeanList = result.getData();
                if (provinceBeanList == null || provinceBeanList.isEmpty()) {
                    getCityListDatas().setValue(XmResource.<List<CityBean>>error(result.getResultCode(),
                            result.getResultMessage()));
                    return;
                }
                List<CityBean> list = new ArrayList<>();
                for (int i = 0; i < provinceBeanList.size(); i++) {
                    List<ProvinceBean.ChildrenRegion> childrenBeanList = provinceBeanList.get(i).getChildrenRegions();
                    setAllProvinceCity(childrenBeanList, list);
                }
                Collections.sort(list, new PinyinComparator());
                getCityListDatas().setValue(XmResource.response(list));
            }

            @Override
            public void onFailure(int code, String msg) {
                getCityListDatas().setValue(XmResource.<List<CityBean>>error(code, msg));
            }
        });
    }


    private void setAllProvinceCity(List<ProvinceBean.ChildrenRegion> childrenBeanList, List<CityBean> list) {
        //获取每个省下面的城市列表
        for (int i = 0; i < childrenBeanList.size(); i++) {
            CityBean cityBean = new CityBean();
            //name
            String cityName = childrenBeanList.get(i).getName();
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
}
