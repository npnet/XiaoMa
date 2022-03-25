package com.xiaoma.launcher.travel.hotel.listener;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.adapter
 *  @file_name:      RoomEtEditorActionListener
 *  @author:         Rookie
 *  @create_time:    2019/1/15 16:23
 *  @description：   TODO             */

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaoma.ui.adapter.XMBaseAbstractLvGvAdapter;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.StringUtil;

import java.util.ArrayList;

public class RoomEtEditorActionListener implements TextView.OnEditorActionListener {

    private EditText mEditText;
    private ArrayList<String> mList;
    private XMBaseAbstractLvGvAdapter<String> mBaseAdapter;
    private Context mContext;
    private Dialog mDialog;
    private int position = -1;

    public RoomEtEditorActionListener(Context context, EditText editText, XMBaseAbstractLvGvAdapter<String> baseAdapter, Dialog dialog) {
        mContext = context;
        mEditText = editText;
        mBaseAdapter = baseAdapter;
        position = -1;
        mDialog = dialog;
        mList= (ArrayList<String>) baseAdapter.getDatas();
    }

    public RoomEtEditorActionListener(Context context, EditText editText, XMBaseAbstractLvGvAdapter<String> baseAdapter, int position, Dialog dialog) {
        mContext = context;
        mEditText = editText;
        mList= (ArrayList<String>) baseAdapter.getDatas();
        mBaseAdapter = baseAdapter;
        this.position = position;
        mDialog = dialog;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (null != event && KeyEvent.KEYCODE_ENTER == event.getKeyCode()) {
            switch (event.getAction()) {
                case KeyEvent.ACTION_UP:
                    String name = mEditText.getText().toString().trim();
                    if (!StringUtil.isEmpty(name)) {
                        if (position == -1) {
                            mList.add(name);
                        } else {
                            String s = mList.get(position);
                            s = name;
                        }
                        mBaseAdapter.setDatas(mList);
                        mDialog.dismiss();
                    } else {
                        XMToast.showToast(mContext, "请输入名字");
                    }
                    return true;
                default:
                    return true;
            }
        }
        return false;
    }
}
