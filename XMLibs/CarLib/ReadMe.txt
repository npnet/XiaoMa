关于方控按键监听com.xiaoma.carlib.wheelcontrol.XmWheelManager
多个进程注册的监听器事件,将以栈的形式存储和分发,即:按键事件总是分发给最后一个注册的监听器,直到此监听器被注销.
另外,对于注册过的监听器,如果其所在的进程已经死亡,则会跳过该监听器,并自动被注销;按键事件会分发给下一个有效的监听器.
请各个应用模块维护好按键的注册/注销行为,避免应用错误长占方控按键.

以下是方法介绍:
init(Context context)
使用之前需要调用此方法对模块进行初始化,如需要全局监听建议在Application内进行初始化.

release()
释放方控监听模块,调用此方法之后,所有通过register注册过的监听器将无法再监听到方控按键.

register(OnWheelKeyListener listener, int[] keyCodes)
初始化完成之后,即可通过此方法注册按键事件监听.
参数:
listener: 按键监听器,将回调按键的行为(按下,抬起,短按,长按),以及按键对应的code(KeyCode).按键行为及按键码相关的常量在com.xiaoma.carlib.wheelcontrol.WheelKeyEvent中有定义.
keyCodes: 需要监听的按键码,需要注意的是: 只有在keyCodes内声明过的按键,才会在listener中收到相应按键的回调; 同一个listener对象重复注册,将会覆盖上一次注册过的按键.

unregister(OnWheelKeyListener listener)
注销按键事件的监听,重复注销或者注销一个未注册过的监听,不会有异常.
参数:
listener: 按键监听器.

OnWheelKeyListener # boolean onKeyEvent(int keyAction, int keyCode);
参数:
keyAction: 按键行为,如按下,抬起,单击,长按.
keyCode:   按键码.
返回值: 返回true表示消费当前按键,不再继续分发;返回false表示不消费当前按键,将继续往下分发.