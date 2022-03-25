## 使用方法

需要关心某个Action的消息，则需要构造一个实现了IPushHandler接口的对象注册到PushManager，如下：
```java
IPushHandler handler = new IPushHandler() {
    @Override
    public void handle(PushMessage pm) {
        // 具体操作
    }

    @Override
    public int getAction() {
        return 0;// 特定 action
    }
};
PushManager.getInstance().registerHandler(handler);

```

如果有对应Action的消息推送到本地，则会自动调用 IPushHandler 接口中的handle方法触发执行相应的操作。

module 中已预置了一些Handler，直接使用即可，包括DeleteFileHandler(测试通过)，NoticeHandler，PopupHandler，ScreenshotHandler(测试通过)，UploadFileHandler，ModifyConfigHandler(未实现具体逻辑)等。

## 集成说明

集成方式 按照最新的 [信鸽3.* SDK集成指南](http://xg.qq.com/docs/android_access/upgrade_guide.html)进行(4.*的版本要求 minsdkversion 最低为20)。

所以不需要再在 AndroidManifest.xml 中配置大量组件，jcenter 会自动导入。

由于manifest的合并并不会将 gradle 中定义好的manifestPlaceholders也进行合并，

所以需要先在manifest中覆写信鸽的mate-data，之后 gradle 中 配置的manifestPlaceholders密钥才会生效，如下：

```xml
<meta-data
    android:name="XG_V2_ACCESS_ID"
    android:value="${XG_ACCESS_ID}"
    tools:replace="android:value"/>

<meta-data
    android:name="XG_V2_ACCESS_KEY"
    android:value="${XG_ACCESS_KEY}"
    tools:replace="android:value"/>
```


```gradle
android {
    ......
    defaultConfig {

        //信鸽官网上注册的包名.注意application ID 和当前的应用包名以及 信鸽官网上注册应用的包名必须一致。
        applicationId "你的包名"
        ......

        ndk {
            //根据需要 自行选择添加的对应cpu类型的.so库。
            abiFilters 'armeabi', 'armeabi-v7a', 'arm64-v8a'
            // 还可以添加 'x86', 'x86_64', 'mips', 'mips64'
        }

        manifestPlaceholders = [
            XG_ACCESS_ID:"注册应用的accessid",
            XG_ACCESS_KEY : "注册应用的accesskey",
        ]
        ......
    }
    ......
}
```

