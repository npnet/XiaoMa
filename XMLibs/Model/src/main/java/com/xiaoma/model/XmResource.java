package com.xiaoma.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/10/15
 *     desc   :
 * </pre>
 *
 * @see <a href="https://developer.android.com/jetpack/docs/guide#addendum">jetpack:Addendum: exposing network status</a>
 */
public class XmResource<T> {
    @Status
    public int status;

    @Nullable
    public String message;

    @Nullable
    public T data;

    public int code;

    public XmResource(@Status int status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public XmResource(@Status int status, String message) {
        this.status = status;
        this.message = message;
    }

    public XmResource(@Status int status, int code, String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

    public static <T> XmResource<T> loading() {
        return new XmResource<>(Status.LOADING, null, null);
    }

    public static <T> XmResource<T> loading(@Nullable T data) {
        return new XmResource<>(Status.LOADING, data, null);
    }

    public static <T> XmResource<T> success(@Nullable T data) {
        return new XmResource<>(Status.SUCCESS, data, null);
    }

    public static <T> XmResource<T> response(@Nullable XMResult<T> data) {
        if (data != null) {
            if (data.isSuccess()) {
                return new XmResource<>(Status.SUCCESS, data.getData(), null);
            }
            return new XmResource<>(Status.FAILURE, null, data.getResultMessage());
        }
        return new XmResource<>(Status.FAILURE, null, null);
    }

    public static <T> XmResource<T> response(@Nullable T data) {
        if (data != null) {
            return new XmResource<>(Status.SUCCESS, data, null);
        } else {
            return new XmResource<>(Status.FAILURE, null, null);
        }
    }

    public static <T> XmResource<T> failure(String message) {
        return new XmResource<>(Status.FAILURE, null, message);
    }

    public static <T> XmResource<T> error(String message) {
        return new XmResource<>(Status.ERROR, message);
    }

    public static <T> XmResource<T> error(int code, String message) {
        return new XmResource<>(Status.ERROR, code, message);
    }

    public void handle(@NonNull OnHandleCallback<T> callback) {
        switch (status) {
            case Status.LOADING:
                callback.onLoading();
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


    public interface OnHandleCallback<T> {
        void onLoading();

        void onSuccess(T data);

        void onFailure(String message);

        void onError(int code, String message);

        void onCompleted();
    }
}

