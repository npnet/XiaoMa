package com.xiaoma.travel.main.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.travel.R;
import com.xiaoma.trip.common.RequestManager;
import com.xiaoma.trip.common.TripConstant;
import com.xiaoma.trip.movie.request.RequestHallSeatsInfoParm;
import com.xiaoma.trip.movie.request.RequestLockSeatParm;
import com.xiaoma.trip.movie.response.CinemaShowDataBean;
import com.xiaoma.trip.movie.response.CinemasPageDataBean;
import com.xiaoma.trip.movie.response.FilmDetailBean;
import com.xiaoma.trip.movie.response.FilmsPageDataBean;
import com.xiaoma.trip.movie.response.HallSeatsInfoBean;
import com.xiaoma.trip.movie.response.LockSeatResponseBean;

/**
 * Created by zhushi.
 * Date: 2018/12/5
 */
public class MovieActivity extends BaseActivity {

    final String city = "深圳";
    final String cinemaId = "34492";
    final String filmId = "6120";
    final String filmName = "毒液：致命守护者";
    final String showCode = "596236337";
    final String showDate = "2018-12-04";
    final String showTime = "1610";

    int type = TripConstant.FILM_TYPE_ALL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
    }

    public void queryCinemas(View view) {
        RequestManager.getInstance().queryCinemas(city, "", "", 1, 2,
                new ResultCallback<XMResult<CinemasPageDataBean>>() {

                    @Override
                    public void onSuccess(XMResult<CinemasPageDataBean> result) {
                        Log.d("result", "result:" + result.getData());
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
    }

    public void queryFilms(View view) {
        RequestManager.getInstance().queryFilms(type, city, 1, 5,
                new ResultCallback<XMResult<FilmsPageDataBean>>() {
                    @Override
                    public void onSuccess(XMResult<FilmsPageDataBean> result) {
                        Log.d("result", "result:" + result.getData());
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
    }

    public void queryCinemaShow(View view) {
        RequestManager.getInstance().queryCinemaShow(cinemaId, "", city, "",
                new ResultCallback<XMResult<CinemaShowDataBean>>() {
                    @Override
                    public void onSuccess(XMResult<CinemaShowDataBean> result) {
                        Log.d("result", "result:" + result.getData());
                    }

                    @Override
                    public void onFailure(int code, String msg) {

                    }
                });
    }

    public void queryFilmShow(View view) {
//        RequestManager.getInstance().queryFilmShow(filmId, city, "", new ResultCallback<XMResult<FilmShowBean>>() {
//            @Override
//            public void onSuccess(XMResult<FilmShowBean> result) {
//                Log.d("result", "result:" + result.getData());
//            }
//
//            @Override
//            public void onFailure(int code, String msg) {
//
//            }
//        });
    }

    public void queryFilmDetail(View view) {
        RequestManager.getInstance().queryFilmDetail(filmName, city, new ResultCallback<XMResult<FilmDetailBean>>() {
            @Override
            public void onSuccess(XMResult<FilmDetailBean> result) {
                Log.d("result", "result:" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    public void queryHallSeatsInfo(View view) {
        RequestHallSeatsInfoParm parm = new RequestHallSeatsInfoParm();
        parm.cinemaId = cinemaId;
        parm.showCode = showCode;
        parm.showDate = showDate;
        parm.showTime = showTime;
        parm.cinemaLinkId = "";
        parm.hallCode = "";
        parm.sectionId = "";
        RequestManager.getInstance().queryHallSeatsInfo(parm, new ResultCallback<XMResult<HallSeatsInfoBean>>() {
            @Override
            public void onSuccess(XMResult<HallSeatsInfoBean> result) {
                Log.d("result", "result:" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }

    public void lockSeat(View view) {
        RequestLockSeatParm lockSeatParm = new RequestLockSeatParm();
        lockSeatParm.cinemaId = "";
        lockSeatParm.ticketCount ="";
        lockSeatParm.filmId ="";
        lockSeatParm.userPhone ="";
        lockSeatParm.sectionId ="";
        lockSeatParm.hallCode ="";
        lockSeatParm.showDate ="";
        lockSeatParm.showTime ="";
        lockSeatParm.filmName ="";
        lockSeatParm.cinemaName ="";
        lockSeatParm.showCode ="";
        lockSeatParm.hallName ="";
        lockSeatParm.seatIds ="";
        lockSeatParm.seatCross ="";
        lockSeatParm.prices ="";
        lockSeatParm.channelId ="";
        lockSeatParm.uid ="";
        RequestManager.getInstance().lockSeat(lockSeatParm, new ResultCallback<XMResult<LockSeatResponseBean>>() {
            @Override
            public void onSuccess(XMResult<LockSeatResponseBean> result) {
                Log.d("result", "result:" + result.getData());
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });
    }


}
