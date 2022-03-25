package com.xiaoma.network.callback;

import android.support.annotation.NonNull;

import com.xiaoma.network.model.Response;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.Work;

/**
 * Created by LKF on 2018/9/25 0025.
 * 支持异步解析的泛型Model回调
 */
public abstract class ModelCallback<M> extends StringCallback implements SimpleCallback<M> {
    private Priority mPriority;

    public ModelCallback() {
        this(Priority.NORMAL);
    }

    public ModelCallback(Priority priority) {
        mPriority = priority;
    }

    public abstract M parse(String data) throws Exception;

    @Override
    final public void onSuccess(final Response<String> response) {
        SeriesAsyncWorker.create().next(new Work(getPriority()) {
            @Override
            public void doWork(Object lastResult) {
                final Object[] results = new Object[2];
                try {
                    final M model = parse(response.body());
                    if (model != null) {
                        results[0] = model;
                    } else {
                        results[1] = new NullPointerException("");
                    }
                } catch (Exception e) {
                    results[1] = e;
                }
                doNext(results);
            }
        }).next(new Work<Object[]>() {
            @Override
            public void doWork(@NonNull Object[] results) {
                if (results[0] != null) {
                    onSuccess((M) results[0]);
                } else {
                    onError(-1, ((Exception) results[1]).getMessage());
                }
            }
        }).start();
    }

    @Override
    final public void onError(Response<String> response) {
        super.onError(response);
        onError(response.code(), response.message());
    }

    public Priority getPriority() {
        return mPriority != null ? mPriority : Priority.NORMAL;
    }
}
