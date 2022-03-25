package com.xiaoma.component.nodejump;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.utils.log.KLog;

/**
 * Created by kaka
 * on 19-3-29 下午5:48
 * <p>
 * desc: #a
 * </p>
 */
public class NodeJumpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String jumpNodes = intent.getStringExtra(NodeConst.JUMP_NODES);
        Intent jumpIntent = new Intent();

        String actUri = intent.getStringExtra(NodeConst.ACTIVITY_URI);
        String pkg = intent.getStringExtra(NodeConst.ACTIVITY_PKG);
        String clz = intent.getStringExtra(NodeConst.ACTIVITY_CLZ);
        if (!TextUtils.isEmpty(actUri)) {
            Uri uri = Uri.parse(actUri);
            jumpIntent.setData(uri);
            //非目的地app接收到广播后不处理，因为rootNode的可见性判断需要本app来处理
            if (!context.getPackageName().equals(uri.getHost())) {
                return;
            }
        } else if (!TextUtils.isEmpty(pkg) && !TextUtils.isEmpty(clz)) {
            jumpIntent.setClassName(pkg, clz);
            //非目的地app接收到广播后不处理，因为rootNode的可见性判断需要本app来处理
            if (!context.getPackageName().equals(pkg)) {
                return;
            }
        } else {
            return;
        }

        jumpIntent.putExtra(NodeConst.JUMP_NODES, intent.getStringExtra(NodeConst.JUMP_NODES));
        if (NodeUtils.isFirstNodeAlive(jumpNodes)) {
            if (NodeUtils.isFirstNodeShowing(jumpNodes)) {
                ((BaseActivity) NodeUtils.ActNodeManager.getInstance().getTopActNode()).parsingNodes(jumpNodes);
            } else {
                jumpIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                NodeUtils.ActNodeManager.getInstance().getTopActNode().startActivity(jumpIntent);
            }
        } else {
            jumpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(jumpIntent);
        }
    }
}
