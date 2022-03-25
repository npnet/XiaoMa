# 以Activity作为根节点的Fragment节点跳转框架使用介绍

## 实现的目标
能接收外部**节点链**消息，基于Activity跳转到该Activity内部的任一Fragment及嵌套Fragment界面
## 几个重要的接口
1. `Node`接口定义了**节点**的相关接口：当前节点信息，下一个节点信息，根据当前的节点显示情况解析节点链,具体的节点跳转任务的分发
2. `RootNode`接口继承自`Node`接口，定义了**根节点**的相关接口：节点链信息的管理,跳转状态管理，以及所有子节点信息的获取
3. `ChildNode`接口继承自`Node`接口，定义了**子节点**的相关接口：子节点是否已经对用户可见，获取子节点的父节点

**注意**：由于fragemnt的add方法没有任何可见性的方法回调在使用该框架的时候请使用replace替代add

`RootNode`和`ChildNode`接口在本项目中已经分别由BaseActivity和BaseFragment实现。
## 接入步骤
1. 首先要在NodeConst中定义好对应页面的节点名。并在相关的Activity和Fragment中覆写getThisNode()并返前面定义好的节点名。
2. 在根节点(也即是Activity)中复写`Node#handleJump(String nextNode)`方法，其中`nextNode`为本页面需要跳转的下一个节点名。  

    我们需要在这个方法中根据不同的`nextNode`信息，实现具体的跳转到`nextNode`的业务逻辑，并根据逻辑返回是否处理成功(**这里的返回信息十分重要，一定不要随便返回!**)。
3. 在子节点(也即是Fragment)中也复写`Node#handleJump(String nextNode)`方法，其中`nextNode`为本页面需要跳转的下一个节点名。  

    我们需要在这个方法中根据不同的`nextNode`信息，实现具体的跳转到`nextNode`的业务逻辑，并根据逻辑返回是否处理成功(**这里的返回信息十分重要，一定不要随便返回!**)。
4. 主动跳转可调用方法:
    - NodeUtils.jumpTo(Context context, String actUri,String nodeChains)
        - 使用scheme协议的方式进行跳转，使用该方法时,传递的actUri中的host部分必须为包名
    - NodeUtils.jumpTo(Context context, String pkg, String fullClzName, String nodeChains)
        - 使用包名及类名的方式进行跳转
5. 上面跳转的`nodeChains`，结构如下：  
    Activity根节点名/fragment节点名/子fragment节点名/子fragment节点名/...  
    拿想听为例，要跳转到我的播放历史页面，根据如下图定义的节点名：  
    需要`nodeChains`定义为"XTING_ACT_MAIN/XTING_FGT_HOME/XTING_FGT_MY/XTING_FGT_MY_HISTORY"

    ![![Xting页面](https://images.gitee.com/uploads/images/2019/0423/120848_aab31770_2025789.png "2019-04-23_12-07-23.png")](https://images.gitee.com/uploads/images/2019/0423/120709_b4239ae5_2025789.png "屏幕截图.png")
    