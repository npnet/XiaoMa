本 module 有两种用法(单应用和多应用)：
---
1. 每个应用单独的同步和显示自己的广告，只需在application中初始化{@link AdManager#init(Context,boolean)}时传入false即可，无需其他配置

    eg:`AdManager.init(context,false)`

2. 只有一个应用(一般为launcher)去同步广告，其他应用通过跨进程通信的方式去获取广告。在application中初始化{@link AdManager#init(Context,boolean)}时传入true，需要在launcher的manifest中配置一个provider，其exported属性设为true。其他App就可以调用 {@link AdManager#getCachedAd()}获取launcher中保存的数据。
    eg:

    `AdManager.init(context,true)`

    ```xml
    <provider
        android:authorities="com.xiaoma.preference"
        android:name="com.xiaoma.ad.provider.SharedPreferenceProvider"
        android:exported="true"/>
    ```