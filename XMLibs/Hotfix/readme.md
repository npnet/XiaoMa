## Tinker接入
1. 在 [com.xiaoma.hotfix.HotfixConstants.App] 中添加用于Tinker动态生成的App类名, 以下用 ${app} 指代;
2. 在 Module 级的 build.gradle 添加以下依赖:

```
 apply from: "$rootDir$Tinker"
 tinker {
    appClassName \'${app}'
    buildPatch {
        baseApkPath ''
        baseResMapping ''
        baseMapping ''
    }
    patchDesc {
        baseVersion ''
        patchVersion 0
        patchMessage ''
    }
}
```
 - 注意: 修改完毕后 Sync 一遍 Gradle ;
3. 将 Application 继承 BaseApp ,并且添加以下注解用于生成真实的App;
```
 @SuppressWarnings("unused")
 @DefaultLifeCycle(application = HotfixConstants.App.${app}, flags = ShareConstants.TINKER_ENABLE_ALL)
```
 - 注意: 继承 BaseApp 之后, 需要添加构造器用于调用super(...)方法;
 - 注意: 需要重写 [attachBaseContext()] 的话请更改为重写 [onBaseContextAttached()]
4. 继承BaseApp之后,需要实现obtainPatchConfig()方法,将 App Module 中 BuildConfig 生成的补丁版本信息提供给升级服务:
 ```
 @Override
 protected PatchConfig obtainPatchConfig() {
     String baseVersion = BuildConfig.TINKER_BASE_VERSION;
     int patchVersion = BuildConfig.TINKER_PATCH_VERSION;
     PatchConfig patchConfig = new PatchConfig();
     patchConfig.setBasePkgVersion(baseVersion);
     patchConfig.setPatchVersion(patchVersion);
     return patchConfig;
 }
 ```
注意: 此处的BuildConfig必须是当前包路径的 BuildConfig 类,例如:<br/>
 当前的包名为 com.xiaoma.app ,那么需要使用 com.xiaoma.app.BuildConfig ;

5. 修改 AndroidManifest.xml 中的 ApplicationName:
```
 <application
    android:name="${app}"
    ... />
 </application>
```
 - 注意: 此时会出现无法找到 ${app} 的问题, 因为Tinker还没有生成相应代码,编译一遍就可以解决这个问题;
 - 注意: 需要为 Application 添加一个 icon , 否则 Tinker 在设置 Notification 的时候可能会出现 RemoteServiceException;

---

## 构建基础包
#### 准备工作
 1. 将项目根目录下的 gradle.properties 文件中 TINKER_ENABLE 配置为 true ;
 2. 在 Module 的 build.gradle 中修改基础包补丁信息:
```
 tinker {
    ...
    patchDesc {
        // 生成补丁对应的基础包版本,基础包可以使用当前的版本号
        baseVersion 'xxxx'
        // 生成补丁包的版本,基础包可以配置为 0
        patchVersion 0
        // 补丁包修复内容,基础包可以无需填写
        patchMessage 'base'
    }
}
```
#### 开始构建基础包
 1. 执行正常的打包操作;
 - 注意: 生成的基础包可以在以下目录中找到:
```
    ${project}
        / output
            / tinker
                / ${ModuleName}
                    / base
                        / ${GenerateDate}
                            / xxx.apk
```

---

## 构建补丁包
#### 准备工作
 1. 将项目根目录下的 gradle.properties 文件中 TINKER_ENABLE 配置为 true ;
 2. 构建好基础包,一般会一起生成一份R文件的拷贝;
 3. 在对应 Module 的 build.gradle 文件中修改以下属性:
```
 tinker {
    buildPatch {
        // 构建补丁时基础包的版本,构建补丁包时必须配置
        baseApkPath '基础包路径'
        // 构建补丁时基础包资源映射文件,构建补丁包时配置(如果有)
        baseResMapping '基础包资源映射文件路径'
        // 构建补丁时基础包混淆映射文件,构建补丁包时配置(如果有)
        baseMapping '基础包混淆文件路径'
     }
    patchDesc {
        // 生成补丁对应的基础包版本,补丁包必须配置!!!
        baseVersion '5.1.1.3'
        // 生成补丁包的版本,新版本的补丁会直接替换旧版本的补丁
        patchVersion 1
        // 补丁包的描述信息
        patchMessage 'patch'
    }
}
```

#### 开始构建补丁包
 1. 首先确保 gradle.properties 文件中 TINKER_ENABLE 配置为 true ,
 如果原本为 false ,那么更改为 true 之后, 需要 Sync 一遍 Gradle;
 2. 在 Android Studio 的 Gradle 面板中找到要生成补丁包的 Module ,定位到以下 Task :
 ```
    ${ModuleTaskGroup}
        / Tasks
            / tinker
                / tinkerPatch${BuildType}
 ```
 3. 根据基础包的构建类型( Debug / Release )来选择 Gradle Task , 双击执行即可构建出补丁包文件;
 - 注意: 生成的补丁包可以在以下目录中找到:
```
    ${project}
        / output
            / tinker
                / ${ModuleName}
                    / patch
                        / ${GenerateDate}
                            / xxx.xmpatch
```

---

## 服务器配置补丁包
 1. 联系后台同事,获取到对应项目的服务器管理端网站;
 2. 进入到管理端网站,找到补丁配置页面;
 3. 新增补丁文件,管理端会自动读取补丁中的元数据,无需手动操作;
 - 注意: 配置完成后请在客户端安装基础包,连接可用网络,及时验证当前补丁是否能够成功下发.

---
### 结束