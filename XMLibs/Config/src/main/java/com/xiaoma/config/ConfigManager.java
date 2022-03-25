package com.xiaoma.config;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.SystemProperties;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.config.bean.Env;
import com.xiaoma.config.bean.EnvType;
import com.xiaoma.config.bean.Ids;
import com.xiaoma.config.bean.TboxInfo;
import com.xiaoma.config.utils.ConfigFileUtils;
import com.xiaoma.config.utils.ConfigMD5Utils;

import java.io.File;
import java.io.IOException;


public class ConfigManager {
    private static final String TAG = ConfigManager.class.getSimpleName();
    private static ConfigManager instance;
    private String deviceInfo;

    private ConfigManager() {
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public boolean deviceIsEmpty() {
        return TextUtils.isEmpty(deviceInfo);
    }

    public static class FileConfig {
        /*
         *  配置文件目录结构(2018/09/17)
         *
         *  /sdcard
         *    /com.xiaoma
         *      /global/全局配置文件
         *      /local/本地配置文件
         *      /log/本地log文件
         *      /skin/皮肤文件
         *      /ad/广告文件
         *      /crash/崩溃log(时间戳)
         *      /patch/补丁包
         *      /plugin/插件安装包
         *      /login/LoginStatus.xmcfg
         *      /渠道号
         *        /update/版本号(VersionName)/apk文件
         *        /{服务器环境}(MD5)
         *            /active/设备激活文件
         *            /{userId + 设备信息}(MD5)
         *               /user.xmcfg
         *               /config/应用配置文件(各App包名)
         */
        public static final File getGlobalConfigFolder() {
            File globalConfigFolder = new File(getXiaoMaFolder(), ConfigConstants.GLOBAL_CONFIG_FOLDER_NAME);
            return createFolder(globalConfigFolder);
        }

        public static final File getPropertiesConfigFolder() {
            File propertiesFile = new File(getGlobalConfigFolder(), ConfigConstants.PROPERTIES_NAME);
            return propertiesFile;
        }

        public static final File getUserPropertiesConfigFolder(String uid) {
            File propertiesFile = new File(getUserFolder(uid), ConfigConstants.PROPERTIES_NAME);
            return propertiesFile;
        }

        public static final File getPropertiesConfigFolder(String userId) {
            File propertiesFile = new File(getUserFolder(userId), ConfigConstants.PROPERTIES_NAME);
            return propertiesFile;
        }

        public static final File getXiaoMaFolder() {
            File esd = Environment.getExternalStorageDirectory();
            File xiaomaFolder = new File(esd, ConfigConstants.XIAOMA_FOLDER_NAME);
            return createFolder(xiaomaFolder);
        }

        private static final File getPrivateFolder() {
            File esd = Environment.getExternalStorageDirectory();
            File privateFolder = new File(esd, ConfigConstants.PRIVATE_NAME);
            return createFolder(privateFolder);
        }

        private static File createFolder(File folder) {
            if (folder == null) {
                return null;
            }
            if (!folder.exists()) {
                folder.mkdirs();
            } else if (folder.isFile()) {
                ConfigFileUtils.delete(folder);
                folder.mkdirs();
            }
            return folder;
        }


        public static final File get3DFolder() {
            File threeDFolder = new File(getPrivateFolder(), ConfigConstants.THREE_D_NAME);
            return createFolder(threeDFolder);
        }

        public static final File getICHFolder() {
            File ICHFolder = new File(getPrivateFolder(), ConfigConstants.ICH_NAME);
            return createFolder(ICHFolder);
        }

        public static final File getICLFolder() {
            File ICLFolder = new File(getPrivateFolder(), ConfigConstants.ICL_NAME);
            return createFolder(ICLFolder);
        }

        public static final File getHUFolder() {
            File HUFolder = new File(getPrivateFolder(), ConfigConstants.HU_NAME);
            return createFolder(HUFolder);
        }

        public static final File getLogFolder() {
            File logFolder = new File(getXiaoMaFolder(), ConfigConstants.LOG_FOLDER_NAME);
            return createFolder(logFolder);
        }

        public static final File getLogUploadFolder() {
            File logFolder = new File(getXiaoMaFolder(), ConfigConstants.LOG_UPLOAD_FOLDER_NAME);
            return createFolder(logFolder);
        }

        public static final String getLogUploadFolderPath() {
            File logFolder = new File(getXiaoMaFolder(), ConfigConstants.LOG_UPLOAD_FOLDER_NAME);
            return logFolder.getAbsolutePath();
        }

        public static final File getCrashFolder() {
            File crashFolder = new File(getXiaoMaFolder(), ConfigConstants.CRASH_FOLDER_NAME);
            return createFolder(crashFolder);
        }

        public static final File getSkinFolder() {
            File skinFolder = new File(getXiaoMaFolder(), ConfigConstants.SKIN_FOLDER_NAME);
            return createFolder(skinFolder);
        }

        public static final File getShopFolder() {
            File shopFolder = new File(getXiaoMaFolder(), ConfigConstants.SHOP_SOURCE_NAME);
            return createFolder(shopFolder);
        }

        public static final File getShopSkinFolder() {
            File shopSkin = new File(getShopFolder(), ConfigConstants.SKIN_FOLDER_NAME);
            return createFolder(shopSkin);
        }

        public static final File getShopSoundEffFolder() {
            File shopSkin = new File(getShopFolder(), ConfigConstants.SOUND_EFF_FOLDER_NAME);
            return createFolder(shopSkin);
        }

        public static final File getShopSkinDownloadFolder() {
            File shopSkin = new File(getShopSkinFolder(), ConfigConstants.SKIN_DOWNLOAD_FOLDER_NAME);
            return createFolder(shopSkin);
        }

        public static final File getShopHologramDownlaodFolder() {
            File shopHolo = new File(getShopHologramFolder(), ConfigConstants.HOLO_DOWNLOAD_FOLDER_NAME);
            return createFolder(shopHolo);
        }

        public static final File getShopSkinUnzipFolder() {
            File shopSkin = new File(getShopSkinFolder(), ConfigConstants.SKIN_UNZIP_FOLDER_NAME);
            return createFolder(shopSkin);
        }

        public static final File getShopAssistantFolder() {
            File shopSkin = new File(getShopFolder(), ConfigConstants.SHOP_ASSISTANT_NAME);
            return createFolder(shopSkin);
        }

        public static final File getPetFolder() {
            File petFolder = new File(getXiaoMaFolder(), ConfigConstants.PET_FOLDER);
            return createFolder(petFolder);
        }


        public static final File getPetInfoFile() {
            return new File(getPetFolder(), ConfigConstants.PET_INFO_FILE_NAME);
        }


        public static final File getShopHologramFolder() {
            File shopSkin = new File(getShopFolder(), ConfigConstants.SHOP_HOLOGRAM_NAME);
            return createFolder(shopSkin);
        }

        /**
         * 获取音响音效下载目录
         */
        public static final File getHUSoundEffDownloadFolder() {
            File shopSound = new File(getShopSoundEffFolder(), ConfigConstants.HU_SOUND_EFF_DOWNLOAD_FOLDER_NAME);
            return createFolder(shopSound);
        }

        /**
         * 获取仪表音效下载目录
         */
        public static final File getLCDSoundEffDownloadFolder() {
            File shopSound = new File(getShopSoundEffFolder(), ConfigConstants.LCD_SOUND_EFF_DOWNLOAD_FOLDER_NAME);
            return createFolder(shopSound);
        }

        public static final File getShopTrySoundFolder() {
            File trySound = new File(getShopFolder(), ConfigConstants.SHOP_TRY_SOUND_NAME);
            return createFolder(trySound);
        }

        public static final File getShopTrySoundFile(String fileName) {
            return new File(getShopTrySoundFolder(), fileName);
        }

        public static final File getPatchFolder() {
            File pluginFolder = new File(getXiaoMaFolder(), ConfigConstants.PATCH_FOLDER_NAME);
            return createFolder(pluginFolder);
        }

        public static final File getPluginFolder() {
            File pluginFolder = new File(getXiaoMaFolder(), ConfigConstants.PLUGIN_FOLDER_NAME);
            return createFolder(pluginFolder);
        }

        public static final File getAdFolder() {
            File adFolder = new File(getXiaoMaFolder(), ConfigConstants.AD_FOLDER_NAME);
            return createFolder(adFolder);
        }

        public static final File getSplashVideoFolder() {
            File videoFolder = new File(getXiaoMaFolder(), ConfigConstants.VIDEO_FOLDER_NAME);
            return createFolder(videoFolder);
        }

        public static final File getLoginStatusFile() {
            File loginStatusFile = new File(getLoginFolder(), ConfigConstants.LOGIN_STATUS_FILE_NAME);
            return loginStatusFile;
        }

        private static final File getLoginFolder() {
            File file = new File(getXiaoMaFolder(), ConfigConstants.LOGIN_FOLDER_NAME);
            return createFolder(file);
        }

        public static final File getLoginTypeFolder() {
            File loginTypeFolder = new File(getXiaoMaFolder(), ConfigConstants.LOGIN_TYPE_NAME);
            return createFolder(loginTypeFolder);
        }

        public static final File getLoginTypeFile() {
            File loginTypeFile = new File(getLoginTypeFolder(), ConfigConstants.LOGIN_TYPE_FILE_NAME);
            return loginTypeFile;
        }

        public static final File getLoinTypeCfgFile() {
            File loginTypeCfgFile = new File(getLoginTypeFolder(), ConfigConstants.LOGIN_CONFIG_FILE_NAME);
            return loginTypeCfgFile;
        }

        public static final File getRecoveryFolder() {
            File recoveryFolder = new File(getXiaoMaFolder(), ConfigConstants.RECOVERY_FOLDER_NAME);
            return createFolder(recoveryFolder);
        }

        public static final File getUpdateFolder() {
            File updateFolder = new File(getServiceFolder(), ConfigConstants.UPDATE_FOLDER_NAME);
            return createFolder(updateFolder);
        }

        private static final File getServiceFolder() {
            String business = EnvConfig.getEnv().getBusiness();
            String businessMD5 = ConfigMD5Utils.getStringMD5(business);
            File serviceFolder = new File(getChannelFolder(), "{" + businessMD5 + "}");
            return createFolder(serviceFolder);
        }

        private static final File getChannelFolder() {
            File channelFolder = new File(getXiaoMaFolder(), ConfigConstants.CHANNEL_FOLDER_NAME);
            return createFolder(channelFolder);
        }

        public static final File getActiveFolder() {
            File activeFolder = new File(getServiceFolder(), ConfigConstants.ACTIVE_FOLDER_NAME);
            return createFolder(activeFolder);
        }

        public static final boolean isActive() {
            File activeFile = new File(getActiveFolder(), ConfigConstants.ACTIVE_FILE_NAME);
            return activeFile.exists() && activeFile.isFile();
        }

        public static final boolean Active() {
            File activeFolder = getActiveFolder();
            File activeFile = new File(activeFolder, ConfigConstants.ACTIVE_FILE_NAME);
            if (activeFile.exists() && activeFile.isDirectory()) {
                ConfigFileUtils.delete(activeFile);
            } else if (activeFile.exists() && activeFile.isFile()) {
                return true;
            }
            try {
                return activeFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static final File getActiveFile() {
            File activeFile = new File(getActiveFolder(), ConfigConstants.ACTIVE_FILE_NAME);
            return activeFile;
        }

        //判断是否有有效的用户信息(判断 用户是否实名认证)
        public static final boolean isUserValid() {
            File userValidFile = new File(getActiveFolder(), ConfigConstants.USER_VALID_FILE_NAME);
            return userValidFile.exists() && userValidFile.isFile();
        }

        //标记已经有有效的用户信息了(标记为 用户已实名认证)
        public static final boolean validUser() {
            File userValidFile = new File(getActiveFolder(), ConfigConstants.USER_VALID_FILE_NAME);
            if (userValidFile.exists() && userValidFile.isDirectory()) {
                ConfigFileUtils.delete(userValidFile);
            } else if (userValidFile.exists() && userValidFile.isFile()) {
                return true;
            }
            try {
                return userValidFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        public static final File getUserFile(String userId) {
            if (TextUtils.isEmpty(userId)) {
                throw new IllegalArgumentException("user id is empty");
            }
            File userFile = new File(getUserFolder(userId), ConfigConstants.USER_FILE_NAME);
            if (userFile.exists() && userFile.isDirectory()) {
                ConfigFileUtils.delete(userFile);
            }
            return userFile;
        }

        private static final File getUserFolder(String userId) {
            if (TextUtils.isEmpty(userId)) {
                throw new IllegalArgumentException("user id is empty");
            }
            String deviceInfo = ConfigManager.getInstance().deviceInfo;
            if (TextUtils.isEmpty(deviceInfo)) {
                // pad上可能无法获取imei, 请在gradle.properties中配置TEST_IMEI
                throw new IllegalArgumentException("device info is empty");
            }
            if (TextUtils.isEmpty(userId)) {
                throw new IllegalArgumentException("userId info is empty");
            }
            // ↓↓↓↓↓↓ 绑定user与imei,防止拷贝user数据到其他设备即可直接可以登录
            String userFolderName = ConfigMD5Utils.getStringMD5(userId + deviceInfo);
            // ↑↑↑↑↑↑ 绑定user与imei,防止拷贝user数据到其他设备即可直接可以登录
            File userFolder = new File(getServiceFolder(), "{" + userFolderName + "}");
            return createFolder(userFolder);
        }

        public static final File getAppConfigFile(String userId, String packageName) {
            File appConfigFile = new File(getAppConfigFolder(userId), packageName + ConfigConstants.CONFIG_FILE_SUFFIX);
            return appConfigFile;
        }

        public static final File getAppConfigFolder(String userId) {
            File appConfigFolder = new File(getUserFolder(userId), ConfigConstants.CONFIG_FOLDER_NAME);
            return createFolder(appConfigFolder);
        }

        public static final File getUserDBFolder(Context context, String userId) {
            if (context == null) {
                throw new IllegalArgumentException("context is null");
            }
            Context appContext = context.getApplicationContext();
            File dataFolder = appContext.getFilesDir();
            String userIdMD5 = ConfigMD5Utils.getStringMD5(userId);
            return createFolder(new File(dataFolder, userIdMD5));
        }

        public static final File getMacDBFolder(Context context, String macAddress) {
            if (context == null) {
                throw new IllegalArgumentException("context is null");
            }
            Context appContext = context.getApplicationContext();
            File dataFolder = appContext.getFilesDir();
            String macAddressMD5 = ConfigMD5Utils.getStringMD5(macAddress);
            return createFolder(new File(dataFolder, macAddressMD5));
        }

        public static final File getScreenshotFolder() {
            File file = new File(getXiaoMaFolder(), ConfigConstants.SCREEN_CAPTURE_FOLDER_NAME);
            return createFolder(file);
        }

        private static File getLocalEnvConfigFile() {
            if (!ApkConfig.isDebug()) {
                return null;
            }
            File localFolder = getLocalConfigFolder();
            File envConfigFile = new File(localFolder, ConfigConstants.LOCAL_ENV_CONFIG_FILE_NAME);
            return envConfigFile;
        }

        private static File getLocalIdsConfigFile() {
            //TODO:版本中为方便测试暂时屏蔽DEBUG判断
//            if (!ApkConfig.isDebug()) {
//                return null;
//            }
            File localFolder = getLocalConfigFolder();
            File idsConfigFile = new File(localFolder, ConfigConstants.LOCAL_IDS_CONFIG_FILE_NAME);
            return idsConfigFile;
        }

        public static File getLocalTboxConfigFile() {
            File localFolder = getLocalConfigFolder();
            File tboxConfigFile = new File(localFolder, ConfigConstants.TBOX_CONFIG_FILE_NAME);
            return tboxConfigFile;
        }

        public static final File getLocalConfigFolder() {
            File localConfigFolder = new File(getXiaoMaFolder(), ConfigConstants.LOCAL_CONFIG_FOLDER_NAME);
            return createFolder(localConfigFolder);
        }

        public static final File getGuideFile() {
            File guideFile = new File(getGlobalConfigFolder(), ConfigConstants.GUIDE_FILE_NAME);
            return guideFile;
        }

        public static final File getLocalUserDBFile(Context context) {
            File localFolder = getLocalConfigFolder();
            File localUserFolder = new File(localFolder, ConfigMD5Utils.getStringMD5(DeviceConfig.getICCID(context)));
            File localUserDBFile = new File(createFolder(localUserFolder), ConfigConstants.LOCAL_USER_DB_FILE_NAME);
            return localUserDBFile;
        }

        public static final File getGlobalSkinConfigFile() {
            File globalFolder = getGlobalConfigFolder();
            File globalSkinConfigFile = new File(globalFolder, ConfigConstants.GLOBAL_SKIN_CONFIG);
            return globalSkinConfigFile;
        }

        public static final File getGlobalAudioVehicleConfigFile() {
            File globalFolder = getGlobalConfigFolder();
            File globalAudioVehicleConfigFile = new File(globalFolder, ConfigConstants.GLOBAL_AUDIO_VEHICLE_CONFIG);
            return globalAudioVehicleConfigFile;
        }

        public static final File getGlobalInstrumentVehicleConfigFile() {
            File globalFolder = getGlobalConfigFolder();
            File globalInstrumentConfigFile = new File(globalFolder, ConfigConstants.GLOBAL_INSTRUMENT_VEHICLE_CONFIG);
            return globalInstrumentConfigFile;
        }
    }

    public static class ApkConfig {
        // 是否支持debug
        public static boolean isDebug() {
            return BuildConfig.DEBUG;
        }

        // 获取构建平台
        public static String getBuildPlatform() {
            return BuildConfig.BUILD_PLATFORM;
        }

        public static boolean isCarPlatform() {
            return "CAR".equalsIgnoreCase(BuildConfig.BUILD_PLATFORM);
        }

        // 构建的渠道号
        public static String getChannelID() {
            return BuildConfig.CAR_CHANNEL_ID;
        }

        // 构建服务器环境
        public static String getBuildSerEnv() {
            return BuildConfig.SERVICE_ENV;
        }

        // 构建版本号
        public static int getBuildVersionCode() {
            return BuildConfig.VERSION_CODE;
        }

        // 构建版本名
        public static String getBuildVersionName() {
            return BuildConfig.VERSION_NAME;
        }

        // 热修复补丁版本
        // public static String getHotfixPatchVersionCode() { }

    }

    public static class EnvConfig {
        public static EnvType currentEnvType;
        public static Env currentEnv;

        public static final Env getEnv() {
            if (currentEnv != null) {
                return currentEnv;
            }
            synchronized (Env.class) {
                if (currentEnv == null) {
                    EnvType envType = getEnvType();
                    currentEnv = createEnv(envType);
                }
            }
            return currentEnv;
        }

        public static final EnvType getEnvType() {
            if (currentEnvType != null) {
                return currentEnvType;
            }
            synchronized (EnvType.class) {
                if (currentEnvType == null) {
                    File file = FileConfig.getLocalEnvConfigFile();
                    if (file == null || !file.exists()) {
                        String buildEnv = ApkConfig.getBuildSerEnv();
                        currentEnvType = EnvType.getEnvType(buildEnv);
                        return currentEnvType;
                    }
                    String content = ConfigFileUtils.read(file);
                    if (TextUtils.isEmpty(content)) {
                        String buildEnv = ApkConfig.getBuildSerEnv();
                        currentEnvType = EnvType.getEnvType(buildEnv);
                        return currentEnvType;
                    }
                    currentEnvType = EnvType.getEnvType(content);
                }
            }
            return currentEnvType;
        }

        private synchronized static Env createEnv(EnvType type) {
            Env env = new Env();

            switch (type) {
                case DEV:
                    final String DEV_HOST = "http://www.maicheyo.net";
                    env.setBusiness(DEV_HOST + ":18082/rest/");
                    env.setFile(DEV_HOST + ":18082/rest/");
                    env.setToken(DEV_HOST + ":18082/byingcar/token");
                    env.setLog(DEV_HOST + ":18082/rest/");
                    break;
                case TEST:
                    final String TEST_HOST = "https://ssl2.gz.1252871617.clb.myqcloud.com";
                    env.setBusiness(TEST_HOST + ":8082/rest/");
                    env.setFile(TEST_HOST + ":8082/rest/");
                    env.setToken(TEST_HOST + ":8087/byingcar/token");
                    env.setLog(TEST_HOST + ":8088/rest/");
                    break;
                case OFFICE:
                    final String OFFICE_HOST = "https://new58prod.gz.1252871617.clb.myqcloud.com";
                    env.setBusiness(OFFICE_HOST + ":9092/rest/");
                    env.setFile(OFFICE_HOST + ":9092/rest/");
                    env.setToken(OFFICE_HOST + ":8083/byingcar/token");
                    env.setLog(OFFICE_HOST + ":8084/rest/");
                    break;
                case EXP:
                    final String EXP_HOST = "https://ysnewssl2.gz.1252871617.clb.myqcloud.com";
                    env.setBusiness(EXP_HOST + ":9092/rest/");
                    env.setFile(EXP_HOST + ":9092/rest/");
                    env.setToken(EXP_HOST + ":8083/byingcar/token");
                    env.setLog(EXP_HOST + ":9093/rest/");

//                    env.setBusiness("https://ys-ssl2.gz.1252871617.clb.myqcloud.com:9092/rest/");
//                    env.setFile("https://ys-ssl2.gz.1252871617.clb.myqcloud.com:9092/rest/");
//                    env.setToken("https://ys-ssl2.gz.1252871617.clb.myqcloud.com:8083/byingcar/token");
//                    env.setLog("https://ys-ssl2.gz.1252871617.clb.myqcloud.com:9093/rest/");
                    break;
            }
            return env;
        }

        /**
         * TODO: Http的域名和端口号需要每个项目各自找后台确认
         * 获取备用服务器
         */
        public static Env getStandByEnv() {
            Env env = null;
            switch (getEnvType()) {
                case DEV:
                    break;
                case TEST:
                    /*env = new Env();
                    env.setBusiness("http://111.230.137.157:18082/rest/");
                    env.setFile("http://111.230.137.157:18082/rest/");
                    env.setToken("http://111.230.137.157:18083/byingcar/token");
                    env.setLog("http://111.230.137.157:18088/rest/");*/
                    break;
                case OFFICE:
                    break;
                case EXP:
                    break;
            }
            return env;
        }

        public static final void setEnv(Env env) {
            if (env == null
                    || TextUtils.isEmpty(env.getBusiness())
                    || TextUtils.isEmpty(env.getBusiness().trim())
                    || TextUtils.isEmpty(env.getFile())
                    || TextUtils.isEmpty(env.getFile().trim())
                    || TextUtils.isEmpty(env.getToken())
                    || TextUtils.isEmpty(env.getToken().trim())
                    || TextUtils.isEmpty(env.getLog())
                    || TextUtils.isEmpty(env.getLog().trim())
            ) {
                throw new IllegalArgumentException("env url prefix is empty!");
            }
            currentEnvType = EnvType.CUSTOM;
            currentEnv = env;
        }

        public static final boolean setLocalEnvConfig(EnvType type) {
            if (type == EnvType.CUSTOM) {
                throw new IllegalArgumentException("illegal env type");
            }
            boolean success = clearLocalEnvConfig();
            if (!success) {
                return false;
            }
            String envType = EnvType.getEnvDesc(type);
            File file = FileConfig.getLocalEnvConfigFile();
            return ConfigFileUtils.writeCover(envType, file);
        }

        public static final boolean clearLocalEnvConfig() {
            File file = FileConfig.getLocalEnvConfigFile();
            return ConfigFileUtils.delete(file);
        }

        public static final EnvType getLocalEnvConfig() {
            File file = FileConfig.getLocalEnvConfigFile();
            if (!file.exists()) {
                return null;
            }
            String content = ConfigFileUtils.read(file);
            if (TextUtils.isEmpty(content)) {
                return null;
            }
            return EnvType.getEnvType(content);
        }
    }

    public static class DeviceConfig {
        private static Ids mIds; // 配置文件
        private static TboxInfo mTboxInfo; // 从Tbox中获取的值

        public static String getICCID(Context context) {
            // 最优先拿车机系统的ICCID配置
            String SIM_ICCID = SystemProperties.get("persist.vendor.SIM_ICCID");
            if (!TextUtils.isEmpty(SIM_ICCID)) {
                // 返回伟世通提供的iccid
                return SIM_ICCID;
            }
            // 第二优先,原生拿ICCID
            try {
                context = context.getApplicationContext();
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String simSerialNumber = tm.getSimSerialNumber();

                if (!TextUtils.isEmpty(simSerialNumber)) {
                    // 返回原生接口获取的iccid
                    return simSerialNumber;
                }
            } catch (Throwable ignored) {
            }
            // 第三优先,读取本地配置
            try {
                if (mIds == null) {
                    mIds = getIds();
                }
                if (mIds != null) {
                    Log.d(TAG, "getICCID: " + mIds.getIccid());
                    // 返回配置文件中的iccid
                    return mIds.getIccid();
                }
            } catch (Throwable ignored) {
            }
            // 第四优先,用打包配置的ICCID
            if (!TextUtils.isEmpty(BuildConfig.TEST_ICCID)) {
                // 返回gradle中配置的iccid
                return BuildConfig.TEST_ICCID;
            }

            if (context == null) {
                return null;
            }
            // 创建一个随机的iccid
            return createDeviceId(context);
        }

        public static String getIMEI(Context context) {
            if (mIds == null) {
                mIds = getIds();
            }

            if (mIds != null) {
                Log.d(TAG, "getIMEIByCFG: " + mIds.getImei());
                return mIds.getImei();
            }

            if (!TextUtils.isEmpty(BuildConfig.TEST_IMEI)) {
                return BuildConfig.TEST_IMEI;
            }

            if (mTboxInfo == null) {
                mTboxInfo = getTboxInfo();
            }

            if (mTboxInfo != null) {
                Log.d(TAG, "getIMEIByCFG: " + mTboxInfo.getImei());
                return mTboxInfo.getImei();
            }

            if (context == null) {
                return null;
            }
            context = context.getApplicationContext();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = tm.getDeviceId();

            if (TextUtils.isEmpty(deviceId)) {
                // 随机生成一个
                return createDeviceId(context);
            }
            return deviceId;
        }

        public static String createDeviceId(Context context) {
            StringBuilder deviceId = new StringBuilder();
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(androidId)) {
                deviceId.append(androidId);
            }
            String serial = Build.SERIAL;
            if (!TextUtils.isEmpty(serial)) {
                deviceId.append(serial);
            }
            return deviceId.toString();
        }

        public static String getVIN(Context context) {
            if (mIds == null) {
                mIds = getIds();
            }

            if (mIds != null) {
                Log.d(TAG, "getVIN: " + mIds.getVin());
                return mIds.getVin();
            }

            if (!TextUtils.isEmpty(BuildConfig.TEST_VIN)) {
                return BuildConfig.TEST_VIN;
            }

            if (mTboxInfo == null) {
                mTboxInfo = getTboxInfo();
            }

            if (mTboxInfo != null) {
                Log.d(TAG, "getVIN: " + mTboxInfo.getVin());
                return mTboxInfo.getVin();
            }

            // 部分车型不支持,根据实际情况配置
            return "";
        }

        public static String getUUID() {
            if (!TextUtils.isEmpty(BuildConfig.TEST_UUID)) {
                return BuildConfig.TEST_UUID;
            }

            if (mIds == null) {
                mIds = getIds();
            }

            if (mIds != null) {
                Log.d(TAG, "getUUID: " + mIds.getUuid());
                return mIds.getUuid();
            }
            // 部分车型不支持,根据实际情况配置
            return "";
        }

        /**
         * 获取是否为高配版
         *
         * @return 是否为高配版
         */
        public static String getVersion() {
            if (!TextUtils.isEmpty(BuildConfig.CAR_VERSION)) {
                return BuildConfig.CAR_VERSION;
            }

            if (mIds == null) {
                mIds = getIds();
            }

            if (mIds != null && !TextUtils.isEmpty(mIds.getVersion())) {
                Log.d(TAG, "getVersion: " + mIds.getVersion());
                return mIds.getVersion();
            }
            //默认返回高配版
            return "PRO";
        }

        public static String getOSVersion() {
            return String.valueOf(Build.VERSION.SDK_INT);
        }

        public static String getDeviceModel() {
            return Build.BRAND + Build.MODEL;
        }

        private static Ids getIds() {
            Ids ids = new Ids();
            File idsConfigFile = FileConfig.getLocalIdsConfigFile();
            if (idsConfigFile == null || !idsConfigFile.exists()) {
                return null;
            }

            String idsStr = ConfigFileUtils.readWidthDoc(idsConfigFile, null, ":", 8 * 1024);
            Log.i(TAG, "getIds: " + idsStr);
            String[] idArray = idsStr.split(":");
            if (idArray.length == 4) {
                ids.setIccid(idArray[0]);
                ids.setUuid(idArray[1]);
                ids.setImei(idArray[2]);
                ids.setVin(idArray[3]);
                return ids;
            } else if (idArray.length == 5) {
                ids.setIccid(idArray[0]);
                ids.setUuid(idArray[1]);
                ids.setImei(idArray[2]);
                ids.setVin(idArray[3]);
                ids.setVersion(idArray[4]);
                return ids;
            } else {
                return null;
            }
        }


        private static TboxInfo getTboxInfo() {

            TboxInfo tboxInfo = new TboxInfo();

            File tboxConfigFile = FileConfig.getLocalTboxConfigFile();

            if (tboxConfigFile == null || !tboxConfigFile.exists()) {
                return null;
            }

            String tboxFileInfo = ConfigFileUtils.read(tboxConfigFile);

            Log.i(TAG, "getTbox: " + tboxFileInfo);

            String[] tboxInfoArray = tboxFileInfo.split("-");

            if (tboxInfoArray.length == 2) {

                tboxInfo.setImei(tboxInfoArray[0]);
                tboxInfo.setVin(tboxInfoArray[1]);

                return tboxInfo;
            } else {
                return null;
            }
        }
    }
}
