package com.xiaoma.systemui.navigationbar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LKF on 2019-7-12 0012.
 */
public class NavConstant {
    /**
     * 屏蔽NavigationBar的APP
     */
    public static final Set<String> HIDE_NAV_BAR_APP = new HashSet<>(Arrays.asList(
            "com.qiming.fawcard.synthesize",// 驾驶评分
            "com.xylink.mc.faw.bab2019"// 车载微信
    ));

}
