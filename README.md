# Android

#### Android 知识框架图

https://github.com/MemoryLimitExceeded/Android-Learning/blob/master/20180414161936480.png

#### Android Guide

https://developer.android.google.cn/guide.html

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

#### Android

- 四大组件
  - Activity
    - 概览

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
