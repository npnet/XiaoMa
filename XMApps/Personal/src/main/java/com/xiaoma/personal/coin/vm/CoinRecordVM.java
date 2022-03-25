package com.xiaoma.personal.coin.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.PageWrapper;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.personal.coin.model.CoinAndSignInfo;
import com.xiaoma.personal.coin.model.CoinRecord;
import com.xiaoma.personal.common.RequestManager;

import java.util.List;

/**
 * @author Gillben
 * date: 2018/12/07
 * <p>
 * 请求车币记录列表
 */
public class CoinRecordVM extends BaseViewModel {

    public CoinRecordVM(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<XmResource<Integer>> mCarCoin;
    private MutableLiveData<XmResource<List<CoinRecord>>> mCoinRecords;

    public MutableLiveData<XmResource<Integer>> getCarCoin() {
        if (mCarCoin == null) {
            mCarCoin = new MutableLiveData<>();
        }
        return mCarCoin;
    }

    public MutableLiveData<XmResource<List<CoinRecord>>> getCoinRecords() {
        if (mCoinRecords == null) {
            mCoinRecords = new MutableLiveData<>();
        }
        return mCoinRecords;
    }

    public void fetchCarCoin() {
        getCarCoin().setValue(XmResource.<Integer>loading());
        RequestManager.getUserCarCoin(new ResultCallback<XMResult<CoinAndSignInfo>>() {
            @Override
            public void onSuccess(XMResult<CoinAndSignInfo> model) {
                if (model.isSuccess() && model.getData() != null) {
                    getCarCoin().setValue(XmResource.success(model.getData().getCarCoin()));
                } else {
                    getCarCoin().setValue(XmResource.<Integer>error(model.getResultMessage()));
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                getCarCoin().setValue(XmResource.<Integer>error(code, msg));
            }
        });
    }

    public void fetchCoinRecords(int page) {
        getCoinRecords().setValue(XmResource.<List<CoinRecord>>loading());
        RequestManager.getCoinRecordList(new ResultCallback<XMResult<PageWrapper<CoinRecord>>>() {
            @Override
            public void onSuccess(XMResult<PageWrapper<CoinRecord>> model) {
                if (model.isSuccess() && model.getData() != null) {
                    getCoinRecords().setValue(XmResource.success(model.getData().getList()));
                } else {
                    getCoinRecords().setValue(XmResource.<List<CoinRecord>>error(model.getResultMessage()));
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                getCoinRecords().setValue(XmResource.<List<CoinRecord>>error(code, msg));
            }
        }, page);
    }

}
