package com.xiaoma.vr.dispatch;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.Linker;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.Request;
import com.xiaoma.center.logic.model.RequestHead;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.client.DispatchClient;
import com.xiaoma.vr.dispatch.client.WakeupWordClient;
import com.xiaoma.vr.dispatch.model.Command;
import com.xiaoma.vr.dispatch.model.Result;
import com.xiaoma.vr.dispatch.test.CommandTestReceiver;

import java.util.ArrayList;
import java.util.List;

import static com.xiaoma.center.logic.CenterManifest.AssistantClient.BUSINESS_ACTION_DISPATCH;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.BUSINESS_PORT_DISPATCH;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.CLIENT_LOCATION;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.DATA_KEY_FOLLOW_VOICE;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.DATA_KEY_HANDLE_DETAIL;
import static com.xiaoma.center.logic.CenterManifest.AssistantClient.DATA_KEY_PACKAGE_NAME;


/**
 * 面向使用者的命令分发管理器
 * 1、初始化指令分发逻辑;
 * 2、自动为App连接语音助理指令分发服务;
 * 3、自动执行语音助理断线重连;
 * 4、为语音助理提供命令分发接口;
 * 5、为App提供指令注册注册/删除接口;
 *
 * @author youthyJ
 * @date 2019/3/11
 */
public class DispatchManager {
    private static final String TAG = DispatchManager.class.getSimpleName() + "_LOG";
    private static DispatchManager instance;
    private boolean isInited;
    private Context appContext;
    private DispatchClient dispatchClient;
    private WakeupWordClient wakeupWordClient;
    private List<OnDispatch> dispatchList = new ArrayList<>();
    private IClientCallback callback = new IClientCallback.Stub() {
        @Override
        public void callback(Response response) {
            Bundle data = response.getExtra();
            if (data != null) {
                data.setClassLoader(Command.class.getClassLoader());
                Command command = data.getParcelable(Command.class.getName());
                KLog.d(TAG, "* [app] new command receive"
                        + "\n* " + command
                );
                Bundle resultData = dispatch(command);
                if (resultData == null) {
                    return;
                }
                Request request = genRequest(response.getSource(), resultData);
                Linker.getInstance().send(request);
            }
        }
    };

    private DispatchManager() {
    }

    public static DispatchManager getInstance() {
        if (instance == null) {
            synchronized (DispatchManager.class) {
                if (instance == null) {
                    instance = new DispatchManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        if (isInited) {
            return;
        }
        isInited = true;
        if (context == null) {
            throw new IllegalArgumentException("context is null!");
        }
        appContext = context.getApplicationContext();
        String packageName = appContext.getPackageName();
        if (CLIENT_LOCATION.equals(packageName)) {
            assistantInit();
        } else {
            appInit();
        }
    }

    public Result notifyCommand(Command command) {

        if (command == null) {
            return new Result(null);
        }
        if (dispatchClient == null) {
            Result result = new Result(command);
            result.setState(Result.State.ClientError);
            result.setHandleApp(Result.NO_HANDLE_APP);
            return result;
        }
        return dispatchClient.dispatchCommand(command);
    }

    public void setWakeupWordHandler(Handler handler) {
        if (handler == null) {
            return;
        }
        wakeupWordClient.setWakeupWordHandler(handler);
    }


    private void assistantInit() {
        Center.getInstance().init(appContext);
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                dispatchClient = DispatchClient.register(appContext);
                wakeupWordClient = WakeupWordClient.register(appContext);
            }
        });
        CommandTestReceiver.register(appContext);
    }

    private void appInit() {
        Center.getInstance().init(appContext);
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                final SourceInfo remote = new SourceInfo(CLIENT_LOCATION, BUSINESS_PORT_DISPATCH);
                boolean clientAlive = Center.getInstance().isClientAlive(remote);
                if (clientAlive) {
                    connectAssistant(remote);
                } else {
                    KLog.d(TAG, "[app] client disconnect, waiting!");
                    StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
                        @Override
                        public void onClientIn(SourceInfo source) {
                            if (remote.equals(source)) {
                                connectAssistant(source);
                                StateManager.getInstance().removeCallback(this);
                            }
                        }
                    });
                }
            }
        });
    }

    private void connectAssistant(final SourceInfo remote) {
        Request request = genRequest(remote, null);
        int state = Linker.getInstance().connect(request, callback);
        KLog.d(TAG, "* [app] connect to client"
                + "\n* request: " + request
                + "\n* state:  " + state
        );

        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onClientOut(SourceInfo out) {
                if (!remote.equals(out)) {
                    return;
                }
                KLog.d(TAG, "[app] client out: " + out);
                StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
                    @Override
                    public void onClientIn(SourceInfo in) {
                        if (!remote.equals(in)) {
                            return;
                        }
                        KLog.d(TAG, "[app] client in again, reconnect");
                        connectAssistant(in);
                        StateManager.getInstance().removeCallback(this);
                    }
                });
                StateManager.getInstance().removeCallback(this);
            }
        });
    }

    private Request genRequest(SourceInfo remote, Bundle extra) {
        SourceInfo local = new SourceInfo(appContext.getPackageName(), BUSINESS_PORT_DISPATCH);
        RequestHead head = new RequestHead(remote, BUSINESS_ACTION_DISPATCH);
        if (extra == null) {
            extra = new Bundle();
        }
        extra.putString("source", String.valueOf(local));
        return new Request(local, head, extra);
    }

    private Bundle dispatch(Command command) {
        if (command == null) {
            KLog.d(TAG, "[app] command is null"
            );
            return null;
        }
        for (OnDispatch impl : dispatchList) {
            if (impl == null) {
                continue;
            }
            String content = command.getContent();
            boolean isHit = false;
            try {
                isHit = impl.hit(content, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isHit) {
                Bundle resultData = new Bundle();
                resultData.putParcelable(Command.class.getName(), command);
                resultData.putString(DATA_KEY_PACKAGE_NAME, appContext.getPackageName());
                resultData.putString(DATA_KEY_HANDLE_DETAIL, impl.getHandleDetail());
                resultData.putString(DATA_KEY_FOLLOW_VOICE, impl.getFollowVoice());
                return resultData;
            }
        }
        KLog.d(TAG, "[app]  no handler hit command"
        );
        return null;
    }

    boolean addOnDispatchImpl(OnDispatch impl) {
        if (impl == null) {
            return false;
        }
        if (dispatchList.contains(impl)) {
            return true;
        }
        return dispatchList.add(impl);
    }

    boolean removeOnDispatchImpl(OnDispatch impl) {
        if (impl == null) {
            return false;
        }
        return dispatchList.remove(impl);
    }

    boolean removeOnDispatchImpl(String command) {
        int removeCount = 0;
        for (int i = dispatchList.size() - 1; i >= 0; i--) {
            OnDispatch onDispatchImpl = dispatchList.get(i);
            if (onDispatchImpl == null) {
                continue;
            }
            if (onDispatchImpl.hit(command, false)) {
                dispatchList.remove(onDispatchImpl);
                removeCount++;
            }
        }
        return removeCount > 0;
    }

    interface OnDispatch {
        boolean hit(String command, boolean forEvent);

        String getHandleDetail();

        String getFollowVoice();
    }


    // 语音助理端
    //  注册一个client √
    //  阻塞等待语音引擎发送的语音指令 √
    //  维护一个静态指令池 ×
    //  维护一个App连接器列表 ×
    //  提供唤醒词开启关闭接口 -

    // App端
    //  对内提供指令注册移除接口 √
    //  对内提供关键词开启关闭接口 -
    //  对外注册一个长连接到远端等待远端回调 √
    //  对外注册时提供App关键信息 √
}
