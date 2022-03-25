package com.xiaoma.assistant;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.business.ui.ChooseUserActivity;
import com.xiaoma.vrfactory.tts.XmTtsManager;

public class MainActivity extends AppCompatActivity {


    private boolean canshow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_click_into_assistant).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jump to voice assistant
                if (canshow) {
                    AssistantManager.getInstance().show(false);
                }
            }
        });
        findViewById(R.id.btn_back_pressed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jump to voice assistant
                onBackPressed();
            }
        });


        findViewById(R.id.id_tts_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmTtsManager.getInstance().startSpeakingByPhone("蓝牙电话tts播报");
            }
        });
        findViewById(R.id.id_tts_assistant).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmTtsManager.getInstance().startSpeakingByAssistant("语音助手tts播报");
            }
        });
        findViewById(R.id.id_tts_third).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmTtsManager.getInstance().startSpeakingByThird("三方应用tts播报");
            }
        });
        findViewById(R.id.id_tts_navi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmTtsManager.getInstance().startSpeakingByNavi("导航tts播报");
            }
        });
        findViewById(R.id.id_tts_xiaoma).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmTtsManager.getInstance().startSpeakingByXmApp("小马应用tts播报");
            }
        });

        if (!LoginManager.getInstance().isUserLogin()) {
            ChooseUserActivity.start(this, true);
            finish();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            } else {
                canshow = true;
            }
        } else {
            canshow = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!BuildConfig.DEBUG) {
            //正式发版不显示activity
            moveTaskToBack(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    canshow = true;
                }
            }
        }
    }
}
