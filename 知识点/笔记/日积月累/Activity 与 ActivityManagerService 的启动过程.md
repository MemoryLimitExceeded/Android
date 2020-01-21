参考资料：[Android 8.0 ActivityManagerService 启动流程](https://www.jianshu.com/p/98ccde25a57c)

​                   [Android四大组件之Activity--管理方式](https://duanqz.github.io/2016-02-01-Activity-Maintenance)（这个博主写的其他文章都很不错，推荐也都去看一看）

​                   [基于Android 9.0的Activity启动流程源码分析](https://www.jianshu.com/p/e1153fed5b23) 	

SDK 版本：29.0.2

从 Android 的启动流程开始讲起。

Android 设备启动要经历三个阶段，Boot Loader、Linux Kernel 和 Android 系统服务，而第一个启动的进程是 init 进程，然后由 init 进程创建出其他进程（其中就有 Zygote 进程）。ServiceManager 进程用于 Binder 机制，这里不过多提及。 ActivityManagerService 是寄存在 SystemServer 中的，它会在系统启动时创建一个线程来进行工作。而 SystemServer 由 Zygote 进程创建（Android 的大多数应用进程和系统进程都是由它来创建的），而入口就是 ZygoteInit 的 main 函数：

```
/*ZygoteInit*/
@UnsupportedAppUsage
public static void main(String argv[]) {
    ...
    try {
    	...
    	if (startSystemServer) {
            Runnable r = forkSystemServer(abiList, zygoteSocketName, zygoteServer);

            // {@code r == null} in the parent (zygote) process, and {@code r != null} in the
            // child (system_server) process.
            if (r != null) {
                r.run();
                return;
            }
        }
        ...
    }
    ...
}
private static Runnable forkSystemServer(String abiList, String socketName,
        ZygoteServer zygoteServer) {
    ...
    try {
    	...
    	/* Request to fork the system server process */
    	pid = Zygote.forkSystemServer(
                parsedArgs.mUid, parsedArgs.mGid,
                parsedArgs.mGids,
                parsedArgs.mRuntimeFlags,
                null,
                parsedArgs.mPermittedCapabilities,
                parsedArgs.mEffectiveCapabilities);
    }
    ...
    /* For child process （也就是 SystemServer 进程）*/
    if (pid == 0) {
        if (hasSecondZygote(abiList)) {
            waitForSecondaryZygote(socketName);
        }

        zygoteServer.closeServerSocket();
        return handleSystemServerProcess(parsedArgs);	//启动各种支撑系统运行的Sytem Server
    }

    return null;
}

/*Zygote*/
public static int forkSystemServer(int uid, int gid, int[] gids, int runtimeFlags,
        int[][] rlimits, long permittedCapabilities, long effectiveCapabilities) {
    ...
	int pid = nativeForkSystemServer(
            uid, gid, gids, runtimeFlags, rlimits,
            permittedCapabilities, effectiveCapabilities);
    ...
}
private static native int nativeForkSystemServer(int uid, int gid, int[] gids, int runtimeFlags,
        int[][] rlimits, long permittedCapabilities, long effectiveCapabilities);
```

Zygote 进程调用了 native 的方法 fork 了 SystemServer 进程，上面的步骤了解就行，重点放在 ActivityManagerService$^{①}$ 代码即可，由于 SystemServer 在新进程中，接下来就看 SystemServer 的 main 函数：

```
/*SystemServer*/
/**
 * The main entry point from zygote.
 */
public static void main(String[] args) {
    new SystemServer().run();
}
private void run() {
	...
	// Start services.
    try {
        ...
        startBootstrapServices();		//系统引导服务
        ...
    }
    ...
}
private void startBootstrapServices() {
	...
	mActivityManagerService = ActivityManagerService.Lifecycle.startService(
            mSystemServiceManager, atm);
    ...
}

/*ActivityManagerService.Lifecycle*/
public static final class Lifecycle extends SystemService {
	private final ActivityManagerService mService;
    private static ActivityTaskManagerService sAtm;

    public Lifecycle(Context context) {
        super(context);
        mService = new ActivityManagerService(context, sAtm);
    }
    public static ActivityManagerService startService(
            SystemServiceManager ssm, ActivityTaskManagerService atm) {
        sAtm = atm;
        return ssm.startService(ActivityManagerService.Lifecycle.class).getService();
    }
    ...
    public ActivityManagerService getService() {
        return mService;
    }
}

/*SystemServiceManager*/
/*
 * Creates and starts a system service. The class must be a subclass of
 * 这里通过反射的方法获得了 Lifecycle 实例
 */
@SuppressWarnings("unchecked")
public <T extends SystemService> T startService(Class<T> serviceClass) {
    try {
        ...
        try {
            Constructor<T> constructor = serviceClass.getConstructor(Context.class);
            service = constructor.newInstance(mContext);
        }
        ...
        startService(service);
        return service;
    }
    ...
}
```

它通过 SystemServiceManager 获得了 Lifecycle 实例，再调用 getService 方法获取到了 ActivityManagerService 实例，这样 SystemServer 就持有了这个实例。

不过 ActivityManagerService 还没有启动起来，再看 startService 方法，它调用的是 SystemService 的抽象方法 onStart，ActivityManagerService.Lifecycle 继承于它。

```
/*SystemServiceManager*/
public void startService(@NonNull final SystemService service) {
    ...
    // Start it.
    ...
    try {
        service.onStart();
    }
    ...
}

/*ActivityManagerService.Lifecycle*/
public static final class Lifecycle extends SystemService {
    private final ActivityManagerService mService;
    ...
    @Override
    public void onStart() {
        mService.start();
    }
    ...
}

/*ActivityManagerService*/
private void start() {
    removeAllProcessGroups();
    mProcessCpuThread.start();
    mBatteryStatsService.publish();
    mAppOpsService.publish(mContext);
    Slog.d("AppOps", "AppOpsService published");
    LocalServices.addService(ActivityManagerInternal.class, new LocalService());
    mActivityTaskManager.onActivityManagerInternalAdded();
    mUgmInternal.onActivityManagerInternalAdded();
    mPendingIntentController.onActivityManagerInternalAdded();
    // Wait for the synchronized block started in mProcessCpuThread,
    // so that any other access to mProcessCpuTracker from main thread
    // will be blocked during mProcessCpuTracker initialization.
    try {
        mProcessCpuInitLatch.await();
    } catch (InterruptedException e) {
        Slog.wtf(TAG, "Interrupted wait during start", e);
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Interrupted wait during start");
    } 
}
```

这样 ActivityManagerService 就算是启动起来了（ActivityManagerService 还在 startBootstrapServices 方法中进行了一系列的设置，这里就不一一列举出来了）。

ActivityManagerService 持有一个叫做 ActivityTaskManagerService 的类，它的描述是这样的：

```
System service for managing activities and their containers (task, stacks, displays,... ).
用于管理 activities 及其容器（任务，堆栈，屏幕显示等）的系统服务。
```

然后 ActivityManagerService 还持有这一个对象，刚才调用 ActivityManagerService 构造方法的时候，就开始创建 ActivityTaskManagerService。

```
@VisibleForTesting
public ActivityTaskManagerService mActivityTaskManager;

// Note: This method is invoked on the main thread but may need to attach various
// handlers to other threads.  So take care to be explicit about the looper.
public ActivityManagerService(Context systemContext, ActivityTaskManagerService atm) {
	...
	mActivityTaskManager = atm;
	...
}
```

ActivityTaskManagerService 从何而来？我们回想一下，这个构造方法是在 ActivityManagerService.Lifecycle 的 startService 方法中调用的，而它又是从 SytemServer 的 startBootstrapServices 调用的，往回一找，看到：

```
ActivityTaskManagerService atm = mSystemServiceManager.startService(
            ActivityTaskManagerService.Lifecycle.class).getService();
```

这个家伙也是使用相同的方法创建的，继续跟进：

```
/*SystemServiceManager*/
@SuppressWarnings("unchecked")
public <T extends SystemService> T startService(Class<T> serviceClass) {
    try {
        ...
        try {
            Constructor<T> constructor = serviceClass.getConstructor(Context.class);
            service = constructor.newInstance(mContext);
        }
        ...
        startService(service);
        return service;
    }
    ...
}

/*ActivityTaskManagerService.Lifecycle*/
public static final class Lifecycle extends SystemService {
    private final ActivityTaskManagerService mService;

    public Lifecycle(Context context) {
        super(context);
        mService = new ActivityTaskManagerService(context);
    }
	...
    public ActivityTaskManagerService getService() {
        return mService;
    }
}
```

ActivityManagerService 就获取到了 ActivityTaskManagerService 实例。

我们回到 SystemServer 类的 run 方法再接着往下看：

```
private void run() {
	...
	// Start services.
    try {
        ...
        startBootstrapServices();		//系统引导服务
        startCoreServices();			//核心服务
        startOtherServices();			//其他服务
        ...
    }
    ...
}
```

不用关心核心服务，就看其他服务：

```
/*SystemServer*/
/**
 * Starts a miscellaneous grab bag of stuff that has yet to be refactored and organized.
 */
private void startOtherServices() {
    ...
    // We now tell the activity manager it is okay to run third party
    // code.  It will call back into us once it has gotten to the state
    // where third party code can really run (but before it has actually
    // started launching the initial applications), for us to complete our
    // initialization.
    // 这段话我翻不了，google 翻译的不太通顺，顺便贴一下
    // 现在，我们告诉活动管理器可以运行第三方代码。 一旦到达了可以真正运行第三方代码的状态
    // （但实际上尚未启动初始应用程序之前），它将回叫我们，以便我们完成初始化。
    mActivityManagerService.systemReady(() -> {
        ...
    }, BOOT_TIMINGS_TRACE_LOG);
}

/*ActivityManagerService*/
public void systemReady(final Runnable goingCallback, TimingsTraceLog traceLog) {
    traceLog.traceBegin("PhaseActivityManagerReady");
    synchronized(this) {
        if (mSystemReady) {
            // If we're done calling all the receivers, run the next "boot phase" passed in
            // by the SystemServer
            if (goingCallback != null) {
                goingCallback.run();
            }
            return;
        }
        ...
    }
    ...
    synchronized (this) {
    	...
    	mAtmInternal.startHomeOnAllDisplays(currentUserId, "systemReady");
    	...
    }
}
@VisibleForTesting
public ActivityTaskManagerInternal mAtmInternal;
```

它调用了 ActivityTaskManagerInternal 的 startHomeOnAllDisplays 方法要启动桌面了。不过 ActivityTaskManagerInternal 可是一个抽象类，ActivityManagerService.LocalService 继承了它。并且它调用的是 RootActivityContainer.startHomeOnAllDisplays。

```
/*ActivityTaskManagerInternal*/
/**
 * Activity Task manager local system service interface.
 */
public abstract class ActivityTaskManagerInternal {
	...
}

/*ActivityManagerService.LocalService*/
final class LocalService extends ActivityTaskManagerInternal {
	...
	@Override
    public boolean startHomeOnAllDisplays(int userId, String reason) {
        synchronized (mGlobalLock) {
            return mRootActivityContainer.startHomeOnAllDisplays(userId, reason);
        }
    }
	...
}

/*RootActivityContainer*/
boolean startHomeOnAllDisplays(int userId, String reason) {
    boolean homeStarted = false;
    for (int i = mActivityDisplays.size() - 1; i >= 0; i--) {
        final int displayId = mActivityDisplays.get(i).mDisplayId;
        homeStarted |= startHomeOnDisplay(userId, reason, displayId);
    }
    return homeStarted;
}
```

RootActivityContainer 的定义是：

```
Root node for activity containers.
activity 容器的根节点。
```

它通过 ActivityTaskManagerService 构造来的：

```
/*RootActivityContainer*/
RootActivityContainer(ActivityTaskManagerService service) {
    mService = service;
    mStackSupervisor = service.mStackSupervisor;
    mStackSupervisor.mRootActivityContainer = this;
}

/*ActivityTaskManagerService*/
public void initialize(IntentFirewall intentFirewall, PendingIntentController intentController, Looper looper) {
	...
	mRootActivityContainer = new RootActivityContainer(this);
	...
}

/*ActivityManagerService*/
public ActivityManagerService(Context systemContext, ActivityTaskManagerService atm) {
	...
	mActivityTaskManager = atm;
    mActivityTaskManager.initialize(mIntentFirewall, mPendingIntentController,
            DisplayThread.get().getLooper());
	...
}
```

在创建 ActivityManagerService 的时候，RootActivityContainer 就被创建了。

mActivityDisplays 变量是：

```
/**
 * List of displays which contain activities, sorted by z-order.
 * The last entry in the list is the topmost.
 * 包含 activity 的显示列表，按 z-order 排序。列表中的最后一个条目是在最上面的。
 */
private final ArrayList<ActivityDisplay> mActivityDisplays = new ArrayList<>();
```

那 ActivityDisplay 是？

```
/**
 * Exactly one of these classes per Display in the system. Capable of holding zero or
 * more attached {@link ActivityStack}s.
 */
 每一个物理屏幕对应着一个实例，能够容纳零个或多个 ActivityStacks。
```

因为 Android 是支持多屏显示的，而 mActivityDisplays 就存着物理屏幕上的 ActivityStack。ActivityStack 又包含 TaskStack，TaskStack 又包含 ActivityRecord。

搬运工来了，以下文字和图片来自开头给的资料链接：

**![img](C:\Users\Administrator\Desktop\1-activity-maintenace-structure.png)**

*图中的方框可以理解为一个中包含关系：譬如一个 TaskRecord 中包含多个 ActivityRecord ; 图中的连接线可以理解为等价关系，譬如同一个 ActivityRecord 会被 TaskRecord 和 ProcessRecord 引用，两者是从不同维度来管理 ActivityRecord 。*

- *ActivityRecord 是 Activity 管理的最小单位，它对应着一个用户界面，是 AMS调度 Activity的基本单位。*
- *TaskRecord 是一个栈式管理结构，每一个 TaskRecord 都可能存在一个或多个 ActivityRecord，栈顶的ActivityRecord 表示当前可见的界面。启动 Activity时，需要找到 Activity的宿主任务，如果不存在，则需要新建一个，也就是说所有的 ActivityRecord都必须有宿主。*
- *ActivityStack 是一个栈式管理结构，每一个 ActivityStack 都可能存在一个或多个 TaskRecord，栈顶的TaskRecord 表示当前可见的任务。*
- *ActivityStackSupervisor 管理着多个 ActivityStack，但当前只会有一个获取焦点 (Focused)的 ActivityStack。*
- *ProcessRecord 记录着属于一个进程的所有 ActivityRecord，运行在不同 TaskRecord 中的 ActivityRecord 可能是属于同一个 ProcessRecord。AMS采用 ProcessRecord 这个数据结构来维护进程运行时的状态信息，当创建系统进程 (system_process) 或应用进程的时候，就会通过 AMS初始化一个 ProcessRecord。*

 不过上面的文章是 16 年的，目前可能会有些改动，但不妨碍我们理解它的机制。接下来继续跟进（我这个英语渣就不翻译下面这些注释了）：

```
/*RootActivityContainer*/
boolean startHomeOnDisplay(int userId, String reason, int displayId) {
    return startHomeOnDisplay(userId, reason, displayId, false /* allowInstrumenting */,
            false /* fromHomeKey */);
}

/**
 * This starts home activity on displays that can have system decorations based on 
 * displayId -
 * Default display always use primary home component.
 * For Secondary displays, the home activity must have category SECONDARY_HOME and then 
 * resolves according to the priorities listed below.
 *  - If default home is not set, always use the secondary home defined in the config.
 *  - Use currently selected primary home activity.
 *  - Use the activity in the same package as currently selected primary home activity.
 *    If there are multiple activities matched, use first one.
 *  - Use the secondary home defined in the config.
 */
boolean startHomeOnDisplay(int userId, String reason, int displayId, boolean allowInstrumenting, boolean fromHomeKey) {
    // Fallback to top focused display if the displayId is invalid.
    if (displayId == INVALID_DISPLAY) {
        displayId = getTopDisplayFocusedStack().mDisplayId;
    }
    Intent homeIntent = null;
    ActivityInfo aInfo = null;
    if (displayId == DEFAULT_DISPLAY) {
        homeIntent = mService.getHomeIntent();
        aInfo = resolveHomeActivity(userId, homeIntent);
    } else if (shouldPlaceSecondaryHomeOnDisplay(displayId)) {
        Pair<ActivityInfo, Intent> info = resolveSecondaryHomeActivity(userId, displayId);
        aInfo = info.first;
        homeIntent = info.second;
    }
    if (aInfo == null || homeIntent == null) {
        return false;
    }

    if (!canStartHomeOnDisplay(aInfo, displayId, allowInstrumenting)) {
        return false;
    }

    // Updates the home component of the intent.
    homeIntent.setComponent(new ComponentName(aInfo.applicationInfo.packageName, aInfo.name));
    homeIntent.setFlags(homeIntent.getFlags() | FLAG_ACTIVITY_NEW_TASK);
    // Updates the extra information of the intent.
    if (fromHomeKey) {
        homeIntent.putExtra(WindowManagerPolicy.EXTRA_FROM_HOME_KEY, true);
    }
    // Update the reason for ANR debugging to verify if the user activity is the one that
    // actually launched.
    final String myReason = reason + ":" + userId + ":" + UserHandle.getUserId(
            aInfo.applicationInfo.uid) + ":" + displayId;
    mService.getActivityStartController().startHomeActivity(homeIntent, aInfo, myReason,
            displayId);
    return true;
}

/*ActivityTaskManagerService*/
Intent getHomeIntent() {
    Intent intent = new Intent(mTopAction, mTopData != null ? Uri.parse(mTopData) : null);
    intent.setComponent(mTopComponent);
    intent.addFlags(Intent.FLAG_DEBUG_TRIAGED_MISSING);
    if (mFactoryTest != FactoryTest.FACTORY_TEST_LOW_LEVEL) {
        intent.addCategory(Intent.CATEGORY_HOME);
    }
    return intent;
}

/**
 * The default Display id, which is the id of the built-in primary display
 * assuming there is one.
 * 默认 Display ID，即假设有一个内置主 Display ID。（我可以理解为是手机屏幕）
 */
public static final int DEFAULT_DISPLAY = 0;
```

看到了吗，获取了一个叫 homeIntent 的实例，然后调用了 startHomeActivity，在此之前先看看它前面都是什么：

```
ActivityTaskManagerService mService;

/*ActivityTaskManagerService*/
ActivityStartController getActivityStartController() {
    return mActivityStartController;
}
private ActivityStartController mActivityStartController;
public void initialize(IntentFirewall intentFirewall, PendingIntentController intentController, Looper looper) {
	...
	mRootActivityContainer = new RootActivityContainer(this);
	...
	mActivityStartController = new ActivityStartController(this);
	...
}

/*ActivityStartController*/
void startHomeActivity(Intent intent, ActivityInfo aInfo, String reason, int displayId) {
	...
	mLastHomeActivityStartResult = obtainStarter(intent, "startHomeActivity: " + reason)
            .setOutActivity(tmpOutRecord)
            .setCallingUid(0)
            .setActivityInfo(aInfo)
            .setActivityOptions(options.toBundle())
            .execute();
    ...
}
/**
 * @return A starter to configure and execute starting an activity. It is valid until after
 * {@link ActivityStarter#execute} is invoked. At that point, the starter should be
 * considered invalid and no longer modified or used.
 */
ActivityStarter obtainStarter(Intent intent, String reason) {
    return mFactory.obtain().setIntent(intent).setReason(reason);
}

private final Factory mFactory
```

Factory 从哪来？

```
/*ActivityStartController*/
ActivityStartController(ActivityTaskManagerService service) {
    this(service, service.mStackSupervisor,
        new DefaultFactory(service, service.mStackSupervisor,
            new ActivityStartInterceptor(service, service.mStackSupervisor)));
}
@VisibleForTesting
ActivityStartController(ActivityTaskManagerService service, ActivityStackSupervisor supervisor,
        Factory factory) {
    mService = service;
    mSupervisor = supervisor;
    mHandler = new StartHandler(mService.mH.getLooper());
    mFactory = factory;
    mFactory.setController(this);
    mPendingRemoteAnimationRegistry = new PendingRemoteAnimationRegistry(service,
            service.mH);
}

/*ActivityStarter.DefaultFactory*/
static class DefaultFactory implements Factory {
    ...
    DefaultFactory(ActivityTaskManagerService service,
            ActivityStackSupervisor supervisor, ActivityStartInterceptor interceptor)         {
        mService = service;
        mSupervisor = supervisor;
        mInterceptor = interceptor;
    }
    ...
    @Override
    public ActivityStarter obtain() {
        ActivityStarter starter = mStarterPool.acquire();	//SynchronizedPool

        if (starter == null) {
            starter = new ActivityStarter(mController, mService, mSupervisor, mInterceptor);
        }
        return starter;
    }
    ...
}

/*ActivityStarter*/
ActivityStarter(ActivityStartController controller, ActivityTaskManagerService service,
        ActivityStackSupervisor supervisor, ActivityStartInterceptor interceptor) {
    mController = controller;
    mService = service;
    mRootActivityContainer = service.mRootActivityContainer;
    mSupervisor = supervisor;
    mInterceptor = interceptor;
    reset(true);
}
```

先来看新来的这两个类：

```
/**
 * Controller for delegating activity launches.
 * 委派 activity 启动的控制器。
 * This class' main objective is to take external activity start requests and prepare them into
 * a series of discrete activity launches that can be handled by an {@link ActivityStarter}. It is
 * also responsible for handling logic that happens around an activity launch, but doesn't
 * necessarily influence the activity start. Examples include power hint management, processing
 * through the pending activity list, and recording home activity launches.
 */
public class ActivityStartController
 
 /**
 * Controller for interpreting how and then launching an activity.
 * 就是启动 activity 的控制器
 * This class collects all the logic for determining how an intent and flags should be turned into
 * an activity and associated task and stack.
 */
class ActivityStarter
```

再接着往下看：

```
/**
 * Starts an activity based on the request parameters provided earlier.
 * @return The starter result.
 * 根据之前提供的请求参数 activity 活动。
 */
int execute() {
   try {
        // TODO(b/64750076): Look into passing request directly to these methods to allow
        // for transactional diffs and preprocessing.
        if (mRequest.mayWait) {
            return startActivityMayWait(mRequest.caller, mRequest.callingUid,
                    mRequest.callingPackage, mRequest.realCallingPid, mRequest.realCallingUid,
                    mRequest.intent, mRequest.resolvedType,
                    mRequest.voiceSession, mRequest.voiceInteractor, mRequest.resultTo,
                    mRequest.resultWho, mRequest.requestCode, mRequest.startFlags,
                    mRequest.profilerInfo, mRequest.waitResult, mRequest.globalConfig,
                    mRequest.activityOptions, mRequest.ignoreTargetSecurity, mRequest.userId,
                    mRequest.inTask, mRequest.reason,
                    mRequest.allowPendingRemoteAnimationRegistryLookup,
                    mRequest.originatingPendingIntent, mRequest.allowBackgroundActivityStart);
        } else {
            return startActivity(mRequest.caller, mRequest.intent, mRequest.ephemeralIntent,
                    mRequest.resolvedType, mRequest.activityInfo, mRequest.resolveInfo,
                    mRequest.voiceSession, mRequest.voiceInteractor, mRequest.resultTo,
                    mRequest.resultWho, mRequest.requestCode, mRequest.callingPid,
                    mRequest.callingUid, mRequest.callingPackage, mRequest.realCallingPid,
                    mRequest.realCallingUid, mRequest.startFlags, mRequest.activityOptions,
                    mRequest.ignoreTargetSecurity, mRequest.componentSpecified,
                    mRequest.outActivity, mRequest.inTask, mRequest.reason,
                    mRequest.allowPendingRemoteAnimationRegistryLookup,
                    mRequest.originatingPendingIntent, mRequest.allowBackgroundActivityStart);
        }
    } finally {
        onExecutionComplete();
    }
}
```

通过 ActivityStarter 类启动了 HomeActivity。个人能力有限，我就不往下分析了。启动完桌面以后就要从桌面启动 app 了。由于桌面也是一个 Activity，那么我们就把问题看做 Activity 启动 Activity 即可。我们来看 Activity 的 startActivity 方法：

```
@Override
public void startActivity(Intent intent) {
    this.startActivity(intent, null);
}
@Override
public void startActivity(Intent intent, @Nullable Bundle options) {
    if (options != null) {
        startActivityForResult(intent, -1, options);
    } else {
        // Note we want to go through this call for compatibility with
        // applications that may have overridden the method.
        startActivityForResult(intent, -1);
    }
}
```

startActivity 调用了 startActivityForResult：

```
public void startActivityForResult(@RequiresPermission Intent intent, int requestCode) {
    startActivityForResult(intent, requestCode, null);
}
public void startActivityForResult(@RequiresPermission Intent intent, int requestCode,
            @Nullable Bundle options) {
    if (mParent == null) {
        options = transferSpringboardActivityOptions(options);
        Instrumentation.ActivityResult ar =
            mInstrumentation.execStartActivity(
                this, mMainThread.getApplicationThread(), mToken, this,
                intent, requestCode, options);
        ...
    } else {
        if (options != null) {
            mParent.startActivityFromChild(this, intent, requestCode, options);
        } else {
            // Note we want to go through this method for compatibility with
            // existing applications that may have overridden it.
            mParent.startActivityFromChild(this, intent, requestCode);
        }
    }
}
public void startActivityFromChild(@NonNull Activity child, @RequiresPermission Intent intent,
        int requestCode) {
    startActivityFromChild(child, intent, requestCode, null);
}
public void startActivityFromChild(@NonNull Activity child, @RequiresPermission Intent intent,
        int requestCode, @Nullable Bundle options) {
    options = transferSpringboardActivityOptions(options);
    Instrumentation.ActivityResult ar =
        mInstrumentation.execStartActivity(
            this, mMainThread.getApplicationThread(), mToken, child,
            intent, requestCode, options);
    if (ar != null) {
        mMainThread.sendActivityResult(
            mToken, child.mEmbeddedID, requestCode,
            ar.getResultCode(), ar.getResultData());
    }
    cancelInputsAndStartExitTransition(options);
}
```

mParent 变量是 Activity 类型，都调用的是 Instrumentation.execStartActivity 方法。Instrumentation 是啥？

```
/**
 * Base class for implementing application instrumentation code.  When running
 * with instrumentation turned on, this class will be instantiated for you
 * before any of the application code, allowing you to monitor all of the
 * interaction the system has with the application.  An Instrumentation
 * implementation is described to the system through an AndroidManifest.xml's
 * <instrumentation> tag.
 */
Instrumentation 提供了一种允许用户获取（及改变）应用程序与系统之间的交互流程的机制。而自动化测试框架可以看成是这种机制的一种典型的应用形式，但绝不是全部。(来自深入理解 Android 内核设计思想 Instrumentation 小节)
```

其强大的跟踪 application 及 activity 生命周期的功能，一般用于 android 应用测试框架中，这里不过多提及。

```
/*Activity*/
@UnsupportedAppUsage
private Instrumentation mInstrumentation;
@UnsupportedAppUsage
final void attach(Context context, ActivityThread aThread,
        Instrumentation instr, IBinder token, int ident,
        Application application, Intent intent, ActivityInfo info,
        CharSequence title, Activity parent, String id,
        NonConfigurationInstances lastNonConfigurationInstances,
        Configuration config, String referrer, IVoiceInteractor voiceInteractor,
        Window window, ActivityConfigCallback activityConfigCallback, IBinder assistToken) {
    ...
    mInstrumentation = instr;
    ...
}

/*ActivityThread*/
/**  Core implementation of activity launch. */
private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
	...
	try {
        ...
        if (activity != null) {
            ...
            activity.attach(appContext, this, getInstrumentation(), r.token,
                    r.ident, app, r.intent, r.activityInfo, title, r.parent,
                    r.embeddedID, r.lastNonConfigurationInstances, config,
                    r.referrer, r.voiceInteractor, window, r.configCallback,
                    r.assistToken);
            ...
        }
        ...
    }
	...
}
@UnsupportedAppUsage
public Instrumentation getInstrumentation()
{
    return mInstrumentation;
}
@UnsupportedAppUsage
Instrumentation mInstrumentation;
```

好了，再接着往下深♂入：

```
/*Instrumentation*/
@UnsupportedAppUsage
public ActivityResult execStartActivity(
        Context who, IBinder contextThread, IBinder token, Activity target,
        Intent intent, int requestCode, Bundle options) {
    ...
    try {
        intent.migrateExtraStreamToClipData();
        intent.prepareToLeaveProcess(who);
        int result = ActivityTaskManager.getService()
            .startActivity(whoThread, who.getBasePackageName(), intent,
                    intent.resolveTypeIfNeeded(who.getContentResolver()),
                    token, target != null ? target.mEmbeddedID : null,
                    requestCode, 0, null, options);
        checkStartActivityResult(result, intent);		//这里大家可以去看看，平时启动 Activity 抛的异常就在里面
    } catch (RemoteException e) {
        throw new RuntimeException("Failure from system", e);
    }
    return null;
}

/*ActivityTaskManager*/
public static IActivityTaskManager getService() {
    return IActivityTaskManagerSingleton.get();
}
@UnsupportedAppUsage(trackingBug = 129726065)
private static final Singleton<IActivityTaskManager> IActivityTaskManagerSingleton =
        new Singleton<IActivityTaskManager>() {
            @Override
            protected IActivityTaskManager create() {
                final IBinder b = ServiceManager.getService(Context.ACTIVITY_TASK_SERVICE);
                return IActivityTaskManager.Stub.asInterface(b);
            }
        };
        
/*Singleton*/    
/**
 * Singleton helper class for lazily initialization.
 * Modeled after frameworks/base/include/utils/Singleton.h
 */
public abstract class Singleton<T> {
    @UnsupportedAppUsage
    private T mInstance;

    protected abstract T create();

    @UnsupportedAppUsage
    public final T get() {
        synchronized (this) {
            if (mInstance == null) {
                mInstance = create();
            }
            return mInstance;
        }
    }
}
```

IActivityTaskManager.Stub.asInterface(b) 是通过 AIDL 生成了 IActivityManager （这里也是没有源码，跟踪不了）并且返回实现类  ActivityManagerService。跟踪它的 startActivity 方法：

```
@Override
public int startActivity(IApplicationThread caller, String callingPackage,
        Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
        int startFlags, ProfilerInfo profilerInfo, Bundle bOptions) {
    return mActivityTaskManager.startActivity(caller, callingPackage, intent, resolvedType,
            resultTo, resultWho, requestCode, startFlags, profilerInfo, bOptions);
}
```

mActivityTaskManager 之前说过是 ActivityTaskManagerService 的引用，如果忘了从哪获取的回去看一下。

```
@Override
public final int startActivity(IApplicationThread caller, String callingPackage,
        Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
        int startFlags, ProfilerInfo profilerInfo, Bundle bOptions) {
    return startActivityAsUser(caller, callingPackage, intent, resolvedType, resultTo,
            resultWho, requestCode, startFlags, profilerInfo, bOptions,
            UserHandle.getCallingUserId());
}
@Override
public int startActivityAsUser(IApplicationThread caller, String callingPackage,
        Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
        int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId) {
    return startActivityAsUser(caller, callingPackage, intent, resolvedType, resultTo,
            resultWho, requestCode, startFlags, profilerInfo, bOptions, userId,
            true /*validateIncomingUser*/);
}
int startActivityAsUser(IApplicationThread caller, String callingPackage,
        Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
        int startFlags, ProfilerInfo profilerInfo, Bundle bOptions, int userId,
        boolean validateIncomingUser) {
    enforceNotIsolatedCaller("startActivityAsUser");

    userId = getActivityStartController().checkTargetUser(userId, validateIncomingUser,
            Binder.getCallingPid(), Binder.getCallingUid(), "startActivityAsUser");
    // TODO: Switch to user app stacks here.
    return getActivityStartController().obtainStarter(intent, "startActivityAsUser")
            .setCaller(caller)
            .setCallingPackage(callingPackage)
            .setResolvedType(resolvedType)
            .setResultTo(resultTo)
            .setResultWho(resultWho)
            .setRequestCode(requestCode)
            .setStartFlags(startFlags)
            .setProfilerInfo(profilerInfo)
            .setActivityOptions(bOptions)
            .setMayWait(userId)
            .execute();
}
ActivityStartController getActivityStartController() {
    return mActivityStartController;
}
```

这就和前面讲启动 homeActivity 一样，ActivityManagerService 和 Activity 创建流程就算是过了一遍。（文章开头给的第三篇文章他往下分析了，感兴趣的可以继续跟进）

注释：

① ActivityManagerService 实际上不仅仅管理 Activity，四大组件都是由它来管理的。组件状态的管理和查询， Task ，电池信息状态，权限管理服务以及提供了系统运行时信息查询的辅助功能。