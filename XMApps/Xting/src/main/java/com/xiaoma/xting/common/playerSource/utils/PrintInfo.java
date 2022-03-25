package com.xiaoma.xting.common.playerSource.utils;

import android.util.Log;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/21
 */
public class PrintInfo {

    public static final String SQUARE_BRACKETS_START = "[";
    public static final String SQUARE_BRACKETS_END = "]";
    public static final String PARENTHESES_START = "(";
    public static final String PARENTHESES_END = ")";
    public static final String ANGLE_BRACKETS_START = "<";
    public static final String ANGLE_BRACKETS_END = ">";
    public static final String NEW_LINE = System.getProperty("line.separator");


    private PrintInfo() {
        throw new UnsupportedOperationException("not allowed!");
    }

    public static void print(String tag, String methodName) {
        print(tag, methodName, null);
    }

    public static void print(String tag, String methodName, String params) {
        print(tag, methodName, params, false);
    }

    public static void print(String tag, String methodName, String params, boolean printStackInfo) {
        print(tag, methodName, params, null, printStackInfo);
    }

    public static void print(String tag, String methodName, String params, String returnValue) {
        print(tag, methodName, params, returnValue, false);
    }

    public static void printError(String tag, String methodName, int errorCode, String errorMsg) {
        print(tag, methodName, String.format("errorCode = %1$s , errorMsg = %2$s", String.valueOf(errorCode), errorMsg));
    }

    private static void print(String tag, String methodName, String params, String returnValue, boolean printStackInfo) {
        StringBuilder logInfo = new StringBuilder();
        logInfo.append(ANGLE_BRACKETS_START)
                .append(methodName)
                .append(ANGLE_BRACKETS_END);
        if (params != null) {
            logInfo.append(PARENTHESES_START)
                    .append(params)
                    .append(PARENTHESES_END);
        }
        if (returnValue != null) {
            logInfo.append("=>")
                    .append(SQUARE_BRACKETS_START)
                    .append(returnValue)
                    .append(SQUARE_BRACKETS_END);
        }

        if (printStackInfo) {
            logInfo.append(NEW_LINE);
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            if (stackTrace != null) {
                for (StackTraceElement element : stackTrace) {
                    if (element == null) {
                        continue;
                    }

                    String content = element.toString();
                    if (content.contains("com.xiaoma.xting")
                            && !content.contains("PrintInfo.print")) {
                        logInfo.append(content).append(NEW_LINE);
                    }
                }
            }
        }

        Log.d(tag, logInfo.toString());
    }
}
