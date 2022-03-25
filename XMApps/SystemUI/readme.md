### 替换原生SystemUI步骤
 - 如果想要运行SystemUI，请先将系统原生SystemUI移除掉
 - 移除原生SystemUI步骤：

 ```
  adb root
  adb remount
  adb shell
  cd /system/priv-app
  mv SystemUI SystemUI_Orginal
  sync
  reboot -> reboot命令可能无法重启车机,可以断电重启
  ```

 - 移除成功后, 请使用系统的keystore(shinco)打包安装