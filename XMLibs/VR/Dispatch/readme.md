## 语音指令分发模块说明

### 一、App端如何使用

- __引入Module__ <br>
请在modules.gradle中找到需要注册语音助理指令的AppModule,然后引入以下LibsModule:<br><br>
__'\:XMLibs\:VR\:Dispatch',__

- __初始化__ <br>
在BaseApp中已经添加了初始化代码,所以不需要再重复初始化.<br>
如果是独立的App可以手动初始化:
```
DispatchManager.getInstance().init(context);
```


- __注册语音指令: 通过注解注册__ <br>
 在继承了BaseActivity / VisibleFragment 的类中,可以使用注解直接注册语音指令<br><br>
 __注意事项:__ <br>
 1\. 方法的访问权限请设置为 __public__ ;<br>
 2\. 方法的返回值请设置为 __void__ ;<br>
 3\. 方法可以随意命名;<br>
 4\. 方法可以配置为无参,或者携带一个 __String__ 类型参数,如果接收到对应的语音指令,该方法会被调用,如果携带 __String__ 参数,那么会收到对应的指令内容;<br>
```
    @Command("语音指令")
    public void test() { ... } 
    
    @Command("另一个语音指令")
    public void test(String command) { ... }
```

- __注册语音指令: 手动注册__ <br>
 使用 __com.xiaoma.vr.dispatch.Dispatch__ 工具可以动态的注册语音指令:
 
 ```
 Dispatch.with(handler.getClass())
    .match("语音指令")
    .event(new Dispatch.Event() {
        @Override
        public void onEvent() {
            // 收到了指令
        }
    })
    .done();
 ```
- __开启/关闭唤醒词__ <br>
 使用 __com.xiaoma.vr.dispatch.Wakeup__ 工具可以动态添加唤醒词:
```
Wakeup.register(context, "激活唤醒词");
Wakeup.remove(context, "取消唤醒词");
```
 
- __移除语音指令__ <br>
 1\. 通过注解的形式注册的语音指令,会与对应的Activity/Fragment的可见性自动同步,所以不需要手动移除;<br><br>
 2\. 手动注册的语音指令需要在语音指令不再需要的时候手动移除掉:
```
Dispatch.remove("语音指令");
```
 
 
### 二、Assistant端如何使用

 - __分发指令__ <br>
 Assistant调用以下接口可以直接分发语音指令:
 ```
Command command = new Command(commandContent);
Result result = DispatchManager.getInstance().notifyCommand(command);
 ```
 

 - __处理唤醒词__ <br>
 Assistant设置用于处理唤醒词的Handler:
```
Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case BUSINESS_ACTION_ACTIVATE_WAKEUP_WORD: {
                WakeupWord word = (WakeupWord) msg.obj;
                // TODO: 2019/3/24 处理关键字注册 
                break;
            }
            case BUSINESS_ACTION_CANCEL_WAKEUP_WORD: {
                WakeupWord word = (WakeupWord) msg.obj;
                // TODO: 2019/3/24 处理关键字取消 
                break;
            }
        }
    }
};
DispatchManager.getInstance().setWakeupWordHandler(handler);
```