package com.xiaoma.instructiondistribute.subscribe;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;

import static com.xiaoma.instructiondistribute.utils.DistributeConstants.PORT_EOL_XTING;

@Deprecated
public class InstructionDistributeClient extends Client {
    private final String TAG = this.getClass().getSimpleName();
    private static InstructionDistributeClient instance;
    private boolean isInited;
    private Context context;

    public InstructionDistributeClient(Context context) {
        super(context, PORT_EOL_XTING);
        this.context = context.getApplicationContext();
    }

    public static InstructionDistributeClient getInstance(Context context) {
        if (instance == null) {
            synchronized (InstructionDistributeClient.class) {
                if (instance == null) {
                    instance = new InstructionDistributeClient(context);
                }
            }
        }
        return instance;
    }

    public void init() {
        if (isInited) {
            return;
        }
        isInited = false;
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                Center.getInstance().register(InstructionDistributeClient.getInstance(context));
                StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
                    @Override
                    public void onClientOut(SourceInfo source) {
                        if (source.equals(getSource())) {
                            isInited = false;
                            StateManager.getInstance().removeCallback(this);
                        }
                    }
                });
            }
        });
    }


    @Override
    protected void onReceive(int action, Bundle data) {

    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {

    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
        // TODO: 2019/7/3 0003 通过actin 判断接口 调用carlib中的接口 直接返回结果
        switch (action) {

        }
        int frequency = data.getInt("frequency");
        Log.d(TAG, "onConnect: frequency="+frequency);
        Bundle bundle = new Bundle();
        bundle.putInt("test",2);
        callback.setData(bundle);
        callback.callback();
    }
}
