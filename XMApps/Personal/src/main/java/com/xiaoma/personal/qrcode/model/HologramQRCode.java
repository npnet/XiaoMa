package com.xiaoma.personal.qrcode.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/21 0021 18:32
 *   desc:
 * </pre>
 */
public class HologramQRCode implements Parcelable {


    public static final Creator<HologramQRCode> CREATOR = new Creator<HologramQRCode>() {
        @Override
        public HologramQRCode createFromParcel(Parcel in) {
            return new HologramQRCode(in);
        }

        @Override
        public HologramQRCode[] newArray(int size) {
            return new HologramQRCode[size];
        }
    };
    /**
     * holoApp : {"qrcode":"http://www.carbuyin.net/by2/qrCodeImage/d76e8b0a-383e-4785-8c2f-5ffe65b2c463.png","desc":"远程控制app"}
     * bind : {"qrcode":"http://www.carbuyin.net/by2/qrCodeImage/d76e8b0a-383e-4785-8c2f-5ffe65b2c463.png","desc":"远程控制app"}
     */

    private HoloAppBean holoApp;
    private BindBean bind;

    protected HologramQRCode(Parcel in) {
        holoApp = in.readParcelable(HoloAppBean.class.getClassLoader());
        bind = in.readParcelable(BindBean.class.getClassLoader());
    }

    public HoloAppBean getHoloApp() {
        return holoApp;
    }

    public void setHoloApp(HoloAppBean holoApp) {
        this.holoApp = holoApp;
    }

    public BindBean getBind() {
        return bind;
    }

    public void setBind(BindBean bind) {
        this.bind = bind;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(holoApp, flags);
        dest.writeParcelable(bind, flags);
    }

    public static class HoloAppBean implements Parcelable {
        public static final Creator<HoloAppBean> CREATOR = new Creator<HoloAppBean>() {
            @Override
            public HoloAppBean createFromParcel(Parcel in) {
                return new HoloAppBean(in);
            }

            @Override
            public HoloAppBean[] newArray(int size) {
                return new HoloAppBean[size];
            }
        };
        /**
         * qrcode : http://www.carbuyin.net/by2/qrCodeImage/d76e8b0a-383e-4785-8c2f-5ffe65b2c463.png
         * desc : 远程控制app
         */

        private String qrcode;
        private String desc;

        protected HoloAppBean(Parcel in) {
            qrcode = in.readString();
            desc = in.readString();
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(qrcode);
            dest.writeString(desc);
        }
    }

    public static class BindBean implements Parcelable {
        public static final Creator<BindBean> CREATOR = new Creator<BindBean>() {
            @Override
            public BindBean createFromParcel(Parcel in) {
                return new BindBean(in);
            }

            @Override
            public BindBean[] newArray(int size) {
                return new BindBean[size];
            }
        };
        /**
         * qrcode : http://www.carbuyin.net/by2/qrCodeImage/d76e8b0a-383e-4785-8c2f-5ffe65b2c463.png
         * desc : 远程控制app
         */

        private String qrcode;
        private String desc;

        protected BindBean(Parcel in) {
            qrcode = in.readString();
            desc = in.readString();
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(qrcode);
            dest.writeString(desc);
        }
    }
}
