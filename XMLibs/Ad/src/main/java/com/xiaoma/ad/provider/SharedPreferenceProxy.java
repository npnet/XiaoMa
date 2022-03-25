package com.xiaoma.ad.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author KY
 * @date 2018/9/13
 */
public class SharedPreferenceProxy implements SharedPreferences {
    private static Map<String, SharedPreferenceProxy> sharedPreferenceProxyMap;

    /**
     * 标志位，用于判断当前进程是为Provider的进程
     *
     * 0: 初始值，需要进一步判断
     * 1: 当前进程即是Provider的进程
     * -1: 当前进程非Provider进程
     */
    private static AtomicInteger processFlag = new AtomicInteger(0);

    /**
     * 标志位，用于判断是否是多应用模式
     */
    private static AtomicBoolean multipleFlag = new AtomicBoolean(false);

    public static void setMultipleFlag(boolean multiple) {
        SharedPreferenceProxy.multipleFlag.set(multiple);
    }

    private Context ctx;
    private String preferName;

    private SharedPreferenceProxy(Context ctx, String name) {
        this.ctx = ctx.getApplicationContext();
        this.preferName = name;
    }

    @Override
    public Map<String, ?> getAll() {
        throw new UnsupportedOperationException("Not support method getAll");
    }

    @Nullable
    @Override
    public String getString(String key, String defValue) {
        OpEntry result = getResult(OpEntry.obtainGetOperation(key).setStringValue(defValue));
        return result == null ? defValue : result.getStringValue(defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValues) {
        OpEntry result = getResult(OpEntry.obtainGetOperation(key).setStringSettingsValue(defValues));
        if (result == null) {
            return defValues;
        }
        Set<String> set = result.getStringSet();
        if (set == null) {
            return defValues;
        }
        return set;
    }

    @Override
    public int getInt(String key, int defValue) {
        OpEntry result = getResult(OpEntry.obtainGetOperation(key).setIntValue(defValue));
        return result == null ? defValue : result.getIntValue(defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        OpEntry result = getResult(OpEntry.obtainGetOperation(key).setLongValue(defValue));
        return result == null ? defValue : result.getLongValue(defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        OpEntry result = getResult(OpEntry.obtainGetOperation(key).setFloatValue(defValue));
        return result == null ? defValue : result.getFloatValue(defValue);
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        OpEntry result = getResult(OpEntry.obtainGetOperation(key).setBooleanValue(defValue));
        return result == null ? defValue : result.getBooleanValue(defValue);
    }

    @Override
    public boolean contains(String key) {
        Bundle input = new Bundle();
        input.putString(OpEntry.KEY_KEY, key);
        try {
            Bundle res = ctx.getContentResolver().call(Constant.URI
                    , Constant.METHOD_CONTAIN_KEY
                    , preferName
                    , input);
            return res != null && res.getBoolean(Constant.KEY_VALUES);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Editor edit() {
        return new EditorImpl();
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException();
    }

    private OpEntry getResult(@NonNull OpEntry input) {
        try {
            Bundle res = ctx.getContentResolver().call(Constant.URI
                    , Constant.METHOD_QUERY_VALUE
                    , preferName
                    , input.getBundle());
            return res != null ? new OpEntry(res) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public class EditorImpl implements Editor {
        private ArrayList<OpEntry> mModified = new ArrayList<>();

        @Override
        public Editor putString(String key, @Nullable String value) {
            OpEntry entry = OpEntry.obtainPutOperation(key).setStringValue(value);
            return addOps(entry);
        }

        @Override
        public Editor putStringSet(String key, @Nullable Set<String> values) {
            OpEntry entry = OpEntry.obtainPutOperation(key).setStringSettingsValue(values);
            return addOps(entry);
        }

        @Override
        public Editor putInt(String key, int value) {
            OpEntry entry = OpEntry.obtainPutOperation(key).setIntValue(value);
            return addOps(entry);
        }

        @Override
        public Editor putLong(String key, long value) {
            OpEntry entry = OpEntry.obtainPutOperation(key).setLongValue(value);
            return addOps(entry);
        }

        @Override
        public Editor putFloat(String key, float value) {
            OpEntry entry = OpEntry.obtainPutOperation(key).setFloatValue(value);
            return addOps(entry);
        }

        @Override
        public Editor putBoolean(String key, boolean value) {
            OpEntry entry = OpEntry.obtainPutOperation(key).setBooleanValue(value);
            return addOps(entry);
        }

        @Override
        public Editor remove(String key) {
            OpEntry entry = OpEntry.obtainRemoveOperation(key);
            return addOps(entry);
        }

        @Override
        public Editor clear() {
            return addOps(OpEntry.obtainClear());
        }

        @Override
        public boolean commit() {
            Bundle input = new Bundle();
            input.putParcelableArrayList(Constant.KEY_VALUES, convertBundleList());
            input.putInt(OpEntry.KEY_OP_TYPE, OpEntry.OP_TYPE_COMMIT);
            Bundle res = null;
            try {
                res = ctx.getContentResolver().call(Constant.URI, Constant.METHOD_EDIT_VALUE, preferName, input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res != null && res.getBoolean(Constant.KEY_VALUES, false);
        }

        @Override
        public void apply() {
            Bundle input = new Bundle();
            input.putParcelableArrayList(Constant.KEY_VALUES, convertBundleList());
            input.putInt(OpEntry.KEY_OP_TYPE, OpEntry.OP_TYPE_APPLY);
            try {
                ctx.getContentResolver().call(Constant.URI, Constant.METHOD_EDIT_VALUE, preferName, input);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private Editor addOps(OpEntry op) {
            synchronized (this) {
                mModified.add(op);
                return this;
            }
        }

        private ArrayList<Bundle> convertBundleList() {
            synchronized (this) {
                ArrayList<Bundle> bundleList = new ArrayList<>(mModified.size());
                for (OpEntry entry : mModified) {
                    bundleList.add(entry.getBundle());
                }
                return bundleList;
            }
        }
    }

    public static SharedPreferences getSharedPreferences(@NonNull Context ctx, String preferName) {
        if(multipleFlag.get()) {
            //First check if the same process
            if (processFlag.get() == 0) {
                Bundle bundle;
                try {
                    bundle = ctx.getContentResolver().call(Constant.URI, Constant.METHOD_QUERY_PID, "", null);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("未在manifest中声明SharedPreferenceProvider!");
                }
                int pid = 0;
                if (bundle != null) {
                    pid = bundle.getInt(Constant.KEY_VALUES);
                }
                //Can not get the pid, something wrong!
                if (pid == 0) {
                    return getFromLocalProcess(ctx, preferName);
                }
                processFlag.set(Process.myPid() == pid ? 1 : -1);
                return getSharedPreferences(ctx, preferName);
            } else if (processFlag.get() > 0) {
                return getFromLocalProcess(ctx, preferName);
            } else {
                return getFromRemoteProcess(ctx, preferName);
            }
        }else {
            return getFromLocalProcess(ctx, preferName);
        }
    }


    private static SharedPreferences getFromRemoteProcess(@NonNull Context ctx, String preferName) {
        synchronized (SharedPreferenceProxy.class) {
            if (sharedPreferenceProxyMap == null) {
                sharedPreferenceProxyMap = new ArrayMap<>();
            }
            SharedPreferenceProxy preferenceProxy = sharedPreferenceProxyMap.get(preferName);
            if (preferenceProxy == null) {
                preferenceProxy = new SharedPreferenceProxy(ctx.getApplicationContext(), preferName);
                sharedPreferenceProxyMap.put(preferName, preferenceProxy);
            }
            return preferenceProxy;
        }
    }

    private static SharedPreferences getFromLocalProcess(@NonNull Context ctx, String preferName) {
        return ctx.getSharedPreferences(preferName, Context.MODE_PRIVATE);
    }
}
