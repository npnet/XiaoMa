// ISettingAidlInterface.aidl
package com.xiaoma.aidl.setting;

// Declare any non-default types here with import statements

interface ISettingDisplayAidlInterface {

     //获取当前显示模式
     int getDisplayMode();
     //设置当前显示模式
     void setDisplayMode(int mode);

     //获取屏幕亮度
     int getDisplayLevel();
     //设置屏幕亮度
     void setDisplayLevel(int level);

     //获取按键亮度
     int getKeyBoardLevel();
     //设置按键亮度
     void setKeyBoardLevel(int value);

     //获取屏保状态
     boolean getBanVideoStatus();
     //设置屏保状态
     void setBanVideoStatus(boolean value);

     //关闭屏幕
      void closeScreen();

}
