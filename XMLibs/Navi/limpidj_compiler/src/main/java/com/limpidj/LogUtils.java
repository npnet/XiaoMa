package com.limpidj;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 */
public class LogUtils {

    public static boolean isLoggable(Diagnostic.Kind kind) {
        return true;
    }

    public static void n(CharSequence msg) {
        getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private static Messager getMessager() {
        return ContextUtils.getContext().messager;
    }

}
