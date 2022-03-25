package com.xiaoma.shop.business.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.shop.business.model.VehicleSoundBean;
import com.xiaoma.shop.business.model.VehicleSoundEntity;
import com.xiaoma.shop.common.RequestManager;
import com.xiaoma.shop.common.constant.LoadMoreState;
import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.constant.ThemeContract;
import com.xiaoma.shop.common.constant.VehicleSoundType;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/20
 * @Describe:
 */

public class VehicleSoundVm extends AndroidViewModel {

    private int mTotalCount = 20;//总数量
    private int mTotalPageNum = 10;//总页数
    private int mPageCount = 10;//每页请求数
    private int mCurPageNum;//当前页数

    private @ThemeContract.SortRule
    String mCurSortRule;
    private @ShopContract.PriceType
    int mPriceType;
    private String mCurrentVehicleSoundType;
    private int mDeploy;

    public VehicleSoundVm(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<XmResource<List<VehicleSoundEntity.SoundEffectListBean>>> mListMutableLiveData;
    private MutableLiveData<XmResource<List<VehicleSoundBean>>> mVehicleSoundBeans;
    private MutableLiveData<Integer> mLoadStatusLiveData;

    public MutableLiveData<Integer> getLoadStatus() {
        if (mLoadStatusLiveData == null) {
            mLoadStatusLiveData = new MutableLiveData<>();
        }
        return mLoadStatusLiveData;
    }

    public MutableLiveData<XmResource<List<VehicleSoundBean>>> getVehicleSoundBeans() {
        if (mVehicleSoundBeans == null) {
            mVehicleSoundBeans = new MutableLiveData<>();
        }
        return mVehicleSoundBeans;
    }

    public MutableLiveData<XmResource<List<VehicleSoundEntity.SoundEffectListBean>>> getVehicleSoundEntity() {
        if (mListMutableLiveData == null) {
            mListMutableLiveData = new MutableLiveData<>();
        }
        return mListMutableLiveData;
    }

    /**
     * 获取整车音效产品数据
     *
     * @param productType 产品类型{@link VehicleSoundType.ProductType}
     * @param sortRule    排序规则{@link ThemeContract}
     * @param pageNum     页数
     * @param type        金币还是现金
     * @param deploy      高低配 类型 0低配仪表 1高配仪表
     */
    public void fetchVehicleSoundBeans(@VehicleSoundType.ProductType String productType,
                                       @ThemeContract.SortRule String sortRule,
                                       int pageNum,
                                       int type,
                                       int deploy) {
        this.mCurrentVehicleSoundType = productType;
        this.mCurSortRule = sortRule;
        this.mPriceType = type;
        this.mDeploy = deploy;
        RequestManager.requestVehicleSound(productType, sortRule, pageNum, mPageCount, type, deploy, new ResultCallback<XMResult<VehicleSoundEntity>>() {
            @Override
            public void onSuccess(XMResult<VehicleSoundEntity> result) {
                handleVehicleSoundEntiry(result);
            }

            @Override
            public void onFailure(int code, String msg) {
                getVehicleSoundEntity().setValue(XmResource.<List<VehicleSoundEntity.SoundEffectListBean>>error(code, msg));
                getLoadStatus().setValue(LoadMoreState.FAIL);
            }
        });
    }

    private void handleVehicleSoundEntiry(XMResult<VehicleSoundEntity> result) {
        if (result != null && result.getData() != null) {
            VehicleSoundEntity data = result.getData();
            List<VehicleSoundEntity.SoundEffectListBean> dataList;
            if (VehicleSoundType.ProductType.AUDIO_SOUND.equals(mCurrentVehicleSoundType)) {
                dataList = data.getSoundEffectList();
            } else if (VehicleSoundType.ProductType.INSTRUMENT_SOUND.equals(mCurrentVehicleSoundType)) {
                dataList = data.getInstrumentList();
            } else {
                dataList = new ArrayList<>();
            }
            if (!ListUtils.isEmpty(dataList)) {
                VehicleSoundEntity.PageInfoBean pageInfo = data.getPageInfo();
                getVehicleSoundEntity().setValue(XmResource.response(dataList));
                if (pageInfo == null) {
                    handleEnd();
                } else {
                    mTotalCount = pageInfo.getTotalRecord();//总数量
                    mTotalPageNum = pageInfo.getTotalPage();//总页数
                    mCurPageNum = pageInfo.getPageNum();//当前页数
                    getLoadStatus().setValue(judgeLoadStatus());

                }

            } else {
                handleFailure();
            }
        } else {
            handleFailure();
        }
    }

    private void changeTheData(List<VehicleSoundEntity.SoundEffectListBean> dataList) {
        List<VehicleSoundEntity.SoundEffectListBean> listBeans = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            VehicleSoundEntity.SoundEffectListBean bean = dataList.get(i);
            bean.setBuy(true);
            if (bean.getThemeName().equals("音效1")) {
                bean.setDownloadNum(1000000);
                bean.setFilePath("http://dldir1.qq.com/weixin/android/weixin704android1420.apk");
                bean.setSize(101607014);
                bean.setDiscountPrice(0);
                bean.setDiscountScorePrice(0);
                bean.setAuditionPath("http://ydown.smzy.com/yinpin/2018-11/smzy_2018111509.mp3");
            } else if (bean.getThemeName().equals("音效2")) {
                bean.setDownloadNum(1000000);
                bean.setFilePath("http://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk");
                bean.setSize(71276191);
                bean.setDiscountPrice(0);
                bean.setDiscountScorePrice(0);
                bean.setAuditionPath("http://ydown.smzy.com/yinpin/2018-11/smzy_2018111509.mp3");
            } else if (bean.getThemeName().equals("音效3")) {
                bean.setDownloadNum(1000000);
                bean.setFilePath("http://sqdd.myapp.com/myapp/qqteam/tim/down/tim.apk");
                bean.setSize(54417598);
                bean.setDiscountPrice(0);
                bean.setDiscountScorePrice(0);
                bean.setAuditionPath("http://ydown.smzy.com/yinpin/2018-11/smzy_2018111509.mp3");
            } else {
                bean.setFilePath(bean.getFilePath() + i);
                listBeans.add(bean);
            }
        }
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
        dataList.addAll(listBeans);
    }

    private void handleEnd() {
        getLoadStatus().setValue(LoadMoreState.END);
    }

    /**
     * 失败处理
     */
    private void handleFailure() {
        getVehicleSoundEntity().setValue(XmResource.<List<VehicleSoundEntity.SoundEffectListBean>>failure("未获取到数据"));
        getLoadStatus().setValue(LoadMoreState.FAIL);
    }

    int test = 0;

    /**
     * 加载更多
     *
     * @param productType 产品类型 <> 音响音效，仪表音效</>
     */
    public void fetchLoadMore(@VehicleSoundType.ProductType String productType) {
        switch (productType) {
            default:// default
            case VehicleSoundType.ProductType.AUDIO_SOUND://音响音效
                // do something
                break;
            case VehicleSoundType.ProductType.INSTRUMENT_SOUND://仪表音效
                // do something
                break;
        }
        fetchVehicleSoundBeans(mCurrentVehicleSoundType, mCurSortRule, mCurPageNum + 1, mPriceType, mDeploy);
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
