#### 使用说明

事件上报分为直接上报和批量上报

- 直接上报，需传入相应的事件id.
- 批量上报，需传入相应的事件类型，事件类型分为如下几种：
  - 操作日志上报
  - 位置上报
  - 流量统计上报

直接上报，调用

```
EventAgent.getInstance().onEvent(xxx);
```

批量上报，调用

```
EventAgent.getInstance().onBatchEvent(xxx);
```

