package com.xiaoma.aidl.xmMusic;

interface IXMAudioStatusNotifyAidlInterface {

    //searchStatus :  0->Success; 1->Failure; 2->Loading; 3->net_unavailable
    void onSearchStatusChanaged(int searchStatus);

    //playStatus : 0->INT; 1->Playing; 2->Buffering; 3->Pause; 4->Stop
    void onPlayStatusChanged(int playStatus);

}
