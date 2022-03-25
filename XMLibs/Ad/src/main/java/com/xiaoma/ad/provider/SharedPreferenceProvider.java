package com.xiaoma.ad.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用 contentProvider 对sp进行一层封装，使sp继承到contentProvider的跨进程特性
 *
 * @author KY
 * @date 2018/9/13
 */
public class SharedPreferenceProvider extends ContentProvider{

    private Map<String, MethodHandler> handlerMap = new ArrayMap<>();
    @Override
    public boolean onCreate() {
        // query
        handlerMap.put(Constant.METHOD_QUERY_VALUE, methodQueryValues);
        // contain
        handlerMap.put(Constant.METHOD_CONTAIN_KEY, methodContainKey);
        // edit
        handlerMap.put(Constant.METHOD_EDIT_VALUE, methodEditor);
        // get runtime pid
        handlerMap.put(Constant.METHOD_QUERY_PID, methodQueryPid);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        MethodHandler handler = handlerMap.get(method);
        return handler == null?null:handler.handle(arg, extras);
    }

    public interface MethodHandler {
        /**
         * sp操作的具体处理接口，通过bundle来传递入参信息，以及返回值
         *
         * @param preferName sp名称
         * @param extras 封装在bundle中的sp操作信息，包括Key、type、value以及sp提交方式
         * @return 封装有结果的bundle对象
         */
        Bundle handle(@Nullable String preferName, @Nullable Bundle extras);
    }

    private MethodHandler methodQueryPid = new MethodHandler() {
        @Override
        public Bundle handle(@Nullable String preferName, @Nullable Bundle extras) {
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.KEY_VALUES, Process.myPid());
            return bundle;
        }
    };

    /**
     * 查询操作的具体实现
     */
    private MethodHandler methodQueryValues = new MethodHandler() {
        @Override
        public Bundle handle(@Nullable String preferName, @Nullable Bundle extras) {
            if (extras == null) {
                throw new IllegalArgumentException("methodQueryValues, extras is null!");
            }
            Context ctx = getContext();
            if (ctx == null) {
                throw new IllegalArgumentException("methodQueryValues, ctx is null!");
            }
            String key = extras.getString(OpEntry.KEY_KEY);
            SharedPreferences preferences = ctx.getSharedPreferences(preferName, Context.MODE_PRIVATE);
            int valueType = extras.getInt(OpEntry.KEY_VALUE_TYPE);
            switch (valueType) {
                case OpEntry.VALUE_TYPE_BOOLEAN:{
                    boolean value = preferences.getBoolean(key, extras.getBoolean(OpEntry.KEY_VALUE));
                    extras.putBoolean(OpEntry.KEY_VALUE, value);
                    return extras;
                }
                case OpEntry.VALUE_TYPE_FLOAT:{
                    float value = preferences.getFloat(key, extras.getFloat(OpEntry.KEY_VALUE));
                    extras.putFloat(OpEntry.KEY_VALUE, value);
                    return extras;
                }
                case OpEntry.VALUE_TYPE_INT:{
                    int value = preferences.getInt(key, extras.getInt(OpEntry.KEY_VALUE));
                    extras.putInt(OpEntry.KEY_VALUE, value);
                    return extras;
                }
                case OpEntry.VALUE_TYPE_LONG:{
                    long value = preferences.getLong(key, extras.getLong(OpEntry.KEY_VALUE));
                    extras.putLong(OpEntry.KEY_VALUE, value);
                    return extras;
                }
                case OpEntry.VALUE_TYPE_STRING:{
                    String value = preferences.getString(key, extras.getString(OpEntry.KEY_VALUE));
                    extras.putString(OpEntry.KEY_VALUE, value);
                    return extras;
                }
                case OpEntry.VALUE_TYPE_STRING_SET:{
                    Set<String> value = preferences.getStringSet(key, null);
                    extras.putStringArrayList(OpEntry.KEY_VALUE, value == null?null:new ArrayList<>(value));
                    return extras;
                }
                default:{
                    throw new IllegalArgumentException("unknown valueType:" + valueType);
                }
            }
        }
    };

    /**
     * 判断是否存在key操作的具体实现
     */
    private MethodHandler methodContainKey = new MethodHandler() {
        @Override
        public Bundle handle(@Nullable String preferName, @Nullable Bundle extras) {
            if (extras == null) {
                throw new IllegalArgumentException("methodQueryValues, extras is null!");
            }
            Context ctx = getContext();
            if (ctx == null) {
                throw new IllegalArgumentException("methodQueryValues, ctx is null!");
            }
            String key = extras.getString(OpEntry.KEY_KEY);
            SharedPreferences preferences = ctx.getSharedPreferences(preferName, Context.MODE_PRIVATE);
            extras.putBoolean(Constant.KEY_VALUES, preferences.contains(key));
            return extras;
        }
    };

    /**
     * 删、改操作的具体实现
     */
    private MethodHandler methodEditor = new MethodHandler() {
        @Override
        public Bundle handle(@Nullable String preferName, @Nullable Bundle extras) {
            if (extras == null) {
                throw new IllegalArgumentException("methodQueryValues, extras is null!");
            }
            Context ctx = getContext();
            if (ctx == null) {
                throw new IllegalArgumentException("methodQueryValues, ctx is null!");
            }
            SharedPreferences preferences = ctx.getSharedPreferences(preferName, Context.MODE_PRIVATE);
            ArrayList<Bundle> ops = extras.getParcelableArrayList(Constant.KEY_VALUES);
            if (ops == null) {
                ops = new ArrayList<>();
            }
            SharedPreferences.Editor editor = preferences.edit();
            for (Bundle opBundle : ops) {
                int opType = opBundle.getInt(OpEntry.KEY_OP_TYPE);
                switch (opType) {
                    case OpEntry.OP_TYPE_PUT: {
                        editor = editValue(editor, opBundle);
                        break;
                    }
                    case OpEntry.OP_TYPE_REMOVE: {
                        editor = editor.remove(opBundle.getString(OpEntry.KEY_KEY));
                        break;
                    }
                    case OpEntry.OP_TYPE_CLEAR: {
                        editor = editor.clear();
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("unkonw op type:" + opType);
                    }
                }
            }

            int applyOrCommit = extras.getInt(OpEntry.KEY_OP_TYPE);
            if (applyOrCommit == OpEntry.OP_TYPE_APPLY) {
                editor.apply();
                return null;
            } else if (applyOrCommit == OpEntry.OP_TYPE_COMMIT) {
                boolean res = editor.commit();
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constant.KEY_VALUES, res);
                return bundle;
            } else {
                throw new IllegalArgumentException("unknown applyOrCommit:" + applyOrCommit);
            }
        }


        private SharedPreferences.Editor editValue(SharedPreferences.Editor editor, Bundle opBundle) {
            String key = opBundle.getString(OpEntry.KEY_KEY);
            int valueType = opBundle.getInt(OpEntry.KEY_VALUE_TYPE);
            switch (valueType) {
                case OpEntry.VALUE_TYPE_BOOLEAN: {
                    return editor.putBoolean(key, opBundle.getBoolean(OpEntry.KEY_VALUE));
                }
                case OpEntry.VALUE_TYPE_FLOAT: {
                    return editor.putFloat(key, opBundle.getFloat(OpEntry.KEY_VALUE));
                }
                case OpEntry.VALUE_TYPE_INT: {
                    return editor.putInt(key, opBundle.getInt(OpEntry.KEY_VALUE));
                }
                case OpEntry.VALUE_TYPE_LONG: {
                    return editor.putLong(key, opBundle.getLong(OpEntry.KEY_VALUE));
                }
                case OpEntry.VALUE_TYPE_STRING: {
                    return editor.putString(key, opBundle.getString(OpEntry.KEY_VALUE));
                }
                case OpEntry.VALUE_TYPE_STRING_SET: {
                    ArrayList<String> list = opBundle.getStringArrayList(OpEntry.KEY_VALUE);
                    if (list == null) {
                        return editor.putStringSet(key, null);
                    }
                    return editor.putStringSet(key, new HashSet<>(list));
                }
                default: {
                    throw new IllegalArgumentException("unknown valueType:" + valueType);
                }
            }
        }
    };
}
