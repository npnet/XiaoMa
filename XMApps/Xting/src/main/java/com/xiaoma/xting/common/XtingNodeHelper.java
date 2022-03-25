package com.xiaoma.xting.common;

import android.content.Context;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.xting.R;
import com.xiaoma.xting.common.playerSource.PlayerSourceFacade;
import com.xiaoma.xting.sdk.LocalFMFactory;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/5/17
 */
public class XtingNodeHelper {

    private XtingNodeHelper() {
        throw new UnsupportedOperationException("not allowed here!");
    }

    public static void jump2MyCollect(Context context) {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.Xting3DAction.ACTION_TO_COLLECT);
        NodeUtils.jumpTo(context, CenterConstants.XTING, "com.xiaoma.xting.MainActivity",
                NodeConst.Xting.ACT_MAIN
                        + "/" + NodeConst.Xting.FGT_HOME
                        + "/" + NodeConst.Xting.FGT_MY
                        + "/" + NodeConst.Xting.FGT_MY_COLLECT);
    }

    public static void jump2Category(Context context) {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.Xting3DAction.ACTION_TO_CATEGORY_OR_PLAY_LIST);
        NodeUtils.jumpTo(context, CenterConstants.XTING, "com.xiaoma.xting.MainActivity",
                NodeConst.Xting.ACT_MAIN
                        + "/" + NodeConst.Xting.FGT_HOME
                        + "/" + NodeConst.Xting.FGT_NET
                        + "/" + NodeConst.Xting.FGT_NET_INDEX
                        + "/" + NodeConst.Xting.FGT_NET_CATEGORY);
    }

    public static void popoutPlayList(Context context) {
        if (LocalFMFactory.getSDK().isRadioOpen()) {
            XMToast.showToast(context, R.string.local_fm_not_support);
            return;
        } else {
            if (!PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()) {
                XMToast.showToast(context, R.string.no_content_to_play);
                return;
            }
        }
        XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.Xting3DAction.ACTION_TO_CATEGORY_OR_PLAY_LIST);
        NodeUtils.jumpTo(context, CenterConstants.XTING, "com.xiaoma.xting.player.ui.FMPlayerActivity",
                NodeConst.Xting.ACT_PLAYER
                        + "/" + NodeConst.Xting.FGT_PLAYER_ONLINE_DETAILS
                        + "/" + NodeConst.Xting.OPEN_PLAY_LIST);
    }

    public static void openListenToRecognize(final Context context) {
        if (LocalFMFactory.getSDK().isRadioOpen()) {
            XMToast.showToast(context, R.string.hint_operate_unsupport);
            return;
        }
        XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.Xting3DAction.ACTION_OPEN_LISTEN_TO_RECOGNIZE);
        LaunchUtils.launchApp(context, "com.xiaoma.launcher", "com.xiaoma.launcher.recmusic.ui.MusicRecDialogActivity");
    }

    public static void closePlayList(Context context) {
        if (LocalFMFactory.getSDK().isRadioOpen()) {
            XMToast.showToast(context, R.string.local_fm_not_support);
            return;
        } else {
            if (!PlayerSourceFacade.newSingleton().getPlayerControl().isCurPlayerInfoAlive()) {
                XMToast.showToast(context, R.string.no_content_to_play);
                return;
            }
        }
        XmCarFactory.getCarVendorExtensionManager().setRobAction(CenterConstants.Xting3DAction.ACTION_TO_CATEGORY_OR_PLAY_LIST);
        NodeUtils.jumpTo(context, CenterConstants.XTING, "com.xiaoma.xting.player.ui.FMPlayerActivity",
                NodeConst.Xting.ACT_PLAYER
                        + "/" + NodeConst.Xting.FGT_PLAYER_ONLINE_DETAILS
                        + "/" + NodeConst.Xting.CLOSE_PLAY_LIST);
    }
}
