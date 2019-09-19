# Android-Learning

#### Android 知识框架图

https://github.com/MemoryLimitExceeded/Android-Learning/blob/master/20180414161936480.png

#### Android Guide

https://developer.android.google.cn/guide.html

#### 计算机网络

计算机网络自顶向下第 x 版

[韩立刚老师主讲 计算机网络第5版谢希仁编写 95集(网络安全章节部分不全)](https://www.bilibili.com/video/av9876107)

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
  - 扩展局域网
  - 高速以太网

[韩立刚老师主讲 计算机网络第5版谢希仁编写 全集156](https://www.bilibili.com/video/av23124815)

#### 操作系统

现代操作系统第 x 版

[操作系统_清华大学(向勇、陈渝)](https://www.bilibili.com/video/av6538245)

- 计算机体系结构和内存分层体系
- 地址
- 内存分配
  - 连续内存分配
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

#### JVM

#### Android

##### 入门

第一行代码

##### 进阶

Android 进阶之光

##### 深入底层

深入理解 Android 内核设计思想

前置技能：操作系统，Java，Linux 操作系统原理

- Android 进程和线程
  - 进程、线程
  - Handler、MessageQueue、Runnable、Looper
  - UI 主线程
  - Thread、 ThreadLocal
- ActivityManagerService
  - Activity Stack
  - startActivity 流程

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

