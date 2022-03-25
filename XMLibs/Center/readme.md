## 中心服务说明

### 一、如何使用
#### 初始化
```java
    // 初始化Center
    Center.getInstance().init(getApplication());
```

#### 向其他客户端发送消息
```java
    SourceInfo local = new SourceInfo(getApplication().getPackageName(), 1080);
    SourceInfo remote = new SourceInfo("co m.xiaoma.xting", 8888);
    RequestHead head = new RequestHead(remote, action);
    Request request = new Request(local, head, null);
    Linker.getInstance().send(request);
```

#### 注册Client用于接收消息
```java
    Center.getInstance().runAfterConnected(new Runnable() {
        @Override
        public void run() {
            Center.getInstance().register(client.getSource(), client);
        }
    });
```

#### Client实现类事项
可以根据业务需要,每个独立的业务使用一个对应的Client进行管理<br><br>
                                 
#### StateManager
StateManager是一个单例, 接收所有Center连接中产生的所有事件, 并提供事件监听器注册接口, 具体事件参考StateListener的各个回调
 - onPrepare: CenterServer进程已启动,有可能是第一次启动,也有可能是再次启动
 - onBindService: 本地进程开始绑定CenterServer进程时回调
 - onConnected: 本地进程绑定CenterServer进程,连接成功时回调
 - onDisconnected: 本地进程绑定CenterServer进程后,主动断开时回调
 - onClientIn: 当CenterServer有新Client接入时回调
 - onClientOut: 当CenterServer有Client退出时回调

#### 与Client交互的方式

 - send(...)<br/>
 send方法是单纯地从local发送一个消息到remote, 可以携带一些数据, 发送完此次交互即结束;
 
 - request(...)<br/>
 request方法用于local端向remote端请求数据, remote端应该在收到请求后收集相关数据, 然后通过callback回调数据, __remote端不应该多次回调__;
 
 - connect(...)<br/>
 connect方法用于local端监听remote端的某些状态变化,因为需要持续监听,所以remote端需要 __持有callback实例__ , 并在状态变化时回调, __可以多次回调__, 需要注意的是, 相同的RequestHead, 无论local端产生了多少个, 均只会有一个callback实例注册到remote端, 所以只需要持有一个callback实例即可.<br/>

| LOCAL | CENTER | REMOTE |
|---|---|---|
|  Linker.connect<br/>  └ LocalProxy ←───<br/>&emsp;&emsp;(ReqeustHead)<br/>&emsp;&emsp;&emsp;├ callback1<br/>&emsp;&emsp;&emsp;├ callback2<br/>&emsp;&emsp;&emsp;└ ... |&emsp;&emsp;&emsp;&emsp;AgentLinker.connect<br/>───┐&emsp;└ AgentProxy ←───<br/> &emsp;&ensp;&emsp;&emsp;&ensp;│&emsp;&emsp;(RequestHead)<br/>&emsp;&ensp;&emsp;&emsp;&ensp;└─→&ensp;├ callback1<br/> &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;&ensp;├ callback2<br/> &emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&ensp;&ensp;└ ...    | RemoteClient<br> ─→ callback&emsp;&emsp;<br> <br> <br> <br> <br> |                

---

### 二、概念介绍

 * __Location:__      <br>- 第一级, 指代 __一个进程__ , 一般使用应用包名, 也可以自定义, 不可重复

 * __Port:__          <br>- 第二级, 指代 __同一个进程__ 中为其他进程提供的 __不同的服务__ ,不同服务可以用根据port来区分, 也就是说同一个进程中不可以声明重复的port

 * __Action:__        <br>- 第三级, 指代 __同一个服务__ 中 __不同的事件__ , 不同事件可以使用根据action来区分, 同一个服务中可以定义多个Action区分不同事件
 
 * __SourceInfo:__    <br>- SourceInfo = Location + Port, 标识一个进程对外的提供的服务

 * __Client:__        <br>- Client = Location + Port + Action(一个或多个), 进程通过Center将Clint注册到CenterServer, 表示该进程提供此服务, 其他进程判断此Client存在的话,即可通过send/request/connect三种方式与这个服务交互

 * __RequestHead:__   <br>- SourceInfo_remote + Action, 标识一个注册到CenterServer的Client

 * __Request:__       <br>- RequestHead + SourceInfo_local, 标识一次业务交互, 当需要与其他业务交互时, 需要提供一个Request, 然后通过Linker进行交互, 交互的方式即send/request/connect<br><br>
                                                                                                                
---

### 三、技术细节

#### 1、三级连接策略
为了尽可能减少进程间的远程连接,所以采用三级连接策略:

 __LOCAL  >  AGENT  > REMOTE__
 
下面对每个层级进行介绍:

 - __LOCAL__ <br> 这个层级属于服务请求方,它需要别的程序为它提供一些服务,典型的例子就是:<br>Launcher中需要控制音乐的播放暂停,这个时候Launcher就处于 __LOCAL__ 层级
 
 - __AGENT__ <br> 这个层级对于使用者是不存在的,它作为一个中间媒介,为 __LOCAL__ 和 __REMOTE__ 提供连接功能
 
 - __REMOTE__ <br> 这个层级属于服务提供方,他可以根据不同的业务,创建出一个Client,通过Center注册,然后 __LOCAL__ 层级就可以跟他进行交互了
 
另外值得一提的是, 一个程序不局限于 __LOCAL__ 层级或 __REMOTE__ 层级,只要业务有需求,那它既可以扮演请求方, 也可以扮演提供方, 可以根据业务来自由配置<br><br>

 ---
 
#### 2、三级连接策略如何减少进程间的链接
三级连接策略通过两方面减少因为业务而产生的远程连接:

 - __中心服务__ <br><br>
 如果两个程序之间互相需要对方提供服务,那么至少需要两次连接,那如果是多个程序呢?<br>
 最极端的情况下,程序两两进行连接,那么连接数量会达到 __n²-n__ (n指多少个进程) 次,由此可见连接的数量会指数级地增长.<br>
 中心服务的引入,使得所有的进程只需要跟中心服务进行连接,既可以与其他进程进行交互, 也就是说连接数恒等于 __n__ (n指多少个进程).<br>
 因为产生远程连接至少需要两个进程或以上,所以以上问题可以转化为以下数学问题:<br>
   __n ≤ n²-n (n≥2)__ <br>
 求解后会发现, 只需要满足 __n≥2__ ,那么采用中心服务的远程连接数量就会比自由连接少.<br><br>
 
 - __回调兼并__ <br><br>
 一个Remote程序为多个Local程序提供服务的情况下,同一个业务可能有多个Local程序注册回调,
 其实这些回调在 __REMOTE__ 层级中只需要回调一次即可,
 所以在注册回调时,__LOCAL__ 会将这些回调进行兼并,
 同时在 __AGENT__ 层级中也做了相同的回调兼并,这么做使得跨进程的回调数量减少.<br>
 兼并过后的的回调情况是:<br>
 __LOCAL:__  <br>- 维护多个LocalConnect, 只注册一个AgentConnect到 __AGENT__ ;<br>
 __AGENT:__  <br>- 维护多个AgentConnect, 只注册一个RemoteConnect到 __REMOTE__ ;<br>
 __REMOTE:__ <br>- 只维护一个AgentConnect;<br><br>
 注:因为有回调兼并的特性,所以在重写Client( __REMOTE__ 层级)的connect方法时,同一个action只会收到一个IClientCallback对象,所以不需要用列表进行维护IClientCallback, 而且,如果服务产生事件, 需要回调时, 可以通过 Client 的 isConnectActionValid(int action) 接口判断 action 是否还有监听器, 如果没有没有监听器, 是可以无需回调时间的, 当然不做判断直接回调也没有问题的.
 


