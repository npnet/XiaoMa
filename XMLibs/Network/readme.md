### 在Application中初始化
> 备注：Token在网络请求拦截器自己维护，其他项目和Module不要调用TokenManager。
```
    @Override
    public void onCreate() {
        super.onCreate();
        CommonParameter commonParameter = new CommonParameter("TT0000", "101", "1027801702126071808",
                "18726883220322972332", "008796763739900", "101", "AndroidMuMu");
        XmHttp.getDefault().init(this, null, commonParameter);
    }
```

### 添加请求头和参数
- 1.在CallBack的onStart()方法
```
    @Override
    public void onStart(Request<String, ? extends Request> request) {
        super.onStart(request);
        request.headers("header1","value1")
                .headers("header2","value2");
        request.params("sourceFrom","1002")
                .params("userId","1027801702126071808");
    }
```
- 2.请求的方法调用时
```
    void postString(String url, Map<String, Object> params, StringCallback callBack);
```

- 3.全局添加
```
    OkGo.getInstance().init((Application) context.getApplicationContext())
            .addCommonHeaders(headers);
            .addCommonParams(params);
```

- 执行顺序:
3 ---> 2 ---> 1

- 参数替换
第三个参数设置为true，会把前面的替换掉
```
    request.params("sourceFrom","1002")
           .params("sourceFrom","9999",true)
```


### Get请求
```
    XmHttp.getDefault().getString(URL, map, TAG, new StringCallback() {

        @Override
        public void onComplete(Response<String> response) {
            Log.d(TAG, "onComplete: " + response.body());
        }

        @Override
        public void onError(Response<String> response) {
            super.onError(response);
            Log.d(TAG, "onError: " + response.message());
        }
    });
```

### Post请求
```
    XmHttp.getDefault().postString(URL2, mMap, TAG, new StringCallback() {
        @Override
        public void onComplete(Response<String> response) {
            Log.d(TAG, "onComplete: " + response.body());
        }

        @Override
        public void onError(Response<String> response) {
            super.onError(response);
        }
    });
```


### 下载文件
不支持断点、暂停、批量等高级功能，想要高级功能的使用OkDownload,见最后。
```
    XmHttp.getDefault().getFile(URL3, null, TAG, new FileCallback() {
        @Override
        public void onStart(Request<File, ? extends Request> request) {
            System.out.println("正在下载中");
        }
        @Override
        public void onComplete(Response<File> response) {
            System.out.println("下载完成");
        }
        @Override
        public void onError(Response<File> response) {
            System.out.println("下载出错");
        }
        @Override
        public void downloadProgress(Progress progress) {
            System.out.println("进度：" + progress);
        }
    });
```
##### FileCallback具有三个重载的构造方法,分别是
FileCallback()：空参构造
FileCallback(String destFileName)：可以额外指定文件下载完成后的文件名
FileCallback(String destFileDir, String destFileName)：可以额外指定文件的下载目录和下载完成后的文件名

##### 文件目录如果不指定,默认下载的目录为 sdcard/download/,文件名如果不指定,则按照以下规则命名:
1.首先检查用户是否传入了文件名,如果传入,将以用户传入的文件名命名
2.如果没有传入,那么将会检查服务端返回的响应头是否含有Content-Disposition=attachment;filename=FileName.txt该种形式的响应头,如果有,则按照该响应头中指定的文件名命名文件,如FileName.txt
3.如果上述响应头不存在,则检查下载的文件url,例如:http://image.baidu.com/abc.jpg,那么将会自动以abc.jpg命名文件
4.如果url也把文件名解析不出来,那么最终将以"unknownfile_" + System.currentTimeMillis()命名文件



### 取消请求
- 根据 TAG
>  XmHttp.getDefault().cancelTag(TAG);
- 取消所有
> XmHttp.getDefault().cancelAll();

### 其他
- 切换其他网络引擎继承 `IHttpEngine`
- 使用Gson解析器或者其他继承 `AbsCallback`
- Token在 `TokenManager` 中
- 超时时间设置，默认60秒
- 默认失败重试次数是3次
- 缓存设置在 `CacheMode`

---

### 增强版的文件下载：OkDownload主要功能

- 结合OkGo的request进行网络请求，支持与OkGo保持相同的配置方法和传参方式
- 支持断点下载，支持突然断网，强杀进程后，继续断点下载
- 每个下载任务具有无状态、下载、暂停、等待、出错、完成共六种状态
- 所有下载任务按照tag区分，切记不同的任务必须使用不一样的tag，否者断点会发生错乱
- 相同的下载url地址，如果使用不一样的tag，也会认为是两个下载任务
- 不同的下载url地址，如果使用相同的tag，会认为是同一个任务，导致断点错乱
- 默认同时下载数量为3个，默认下载路径/storage/emulated/0/download，下载路径和下载数量都可以在代码中配置
- 下载文件名可以自己定义，也可以不传，让框架自动获取文件名

> OkGo与OkDownload的区别就是，OkGo只是简单的做一个下载功能，不具备断点下载，暂停等操作，但是这在很多时候已经能满足需要了。
  而有些app需要有一个下载列表的功能，就像迅雷下载一样，每个下载任务可以暂停，可以继续，可以重新下载，可以有下载优先级，这时候OkDownload就有用了

- 具体使用见：
> https://github.com/jeasonlzy/okhttp-OkGo/wiki/OkDownload#okdownload%E4%B8%BB%E8%A6%81%E5%8A%9F%E8%83%BD
