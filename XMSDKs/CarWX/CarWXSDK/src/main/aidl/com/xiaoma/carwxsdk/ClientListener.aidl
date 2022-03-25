// ClientListener.aidl
package com.xiaoma.carwxsdk;

// Declare any non-default types here with import statements

interface ClientListener {
    void onSuccess(String result);
    void onFailed(int errCode, String errMsg);
}
