# 单独应用的Apk更新检测 / 下载 / 校验

## 0.符号简介
```
--> : 外界调用该module进行操作时,建议使用类
[] : 注意事项
```

### 1.目录简介
```
    bean
        ApkVersionBean 从服务器返回的最新Bean类
        DownloadInfo 用于进行apk下载更新传递参数的Bean类

    download
        constract 存放下载对外接口类
     -->XMApkDownloadHelper  建议使用进行下载更新的操作类
        XMApkDownloadImpl 实际进行下载的类 (不建议直接调用)

    service
     -->UpdateCheckService 进行文件更新校验的类 [使用前请在对应的module的清单文件中进行注册]

    UpdateConstants 目前没啥用
```
### 2.使用注意事项
```
    1.传递进来的packageName的key : UpdateCheckService.ARGS_PACKAGE_NAME
    2.存储检测更新的时间的key : [PackageName] + UpdateCheckService.ARGS_UPDATE_CHECK_DATE
    3.该module目前只针对功能进行开发,并未定义界面,具体界面操作入口,已在UpdateCheckService中给出
    4.该module需要网络权限和存储权限,且并未对6.0做适配.
    5.已做了断点续传处理,但是如果加载未完成,直接退出之后,默认是重新下载.
    6.apk安装放到各自app中进行,在监听下载的onDownloadFinish()方法中
```
### 3.通用操作
```
    1.startDownload() : 开始下载
    2.pauseDownload() : 暂停操作,但不删除已经下载的文件
    3.cancelDownload() : 暂停操作,并且删除已经下载的文件
```