package com.xiaoma.music.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import com.xiaoma.utils.log.KLog;

/**
 * @author zs
 * @date 2018/11/16 0016.
 */
public class MusicUtils {
    /*public static void setMarquee(BaseViewHolder helper, MarqueeTextView marqueeTextView, int curPosition) {
        int position = helper.getAdapterPosition();
        if (curPosition == position) {
            marqueeTextView.startMarquee();
        } else {
            marqueeTextView.stopMarquee();
        }
    }*/

    //    /**
    //     * 无论文字长度都会滚动的textview
    //     *
    //     * @param helper
    //     * @param marqueeTextView
    //     * @param curPosition
    //     */
    /*public static void setMarquee(BaseViewHolder helper, AutoScrollTextView marqueeTextView, int curPosition) {
        int position = helper.getAdapterPosition();
        if (curPosition == position) {
            marqueeTextView.startMarquee();
        }
//        else {
//            marqueeTextView.stopMarquee();
//        }
    }*/

    /*public static boolean isPlay(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            int status = OnlineMusicFactory.getKWPlayer().getStatus();
            return IKuwoConstant.IAudioStatus.PLAYING == status;
        }
        return false;
    }*/

    /*public static boolean isNetConnected(Context context) {
        if (!NetworkUtils.isConnected(context)) {
            XMToast.showToast(context, context.getString(R.string.no_network), true);
            return false;
        }
        return true;
    }*/

    public static boolean isNetConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] allNetworks = manager.getAllNetworks();
        for (Network network : allNetworks) {
            NetworkInfo networkInfo = manager.getNetworkInfo(network);
            if (isEthernetConnected(networkInfo) || isWifiConnected(networkInfo)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEthernetConnected(NetworkInfo networkInfo) {
        return networkInfo != null
                && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET
                && networkInfo.isConnected();
    }

    private static boolean isWifiConnected(NetworkInfo networkInfo) {
        return networkInfo != null
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                && networkInfo.isConnected();
    }
}
