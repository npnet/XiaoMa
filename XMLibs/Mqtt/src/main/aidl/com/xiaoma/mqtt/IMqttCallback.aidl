package com.xiaoma.mqtt;

interface IMqttCallback {

     oneway void onSuccessConnect();

     oneway void onFailConnect();

}
