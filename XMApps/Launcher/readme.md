### 执行connect协议
request:
Request connectRequest = new Request(mLocalSourceInfo, new RequestHead(source, AudioConstants.Action.PLAYER_CONNECT), null);
  
response:
key:AudioConstants.BundleKey.ACTION
value:AUDIO_PROGRESS        音频进度
      AUDIO_STATE           音频状态
      AUDIO_INFO            音频信息
      AUDIO_FAVORITE        音频是否收藏
      AUDIO_DATA_SOURCE     音频数据来源（音频数据来源是小马后台还是xmly,kuwo）
      CONNECT_STATE:        USB、蓝牙连接状态


### 执行send协议
#暂停 
request:AudioConstants.Action.Option.PAUSE

#收藏  
request:AudioConstants.Action.Option.FAVORITE

#播放器点击播放、列表点击播放、收藏列表点击播放、USB列表点击播放
request:通过封装AudioCategoryBean   key:AudioConstants.BundleKey.EXTRA  ---value:categoryBean
1.播放器点击播放
categoryBean.setAction(AudioConstants.PlayAction.DEFAULT)
categoryBean.setCategoryId(0);
categoryBean.setIndex(0);
                 
2.列表点击播放
categoryBean.setAction(AudioConstants.PlayAction.PLAY_LIST_AT_INDEX)
categoryBean.setCategoryId(categoryId);//节目id
categoryBean.setIndex(index);//播放的index
         
3.收藏列表点击播放
如果是电台收藏
categoryBean.setAction(AudioConstants.PlayAction.PLAY_ALBUM_AT_INDEX)
categoryBean.setCategoryId(categoryId);//专辑id
categoryBean.setIndex(index);//播放的index

4.USB列表点击播放
categoryBean.setAction(AudioConstants.PlayAction.PLAY_LIST_AT_INDEX)
categoryBean.setUsbPath(usbPath);//usbPath
categoryBean.setIndex(index);//播放的index

### 执行request协议
#上下曲切换 
上一首option=AudioConstants.Action.Option.PREVIOUS
下一首option=AudioConstants.Action.Option.NEXT
request:
Bundle bundle = new Bundle();
bundle.putInt(AudioConstants.BundleKey.ACTION, option);
Request request = new Request(mLocalSourceInfo, new RequestHead(getSourceInfoByAudioType(audioType),
AudioConstants.Action.PLAYER_CONTROL), bundle);

response:
key:AudioConstants.BundleKey.AUDIO_CONTROL_CODE
value:AudioConstants.AudioControlCode.TOP 点击上一曲,表示已经加载到顶部
      AudioConstants.AudioControlCode.BOTTOM 点击下一曲,表示已经加载到底部
      AudioConstants.AudioControlCode.MIDDLE 正常状态切换


#获取收藏列表
request:
Bundle searchPurpose = new Bundle();
searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.FAVORITE);
Request searchRequest = new Request(local, new RequestHead(PlayerConnectHelper.getInstance().getSourceInfoByAudioType(audioType),
AudioConstants.Action.SEARCH), searchPurpose);
                
response:
key:AudioConstants.BundleKey.AUDIO_RESPONSE_CODE
value:成功 AudioConstants.AudioResponseCode.SUCCESS
      失败 AudioConstants.AudioResponseCode.ERROR
                 
key:AudioConstants.BundleKey.AUDIO_LIST
value:收藏列表数据


#获取音频列表
#当前音频列表
request:
Bundle searchPurpose = new Bundle();
searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.CURRENT);
Request searchRequest = new Request(local, new RequestHead(connectHelper.getSourceInfoByAudioType(audioType),
AudioConstants.Action.SEARCH), searchPurpose);
                
#分页音频列表
request:
Bundle searchPurpose = new Bundle();
searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.PAGE_LIST);
searchPurpose.putInt(AudioConstants.BundleKey.CURRENT_PAGE, page);
Request searchRequest = new Request(local, new RequestHead(connectHelper.getSourceInfoByAudioType(audioType),
AudioConstants.Action.SEARCH), searchPurpose);
                
response:
response:
key:AudioConstants.BundleKey.AUDIO_RESPONSE_CODE
value:成功 AudioConstants.AudioResponseCode.SUCCESS
      失败 AudioConstants.AudioResponseCode.ERROR
                 
key:AudioConstants.BundleKey.AUDIO_LIST
value:列表数据
key:AudioConstants.BundleKey.PAGE_INFO
value:分页信息
key:AudioConstants.BundleKey.AUDIO_PLAYING_INDEX
value:音频播放index

#桌面切换分类播放
request:
Bundle searchPurpose = new Bundle();
searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_ACTION, AudioConstants.SearchAction.SEARCH_RESULT);
searchPurpose.putInt(AudioConstants.BundleKey.SEARCH_CATEGORY_ID, categoryId);
Request searchRequest = new Request(mLocalSourceInfo, new RequestHead(PlayerConnectHelper.getInstance().getSourceInfoByAudioType(audioType),
AudioConstants.Action.SEARCH), searchPurpose);

response:
key:AudioConstants.BundleKey.AUDIO_RESPONSE_CODE
value:成功 AudioConstants.AudioResponseCode.SUCCESS
      失败 AudioConstants.AudioResponseCode.ERROR
      
#OnlineInfoSource数据划分
桌面栏目点击的音频，包括USB，蓝牙，本地FM为桌面数据    OnlineInfoSource=LAUNCHER
桌面收藏点击的音频，和音乐、电台内部点击为各自应用数据  OnlineInfoSource=XMLY/KUWO
但是,当音乐播放的是USB,蓝牙音乐时,为桌面数据.         OnlineInfoSource=LAUNCHER
当电台播放的是FM时,为桌面数据。                      OnlineInfoSource=LAUNCHER

如果应用需要在Launcher启动后，拉起该应用，需要做如下操作
在Launcher应用的values-arrays.xml文件里，做如下配置，直接加上该应用包名
 <string-array name="launcher_apps">
        <item>com.xiaoma.club</item>
        <item>com.xiaoma.personal</item>
        <item>com.xiaoma.shop</item>
        <item>com.xiaoma.xkan</item>
        <item>com.xiaoma.motorcade</item>
    </string-array>

做完这步后，我们会在Launcher启动后在SplashActivity里拉起该应用。


关于息屏，亮屏操作：
息屏广播ACTION：com.xiaoma.turnoff.screen.action
亮屏广播ACTION：com.xiaoma.turnon.screen.action
