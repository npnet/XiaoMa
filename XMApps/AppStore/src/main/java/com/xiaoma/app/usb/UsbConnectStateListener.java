package com.xiaoma.app.usb;

import java.util.List;

/**
 * Created by Thomas on 2018/11/14 0014
 */

public interface UsbConnectStateListener {

    void onConnection(UsbStatus status, List<String> mountPaths);

}
