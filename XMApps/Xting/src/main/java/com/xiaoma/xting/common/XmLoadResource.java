package com.xiaoma.xting.common;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.xiaoma.model.Status;
import com.xiaoma.model.XMResult;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/15
 *     desc   :
 * </pre>
 *
 * @see <a href="https://developer.android.com/jetpack/docs/guide#addendum">jetpack:Addendum: exposing network status</a>
 */
public class XmLoadResource<L, T> {
    @Status
    public int status;

    @Nullable
    public String message;

    @Nullable
    public T data;

    @Nullable
    public L loadData;

    public int code;

    public XmLoadResource(@Status int status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public XmLoadResource(@Status int status, String message) {
        this.status = status;
        this.message = message;
    }

    public XmLoadResource(@Status int status, int code, String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public XmLoadResource(@Status int status, @Nullable L loadData) {
        this.status = status;
        this.loadData = loadData;
    }

    public static <L, T> XmLoadResource<L, T> loading() {
        return new XmLoadResource<>(Status.LOADING, (L) null);
    }

    public static <L, T> XmLoadResource<L, T> loading(@Nullable L data) {
        return new XmLoadResource<>(Status.LOADING, data);
    }

    public static <L, T> XmLoadResource<L, T> success(@Nullable T data) {
        return new XmLoadResource<>(Status.SUCCESS, data, null);
    }

    public static <L, T> XmLoadResource<L, T> response(@Nullable XMResult<T> data) {
        if (data != null) {
            if (data.isSuccess()) {
                return new XmLoadResource<>(Status.SUCCESS, data.getData(), null);
            }
            return new XmLoadResource<>(Status.FAILURE, null, data.getResultMessage());
        }
        return new XmLoadResource<>(Status.FAILURE, null, null);
    }

    public static <L, T> XmLoadResource<L, T> response(@Nullable T data) {
        if (data != null) {
            return new XmLoadResource<>(Status.SUCCESS, data, null);
        } else {
            return new XmLoadResource<>(Status.FAILURE, null, null);
        }
    }

    public static <L, T> XmLoadResource<L, T> failure(String message) {
        return new XmLoadResource<>(Status.FAILURE, null, message);
    }

    public static <L, T> XmLoadResource<L, T> error(String message) {
        return new XmLoadResource<>(Status.ERROR, message);
    }

    public static <L, T> XmLoadResource<L, T> error(int code, String message) {
        return new XmLoadResource<>(Status.ERROR, code, message);
    }

    public void handle(@NonNull OnHandleCallback<L, T> callback) {
        switch (status) {
            case Status.LOADING:
                callback.onLoading(loadData);
                break;
            case Status.SUCCESS:
                callback.onSuccess(data);
                break;
            case Status.FAILURE:
                callback.onFailure(message);
                break;
            case Status.ERROR:
                callback.onError(code, message);

                break;
        }

        if (status != Status.LOADING) {
            callback.onCompleted();
        }
    }

    @Override
    public String toString() {
        return "XmResource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }


    public interface OnHandleCallback<L, T> {
        void onLoading(L loadData);

        void onSuccess(T data);

        void onFailure(String message);

        void onError(int code, String message);

        void onCompleted();
    }

    public static class OnHandleCallbackImpl<L, T> implements OnHandleCallback<L, T> {

        @Override
        public void onLoading(L loadData) {

        }

        @Override
        public void onSuccess(T data) {

        }

        @Override
        public void onFailure(String message) {

        }

        @Override
        public void onError(int code, String message) {

        }

        @Override
        public void onCompleted() {

        }
    }
}

