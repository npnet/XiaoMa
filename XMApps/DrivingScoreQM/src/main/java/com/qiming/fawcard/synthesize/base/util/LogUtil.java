//package com.qiming.fawcard.synthesize.base.util;
//
//import com.qiming.fawcard.synthesize.base.constant.QMConstant;
//
//
///**
// * Created by Administrator on 2018/4/25.
// * 统一使用小马 KLog
// */
//
//public class LogUtil {
//    public static final String DEFAULT_TAG = "TAG";
//
//
//    /**
//     * 初始化log工具，在app入口处调用 * * @param isLogEnable 是否打印log
//     */
//    public static void init(boolean isLogEnable) {
////        Logger.init(DEFAULT_TAG).hideThreadInfo().logLevel(isLogEnable ? LogLevel.FULL :
////                LogLevel.NONE).methodOffset(2);
//    }
//
//    public static void d(String message) {
//        if (QMConstant.isUnitTest) {
//            System.out.println(message);
//        } else {
//            Logger.d(message);
//        }
//    }
//
//    public static void i(String message) {
//        if (QMConstant.isUnitTest) {
//            System.out.println(message);
//        } else {
//            Logger.i(message);
//        }
//    }
//
//    public static void w(String message, Throwable e) {
//        String info = e != null ? e.toString() : "null";
//        if (QMConstant.isUnitTest) {
//            System.out.println(message + "：" + info);
//        } else {
//            Logger.w(message + "：" + info);
//        }
//    }
//
//    public static void e(String message, Throwable e) {
//        if (QMConstant.isUnitTest) {
//            String info = e != null ? e.toString() : "null";
//            System.out.println(message + "：" + info);
//        } else {
//            Logger.e(e, message);
//        }
//    }
//
//    public static void json(String json) {
//        if (QMConstant.isUnitTest) {
//            System.out.println(json);
//        } else {
//            Logger.json(json);
//        }
//    }
//
//
//}
