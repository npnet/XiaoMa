package com.xiaoma.instructiondistribute.distribute;

import android.os.Bundle;
import android.os.RemoteException;

import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.model.Response;

public abstract class ResultCallback extends IClientCallback.Stub {

    @Override
    public void callback(Response response) throws RemoteException {

    }

    public void onResponse(Bundle data){

    }
}
