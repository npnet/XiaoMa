package com.xiaoma.utils;

import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.config.ConfigManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by iSun on 2017/4/26 0026.
 */

public class XmProperties {

    private static UserProperties defProperties;
    private static UserProperties userProperties;
    private static String TAG = "XmProperties";


    private XmProperties() {
    }


    public static PropertiesInterface build(String uid) {
        //ID为空则返回默认
        if (TextUtils.isEmpty(uid)) {
            return build();
        }
        if (userProperties == null) {
            return userProperties = new UserProperties(uid);
        } else if (!uid.equals(userProperties.getUid())) {
            return userProperties.setUid(uid);
        } else {
            return userProperties;
        }
    }


    public static PropertiesInterface build() {
        if (defProperties == null) {
            return defProperties = new UserProperties(null);
        } else {
            return defProperties;
        }
    }


    private static class UserProperties implements PropertiesInterface {
        private static String mUid;
        private static File configFile;
        private static String path;
        PropertiesControl properControl;

        protected UserProperties(String uid) {
            if (TextUtils.isEmpty(uid)) {
                configFile = ConfigManager.FileConfig.getPropertiesConfigFolder();
                properControl = new PropertiesControl(configFile);
                path = configFile.getAbsolutePath();
            } else {
                this.mUid = uid;
                configFile = ConfigManager.FileConfig.getUserPropertiesConfigFolder(mUid);
                properControl = new PropertiesControl(configFile);
                path = configFile.getAbsolutePath();
            }

        }

        public boolean put(String key, String value) {
            log("put", key, value);
            return properControl.put(key, value);
        }

        public boolean put(String key, int value) {
            log("put", key, value);
            return properControl.put(key, value);
        }

        public boolean put(String key, boolean value) {
            log("put", key, value);
            return properControl.put(key, value);
        }

        public String get(String key, String defValue) {
            log("get", key, defValue);
            return properControl.get(key, defValue);
        }

        public int get(String key, int defValue) {
            log("get", key, defValue);
            return properControl.get(key, defValue);

        }

        public boolean get(String key, boolean defValue) {
            log("get", key, defValue);
            return properControl.get(key, defValue);
        }

        public boolean remove(String key) {
            return properControl.remove(key);

        }

        private void log(String type, String key, Object value) {
            if (ConfigManager.ApkConfig.isDebug()) {
                Log.d(TAG, String.format("path:%s %s key:%s defValue:%s ", path, type, key, value));
            }
        }

        public String getUid() {
            return mUid;
        }

        public UserProperties setUid(String uid) {
            mUid = uid;
            configFile = ConfigManager.FileConfig.getUserPropertiesConfigFolder(mUid);
            properControl.setConfigFile(configFile);
            return this;
        }
    }


    private static class PropertiesControl {

        private File configFile;
        private Properties properties;

        public PropertiesControl(File configFile) {
            this.configFile = configFile;

        }

        private Properties loadConfig() {
            if (!configFile.exists()) {
                try {
                    configFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (properties == null) {
                properties = new Properties();
            }
            FileInputStream s = null;
            try {
                s = new FileInputStream(configFile);
                properties.load(s);
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (s != null)
                    try {
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return properties;
        }

        //保存配置文件
        private boolean saveConfig() {
            if (!configFile.exists()) {
                boolean result = false;
                try {
                    result = configFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!result)
                    return false;
            }
            FileOutputStream s = null;
            try {
                s = new FileOutputStream(configFile);
                if (properties == null) {
                    //配置文件不存在的时候创建配置文件 初始化配置信息
                    properties = new Properties();
                }
                //properties写入流
                properties.store(s, "");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (s != null)
                    try {
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            return true;
        }

        public boolean put(String key, String value) {
            if (TextUtils.isEmpty(key) || value == null)
                return false;
            if (properties == null) {
                properties = loadConfig();
            }
            if (properties == null)
                return false;
            properties.put(key, value);
            return saveConfig();
        }

        public boolean put(String key, int value) {
            return put(key, String.valueOf(value));
        }

        public boolean put(String key, boolean value) {
            return put(key, String.valueOf(value));
        }

        public String get(String key, String defValue) {
            String value;
            Properties properties = loadConfig();
            if (properties != null) {
                value = properties.getProperty(key);
                if (value != null) {
                    return value;
                }
            }
            return defValue;
        }

        public int get(String key, int defValue) {
            String value;
            int intValue = defValue;
            Properties properties = loadConfig();
            if (properties != null) {
                value = properties.getProperty(key);
                if (value != null) {
                    try {
                        intValue = Integer.parseInt(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return intValue;
                }
            }
            return intValue;
        }

        public boolean get(String key, boolean defValue) {
            String value;
            boolean booleanValue = defValue;
            Properties properties = loadConfig();
            if (properties != null) {
                value = properties.getProperty(key);
                if (value != null) {
                    try {
                        booleanValue = Boolean.parseBoolean(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return booleanValue;
                }
            }
            return booleanValue;
        }

        public boolean remove(String key) {
            if (TextUtils.isEmpty(key))
                return false;
            if (properties == null) {
                properties = loadConfig();
                if (properties == null)
                    return false;
            }
            properties.remove(key);
            return saveConfig();
        }

        public void setConfigFile(File configFile) {
            this.configFile = configFile;
        }
    }

    public interface PropertiesInterface {

        boolean put(String key, String value);

        boolean put(String key, int value);

        boolean put(String key, boolean value);

        String get(String key, String defValue);

        int get(String key, int defValue);

        boolean get(String key, boolean defValue);

        boolean remove(String key);
    }
}
