package com.xiaoma.personal.newguide.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.newguide.model.AppInfo;

import java.util.ArrayList;

/**
 * @author Wangjw
 * date: 2019/5/9
 */
public class NewGuideReopenVM extends BaseViewModel {
    public NewGuideReopenVM(@NonNull Application application) {
        super(application);
    }

    private String[] packNames = {"com.xiaoma.launcher", "com.xiaoma.xting", "com.xiaoma.music", "com.xiaoma.app",
            "com.xiaoma.service", "com.xiaoma.club", "com.xiaoma.carpark", "com.xiaoma.shop", "com.xiaoma.personal"};
    private String[] guideStatusFlag = {GuideConstants.LAUNCHER_SHOWED, GuideConstants.XTING_SHOWED, GuideConstants.MUSIC_SHOWED,
            GuideConstants.APPSTORE_SHOWED, GuideConstants.SERVICE_SHOWED, GuideConstants.CLUB_SHOWED, GuideConstants.CAR_PARK_SHOWED,
            GuideConstants.SHOP_SHOWED, GuideConstants.PERSONAL_SHOWED};
    private int[] icons = {R.drawable.icon_launcher, R.drawable.icon_xting, R.drawable.icon_music, R.drawable.icon_app,
            R.drawable.icon_service, R.drawable.icon_club, R.drawable.icon_carpark, R.drawable.icon_shop, R.drawable.icon_personal};

    private MutableLiveData<XmResource<ArrayList<AppInfo.DataBean>>> mAppInfos;

    public MutableLiveData<XmResource<ArrayList<AppInfo.DataBean>>> getAppList() {
        if (mAppInfos == null) {
            mAppInfos = new MutableLiveData<>();
        }
        return mAppInfos;
//        String[] titles = getApplication().getResources().getStringArray(R.array.NewGuilderApp);
//        List<AppInfo> infoArrayList = new ArrayList<>();
//        for (int i = 0; i < titles.length; i++) {
//            infoArrayList.add(new AppInfo(icons[i], titles[i], packNames[i], guideStatusFlag[i]));
//        }
//        return infoArrayList;
    }

    public void getGuideAppList() {
        RequestManager.getGuideAppList(new ResultCallback<XMResult<ArrayList<AppInfo.DataBean>>>() {
            @Override
            public void onSuccess(XMResult<ArrayList<AppInfo.DataBean>> result) {
                mAppInfos.setValue(XmResource.success(result.getData()));
            }

            @Override
            public void onFailure(int code, String msg) {
                mAppInfos.setValue(XmResource.failure(msg));
            }
        });
    }
}
