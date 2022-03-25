package com.xiaoma.component.base;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author taojin
 * @date 2019/6/27
 */
public class LauncherAppService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("LauncherAppService", getPackageName() + "--onCreate");
        if (!this.getApplicationContext().getPackageName().equals("com.xiaoma.bluetooth.phone")) {
            stopSelf();
            if (this.getApplicationContext().getPackageName().equals("com.xiaoma.setting")) {
                Intent intent = new Intent("com.xiaoma.setting.bluetooth.service.BluetoothService");
                intent.setComponent(new ComponentName("com.xiaoma.setting", "com.xiaoma.setting.bluetooth.service.BluetoothService"));
                startService(intent);
            }
        } else {
            Intent intent = new Intent("com.xiaoma.bluetooth.phone.main.service_bt.PhoneBookService");
            intent.setComponent(new ComponentName("com.xiaoma.bluetooth.phone", "com.xiaoma.bluetooth.phone.main.service_bt.PhoneBookService"));
            startService(intent);
        }
    }
}
