#Config 说明文档

#### 2018/09/17

 - FileConfig: 全局配置文件管理,可以直接获取配置文件的路径;
<br/><br/>
  当前配置文件目录接口如下:
<br/><br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;/sdcard <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/com.xiaoma <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/global/全局配置文件 <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/local/本地配置文件 <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/log/本地log文件 <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/skin/皮肤文件 <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/crash/崩溃log(时间戳) <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/login/LoginStatus.xmcfg <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/渠道号 <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/update/版本号(VersionName)/升级包 <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/服务器环境(MD5) <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/active/设备激活文件 <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/user/userId(MD5) + 设备信息(MD5) <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/user文件 <br/>
&nbsp;&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;/config/应用配置文件(各App包名) <br/>

 - ApkConfig: 用户获取当前Apk文件的构建信息;
 
#### 2019/04/22

- 由于账户相关的功能需要，新增iccid，uuid，imei，vin等四项id的本地文件配置功能

在 `/sdcard/com.xiaoma/local/`目录下添加配置文件`ids.xmcfg`配置文件示例如下：

```config
#下一行为iccid配置
iccid1001
#下一行为uuid配置
uuid1001
#下一行为imei配置
imei1001
#下一行为vin配置
vin1001
```
其中配置项的先后顺序不能更改， `#`开头的行为注释行，不影响配置。

**注意**：需要先将`/sdcard/com.xiaoma/`目录下的 `AA1090`以及`login`文件夹删掉，且清除Launcher应用缓存否则因为缓存数据配置不会生效！

目前让后台提供了几个用于测试且绑定了用户及相关功能的配置：  
以下配置绑定的默认用户密码都为1234，如要测试修改密码请测完后将密码改回去，方便其他人使用。  
配置1：  
    - iccid1001  
    - uuid1001  
    - imei1001  
    - vin1001  

配置2：  
    - iccid1002  
    - uuid1002  
    - imei1002  
    - vin1002  

配置3：  
    - iccid1003  
    - uuid1003  
    - imei1003  
    - vin1003  

配置4：  
    - iccid1004  
    - uuid1004  
    - imei1004  
    - vin1004  