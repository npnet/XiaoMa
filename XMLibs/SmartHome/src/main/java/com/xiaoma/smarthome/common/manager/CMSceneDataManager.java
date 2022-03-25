package com.xiaoma.smarthome.common.manager;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.common.manager
 *  @file_name:      CMSceneDataManager
 *  @author:         Rookie
 *  @create_time:    2019/4/24 17:40
 *  @description：   TODO             */

import com.xiaoma.component.AppHolder;
import com.xiaoma.smarthome.common.constants.SmartConstants;
import com.xiaoma.smarthome.common.model.HomeBean;
import com.xiaoma.smarthome.scene.model.SceneBean;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.List;

public class CMSceneDataManager {

    private static List<SceneBean> sData;
    private String cmToken;

    private CMSceneDataManager() {
    }

    private static class SingletonHolder {
        private static final CMSceneDataManager INSTANCE = new CMSceneDataManager();
    }

    public static CMSceneDataManager getInstance() {
        return CMSceneDataManager.SingletonHolder.INSTANCE;
    }

    private static void initData() {

    }

    public String getCmToken() {
        return cmToken;
    }

    public void setCmToken(String cmToken) {
        this.cmToken = cmToken;
    }

    public List<SceneBean> getSceneData() {
        if (ListUtils.isEmpty(sData)) {
            initData();
        }
        return sData;
    }

    public void setData(List<SceneBean> data) {
        sData = data;
    }

    public void setHomeData(HomeBean homeBean) {
        TPUtils.putObject(AppHolder.getInstance().getAppContext(), SmartConstants.HOME_BEAN, homeBean);
    }

    public HomeBean getHomeBean() {
        return TPUtils.getObject(AppHolder.getInstance().getAppContext(), SmartConstants.HOME_BEAN, HomeBean.class);
    }
}
