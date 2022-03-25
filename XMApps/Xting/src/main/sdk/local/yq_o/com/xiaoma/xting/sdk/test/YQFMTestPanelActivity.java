package com.xiaoma.xting.sdk.test;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaoma.utils.GsonHelper;
import com.xiaoma.xting.sdk.LocalFM;
import com.xiaoma.xting.sdk.LocalFMFactory;
import com.xiaoma.xting.sdk.LocalFMStatusListener;
import com.xiaoma.xting.sdk.TestPanelActivity;
import com.xiaoma.xting.sdk.model.BandType;
import com.xiaoma.xting.sdk.model.XMRadioStation;

/**
 * @author youthyJ
 * @date 2018/12/4
 */
public class YQFMTestPanelActivity extends TestPanelActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalFMFactory.getSDK().addLocalFMStatusListener(new LocalFMStatusListener() {
            @Override
            public void onRadioOpen() {

            }

            @Override
            public void onRadioClose() {

            }

            @Override
            public void onTuningAM(int frequency) {

            }

            @Override
            public void onTuningFM(int frequency) {

            }

            @Override
            public void onScanStart() {

            }

            @Override
            public void onNewStation(XMRadioStation station) {
                setResult(GsonHelper.toJson(station));
            }

            @Override
            public void onScanAllResult(List<XMRadioStation> stations) {
                setResult(GsonHelper.toJson(stations));
            }

            @Override
            public void onBandChanged(BandType band) {

            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }

    @Override
    protected void setupItemData() {
        itemList.add(new Item("检查连接状态").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                boolean isConnected = sdk.isConnected();
                setResult("连接状态: " + isConnected);
            }
        }));

        itemList.add(new Item("检查初始化状态").setTask(new Task() {
            @Override
            protected void run() {
                boolean isInited = LocalFMFactory.getSDK().isInited();
                setResult("初始化状态: " + isInited);
            }
        }));

        itemList.add(new Item("打开Radio").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                boolean success = sdk.openRadio();
                setResult("打开Radio状态: " + success);
            }
        }));

        itemList.add(new Item("关闭Radio").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                sdk.closeRadio();
                setResult("已调用关闭Radio接口");
            }
        }));

        itemList.add(new Item("判断是否有音频焦点").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                boolean hasAudioFocus = sdk.isHasAudioFocus();
                setResult("是否有音频焦点: " + hasAudioFocus);
            }
        }));

        itemList.add(new Item("获取当前波段类型").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                BandType currentBand = sdk.getCurrentBand();
                setResult("当前的波段类型: " + currentBand);
            }
        }));

        itemList.add(new Item("获取当前电台信息").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                XMRadioStation currentStation = sdk.getCurrentStation();
                setResult("当前电台信息: " + currentStation);
            }
        }));

        itemList.add(new Item("切换到FM").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                sdk.switchBand(BandType.FM);
                setResult("已调用切换到FM接口");
            }
        }));

        itemList.add(new Item("切换到AM").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                sdk.switchBand(BandType.AM);
                setResult("已调用切换到AM接口");
            }
        }));

        itemList.add(new Item("自动搜台").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                boolean success = sdk.scanAll();
                setResult("自动搜台: " + success);
            }
        }));

        itemList.add(new Item("向下搜台").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                boolean success = sdk.scanDown();
                setResult("向下搜台: " + success);
            }
        }));


        itemList.add(new Item("向上搜台").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                boolean success = sdk.scanUp();
                setResult("向上搜台: " + success);
            }
        }));


        itemList.add(new Item("下一个电台").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                boolean success = sdk.stepNext();
                setResult("下一个电台: " + success);
            }
        }));

        itemList.add(new Item("上一个电台").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                boolean success = sdk.stepPrevious();
                setResult("上一个电台: " + success);
            }
        }));

        itemList.add(new Item("设置AM 666").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                boolean success = sdk.tuneAM(666);
                setResult("设置AM 666: " + success);
            }
        }));


        itemList.add(new Item("设置FM 107.9").setTask(new Task() {
            @Override
            protected void run() {
                LocalFM sdk = LocalFMFactory.getSDK();

                boolean success = sdk.tuneFM(107900);
                setResult("设置FM 107.9: " + success);
            }
        }));

    }


}
