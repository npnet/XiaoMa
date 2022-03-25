//package com.xiaoma.club.common.network;
//
//import com.xiaoma.network.callback.ModelCallback;
//import com.xiaoma.network.request.base.Request;
//import com.xiaoma.thread.Priority;
//
///**
// * Created by LKF on 2019-1-14 0014.
// */
//public class ModelCallbackProxy<M> extends ModelCallback<M> {
//    private ModelCallback<M> mModelCallback;
//
//    public ModelCallbackProxy(ModelCallback<M> modelCallback) {
//        super(modelCallback.getPriority());
//        mModelCallback = modelCallback;
//    }
//
//    @Override
//    public M parse(String data) throws Exception {
//        return mModelCallback.parse(data);
//    }
//
//    @Override
//    public Priority getPriority() {
//        return mModelCallback.getPriority();
//    }
//
//    @Override
//    public void onStart(Request<String, ? extends Request> request) {
//        mModelCallback.onStart(request);
//    }
//
//    @Override
//    public void onFinish() {
//        mModelCallback.onFinish();
//    }
//
//    @Override
//    public void onSuccess(M model) {
//        mModelCallback.onSuccess(model);
//    }
//
//    @Override
//    public void onError(int code, String msg) {
//        mModelCallback.onError(code, msg);
//    }
//}
