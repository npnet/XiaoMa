package com.xiaoma.vr.dispatch.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.vr.dispatch.DispatchManager;
import com.xiaoma.vr.dispatch.model.Command;
import com.xiaoma.vr.dispatch.model.Result;

import static com.xiaoma.center.logic.CenterManifest.AssistantClient.CLIENT_LOCATION;

/**
 * 用于测试的广播接收器,模拟语音指令分发
 * 仅在debuggable为true时可用
 *
 * @author youthyJ
 * @date 2019/3/14
 */
public class CommandTestReceiver extends BroadcastReceiver {
    private static final String ACTION = "com.xiaoma.test.COMMAND";

    public static void register(Context context) {
        if (!ConfigManager.ApkConfig.isDebug()) {
            return;
        }
        if (context == null) {
            return;
        }
        Context appContext = context.getApplicationContext();
        CommandTestReceiver receiver = new CommandTestReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CommandTestReceiver.ACTION);
        appContext.registerReceiver(receiver, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!context.getPackageName().equals(CLIENT_LOCATION)) {
            return;
        }
        if (!ConfigManager.ApkConfig.isDebug()) {
            return;
        }
        String commandContent = intent.getStringExtra("command");
        Command command = new Command(commandContent);
        Result result = DispatchManager.getInstance().notifyCommand(command);
        XMToast.showToast(context, result.toString());
    }
}
