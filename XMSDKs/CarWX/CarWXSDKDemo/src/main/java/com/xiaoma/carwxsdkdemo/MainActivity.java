package com.xiaoma.carwxsdkdemo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoma.carwxsdk.callback.AsrCallBack;
import com.xiaoma.carwxsdk.callback.CarSpeedChangeCallBack;
import com.xiaoma.carwxsdk.callback.TtsCallBack;
import com.xiaoma.carwxsdk.manager.CarWXConstants;
import com.xiaoma.carwxsdk.manager.CarWXManager;

@SuppressLint("LogNotTimber")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView carVin;
    private Button startNaviByPoi;
    private Button startNaviByKey;
    private Button bluetoothCall;
    private Button startTTS;
    private Button stopTTS;
    private Button bindService;
    private TextView carSerial;
    private final String TAG = "MainActivity";
    private final BroadcastReceiver mThemeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 皮肤变化了
            String action = intent.getAction();
            int themeId = intent.getIntExtra(CarWXConstants.EXTRA_THEME_ID_INT, CarWXConstants.DEFAULT_THEME_ID);
            Log.i(TAG, String.format("onSkinChanged -> action: %s, themeId: %s", action, themeId));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean result = CarWXManager.getInstance().init(this);
        if (!result) {
            Toast.makeText(this, "初始化失败", Toast.LENGTH_SHORT).show();
//            return;
        }
        carVin = findViewById(R.id.tv_car_vin);
        carSerial = findViewById(R.id.tv_car_serial);
        startNaviByPoi = findViewById(R.id.btn_start_navi_by_poi);
        startNaviByKey = findViewById(R.id.btn_start_navi_by_key);
        bluetoothCall = findViewById(R.id.btn_blutooth_call);
        bindService = findViewById(R.id.btn_connect_to_service);
        startTTS = findViewById(R.id.btn_start_tts);
        stopTTS = findViewById(R.id.btn_stop_tts);
        carVin.setOnClickListener(this);
        startNaviByKey.setOnClickListener(this);
        startNaviByPoi.setOnClickListener(this);
        bluetoothCall.setOnClickListener(this);
        startTTS.setOnClickListener(this);
        stopTTS.setOnClickListener(this);
        bindService.setOnClickListener(this);
        carSerial.setOnClickListener(this);
        findViewById(R.id.btn_start_record).setOnClickListener(this);
        findViewById(R.id.btn_stop_record).setOnClickListener(this);
        findViewById(R.id.btn_cancle_record).setOnClickListener(this);
        findViewById(R.id.btn_upload_contact).setOnClickListener(this);
        findViewById(R.id.btn_set_speed_listener).setOnClickListener(this);
        findViewById(R.id.btn_cur_theme).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 监听皮肤变化的广播
        registerReceiver(mThemeReceiver, new IntentFilter(CarWXConstants.ACTION_THEME_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mThemeReceiver);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_start_record:
                CarWXManager.getInstance().setASRListener(new AsrCallBack() {
                    @Override
                    public void onVolumeChanged(int volume) {
//                        Log.d(TAG, "onVolumeChanged: " + new Throwable().getStackTrace()[0]
//                                + "\n * volume = " + volume);
                    }

                    @Override
                    public void onSRstatus(int i) {
                        Log.d(TAG, "onSRstatus: " + new Throwable().getStackTrace()[0]
                                + "\n * i = " + i);
                    }

                    @Override
                    public void onError(int errorCode) {
                        Log.d(TAG, "onError: " + new Throwable().getStackTrace()[0]
                                + "\n * errorCode = " + errorCode);
                    }

                    @Override
                    public void showSrText(String voiceFilePath, String recordTxt, boolean isResolving) {
                        Log.d(TAG, "showSrText: " + new Throwable().getStackTrace()[0]
                                + "\n * voiceFilePath = " + voiceFilePath + ",* recordTxt = " + recordTxt + ", * isResolving = " + isResolving);
                    }

                    @Override
                    public void onFailed(int errCode, String errMsg) {
                        Log.d(TAG, "onFailed: " + new Throwable().getStackTrace()[0]
                                + "\n * errCode = " + errCode + ", * errMsg = " + errMsg);
                    }
                });
                CarWXManager.getInstance().startRecord(true);
                break;
            case R.id.btn_stop_record:
                CarWXManager.getInstance().finishRecord();
                break;
            case R.id.btn_cancle_record:
                CarWXManager.getInstance().cancelRecord();
                break;
            case R.id.btn_upload_contact:
                break;
            case R.id.btn_set_speed_listener:
                CarWXManager.getInstance().setSpeedChangeListener(new CarSpeedChangeCallBack() {
                    @Override
                    public void onSpeedChanged(float arg0) {
                        Log.d(TAG, "onSpeedChanged: " + new Throwable().getStackTrace()[0]
                                + "\n * arg0 = " + arg0);
                    }

                    @Override
                    public void onFailed(int errCode, String errMsg) {
                        Log.d(TAG, "onFailed: " + new Throwable().getStackTrace()[0]
                                + "\n * errCode = " + errCode + ", * errMsg = " + errMsg);
                    }
                });
                break;
            case R.id.tv_car_vin:
                if (CarWXManager.getInstance().isServiceConnected()) {
                    String vin = CarWXManager.getInstance().getVin();
                    carVin.setText(carVin.getText() + vin);
                }
                break;
            case R.id.btn_start_navi_by_poi:
                CarWXManager.getInstance().startNaviByPoi("深圳站", 22, 114);
                break;
            case R.id.btn_start_navi_by_key:
                CarWXManager.getInstance().startNaviByKey("径贝社区径贝小学");
                break;
            case R.id.btn_blutooth_call:
                if (CarWXManager.getInstance().hasConnectedBluetoothDevice()) {
                    CarWXManager.getInstance().callPhone("18571842025");
                } else {
                    Toast.makeText(this, "bt is not connected", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_start_tts:
                CarWXManager.getInstance().setTtsListener(new TtsCallBack() {
                    @Override
                    public void onFinish(String id) {
                        Log.d(TAG, "onFinish: " + new Throwable().getStackTrace()[0]
                                + "\n * id = " + id);
                    }

                    @Override
                    public void onStart(String id) {
                        Log.d(TAG, "onStart: " + new Throwable().getStackTrace()[0]
                                + "\n * id = " + id);
                    }

                    @Override
                    public void onError(String id, int code) {
                        Log.d(TAG, "onError: " + new Throwable().getStackTrace()[0]
                                + "\n * id = " + id + ", * code = " + code);
                    }

                    @Override
                    public void onProgress(String id, int voice, int process) {
                        Log.d(TAG, "onProgress: " + new Throwable().getStackTrace()[0]
                                + "\n * id = " + id + ", * voice = " + voice + ", * process = " + process);
                    }

                    @Override
                    public void onFailed(int errCode, String errMsg) {
                        Log.d(TAG, "onFailed: " + new Throwable().getStackTrace()[0]
                                + "\n * errCode = " + errCode + ", * errMsg = " + errMsg);
                    }
                });
                CarWXManager.getInstance().startTTS("123", "后来,我总算学会了,如何去爱,可惜你早已远去\n" +
                        "\n" +
                        "消失在人海  后来  终于在眼泪中明白\n");
                break;
            case R.id.btn_stop_tts:
                CarWXManager.getInstance().stopTTS();
                break;
            case R.id.btn_connect_to_service:
                CarWXManager.getInstance().init(this);
                break;
            case R.id.tv_car_serial:
                String serialNumber = CarWXManager.getInstance().getSerialNumber();
                carSerial.setText(carSerial.getText() + serialNumber);
                break;
            case R.id.btn_cur_theme:
                Toast.makeText(this, "当前主题: " + CarWXManager.getInstance().getCurrentTheme(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
