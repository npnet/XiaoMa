package com.xiaoma.shop.common.manager;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.PersistableBundle;
import android.util.Log;

import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.shop.business.model.SkinVersionsBean;
import com.xiaoma.shop.business.ui.theme.TrialEndActivity;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinInfo;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.utils.GsonHelper;

import java.util.Objects;

/**
 * Created by LKF on 2019-6-20 0020.
 */
public class TrialRestoreService extends JobService {
    private static final String TAG = "TrialRestoreService";

    @Override
    public boolean onStartJob(JobParameters params) {
        if (params != null) {
            PersistableBundle extras = params.getExtras();
            if (extras != null) {
                String skinJson = extras.getString("skin");
                SkinVersionsBean skin = GsonHelper.fromJson(skinJson, SkinVersionsBean.class);
                SkinInfo using = SkinUtils.getSkinMsg();
                if (using != null && skin != null
                        && Objects.equals(using.skinId, String.valueOf(skin.getId()))) {
                    XmSkinManager.getInstance().restoreDefault(TrialRestoreService.this);
                    try {
                        // 还原仪表皮肤
                        XmCarVendorExtensionManager.getInstance().setTheme(SkinConstants.THEME_DEFAULT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(this, TrialEndActivity.class)
                            .putExtra("skin", skinJson)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    Log.i(TAG, "onStartJob: 试用到期,还原默认主题");
                }
                Log.i(TAG, String.format("onStartJob: { skinJson: %s }", skinJson));
            }
        }
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, String.format("onStopJob: { %s }", params.getExtras()));
        return false;
    }
}
