package com.xiaoma.assistant.utils;

import android.content.Context;
import android.content.Intent;

import com.xiaoma.mapadapter.view.Map;
import com.xiaoma.mqtt.model.DatasBean;

import java.util.HashMap;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/08/23
 * @Describe:
 */
public class HolopramHelper {

    private static HashMap<Integer, int[]> datas = new HashMap<>();
    private static int[] cloth_01 = {65281, 65282, 65283, 65284, 65285, 65286};
    private static int[] cloth_02 = {65281, 65282, 65283, 65284, 65285, 65286};
    private static int[] cloth_03 = {65281};
    private static int[] cloth_04 = {65281, 65282, 65283};
    public static final String EXTRA_ROLE_MODEL = "role_model";
    public static final String EXTRA_ROLE_CLOTH_MODEL = "role_cloth_model";
    public static final String ACTION_ROLE_CLOTH_USING = "com.xiaoma.shop.3D_ROLE_CLOTH_USING";

    private HolopramHelper() {
        datas.put(65281, cloth_01);
        datas.put(65282, cloth_02);
        datas.put(65283, cloth_03);
        datas.put(65284, cloth_04);
    }

    public static HolopramHelper getInstance() {
        return Holer.sHelper;
    }

    public HashMap<Integer, int[]> getDatas() {
        return datas;
    }

    public void updateClothChange(Context context, int role, int cloth) {
        // 发送衣服切换通知
        context.sendBroadcast(new Intent(ACTION_ROLE_CLOTH_USING)
                .putExtra(EXTRA_ROLE_MODEL, role)
                .putExtra(EXTRA_ROLE_CLOTH_MODEL, cloth));
    }

    public static class Holer {
        private static HolopramHelper sHelper = new HolopramHelper();
    }
}
