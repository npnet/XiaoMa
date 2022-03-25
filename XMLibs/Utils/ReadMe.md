# APP 外部跳转文档

路径  "router://external.app.com/navigation?pkgName={?}&className={?}"

pkgName = "应用包名"            （com.xiaoma.xting）
className = "目标页实际路径"     (com.xiaoma.xting.MainActivity)

## 备注：className不配置时，直接拉起应用，不跳转指定页面


1、跳转不传递任何数据
LaunchUtils.launchExternalAppByUri(Context context, String uriContent)

2、跳转利用Bundle携带数据
LaunchUtils.launchExternalAppByUri(Context context, String uriContent, Bundle bundle)


目标页Activity在AndroidManifest.xml的配置
```
<manifest>
    <application>

        <activity android:export = true />

    </application>
</manifest>
```