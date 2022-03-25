package com.xiaoma.launcher.travel.film.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.model.CarEvent;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/8/7 0007 17:02
 *   desc:   预览预告片 车速与设置限制条件处理
 * </pre>
 */
public class PreviewFilmLimitCarEvent implements ICarEvent {


    private static final int CAR_SPEED_LIMIT = 10;
    private LimitCallback mLimitCallback;

    private PreviewFilmLimitCarEvent() {
    }

    private static class Holder {
        private static final PreviewFilmLimitCarEvent PREVIEW_FILM_LIMIT_CAR_EVENT = new PreviewFilmLimitCarEvent();
    }

    public static PreviewFilmLimitCarEvent getInstance() {
        return Holder.PREVIEW_FILM_LIMIT_CAR_EVENT;
    }


    public void setLimitCallback(LimitCallback callback) {
        this.mLimitCallback = callback;
    }


    @SuppressLint("LogNotTimber")
    @Override
    public void onCarEvent(CarEvent event) {
        if (event == null) {
            return;
        }

        if (event.id == SDKConstants.ID_SPEED_INFO) {
            int speedData = (int) event.value;
            Log.d(PreviewFilmLimitCarEvent.class.getSimpleName(),
                    "speed data = " + speedData);

            if (CarInfoManager.getInstance().getIsWatchVideoInDriving() && speedData / 100 >= CAR_SPEED_LIMIT) {
                //TODO 禁止观看
                if (mLimitCallback != null) {
                    mLimitCallback.limitPreview();
                }

            } else if (speedData / 100 >= CAR_SPEED_LIMIT) {
                //TODO 速度超出限制条件  警告提示
                if (mLimitCallback != null) {
                    mLimitCallback.warnPreview();
                }
            }
            // else nothing to do.
        }
    }


    public interface LimitCallback {
        void limitPreview();

        void warnPreview();
    }

}
