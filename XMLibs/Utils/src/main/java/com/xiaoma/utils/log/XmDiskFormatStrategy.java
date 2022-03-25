package com.xiaoma.utils.log;


import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.orhanobut.logger.DiskLogStrategy;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by iSun on 2018/9/5 0005.
 */

public class XmDiskFormatStrategy implements FormatStrategy {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String NEW_LINE_REPLACEMENT = " <br> ";
    private static final String SEPARATOR = ",";
    private final int methodOffset = 5;
    private final int methodCount;
    private String pkgName;
    private Context context;

    @NonNull
    private final Date date;
    @NonNull
    private final SimpleDateFormat dateFormat;
    @NonNull
    private final LogStrategy logStrategy;
    @Nullable
    private final String tag;

    public XmDiskFormatStrategy(Context context, @NonNull Date date, @NonNull SimpleDateFormat dateFormat, @NonNull LogStrategy logStrategy, LogConfig logConfig) {
        if (date == null) {
            date = new Date();
        }
        if (logConfig == null) {
            logConfig = LogConfig.getDefaultConfig();
        }
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.UK);
        }
        if (logStrategy == null) {
            HandlerThread ht = new HandlerThread(logConfig.tag, logConfig.threadPriority);
            ht.start();
            Handler handler = new XmLogStrategy.WriteHandler(context,ht.getLooper(), logConfig);
            logStrategy = new DiskLogStrategy(handler);
        }
        if (context != null) {
            pkgName = context.getPackageName();
        }
        this.date = date;
        this.dateFormat = dateFormat;
        this.logStrategy = logStrategy;
        this.tag = logConfig.tag;
        this.methodCount = logConfig.methodCount;
        this.context = context;
    }


    @Override
    public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {
        LogUtils.checkNotNull(message);

        String tag = formatTag(onceOnlyTag);

        date.setTime(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();

//        // machine-readable date/time
//        builder.append(Long.toString(date.getTime()));

        // human-readable date/time
        builder.append(dateFormat.format(date));

        builder.append(SEPARATOR);
        builder.append(pkgName);

        // level
        builder.append(SEPARATOR);
        builder.append(LogUtils.logLevel(priority));

        //thread
        builder.append(SEPARATOR);
        builder.append(" Thread:" + Thread.currentThread().getName());

        // tag
        builder.append(SEPARATOR);
        builder.append(tag);

        //Stack
        builder.append(SEPARATOR);
        builder.append(" Stack:").append(getMethodStackTrace(methodCount));


        // message
        if (message.contains(NEW_LINE)) {
            // a new line would break the CSV format, so we replace it here
            message = message.replaceAll(NEW_LINE, NEW_LINE_REPLACEMENT);
        }
        builder.append(SEPARATOR);
        builder.append(message);

        // new line
        builder.append(NEW_LINE);

        logStrategy.log(priority, tag, builder.toString());
    }

    @Nullable
    private String formatTag(@Nullable String tag) {
        if (!TextUtils.isEmpty(tag) && !TextUtils.equals(this.tag, tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    private static final String HORIZONTAL_LINE = "→";

    private String getMethodStackTrace(int methodCount) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String level = " ";

        int stackOffset = getStackOffset(trace) + methodOffset;

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            builder.append(HORIZONTAL_LINE)
                    .append(' ')
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
        }
        return builder.toString();
    }

    private String getSimpleClassName(@NonNull String name) {
        LogUtils.checkNotNull(name);

        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private static final int MIN_STACK_OFFSET = 5;

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    private int getStackOffset(@NonNull StackTraceElement[] trace) {
        LogUtils.checkNotNull(trace);

        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
//            String Logger=LoggerPrinter.class.getName();
            String LoggerPrinterClass = Logger.class.getPackage() + ".LoggerPrinter";
            if (!name.equals(LoggerPrinterClass) && !name.equals(Logger.class.getName()) && !name.equals(KLog.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

}
