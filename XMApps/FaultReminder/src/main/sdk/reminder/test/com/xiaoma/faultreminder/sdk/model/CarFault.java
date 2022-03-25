package com.xiaoma.faultreminder.sdk.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.xiaoma.faultreminder.R;


/**
 * 故障类型枚举，封装了对应的Icon以及提示内容
 *
 * @author KY
 * @date 12/26/2018
 */
public enum CarFault implements Parcelable {

    fault_engine(1, 7, R.drawable.fault_engine, R.drawable.icon_alert_engine, R.string.engine_fault),
    fault_right_front_tire(2, 44, R.drawable.fault_right_front_tire, R.drawable.icon_alert_tire, R.string.tyre_right_front_fault),
    fault_brake_light(3, 25, R.drawable.fault_brake_light, R.drawable.icon_alert_light, R.string.out_light_fault_brake),
    fault_turn_left_light(4, 7, R.drawable.fault_turn_left_light, R.drawable.icon_altet_leaft_turn, R.string.out_light_fault_turn_left);

    private int type;
    private int endFrameIndex;
    @DrawableRes
    private int endFrameResId;
    @DrawableRes
    private int iconResId;
    @StringRes
    private int tipsId;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    CarFault(int type, int endFrameIndex, @DrawableRes int endFrameResId, @DrawableRes int iconResId, @StringRes int tipsId) {
        this.type = type;
        this.endFrameIndex = endFrameIndex;
        this.endFrameResId = endFrameResId;
        this.iconResId = iconResId;
        this.tipsId = tipsId;
    }

    public int getType() {
        return type;
    }

    public int getEndFrameIndex() {
        return endFrameIndex;
    }

    @DrawableRes
    public int getEndFrameResId() {
        return endFrameResId;
    }

    @DrawableRes
    public int getIconResId() {
        return iconResId;
    }

    @StringRes
    public int getTipsId() {
        return tipsId;
    }

    @Nullable
    public static CarFault valueof(int type) {
        CarFault[] values = CarFault.values();
        for (CarFault value : values) {
            if (value.getType() == type) {
                return value;
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    public static final Creator<CarFault> CREATOR = new Creator<CarFault>() {
        @Override
        public CarFault createFromParcel(Parcel in) {
            return CarFault.values()[in.readInt()];
        }

        @Override
        public CarFault[] newArray(int size) {
            return new CarFault[size];
        }
    };

}
