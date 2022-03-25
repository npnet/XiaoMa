package com.xiaoma.systemui.common.ui;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.xiaoma.systemui.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SimpleLauncherAct extends Activity implements View.OnClickListener {
    private static final int SPAN_COUNT = 5;
    public static final Set<String> SHOWING_PACKAGE_FOR_LAUNCHER = new HashSet<>(Arrays.asList(
            "com.xiaoma.launcher", "com.xiaoma.music", "com.xiaoma.xting", "com.xiaoma.setting", "com.xylink.mc.faw.bab2019",
            "com.xiaoma.service", "com.xiaoma.xkan", "com.xiaoma.app", "com.xiaoma.instruction"
    ));
    private Button mBtnReboot;
    private RecyclerView mRvAppList;
    private AsyncTask mAppLoadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_simple_launcher);

        mBtnReboot = findViewById(R.id.btn_reboot);
        mBtnReboot.setOnClickListener(this);

        mRvAppList = findViewById(R.id.rv_app_list);

        mAppLoadTask = new AppLoadTask(this).execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAppLoadTask.cancel(true);
    }

    @Override
    public void onClick(View v) {
        if (R.id.btn_reboot == v.getId()) {
            try {
                Runtime.getRuntime().exec("reboot");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class AppLoadTask extends AsyncTask<Void, Void, List<ApplicationInfo>> {
        WeakReference<SimpleLauncherAct> actRef;

        AppLoadTask(SimpleLauncherAct act) {
            actRef = new WeakReference<>(act);
        }

        @Override
        protected List<ApplicationInfo> doInBackground(Void... voids) {
            List<ApplicationInfo> appList = null;
            SimpleLauncherAct act = actRef.get();
            if (act != null) {
                act.mRvAppList.setLayoutManager(new GridLayoutManager(act, SPAN_COUNT));
                PackageManager pm = act.getPackageManager();
                appList = pm.getInstalledApplications(0);
                if (appList != null && !appList.isEmpty()) {
                    Iterator<ApplicationInfo> it = appList.iterator();
                    while (it.hasNext()) {
                        ApplicationInfo app = it.next();
                        if (SHOWING_PACKAGE_FOR_LAUNCHER.contains(app.packageName))
                            continue;
                        it.remove();
                    }
                }

            }
            return appList;
        }

        @Override
        protected void onPostExecute(List<ApplicationInfo> appList) {
            SimpleLauncherAct act = actRef.get();
            if (act != null) {
                act.mRvAppList.setAdapter(new AppListAdapter(act, appList, SPAN_COUNT));
            }
        }
    }
}
