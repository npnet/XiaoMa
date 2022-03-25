package com.xiaoma.assistant.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import com.xiaoma.center.logic.CenterConstants;

/**
 * Created by qiuboxiang on 2019/7/12 15:38
 * Desc:
 */
public class QueryHomeAndCompanyManager {

    private static QueryHomeAndCompanyManager instance;
    private Context context;
    private onGetResultListener currentListener;
    private String queryType;

    public static QueryHomeAndCompanyManager getInstance() {
        if (instance == null) {
            synchronized (QueryHomeAndCompanyManager.class) {
                if (instance == null) {
                    instance = new QueryHomeAndCompanyManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        registerReceiver();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CenterConstants.QueryCollectConstants.RESPOND_COLLECT_DATA);
        context.registerReceiver(receiver, filter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String lonString = intent.getStringExtra(CenterConstants.QueryCollectConstants.RESPOND_LONGITUDE);
            String latString = intent.getStringExtra(CenterConstants.QueryCollectConstants.RESPOND_LATITUDE);
            String address = intent.getStringExtra(CenterConstants.QueryCollectConstants.RESPOND_ADDRESS);
            double longitude = -1;
            double latitude = -1;
            if (!TextUtils.isEmpty(lonString)) {
                longitude = Double.parseDouble(lonString) / 100000;
                latitude = Double.parseDouble(latString) / 100000;
            }
            switch (queryType) {
                case CenterConstants.QueryCollectConstants.QUERY_TYPE_HOME:
                    notifyGetHomeResult(longitude, latitude, address);
                    break;
                case CenterConstants.QueryCollectConstants.QUERY_TYPE_COMPANY:
                    notifyGetCompanyResult(longitude, latitude, address);
                    break;
            }
        }
    };

    public void queryHome(onGetResultListener listener) {
        Log.d("QBX", "queryHome: ");
        queryLonAndLat(listener, CenterConstants.QueryCollectConstants.QUERY_TYPE_HOME);
    }

    public void queryCompany(onGetResultListener listener) {
        Log.d("QBX", "queryCompany: ");
        queryLonAndLat(listener, CenterConstants.QueryCollectConstants.QUERY_TYPE_COMPANY);
    }

    private void queryLonAndLat(onGetResultListener listener, String queryType) {
        currentListener = listener;
        this.queryType = queryType;
        Intent intent = new Intent(CenterConstants.QueryCollectConstants.REQUEST_COLLECT_DATA);
        intent.putExtra(CenterConstants.QueryCollectConstants.QUERY_TYPE, queryType);
        context.sendBroadcast(intent);
    }

    public interface onGetResultListener {
        void onGetHomeResult(double longitude, double latitude, String address);

        void onGetCompanyResult(double longitude, double latitude, String address);
    }

    public static class SimpleGetResultListener implements onGetResultListener{
        @Override
        public void onGetHomeResult(double longitude, double latitude, String address) {

        }

        @Override
        public void onGetCompanyResult(double longitude, double latitude, String address) {

        }
    }

    private void notifyGetHomeResult(double longitude, double latitude, String address) {
        Log.d("QBX", "notifyGetHomeResult: longitude=" + longitude + "  latitude=" + latitude + "  address=" + address);
        if (currentListener != null) {
            currentListener.onGetHomeResult(longitude, latitude, address);
        }
    }

    private void notifyGetCompanyResult(double longitude, double latitude, String address) {
        Log.d("QBX", "notifyGetCompanyResult: longitude=" + longitude + "  latitude=" + latitude + "  address=" + address);
        if (currentListener != null) {
            currentListener.onGetCompanyResult(longitude, latitude, address);
        }
    }

}
