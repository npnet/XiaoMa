package com.xiaoma.login;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.manager.CarServiceConnManager;
import com.xiaoma.carlib.manager.CarServiceListener;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.login.business.bean.LoginStatus;
import com.xiaoma.login.business.receive.LoginReceiver;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.common.LoginMethod;
import com.xiaoma.login.sdk.CarKeyFactory;
import com.xiaoma.login.sdk.FaceFactory;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.encrypt.AESUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.manager.FactoryLoginType;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.logintype.manager.NormalLoginType;
import com.xiaoma.utils.logintype.manager.TravellerLoginType;
import com.xiaoma.utils.logintype.manager.VisitorLoginType;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xiaoma.login.common.LoginConstants.LOGIN_STATUS_AES_KEY;

/**
 * Created by youthyj on 2018/9/11.
 */
public class LoginManager {
    private static final String TAG = LoginManager.class.getSimpleName();
    private static final String KEY_STEP = TAG + "_KEY_STEP";
    private static LoginManager instance;
    private List<LoginEventListener> eventListeners = new ArrayList<>();
    private Context appContext;
    private LoginStatus currentLoginStatus;

    public static LoginManager getInstance() {
        if (instance == null) {
            synchronized (LoginManager.class) {
                if (instance == null) {
                    instance = new LoginManager();
                }
            }
        }
        return instance;
    }

    private LoginManager() {
    }

    public void init(final Context context) {
        init(context, true);
    }


    public void init(final Context context, final boolean needFaceOrCarKey) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        appContext = context.getApplicationContext();
        XmCarVendorExtensionManager.getInstance().init(appContext);
        CarServiceConnManager.getInstance(appContext).addCallBack(new CarServiceListener() {
            @Override
            public void onCarServiceConnected(IBinder binder) {
                //大部分应用无需初始化人脸识别和蓝牙钥匙
                if (needFaceOrCarKey) {
                    if (XmCarConfigManager.hasFaceRecognition()) {
                        FaceFactory.getSDK().init(appContext);
                    }
                    CarKeyFactory.getSDK().init(appContext);
                }
            }

            @Override
            public void onCarServiceDisconnected() {

            }
        });
        currentLoginStatus = updateLoginStatus();
        registerReceiver();
        // 初始化uid参数
        XmHttp xmHttp = XmHttp.getDefault();
        xmHttp.init(appContext);
        xmHttp.addCommonParams("uid", getLoginUserId());
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LoginConstants.ACTION_ON_LOGIN);
        filter.addAction(LoginConstants.ACTION_ON_LOGOUT);
        appContext.registerReceiver(new LoginReceiver(), filter);
    }

    public void notifyUserLogin(User data, String loginMethod) {
        try {
            saveLoginStatus(new LoginStatus(String.valueOf(data.getId()), loginMethod, System.currentTimeMillis()));
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            sendLoginBroadcast(data);
        }
    }

    /**
     * 游客模式登陆
     */
    public void touristLogin() {
        User user = new User();
        user.setName(appContext.getString(R.string.traveller));
        user.setBirthDayLong(TimeUtils.getNowMills());
        user.setId(LoginConstants.TOURIST_USER_ID);
        user.setBirthDayLong(TimeUtils.getNowMills());
        user.setBirthDay(TimeUtils.date2String(new Date(), new SimpleDateFormat("yyyy-MM-dd")));
        user.setGender(1);
        user.setAge("1");
        manualLogin(user, LoginMethod.TOURISTS.name());
    }

    /**
     * 仅供CarInitActivity各种登录逻辑使用，其他地方请勿滥用！！！
     *
     * @param data        data
     * @param loginMethod loginMethod
     */
    public void manualLogin(User data, String loginMethod) {
        try {
            boolean userValid = ConfigManager.FileConfig.isUserValid();
            if (LoginMethod.TOURISTS.name().equals(loginMethod) && !userValid) {
                //没有用户信息时进入游客模式需要关闭4G
                XmCarFactory.getSystemManager().setDataSwitch(false);
            } else {
                //有用户信息的游客模式，普通登录和工厂模式登录都要打开数据
                XmCarFactory.getSystemManager().setDataSwitch(true);
            }

            // 配置账号类型：主账号/子账号
            LoginType loginType;
            if (LoginMethod.TOURISTS.name().equals(loginMethod)) {
                if (userValid) {
                    loginType = new TravellerLoginType(null, data.getId() + "");
                } else {
                    loginType = new VisitorLoginType(null, data.getId() + "");
                }
            } else if (LoginMethod.FACTORY.name().equals(loginMethod)) {
                loginType = new FactoryLoginType(null, data.getId() + "");
            } else {
                loginType = new NormalLoginType(null, data.getId() + "");
            }

            LoginTypeManager.getInstance().init(loginType);
            UserManager.getInstance().setCurrentUser(data);
            if (!LoginMethod.TOURISTS.name().equals(loginMethod)
                    && !LoginMethod.FACTORY.name().equals(loginMethod)) {
                UserBindManager.getInstance().saveCacheUser(data);
            }
            currentLoginStatus = new LoginStatus(String.valueOf(data.getId()), loginMethod, System.currentTimeMillis());
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            notifyUserLogin(data, loginMethod);
        }
        Log.d(TAG, "manualLogin: user:" + data.getName() + "method:" + loginMethod);
    }

    private void sendLoginBroadcast(User data) {
        Intent intent = new Intent();
        intent.setAction(LoginConstants.ACTION_ON_LOGIN);
        // intent.putExtra("data", data);
        intent.putExtra("data", (Parcelable) data);
        appContext.sendBroadcast(intent);
    }

    public void onLogin(User data) {
        Log.d(KEY_STEP, "onLogin");
        currentLoginStatus = updateLoginStatus();
        callbackLogin(data);
        if (data != null) {
            XmHttp.getDefault().addCommonParams("uid", String.valueOf(data.getId()));
        }
    }


    public void logout() {
        Log.d(KEY_STEP, "logout");
        UserIconManager.getInstance().removeIcon();
        saveLoginStatus(new LoginStatus("", "", -1));
        notifyUserLogout();
    }

    private void notifyUserLogout() {
        sendLogoutBroadcast();
    }

    private void sendLogoutBroadcast() {
        Intent intent = new Intent();
        intent.setAction(LoginConstants.ACTION_ON_LOGOUT);
        appContext.sendBroadcast(intent);
    }

    public void onLogout() {
        Log.d(KEY_STEP, "onLogout");
        currentLoginStatus = updateLoginStatus();
        callbackLogout();
    }


    public boolean isUserLogin() {
        LoginStatus loginStatus = getLoginStatus();
        if (loginStatus == null) {
            return false;
        }
        boolean isUserLogin = loginStatus.isLogin();
        if (!isUserLogin) {
            return false;
        }
        User user = UserManager.getInstance().getCurrentUser();
        return user != null;
    }


    public String getLoginUserId() {
        LoginStatus loginStatus = getLoginStatus();
        if (loginStatus == null) {
            return null;
        }
        String loginUserId = loginStatus.getLoginUserId();
        if (TextUtils.isEmpty(loginUserId)) {
            return null;
        }
        return loginUserId;
    }

    public LoginStatus getLoginStatus() {
        return currentLoginStatus;
    }

    private LoginStatus updateLoginStatus() {
        Log.d(KEY_STEP, "updateLoginStatus");
        File file = ConfigManager.FileConfig.getLoginStatusFile();
        String content = FileUtils.read(file);
        content = AESUtils.decrypt(content, LOGIN_STATUS_AES_KEY);
        KLog.json(content);
        LoginStatus loginStatus = GsonHelper.fromJson(content, LoginStatus.class);
        if (loginStatus == null) {
            loginStatus = new LoginStatus("", null, -1);
        }
// TODO:如果有了钥匙模块后登录状态还需要跟当前的钥匙匹配才行
//        if (loginStatus == null
//                || !CarKeyFactory.getSDK().getKey().getCarKey().equals(loginStatus.getLoginKeyId())) {
//            loginStatus = new LoginStatus("", null, -1, false);
//        }
        return loginStatus;
    }

    private boolean saveLoginStatus(LoginStatus status) {
        Log.d(KEY_STEP, "saveLoginStatus");
        String content = GsonHelper.toJson(status);
        KLog.json(content);
        content = AESUtils.encryp(content, LOGIN_STATUS_AES_KEY);
        File file = ConfigManager.FileConfig.getLoginStatusFile();
        return FileUtils.writeCover(content, file);
    }


    private void callbackLogin(User data) {
        for (LoginEventListener l : eventListeners) {
            if (l == null) {
                continue;
            }
            try {
                l.onLogin(data);
            } catch (Exception e) {
                Log.e(TAG, "callback onLogin exception occurred");
                e.printStackTrace();
            }
        }
    }

    private void callbackLogout() {
        for (LoginEventListener l : eventListeners) {
            if (l == null) {
                continue;
            }
            try {
                l.onLogout();
            } catch (Exception e) {
                Log.e(TAG, "callback onLogout exception occurred");
                e.printStackTrace();
            }
        }
    }

    public boolean addLoginEventListener(LoginEventListener l) {
        if (l == null) {
            return false;
        }
        if (eventListeners.contains(l)) {
            return false;
        }
        return eventListeners.add(l);
    }

    public boolean removeLoginEventListener(LoginEventListener l) {
        return eventListeners.remove(l);
    }

    public interface LoginEventListener {
        void onLogin(User data);

        void onLogout();
    }
}
