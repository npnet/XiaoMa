package com.xiaoma.launcher.travel.film.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.movie.request.RequestHallSeatsInfoParm;
import com.xiaoma.trip.movie.request.RequestLockSeatParm;
import com.xiaoma.trip.movie.response.CinemaShowDataBean;
import com.xiaoma.trip.movie.response.CinemasBean;
import com.xiaoma.trip.movie.response.ConfirmOrderBean;
import com.xiaoma.trip.movie.response.FilmShowBean;
import com.xiaoma.trip.movie.response.FilmsBean;
import com.xiaoma.trip.movie.response.FilmsPageDataBean;
import com.xiaoma.trip.movie.response.HallSeatsInfoBean;
import com.xiaoma.trip.movie.response.LockSeatResponseBean;
import com.xiaoma.trip.movie.response.NearbyCinemaBean;
import com.xiaoma.trip.movie.response.OrderDetailBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.List;

public class FilmVM extends BaseCollectVM {
    private MutableLiveData<XmResource<FilmsPageDataBean>> filmsPagerData;
    private MutableLiveData<XmResource<List<CinemasBean>>> cinemasData;
    private MutableLiveData<XmResource<CinemaShowDataBean>> cinemasFilmData;
    private MutableLiveData<XmResource<NearbyCinemaBean>> nearbyCinemaData;
    private MutableLiveData<XmResource<HallSeatsInfoBean>> filmSeatsInfo;
    private MutableLiveData<XmResource<LockSeatResponseBean>> lockSeatInfo;
    private MutableLiveData<XmResource<OrderDetailBean>> orderDeatilInfo;
    private MutableLiveData<XmResource<ConfirmOrderBean>> confirmOrderInfo;

    private int cinemaMaxPageNum = -1;
    private boolean isCinemaLoadEnd = false;
    private LocationInfo mLocationInfo;

    public FilmVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<FilmsPageDataBean>> getFilmsPagerData() {
        if (filmsPagerData == null) {
            filmsPagerData = new MutableLiveData<>();
        }
        return filmsPagerData;
    }

    public MutableLiveData<XmResource<List<CinemasBean>>> getCinemasData() {
        if (cinemasData == null) {
            cinemasData = new MutableLiveData<>();
        }
        return cinemasData;
    }

    public MutableLiveData<XmResource<CinemaShowDataBean>> getCinemasFilmData() {
        if (cinemasFilmData == null) {
            cinemasFilmData = new MutableLiveData<>();
        }
        return cinemasFilmData;
    }

    public MutableLiveData<XmResource<NearbyCinemaBean>> getNearbyCinemaData() {
        if (nearbyCinemaData == null) {
            nearbyCinemaData = new MutableLiveData<>();
        }
        return nearbyCinemaData;
    }

    public MutableLiveData<XmResource<HallSeatsInfoBean>> getFilmsSeatsInfo() {
        if (filmSeatsInfo == null) {
            filmSeatsInfo = new MutableLiveData<>();
        }
        return filmSeatsInfo;
    }

    public MutableLiveData<XmResource<LockSeatResponseBean>> getLockSeat() {
        if (lockSeatInfo == null) {
            lockSeatInfo = new MutableLiveData<>();
        }
        return lockSeatInfo;
    }

    public MutableLiveData<XmResource<OrderDetailBean>> getOrderDeatil() {
        if (orderDeatilInfo == null) {
            orderDeatilInfo = new MutableLiveData<>();
        }
        return orderDeatilInfo;
    }

    public MutableLiveData<XmResource<ConfirmOrderBean>> getConfirmOrder() {
        if (confirmOrderInfo == null) {
            confirmOrderInfo = new MutableLiveData<>();
        }
        return confirmOrderInfo;
    }

    public boolean isCineMaLoadEnd() {
        return isCinemaLoadEnd;
    }
    /**
     * 电影列表
     *
     * @param dataStyle
     * @param pageNum
     * @param pageSize
     */
    public void SearchFilm(String type,int dataStyle, int pageNum, int pageSize) {
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();
        if (mLocationInfo == null) {
            XMToast.showToast(getApplication(), R.string.not_nvi_info);
            return;
        }
        getFilmsPagerData().setValue(XmResource.<FilmsPageDataBean>loading());
        RequestManager.getInstance().queryFilms(type,dataStyle, mLocationInfo.getCity(), pageNum, pageSize, new ResultCallback<XMResult<FilmsPageDataBean>>() {
            @Override
            public void onSuccess(XMResult<FilmsPageDataBean> result) {
                getFilmsPagerData().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getFilmsPagerData().setValue(XmResource.<FilmsPageDataBean>failure(msg));
            }
        });
    }

    /**
     * 影片排期
     *
     * @param filmId
     * @param showDate
     * @param pageSize
     * @param cinemaPageNum
     */
    public void queryFilmShow(String filmId, String city, String showDate, String lat, String lon, int pageSize, final int cinemaPageNum) {

        getCinemasData().setValue(XmResource.<List<CinemasBean>>loading());
        //showData默认为当天，如果从电影中拿，时间会不准确
        RequestManager.getInstance().queryFilmShow(filmId, city, "", lat, lon, cinemaPageNum, pageSize, new ResultCallback<XMResult<FilmShowBean>>() {
            @Override
            public void onSuccess(XMResult<FilmShowBean> result) {
                cinemaMaxPageNum = result.getData().getPageInfo().getTotalPage();
                if (cinemaPageNum >= cinemaMaxPageNum && cinemaMaxPageNum != -1) {
                    isCinemaLoadEnd = true;
                }else {
                    isCinemaLoadEnd = false;
                }
                getCinemasData().setValue(XmResource.success(result.getData().getCinemas()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getCinemasData().setValue(XmResource.<List<CinemasBean>>error(msg));
            }
        });
    }

    /**
     * 影院排期
     *
     * @param filmId
     * @param showDate
     */
    public void queryCinemaShow(String cinemaId, String filmId, String showDate) {
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();
        if (mLocationInfo == null) {
            XMToast.showToast(getApplication(), R.string.not_nvi_info);
            return;
        }
        getCinemasFilmData().setValue(XmResource.<CinemaShowDataBean>loading());
        RequestManager.getInstance().queryCinemaShow(cinemaId, filmId, mLocationInfo.getCity(), showDate, new ResultCallback<XMResult<CinemaShowDataBean>>() {
            @Override
            public void onSuccess(XMResult<CinemaShowDataBean> result) {
                getCinemasFilmData().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getCinemasFilmData().setValue(XmResource.<CinemaShowDataBean>error(msg));
            }
        });
    }

    /**
     * 附近影院
     *
     * @param pageNum
     * @param pageSize
     */
    public void queryNearcyCinemas(String type,String city, String dist, final int pageNum, int pageSize, String lat, String lon) {

        getNearbyCinemaData().setValue(XmResource.<NearbyCinemaBean>loading());
        RequestManager.getInstance().queryNearcyCinemas(type,city, dist, "", pageNum, pageSize, lat, lon, new ResultCallback<XMResult<NearbyCinemaBean>>() {
            @Override
            public void onSuccess(XMResult<NearbyCinemaBean> result) {
                if (result.getData().getPageInfo()!=null){
                    cinemaMaxPageNum = result.getData().getPageInfo().getTotalPage();
                }
                if (pageNum >= cinemaMaxPageNum && cinemaMaxPageNum != -1) {
                    isCinemaLoadEnd = true;
                }
                getNearbyCinemaData().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getNearbyCinemaData().setValue(XmResource.<NearbyCinemaBean>failure(msg));
            }
        });
    }

    /**
     * 影院座位
     *
     * @param requestHallSeatsInfoParm
     */
    public void queryFilmSeat(RequestHallSeatsInfoParm requestHallSeatsInfoParm) {
        getFilmsSeatsInfo().setValue(XmResource.<HallSeatsInfoBean>loading());
        RequestManager.getInstance().queryHallSeatsInfo(requestHallSeatsInfoParm, new ResultCallback<XMResult<HallSeatsInfoBean>>() {
            @Override
            public void onSuccess(XMResult<HallSeatsInfoBean> result) {
                getFilmsSeatsInfo().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getFilmsSeatsInfo().setValue(XmResource.<HallSeatsInfoBean>error(msg));
            }
        });
    }

    /**
     * 锁定座位
     *
     * @param lockSeatParm
     */
    public void queryLockSeat(RequestLockSeatParm lockSeatParm) {
        getLockSeat().setValue(XmResource.<LockSeatResponseBean>loading());
        RequestManager.getInstance().lockSeat(lockSeatParm, new ResultCallback<XMResult<LockSeatResponseBean>>() {
            @Override
            public void onSuccess(XMResult<LockSeatResponseBean> result) {
                getLockSeat().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getLockSeat().setValue(XmResource.<LockSeatResponseBean>failure(msg));
            }
        });
    }

    /**
     * 订单详情
     *
     * @param orderNo
     */
    public void queryOrderDeatil(String orderNo) {
        RequestManager.getInstance().orderDetail(orderNo, new ResultCallback<XMResult<OrderDetailBean>>() {
            @Override
            public void onSuccess(XMResult<OrderDetailBean> result) {
                getOrderDeatil().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getOrderDeatil().setValue(XmResource.<OrderDetailBean>error(msg));
            }
        });
    }

    /**
     * 观影码查询
     *
     * @param orderNum
     */
    public void queryConfirmOrder(String orderNum) {
        RequestManager.getInstance().confirmOrder(orderNum, new ResultCallback<XMResult<ConfirmOrderBean>>() {
            @Override
            public void onSuccess(XMResult<ConfirmOrderBean> result) {
                getConfirmOrder().setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                getConfirmOrder().setValue(XmResource.<ConfirmOrderBean>error(msg));
            }
        });
    }

    /**
     * 获取电影推荐
     */
    public void fetchRecommendByFilm() {
        getFilmsPagerData().setValue(XmResource.<FilmsPageDataBean>loading());
        List<FilmsBean> foodBeans = TPUtils.getList(AppHolder.getInstance().getAppContext(), LauncherConstants.RecommendExtras.RECOMMEND_FILM_LIST, FilmsBean[].class);
        if (!ListUtils.isEmpty(foodBeans)) {
            FilmsPageDataBean filmsPageDataBean = new FilmsPageDataBean();
            filmsPageDataBean.setFilms(foodBeans);
            getFilmsPagerData().setValue(XmResource.success(filmsPageDataBean));
        } else {
            getFilmsPagerData().setValue(XmResource.<FilmsPageDataBean>error(AppHolder.getInstance().getAppContext().getString(R.string.error_msg)));
        }
    }
}
