## Skill使用说明

### 一、初始化
1、在modules.gradle中为App引入Skill模块
```
':XMLibs:VR:Skill'
```
2、在合适的时机初始化SkillManager
```
SkillManager.getInstance().init(appContext);
```

### 二、语音助理如何分发技能
1、语音助理直接执行SkillManager#execSkill即可分发技能,以下为代码示例:
```
SkillManager.getInstance().execSkill("测试测试", skill, "aaa", new ExecCallback() {
    @Override
    public void onSuccess(String command, ExecResult result) {
        Log.d(TAG, " * onSuccess"
               + "\n * command" + command
               + "\n * result" + result
        );
    }

    @Override
    public void onCancel(String command) {
        Log.d(TAG, " * onCancel"
               + "\n * command" + command
        );
    }

    @Override
    public void onFailure(int code, String command) {
        Log.d(TAG, " * onFailure"
               + "\n * command" + command
               + "\n * code" + code
        );
    }
});
```
2、调用此方法可能返回的错误码:
- -1: 技能无效
- -2: 远程客户端无法唤醒
- -3: 分发过程中出现错误,分发中断
- -4: 远程App没有配置返回值
---

### 三、App如何声明技能
在App自身的AndroidManifest.xml中声明技能描述,多个技能可以使用"|"号隔开,以下为代码示例:
```
<meta-data
    android:name="com.xiaoma.skill"
    android:value="skill1|skill2"/>
```

### 四、App如何响应技能下发
1、在SkillDispatcher注册SkillHandler,可以实现监听技能分发,需要根据是否处理返回消费结果,__另外如果在onSkill返回了true,则有义务进行callback__,以下为代码示例:
```
SkillDispatcher.getInstance().addSkillHandler(new SkillHandler() {
    @Override
    public boolean onSkill(String command, Skill skill, SkillCallback callback) {
        // TODO 执行技能
        // 如果返回true,则有义务调用callback.onExec回调结果
        ExecResult result = new ExecResult("TTS播报内容", "约定好的返回数据");
        callback.onExec(result);
        return true;
    }
});
```
2、ExecResult中code可能会包含以下值:
-  0: 正常调用
- -1: App收到的Skill非法
- -2: App收到了Skill,但是没有处理
