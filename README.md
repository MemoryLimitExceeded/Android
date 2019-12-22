# Android-Learning

#### Android 知识框架图

https://github.com/MemoryLimitExceeded/Android-Learning/blob/master/20180414161936480.png

#### Android Guide

https://developer.android.google.cn/guide.html

#### 计算机网络

计算机网络自顶向下第 x 版

[韩立刚老师主讲 计算机网络第5版谢希仁编写 95集(网络安全章节部分不全)](https://www.bilibili.com/video/av9876107)

注：按照因特网 5 层模型分层

- 物理层：接口标准 电器标准 如何在物理链路上传输更快的速度
  - 基本概念
  - 数据通信基础知识
    - 奈氏准则和香农公式
  - 传输媒体
  - 信道复用技术
  - 数字传输系统
  - 宽带接入技术
- 数据链路层：帧的开始和结束 透明传输 差错校验
  - 基本概念
  - 三个问题
    - 封装成帧
    - 透明传输
    - 差错校验
      - 循环冗余检验(CRC)
  - 链路协议
    - HDLC
    - PPP
  - 两种情况下的数据链路层
    - 使用点对点信道的数据链路层
    - 使用广播信道的数据链路层
  - CSMA/CD
    - 争用期
    - 二进制指数类型退避算法
  - 以太局域网
    - 概述
    - 拓扑
    - 信道利用率
    - MAC 层
      - MAC 帧格式
  - 扩展局域网
  - 高速以太网
- 网络层：负责选择最佳路径 规划IP地址
  - 基本概念
  - 如何转发数据包
    - 应用层：准备要传输的文件
    - 传输层：将文件分段，编号
    - 网络层：添加目标 IP 地址和源 IP 地址
    - 数据链路层：使用子网掩码判断自己和目标主机是否在同一网段
      - 同一网段，ARP协议解析目标 IP 地址所对应的 MAC 地址
      - 不同网段，查找网关 MAC 地址，往网关发送数据包
  - 网络设备
  - IP 协议
    - RIP
    - OSPF
  - ARP 协议和 RARP (在七层模型中位于数据链路层)
    - ARP 欺骗
  - ICMP 协议
  - IGMP 协议
  - IP 数据包格式
- 传输层：可靠传输 流量控制 不可靠传输
  - UDP
    - 数据包格式
    - 应用场景
  - TCP
    - 数据包格式
    - 实现
      - 快重传
      - 选择传输
      - 累计确认
      - 滑动窗口
    - 流量控制
    - 拥塞控制
    - 运输连接管理
      - 三次握手
      - 四次挥手
    - 应用场景
- 应用层

[韩立刚老师主讲 计算机网络第5版谢希仁编写 全集156](https://www.bilibili.com/video/av23124815)

#### 操作系统

现代操作系统第 x 版

[操作系统_清华大学(向勇、陈渝)](https://www.bilibili.com/video/av6538245)

- 计算机体系结构和内存分层体系
- 地址空间
- 内存
  - 内存碎片
    - 外部碎片
    - 内部碎片
  - 内存分配
    - 连续内存分配
      - 分配算法
        - 第一适配
        - 最佳适配
        - 最差适配
      - 压缩式
      - 交换式
    - 非连续内存分配
      - 分段
      - 分页
      - 页表
        - 多级页表
        - 反向页表
- 虚拟内存
  - 覆盖技术
  - 交换技术
  - 页面置换算法

#### Kotlin

Kotlin 实战

- Kotlin 基础
  - 变量、运算符、流程控制
  - 函数
    - 顶层函数
    - 扩展函数
    - 局部函数
  - 类
    - 访问修饰符
      - final：不能被重写，类和类中成员默认使用
      - open：可以被重写，需要明确表明
      - abstract：必须被重写，只能在抽象类中使用，抽象成员不能有实现
      - override：重写父类或接口中的成员，如果没有使用 final 表明，重写的成员默认是开放的
    - 可见性修饰符：类成员，顶层声明
      - public（默认）：所有地方可见，所有地方可见
      - internal：模块中可见，模块中可见
      - protected：子类可见，——
      - private：类中可见，文件中可见
    - 内部类和嵌套类
    - 密封类

#### JVM

#### Android

##### 入门

第一行代码

- 概述
- Activity
  - 用法
  - Intent
    - 显式
    - 隐式
  - 生命周期
  - 启动模式
    - standard
    - singleTop
    - singleTask
    - singleInstance
- UI 基本组件
  - 常用控件
  - 布局
  - 滑动控件
    - ListView
    - RecyclerView
- Fragment
  - 用法
  - 生命周期
  - 动态加载 Fragment
- 广播机制
  - 概述
    - 无序广播
    - 有序广播
  - 注册方式
    - 静态注册
    - 动态注册
  - 本地广播
- 数据存储
  - 文件
  - SharedPreferences
  - SQLite
  - LitePal
- 权限
  - Android 6.0 以前
  - Android 6.0 至 Android 10.0 以前
  - Android 10.0 以后
- 内容提供器
- 通知
- 网络
  - XML 解析
    - Pull
    - SAX
  - Json 解析
- 服务
  - 两种启动方式

##### 进阶

- Material Design
- View 体系
  - 事件分发
    - dispatchTouchEvent()
    - onInterceptTouchEvent()
    - onTouchEvent()
  - 工作流程
    - measure
    - layout
    - draw
  - 动画
    - [类型](https://blog.csdn.net/carson_ho/article/details/79860980)
      - View Animation (视图动画)
        - [Drawable Animation](https://blog.csdn.net/carson_ho/article/details/73087488) (帧动画)
        - [Tween Animation](https://blog.csdn.net/carson_ho/article/details/72827747) (补间动画)
      - [Property Animation](https://developer.android.google.cn/guide/topics/graphics/prop-animation) (属性动画)
    - [过渡动画](https://developer.android.com/training/transitions?hl=zh_cn) (Transition 框架)
      - 内容过渡动画
      - 共享元素过渡动画
      - 场景过渡动画
      - 共享元素过渡动画 + 揭露效果

##### 深入底层

深入理解 Android 内核设计思想

前置技能：操作系统，Java，Linux 操作系统原理，C++

- Android 进程和线程
  - 进程、线程
  - Handler、MessageQueue、Runnable、Looper
  - UI 主线程
  - Thread、 ThreadLocal
- ActivityManagerService
  - Activity Stack
  - startActivity 流程

##### 框架相关

[RxJava](https://github.com/ReactiveX/RxJava/wiki) 3.x

- 概念
  - Observer (观察者)
  - Observable (被观察者) / Flowable (支持背压)
  - 线程控制——Scheduler
    - Schedulers.immediate()：直接在当前线程运行，相当于不指定线程，这是默认的 Scheduler
    - Schedulers.newThread()：在每执行一个任务时创建一个新的线程，不具有线程缓存机制
    - Schedulers.io()：适合运行I/0和阻塞操作
    - Schedulers.computation()：用于CPU 密集型计算任务，即不会被 I/O 等操作限制性能的耗时操作
    - Schedulers.trampoline()：在当前线程立即执行任务，如果当前线程有任务在执行，则会将其暂停，等插入进来的任务执行完之后，再将未完成的任务接着执行。
    - AndroidSchedulers.mainThread()：Android 主(UI)线程
    - Schedulers.single()：拥有一个线程单例，所有的任务都在这一个线程中执行，当此线程中有任务执行时，其他任务将会按照先进先出的顺序依次执行。
    - Scheduler.from(@NonNull Executor executor)：指定一个线程调度器，由此调度器来控制任务的执行策略。
- 原理
  - 流转换原理
  - 线程控制原理
  - [流控机制](http://zhangtielei.com/posts/blog-rxjava-backpressure.html)
    - 背压（Backpressure）
      - ERROR
      - DROP
      - LATEST
      - BUFFER
      - MISSING
    - 节流（Throttling）
    - 打包处理
    - 调用栈阻塞（Callstack blocking）

RxJava入门：https://www.jianshu.com/p/15b2f3d7141a（讲的是 RxJava 2）



Retrofit

- 功能扩展
  - OkHttpClient
  - addConverterFactory
  - addCallAdapterFactory
- 

##### 设计模式

- 六大原则
  - 单一职责原则
  - 里氏替换原则
  - 依赖倒置原则
  - 接口隔离原则
  - 迪米特原则
  - 开闭原则
- 设计模式
  - 创建型模式
    - 工厂模式
    - 抽象工厂模式
    - 单例模式
    - 建造者模式
    - 原型模式
  - 结构型模式
    - 适配器模式
    - 桥接模式
    - 组合模式
    - 装饰器模式
    - 外观模式
    - 享元模式
    - 代理模式
  - 行为型模式
    - 责任链模式
    - 命令模式
    - 解释器模式
    - 迭代器模式
    - 中介者模式
    - 备忘录模式
    - 观察者模式
    - 模板模式
    - 访问者模式
  - 其他模式
    - 过滤器模式
    - 简单工厂模式

#### 其他文章

[地址栏中输入网址后发生了什么](http://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247485074&idx=1&sn=db1c122d5aedae4d342b5adc415fa607&chksm=ebd74793dca0ce85f747f114b83a1400847d6d39ee9c05190f0e868b21b8239cfcd73aed88c0&mpshare=1&scene=23&srcid=#rd)

[为什么我玩游戏那么卡](http://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247485082&idx=1&sn=7844aab2dec93abbb2083dbde2cb07aa&chksm=ebd7479bdca0ce8d217008008c313d8cfeaabe7d78f78802799e714fa660d2c42cd2971961b9&mpshare=1&scene=23&srcid=#rd)

[Android：你不能忽略的代码命名规范](https://www.jianshu.com/p/b7a644ea0d25)

[一个HTTP打趴80%面试者](https://mp.weixin.qq.com/s?__biz=MzAwNTM0ODY1Mg==&mid=2457116071&idx=1&sn=1d3fc05837fac690829e43fc135e25be&chksm=8c9e31e4bbe9b8f2a64c223c48a458ab4af27146fa6d63d367b89792d6ff7f34887702b2ceeb&token=393364506&lang=zh_CN#rd)

[Java集合框架面试题（详解）](https://blog.csdn.net/qq_42651904/article/details/89089218)

[搞定计算机网络面试，看这篇就够了](https://mp.weixin.qq.com/s?__biz=MzUyOTk5NDQwOA==&mid=2247485219&idx=1&sn=c4b24be02bc3311fe161669555f00406&chksm=fa59c0e4cd2e49f2a7d6ab760feb3dfbba2cbc77da2a9c4ac450b9c5a78f5be8859e1aa586af&mpshare=1&scene=23&srcid=#rd)

[面试江湖：一招破解 Java 集合类面试题](https://mp.weixin.qq.com/s?__biz=MzUyOTk5NDQwOA==&mid=2247485204&idx=1&sn=efb545ad88dec3d17207f8a9d71458be&chksm=fa59c0d3cd2e49c53414128bae049e1d658bc8677896fa4b134f64537ae24dd38868b5ee0ab7&mpshare=1&scene=23&srcid=#rd)

[什么是消息队列](https://mp.weixin.qq.com/s?__biz=MzI4Njg5MDA5NA==&mid=2247485080&idx=1&sn=f223feb9256727bde4387d918519766b&chksm=ebd74799dca0ce8fa46223a33042a79fc16ae6ac246cb8f07e63a4a2bdce33d8c6dc74e8bd20&mpshare=1&scene=23&srcid=0409miJTefirATUKMH8DPy63#rd)

[漫话：如何给女朋友解释什么是乐观锁与悲观锁](https://mp.weixin.qq.com/s?__biz=Mzg3MjA4MTExMw==&mid=2247485251&idx=1&sn=c11d4aaafa072ff00c2d7199ca9bfc8e&chksm=cef5f4f5f9827de3811bad3dbe452a71c78a968d3b6ded2fb5f96b1720cd2d34b85745fbba22&mpshare=1&scene=23&srcid=#rd)

[搞定操作系统面试，看这篇就够了](https://mp.weixin.qq.com/s?__biz=MzUyOTk5NDQwOA==&mid=2247485229&idx=1&sn=398d01889dcc7149bd74ad015258837b&chksm=fa59c0eacd2e49fc379236b83fa3ef7185cda259174966f5af0f51605be48c5c1f323ffe0661&mpshare=1&scene=23&srcid=#rd)

[一篇文章告诉你MVC、MVP、MVVM](https://mp.weixin.qq.com/s?__biz=MzI0MjE3OTYwMg==&mid=2649553050&idx=1&sn=246b773ebcf18469c44fd225a1395e58&chksm=f11813e7c66f9af1df4608f34f39e7813c6cc84c0c2bdcff1212a0fc3cb34152294f20e11905&mpshare=1&scene=23&srcid=#rd)

[搞懂分布式与高并发和多线程，看这篇就够了](http://mp.weixin.qq.com/s?__biz=MzUyOTk5NDQwOA==&mid=2247485317&idx=1&sn=c5a0f84074c0e101ff0464d298ed22d7&chksm=fa59c042cd2e4954550b2d3a552e56868535ea751cca7d2b2ca850d33e6f65f9627d8584820a&mpshare=1&scene=23&srcid=#rd)

 