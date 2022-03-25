package com.xiaoma.assistant.model;

import android.os.Parcel;
import android.text.TextUtils;

import com.xiaoma.model.XMResult;
import com.xiaoma.utils.GsonHelper;

import java.io.Serializable;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/30
 * Desc:
 */
public class LimitInfo extends XMResult implements Serializable {
    private static final int IS_XIAN_XING = 1;

    /*public static LimitView createLimitView(Context context, LimitInfo info) {
        LimitView limitView = new LimitView(context);
        limitView.setInfo(info);
        return limitView;
    }
*/
    private ResultBean result;

    protected LimitInfo(Parcel in) {
        super(in);
    }

    public String getDate() {
        return checkEmpty(result.date);
    }

    public boolean isXianXing() {
        return result.isxianxing == IS_XIAN_XING;
    }

    public void setCityName(String cityName) {
        if (result == null) {
            result = new ResultBean();
        }
        result.cityname = cityName;
    }

    public String getCityName() {
        return checkEmpty(result.cityname);
    }

    public List<Integer> getXxweihao() {
        return result.xxweihao;
    }

    public List<Des> getDes() {
        return result.des;
    }

    public String getFormatDate() {
        return result.formatDate;
    }


    public static class ResultBean {
        private String date;
        private int isxianxing;
        private String cityname;
        private List<Integer> xxweihao;
        private List<Des> des;
        private String formatDate = "";
    }

    public static class Des {
        private String time;
        private String place;

        public String getTime() {
            return checkEmpty(time);
        }

        public String getPlace() {
            return checkEmpty(place);
        }

        /*public static LimitDesView createDesView(Context context, Des info) {
            LimitDesView limitDesView = new LimitDesView(context);
            limitDesView.setInfo(info);
            return limitDesView;
        }*/
    }

    private static String checkEmpty(String ori) {
        return TextUtils.isEmpty(ori) ? "" : ori;
    }

    public String toJson() {
        return GsonHelper.toJson(this);
    }
}
