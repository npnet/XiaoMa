package com.limpidj;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 */
public class ContextUtils {

    private static ThreadLocal<Context> threadLocal = new ThreadLocal<>();

    static void setContext(Context context) {
        threadLocal.set(context);
    }

    static Context getContext() {
        return threadLocal.get();
    }

    static class Context {
        Elements elementUtils;
        Types typeUtils;
        Messager messager;
        AllBuilderData allBuilderData;
        Filer filer;
    }
}
