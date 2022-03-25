package com.qiming.fawcard.synthesize;

import android.app.Dialog;
import android.widget.Button;
import android.widget.TextView;

import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.dialog.LoadingFailDialog;

import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.shadows.ShadowDialog;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ExceptionTest extends BaseTestCase {
    LoadingFailDialog.PriorityListener priorityListener;

    @Override
    public void setUp() {
        super.setUp();
        priorityListener = Mockito.mock(LoadingFailDialog.PriorityListener.class);
    }

    private void init() {
        setUpService();
        ignition();
        setUpHomeActivity();
        bindService();
    }

    // 没有网络
    @Test
    public void onRequestFailed_NetworkDisconnect(){
        init();
        //发送失败消息“网络连接不可用”
        driverServiceController.get().onRequestFailed(QMConstant.RequestFailMessage.NETWORK_DISCONNECT);
        //测试是否显示Dialog
        Dialog latestDialog = ShadowDialog.getLatestDialog();
        assertNotNull(latestDialog);
        assertTrue(latestDialog.isShowing());
        ((LoadingFailDialog)latestDialog).setPriorityListener(priorityListener);
        //测试Dialog内容是否正确
        TextView actualTextView = latestDialog.findViewById(R.id.tv_loading_fail);
        String expectedTextView = getApplication().getString(R.string.loading_fail);
        assertEquals(expectedTextView,actualTextView.getText().toString());

        // 测试【重新加载】按钮，点击可重新连接
        Button buttonFail = latestDialog.findViewById(R.id.btn_fail);
        buttonFail.performClick();
        verify(priorityListener, times(1)).setResult(anyBoolean());
    }

    //网络信号不好
    @Test
    public void onRequestFailed_BadSignal(){
        init();
        //发送失败消息“网络出错啦，请点击按钮重新加载”
        driverServiceController.get().onRequestFailed(QMConstant.RequestFailMessage.NETWORK_BADSIGNAL);
        //测试是否显示Dialog
        Dialog latestDialog = ShadowDialog.getLatestDialog();
        ((LoadingFailDialog)latestDialog).setPriorityListener(priorityListener);
        assertNotNull(latestDialog);
        assertTrue(latestDialog.isShowing());
        //测试Dialog内容是否正确
        TextView actualTextView = latestDialog.findViewById(R.id.tv_loading_fail);
        String expectedTextView = getApplication().getString(R.string.network_error);
        assertEquals(expectedTextView,actualTextView.getText().toString());

        // 测试【重新加载】按钮，点击可重新连接
        Button buttonFail = latestDialog.findViewById(R.id.btn_fail);
        buttonFail.performClick();
        verify(priorityListener, times(1)).setResult(anyBoolean());
    }

    // APP调用后台服务时发生服务器异常或调用不成功返回系统错误
    @Test
    public void onRequestFailed_ServerError(){
        init();
        //发送失败消息“系统异常啦，请联系管理员或拨打客服热线”
        driverServiceController.get().onRequestFailed(QMConstant.RequestFailMessage.SERVER_ERROR);
        //测试是否显示Dialog
        Dialog latestDialog = ShadowDialog.getLatestDialog();
        assertNotNull(latestDialog);
        assertTrue(latestDialog.isShowing());
        ((LoadingFailDialog)latestDialog).setPriorityListener(priorityListener);
        //测试Dialog内容是否正确
        TextView actualTextView = latestDialog.findViewById(R.id.tv_loading_fail);
        String expectedTextView = getApplication().getString(R.string.system_error);
        assertEquals(expectedTextView,actualTextView.getText().toString());

        // 测试【重新加载】按钮，点击可重新连接
        Button buttonFail = latestDialog.findViewById(R.id.btn_fail);
        buttonFail.performClick();
        verify(priorityListener, times(1)).setResult(anyBoolean());
    }
}
