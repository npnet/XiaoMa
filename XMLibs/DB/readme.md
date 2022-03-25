#### 使用说明

数据库增删改查等方法抽为接口IDatabase，具体实现由其实现类实现。当前使用的第三方库为lite-orm，若需切换三方库,只需添加一个IDatabase的实现类，并在DBManager作相应切换即可。

使用前，先初始化：

```
DBManager.init(this);
```

使用时，通过 DBManager 访问接口方法：

```
DBManager.getDBManager().xx
```

