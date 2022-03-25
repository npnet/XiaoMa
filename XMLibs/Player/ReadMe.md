# Player 模块说明

本模块适用于有音频播放的module，共享音频信息和状态到Launcher，以及接收并处理从Launcher触发的播放控制事件

## 接入说明
接入主要使用 com.xiaoma.player.PlayerManager 或 com.xiaoma.player.PlayerManagerHelper这两个类。
后者是对前者的一次简单封装和通用的逻辑预处理。

- 初始化：
    一般在Application中调用`PlayerManagerHelper.getInstance().init(Context context)`进行初始化。

- 注册播放控制监听
    调用`PlayerManagerHelper.getInstance().addOnControlListener(PlayerManagerHelper.OnControlListener listener)`注册播放控制监听。

- 共享音频信息和状态
    调用`PlayerManagerHelper#getInstance()#setNewAudioInfo(AudioInfo audioInfo)`进行音频信息设置。
    调用`PlayerManagerHelper#getInstance()#setNewAudioStatus(PlayerConstants.AudioStatus.*)`进行音频播放状态设置。

示例：

```java
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.player.*;

public class Xting extends BaseApp {
    @Override
    public void onCreate(boolean isMainProcess) {
        super.onCreate(isMainProcess);
        if (!isMainProcess) {
            return;
        }
        
        // 其他初始化逻辑...      
       
        // 初始化PlayerManagerHelper
        PlayerManagerHelper.getInstance().init(getApplication());
        
        // 注册播放控制监听
        PlayerManagerHelper.getInstance().addOnControlListener(new PlayerManagerHelper.OnControlListener() {
            @Override
            public void onControl(@AudioType int audioType, @AudioOption int option) {
                /**
                 * 接受Launcher过来的控制事件
                 * audioType    为int类型的音频类型常量，用于区分音频类型及来源，有新接入的来源需要在{@link PlayerConstants.AudioTypes}中手动添加
                 * option       为int类型的控制事件常量，主要的控制事件定义在{@link PlayerConstants.Options}中
                 */
            }
        });
        
        // 在适合的时机和地方，向Launcher共享音频信息
        AudioInfo audioInfo = new AudioInfo();
        audioInfo.setTitle("Title");
        audioInfo.setSubTitle("SubTitle");
        PlayerManagerHelper.getInstance().setNewAudioInfo(audioInfo);
        
        // 在适合的时机和地方，向Launcher共享音频播放状态
        PlayerManagerHelper.getInstance().setNewAudioStatus(PlayerConstants.AudioStatus.PLAYING);
    }
}
```

