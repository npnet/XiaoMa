固定数据和动态数据打点方案使用说明（使用请参考AppStore）

这是tag是事件响应拦截后打印过滤（TAG = "com.xiaoma.autotracker"）
切点tag(TAG = "com.xiaoma.aspectj")

annotation描述：
PageDescComponent：example {"车应用首页"}
指当前点击按钮所属页面路径描述(用于Activity，Fragment上)，配置到组件上时使用着不需要关注，如手动上报时候需要填组件名称加中文描述。参考下面手动上报api
ResId：点击事件响应对应的R文件id（按钮click事件上）
NormalOnClick：按钮点击事件，只能收集固定文案
BusinessOnClick：按钮点击事件，只能收集动态文案，前提必现实现XMAutoTrackerEventOnClickListener 点击事件listener
SingleClick：防止按钮重复点击事件，默认500ms内不重复响应click事件，可以通过SingleClick配置时间
Ignore: 用于忽略事件统计使用
SingleClick：用于防止双击事件时长配置


关于click场景上报
1.需要配置注解@NormalOnClick，@ResId      固定场景不需要带业务数据按钮
findViewById(R.id.app_market).setOnClickListener(this);
findViewById(R.id.app_manager).setOnClickListener(this);

@Override
@NormalOnClick({EventConstants.NormalClick.appMarket, EventConstants.NormalClick.appManager})//按钮对应的名称
@ResId({R.id.app_market, R.id.app_manager})//按钮对应的R文件id
public void onClick(View v) {
    switch (v.getId()) {
        case R.id.app_market:
            appMarket();
            break;
        case R.id.app_manager:
            appManager();
            break;
    }
}

2.需要配置注解@NormalOnClick可以不配置（单个时候@ResId）		固定场景不需要带业务数据按钮
findViewById(R.id.app_market).setOnClickListener(new View.OnClickListener() {
    @Override
    @NormalOnClick({EventConstants.NormalClick.appMarket})
    public void onClick(View v) {
        appMarket();
    }
});

3.需要配置注解@BusinessOnClick	和实现XMAutoTrackerEventOnClickListener接口非Android原始view事件， 非固定场景需要带业务动态数据按钮
Button button = holder.getView(R.id.progressBtn);
button.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
    @Override
    public ItemEvent returnPositionEventMsg(View view) {
        return new ItemEvent(((DownloadProgressButton) view).getmCurrentText(), downLoadAppInfo.getAppInfo().getAppName());
    }

    @Override
    @BusinessOnClick
    public void onClick(View v) {
        refreshDownloadView(info.getPackageName(), downLoadAppInfo, progressButton);
    }
});

4.ListView and GridView, Adapter item点击事件上报示例
第一步：必需集成XMBaseAbstractLvGvAdapter实现上报打点收集，实现returnPositionEventMsg方法返回item打点数据
public class TestAdapter extends XMBaseAbstractLvGvAdapter<String> {

    private static final String TAG = "TestAdapter";

    public TestAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    protected void convert(XMLvViewHolder viewHolder, String s, int position) {
        TextView tv = viewHolder.getView(R.id.tv);
        tv.setText(s);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(mDatas.get(position), position + "");
    }

}
ListView listView = findViewById(R.id.listview);
TestAdapter testAdapter = new TestAdapter(this, strings, R.layout.tv);
listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        KLog.e("com.xiaoma.autotracker", parent.toString() + " -- " + position);
    }
});
listView.setAdapter(testAdapter);


5.RecycleView, 原生Android Adapter item点击事件上报示例
第一步：必需集成XMBaseAbstractRyAdapter实现上报打点收集，实现returnPositionEventMsg方法返回item打点数据
public class TestAdapter extends XMBaseAbstractRyAdapter<String> {

    private static final String TAG = "TestAdapter";

    public TestAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(mDatas.get(position), position + "");
    }

    @Override
    protected void convert(XMViewHolder holder, String s, int position) {
        TextView tv = holder.getView(R.id.tv);
        tv.setText(s);
    }
}
TestAdapter testAdapter = new TestAdapter(this, strings, R.layout.tv);
testAdapter.setOnItemClickListener(new XMBaseAbstractRyAdapter.OnItemClickListener() {
    @Override
    public void onItemClick(RecyclerView.Adapter adapter, View view, RecyclerView.ViewHolder holder, int position) {
        KLog.e("com.xiaoma.autotracker", adapter.toString() + " -- " + position);
    }
});


6.RecycleView, 三方(com.chad.library.adapter.base.BaseQuickAdapter)RecycleView Adapter item点击事件上报示例
第一步：必需集成XMBaseAbstractBQAdapter实现上报打点收集，实现returnPositionEventMsg方法返回item打点数据
public class TestAdapter extends XMBaseAbstractBQAdapter<String, BaseViewHolder> {

    private static final String TAG = "TestAdapter";

    public TestAdapter() {
        super(R.layout.tv);
    }

	//集成后实现业务数据打点上报
    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(mData.get(position), position + "");
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        TextView tv = helper.getView(R.id.tv);
        tv.setText(item);
    }
}
TestAdapter testAdapter = new TestAdapter();
testAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        KLog.e("com.xiaoma.autotracker", adapter.toString() + " -- " + position);
    }
});

7.TabLayout 的监听使用如下：
        mTabLayout.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {

            private CharSequence mTabText;

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(mTabText.toString(), "0");
            }


            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabText = changeTab(tab);
            }
        });

------------------------------------------------------------------------------------------------------------

关于页面切换场景统计：（请参考AppStore使用）
1.在每个Application中调用XmAutoTracker.getInstance().init(AppHolder.getInstance().getAppContext());
2.在每个Activity上使用@PageDescComponent注解配置你的参数
  例如：@PageDescComponent({EventConstants.PageDescribe.mainActivityPagePath, EventConstants.PageDescribe.mainActivityPagePathDesc})
       public class MainActivity extends BaseActivity
  建议将所有的配置里用到的String统一写在一个配置类里，可参考AppStore里的EventConstants类
3.支持收集Activity,Fragment组件的事件


手动打点调用方式：(详情请可以参考注解方式传参，请参考AppStore相关注解实现功能)
XmAutoTracker.getInstance().onEvent(String event,       //按钮名称(如播放,音乐列表)
                                    String id,          //对应id值(如音乐列表对应item id值)
                                    String pagePath,    //页面路径
                                    String pagePathDesc);//页面路径中午意思

XmAutoTracker.getInstance().onEvent(String event,       //参考上述
                                    String pagePath,
                                    String pagePathDesc);

example:XmAutoTracker.getInstance().onEvent("播放",           //按钮desc
                                            "1000001",        //播放歌曲id
                                            "PlayerActivity", //按钮所属页面，activity或者是fragment
                                            "全局播放器页面"); //按钮所属页面desc

------------------------------------------------------------------------------------------------------------

关于业务场景上报
1.上报歌曲信息，音乐电台里的歌曲或者节目统一定义成收听信息业务，使用如下代码
XmAutoTracker.getInstance().onEventListenInfo(String content);//参数content一定要为Json格式的String
注意:参数一定要是Json格式的String
    Json定义一定要有id和value字段，id为你的业务id，value为你的业务名称，字段不够用请使用h i j k l m 作为扩展字段，传递业务数据，比如电台里面每个专辑有很多节目，h可以定义为节目id，i定义为节目名称
    以此类推，保证调用时传递的顺序，字段值的意义一样
    对于Json格式的定义如下
    {
    	"id": "专辑id",
    	"value": "专辑名称",
    	"h": "节目id",
        "i": "节目名称"
    }

2.上报播放时长
XmAutoTracker.getInstance().onEventPlayTime(String content);
请在自己应用里手动去实现定时上报的逻辑，上报方法调用上述即可，关于定时上报可参考之前主线的想听上报逻辑在PlayService类里
特别注意:记得暂停和停止播放都需要计算播放时长。

3.其他业务
  a.首先在BusinessType枚举类新增业务类型(例如CLUBGROUPSCORE("车信部落计分"))
  b.在CollectBusinessInfo接口里增加该业务需要采集数据的方法
  c.AutoTrackDBManager实现该接口，可参照CollectBusinessInfo接口里其他方法的实现
  特别注意：如果业务数据很多，请转成Json格式作为String去传递Json格式要求如下
     {
      	 "id": "",
      	 "value": "",
      	 "h": "",
         "i": "",
         "j": "",
         "k": "",
         "l": "",
         "m": "",
         "n": ""
      }

   按照这个顺序去传递，id，value字段是必有的，剩下的hijklmn为扩展字段，扩展字段用来去补充业务数据，请需要传递业务数据的都按照此格式，同时保证字段对应的意思是一致，谢谢。
  d.在XmAutoTracker类里，增加方法去采集业务数据，方便外部调用。

------------------------------------------------------------------------------------------------------------

note:
各自App build gradle加入以下配置：
第一步：
apply plugin: 'android-aspectjx'

第二步：对应的module进行依赖事件打点module
比如AppStore
def static AppStore() {
    return [
            ':XMApps:AppStore',
            ':XMLibs:Utils',
            ':XMLibs:Config',
            ':XMLibs:VoicePrint',
            ':XMLibs:Login',
            ':XMLibs:UI',
            ':XMLibs:Component',
            ':XMLibs:Network',
            ':XMLibs:Thread',
            ':XMLibs:Image',
            ':XMLibs:Model',
            ':XMLibs:Update',
            ':XMLibs:Hotfix',
            ':XMLibs:DB',
            ':XMLibs:AutoTracker'//加入事件打点module
    ]
}

第三步：在每个应用的application中去初始化数据库模块，调用initDB()方法

        在initLibs里去调用initDB()方法

        private void initDB() {
                DBManager.getInstance().with(getApplication());
                DBManager.getInstance().initGlobalDB();
                DBManager.getInstance().initUserDB(LoginManager.getInstance().getLoginUserId());
            }

        此外还需要在每个Application中去监听用户登录状态里去重新初始化一次UserDB

        LoginManager.getInstance().addLoginEventListener(new LoginManager.LoginEventListener() {
                   @Override
                   public void onLogin(User user) {
                       if (user != null) {

                           DBManager.getInstance().initUserDB(user.getId());
                       }
                   }

                   @Override
                   public void onLogout() {
                       XmHttp.getDefault().addCommonParams("uid", "");
                   }
               });



第四步：初始化打点模块（注意一定要先初始化数据库再去初始化打点模块否则会抛异常）
Application中调用XmAutoTracker.getInstance().init(AppHolder.getInstance().getAppContext());