package com.xiaoma.systemui.common.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.xiaoma.systemui.R;
import com.xiaoma.systemui.common.constant.PackageName;

public class SosActivity extends Activity implements View.OnClickListener {
    private Button btnCarLauncher;
    private Button btnEngineeringModel;
    private Button btnSimpleLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sos);
        btnCarLauncher = findViewById(R.id.btn_car_launcher);
        btnEngineeringModel = findViewById(R.id.btn_engineering_model);
        btnSimpleLauncher = findViewById(R.id.btn_simple_launcher);
        btnCarLauncher.setOnClickListener(this);
        btnEngineeringModel.setOnClickListener(this);
        btnSimpleLauncher.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_car_launcher:
                startApk(PackageName.SYSTEM_LAUNCHER);
                break;
            case R.id.btn_engineering_model:
                startApk(PackageName.ENGINEER_MODE);
                break;
            case R.id.btn_simple_launcher:
                startActivity(new Intent(this, SimpleLauncherAct.class));
                break;
        }
    }

    private void startApk(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "App not installed", Toast.LENGTH_SHORT).show();
        }
    }
}
