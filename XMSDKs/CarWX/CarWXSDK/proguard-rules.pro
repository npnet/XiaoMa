# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 指定代码优化迭代次数
-optimizationpasses 5

# 混淆时不会产生形形色色的类名
 -dontusemixedcaseclassnames

# 指定不忽略非公共的库类
-dontskipnonpubliclibraryclasses

# 不预校验
-dontpreverify

# 混淆时是否记录日志
-verbose

# 优化选项
-optimizations !code/simplification/arithmetic, !field/*,!class/merging/*

-keep public class com.xiaoma.carwxsdk.manager.CarWXManager {
    public *;
}

-keep public class com.xiaoma.carwxsdk.manager.CarWXConstants {
    public *;
}

-keep public class com.xiaoma.carwxsdk.callback.* {
    public *;
}

#
-keep public class * implements android.os.IBinder {
    *;
}

# aidl文件编译后的接口文件不能混淆
-keep class * implements android.os.IInterface {
    *;
}
