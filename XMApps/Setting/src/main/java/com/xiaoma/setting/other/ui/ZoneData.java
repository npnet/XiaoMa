package com.xiaoma.setting.other.ui;

import com.contrarywind.interfaces.IPickerViewData;

/**
 * Created by Administrator on 2018/10/11 0011.
 */

public class ZoneData implements IPickerViewData {

    private String name;

    public ZoneData(String name){
        this.name = name;
    }

    @Override
    public String getPickerViewText() {
        return name;
    }
}
