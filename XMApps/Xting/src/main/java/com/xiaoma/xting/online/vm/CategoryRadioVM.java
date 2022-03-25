package com.xiaoma.xting.online.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaoma.model.XmResource;
import com.xiaoma.utils.CollectionUtil;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.online.model.ProvinceBean;
import com.xiaoma.xting.online.model.RadioBean;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.bean.XMDataCallback;
import com.xiaoma.xting.sdk.bean.XMProvinceList;
import com.xiaoma.xting.sdk.bean.XMRadioList;
import com.xiaoma.xting.sdk.bean.XMRadioListByCategory;

import java.util.List;

/**
 * @author KY
 * @date 2018/11/1
 */
public class CategoryRadioVM extends AndroidViewModel {
    private static final String CACHE_PREFIX = "CategoryRadioVM";
    private static final String PROVINCE_KEY = CACHE_PREFIX + "provinces";
    private static final String COUNTRY_KEY = CACHE_PREFIX + "Country";
    private static final String NETWORK_KEY = CACHE_PREFIX + "Network";
    private static final String PROVINCE_RADIO_KEY_PREFIX = CACHE_PREFIX + "province_radio";
    private MutableLiveData<XmResource<List<ProvinceBean>>> mProvinces;
    private MutableLiveData<XmResource<List<RadioBean>>> mProvinceRadios;
    private MutableLiveData<XmResource<List<RadioBean>>> mCategoryRadios;
    private MutableLiveData<XmResource<List<RadioBean>>> mCountryRadios;
    private MutableLiveData<XmResource<List<RadioBean>>> mNetworkRadios;

    public CategoryRadioVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<ProvinceBean>>> getProvinces() {
        if (mProvinces == null) {
            mProvinces = new MutableLiveData<>();
        }
        return mProvinces;
    }

    public MutableLiveData<XmResource<List<RadioBean>>> getProvinceRadios() {
        if (mProvinceRadios == null) {
            mProvinceRadios = new MutableLiveData<>();
        }
        return mProvinceRadios;
    }

    public MutableLiveData<XmResource<List<RadioBean>>> getCountryRadios() {
        if (mCountryRadios == null) {
            mCountryRadios = new MutableLiveData<>();
        }
        return mCountryRadios;
    }

    public MutableLiveData<XmResource<List<RadioBean>>> getNetworkRadios() {
        if (mNetworkRadios == null) {
            mNetworkRadios = new MutableLiveData<>();
        }
        return mNetworkRadios;
    }

    public MutableLiveData<XmResource<List<RadioBean>>> getCategoryRadios() {
        if (mCategoryRadios == null) {
            mCategoryRadios = new MutableLiveData<>();
        }
        return mCategoryRadios;
    }

    public void fetchProvinces() {
        getProvinces().setValue(XmResource.<List<ProvinceBean>>loading());
        List<ProvinceBean> caches = TPUtils.getList(getApplication(), PROVINCE_KEY, ProvinceBean[].class);
        if (!CollectionUtil.isListEmpty(caches)) {
            getProvinces().setValue(XmResource.success(caches));
        }
        OnlineFMFactory.getInstance().getSDK().getProvinces(new XMDataCallback<XMProvinceList>() {
            @Override
            public void onSuccess(@Nullable XMProvinceList data) {
                if (data != null) {
                    List<ProvinceBean> provinceBeans = ProvinceBean.convertFromProvince(data);
                    TPUtils.putList(getApplication(), PROVINCE_KEY, provinceBeans);
                    getProvinces().setValue(XmResource.response(provinceBeans));
                } else {
                    getProvinces().setValue(XmResource.<List<ProvinceBean>>failure(XtingConstants.ErrorMsg.NULL_DATA));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getProvinces().setValue(XmResource.<List<ProvinceBean>>error(code, msg));
            }
        });
    }

    public void fetchProvinceRadios(long provinceCode) {
        getProvinceRadios().setValue(XmResource.<List<RadioBean>>loading());
        final String cacheKey = PROVINCE_RADIO_KEY_PREFIX + provinceCode;
        List<RadioBean> caches = TPUtils.getList(getApplication(), cacheKey, RadioBean[].class);
        if (!CollectionUtil.isListEmpty(caches)) {
            getProvinceRadios().setValue(XmResource.success(caches));
        }
        OnlineFMFactory.getInstance().getSDK().getProvinceRadios(provinceCode, 1, new XMDataCallback<XMRadioList>() {
            @Override
            public void onSuccess(@Nullable XMRadioList data) {
                if (data != null) {
                    List<RadioBean> provinceBeans = RadioBean.convertFromRadio(data.getRadios());
                    TPUtils.putList(getApplication(), cacheKey, provinceBeans);
                    getProvinceRadios().setValue(XmResource.response(provinceBeans));
                } else {
                    getProvinceRadios().setValue(XmResource.<List<RadioBean>>failure("null"));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getProvinceRadios().setValue(XmResource.<List<RadioBean>>error(code, msg));
            }
        });
    }

    public void fetchCountryRadios() {
        getCountryRadios().setValue(XmResource.<List<RadioBean>>loading());
        List<RadioBean> caches = TPUtils.getList(getApplication(), COUNTRY_KEY, RadioBean[].class);
        if (!CollectionUtil.isListEmpty(caches)) {
            getCountryRadios().setValue(XmResource.success(caches));
        }
        OnlineFMFactory.getInstance().getSDK().getCountryRadios(1, new XMDataCallback<XMRadioList>() {
            @Override
            public void onSuccess(@Nullable XMRadioList data) {
                if (data != null) {
                    List<RadioBean> provinceBeans = RadioBean.convertFromRadio(data.getRadios());
                    TPUtils.putList(getApplication(), COUNTRY_KEY, provinceBeans);
                    getCountryRadios().setValue(XmResource.response(provinceBeans));
                } else {
                    getCountryRadios().setValue(XmResource.<List<RadioBean>>failure("null"));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getCountryRadios().setValue(XmResource.<List<RadioBean>>error(code, msg));
            }
        });
    }

    public void fetchNetworkRadios() {
        getNetworkRadios().setValue(XmResource.<List<RadioBean>>loading());
        List<RadioBean> caches = TPUtils.getList(getApplication(), NETWORK_KEY, RadioBean[].class);
        if (!CollectionUtil.isListEmpty(caches)) {
            getNetworkRadios().setValue(XmResource.success(caches));
        }
        OnlineFMFactory.getInstance().getSDK().getNetworkRadios(1, new XMDataCallback<XMRadioList>() {
            @Override
            public void onSuccess(@Nullable XMRadioList data) {
                if (data != null) {
                    List<RadioBean> provinceBeans = RadioBean.convertFromRadio(data.getRadios());
                    TPUtils.putList(getApplication(), NETWORK_KEY, provinceBeans);
                    getNetworkRadios().setValue(XmResource.response(provinceBeans));
                } else {
                    getNetworkRadios().setValue(XmResource.<List<RadioBean>>failure("null"));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getNetworkRadios().setValue(XmResource.<List<RadioBean>>error(code, msg));
            }
        });
    }

    public void fetchCategoryRadios(long categoryId) {
        getCategoryRadios().setValue(XmResource.<List<RadioBean>>loading());
        final String cacheKey = PROVINCE_RADIO_KEY_PREFIX + categoryId;
        List<RadioBean> caches = TPUtils.getList(getApplication(), cacheKey, RadioBean[].class);
        if (!CollectionUtil.isListEmpty(caches)) {
            getCategoryRadios().setValue(XmResource.success(caches));
        }
        OnlineFMFactory.getInstance().getSDK().getRadiosByCategory(categoryId, 1, new XMDataCallback<XMRadioListByCategory>() {
            @Override
            public void onSuccess(@Nullable XMRadioListByCategory data) {
                if (data != null) {
                    List<RadioBean> provinceBeans = RadioBean.convertFromRadio(data.getRadios());
                    TPUtils.putList(getApplication(), cacheKey, provinceBeans);
                    getCategoryRadios().setValue(XmResource.response(provinceBeans));
                } else {
                    getCategoryRadios().setValue(XmResource.<List<RadioBean>>failure("null"));
                }
            }

            @Override
            public void onError(int code, String msg) {
                getCategoryRadios().setValue(XmResource.<List<RadioBean>>error(code, msg));
            }
        });
    }
}
