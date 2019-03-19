# amap-lieying-sdk
使用java语言封装了高德地图的猎鹰轨迹服务
# 一、概述
> 最初是由于公司项目的需要，使用到高德地图的猎鹰轨迹服务。使用场景是，后台对数据进行过滤并且规划出轨迹然后送给前端进行展示。高德的猎鹰服务刚好就满足这个需求。但是高德官方只提供http的接口文档，并没有其他语言的SDK。所以在做完公司这个项目之后，决定把当时写的代码放出来，有同样需求的朋友可以直接拷贝代码使用。主要是对各个http接口用java语言进行封装，然后可以使用Java代码方便的调用。


# 二、快速开始 
最简单的使用方式就是把这个项目，代码直接拷贝到工程里面。然后就可以如下使用：
```java
    
    // 表示高德客户端，使用这个类就可以管理Service
    AMapTrackClient client = new AMapTrackClient("高德key");
    
    // 通过service_id或者service_name获取已经创建的Service
    Service service = client.getService(null, "battery");
    
    // 通过终端名称找到这个service下面的终端
    Terminal terminal = service.listTerminal("860000000002019");
    
    // 为终端创建一个默认的轨迹，官方api是没有这个功能的，这是通过在终端属性字段中保存默认轨迹方式实现的
    Trace trace = terminal.createDefaultTrace();
    
    // 轨迹规划，该方法具体定义如下：
    trace.trsearch()
    
```

Trace.trsearch方法定义：

```java

      /**
	 * 对应高德API https://lbs.amap.com/api/track/lieying-kaifa/api/grasproad 返回
	 * 纠偏或者补充后的点
         *
	 * @param starttime 可为空
	 * @param endtime   可为空
	 * @param recoup    可为空 表示是否填充距离大于gap的点
	 * @param gap       定义两点之间需要补点的最小距离 50<= gap <=10000 单位m
	 * @param page      查询第几页
	 * @param pagesize  每页数据条数
	 * @return Points
	 */
	public Points trsearch(Long starttime, Long endtime, Boolean recoup, Integer gap, Integer page, Integer pagesize)
``` 
# 三、 最后
Service、Terminal、Trace这些都有增删查改的方法，这里就不列举了，可以直接看代码。


