参考：https://www.jianshu.com/p/180d2cc0feb5

版本：androidx 1.1.0

大部分都使用 AppCompatActivity，所以就以它的父类 FragmentActivity$^{①}$ 来进行分析。

FragmentActivity 通过使用 FragmentController 来对 Framgent 进行管理，不过 FragmentController 并没有真正实现对 Fragment 进行管理，而是通过内部持有的一个成员 FragmentHostCallback$^{②}$ 来间接管理。

```.java
final FragmentController mFragments = FragmentController.createController(new HostCallbacks());
```

FragmentActiviy 在创建的时候 new 了一个继承了 FragmentHostCallback 的类 HostCallback，它是FragmentActivity 的内部类，而 FragmentHostCallback 当中持有 Activity、Context、Handler 以及 FragmentManagerImpl$^{③}$ 的引用。

```.java
@Nullable private final Activity mActivity;
@NonNull private final Context mContext;
@NonNull private final Handler mHandler;
private final int mWindowAnimations;					//这个不清楚是啥
final FragmentManagerImpl mFragmentManager = new FragmentManagerImpl();
```

以下是构造函数：

```.java
public FragmentHostCallback(@NonNull Context context, @NonNull Handler handler,
        int windowAnimations) {
    this(context instanceof Activity ? (Activity) context : null, context, handler,
                windowAnimations);
}
FragmentHostCallback(@NonNull FragmentActivity activity) {
    this(activity, activity /*context*/, new Handler(), 0 /*windowAnimations*/);
}
FragmentHostCallback(@Nullable Activity activity, @NonNull Context context,
        @NonNull Handler handler, int windowAnimations) {
    mActivity = activity;
    mContext = Preconditions.checkNotNull(context, "context == null");
    mHandler = Preconditions.checkNotNull(handler, "handler == null");
    mWindowAnimations = windowAnimations;
}
```

FragmentActivity 用的是第二个构造函数：

```.java
public HostCallbacks() {
    super(FragmentActivity.this /*fragmentActivity*/);
}
```

从上面得知，每一个 FragmentActivity 都有自己的 FragmentController，而 FragmentController 中的 FragmentHostCallback 中 mActivity 就是 FragmentActivity 自己，mContext 也是用的 mActivity 的 引用，

mHandler 是新建的。

另外 FragmentHostCallback 类头顶有这么一段注释：

```.java
/**
 * Integration points with the Fragment host.
 * Fragments may be hosted by any object; such as an {@link Activity}. In order to
 * host fragments, implement {@link FragmentHostCallback}, overriding the methods
 * applicable to the host.
 */
```

大意就是 Fragment 可以由任意对象托管，只要继承 FragmentHostCallback 即可，例如 Activity 。 

FragmentManagerImpl 是 FragmentManager 的实现类，它实现了对 Fragment 的管理。FragmentManager 类当中有一堆的抽象方法，仅有一个静态的 FragmentFactroy 常量和公有的 getFragmentFactory() 方法，主要看它的实现类 FragmentManagerImpl 。

Google 对它的定义是：

```.Java
/**
 * Container for fragments associated with an activity.
 */
与 activity 关联的 Fragment 的容器。
```

由于 FragmentHostCallback 中持有的 FragmentManagerImpl 能对 Fragment 进行管理并且常量声明是包私有，FragmentController 提供了获取 FragmentManagerImpl 的方法，那么 FragmentActivity 就能对 Fragment 进行管理。

```
/*FragmentActivity*/
@NonNull
public FragmentManager getSupportFragmentManager() {
    return mFragments.getSupportFragmentManager();
}

/*FragmentController*/
@NonNull
public FragmentManager getSupportFragmentManager() {
    return mHost.mFragmentManager;
}
```

Fragment 的生命周期是受 FragmentActivity 的生命周期影响的，在 FragmentActivity 调用 onCreate、onStart 等函数时，FragmentActivity 会调用由 FragmentController.dispatchXXX 方法，对 Fragment 的生命周期进行控制。(实际上 FragmentController 只是作为代理，本质还是调用了      FragmentHostCallback.FragmentManagerImpl.dispatchXXX 方法)

以 onDestroy 为例：

```
/*FragmentActivity*/
@Override
protected void onDestroy() {
    super.onDestroy();
    mFragments.dispatchDestroy();
    mFragmentLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
}

/*FragmentController*/
public void dispatchDestroy() {
    mHost.mFragmentManager.dispatchDestroy();
}

/*FragmentManagerImpl*/
public void dispatchDestroy() {
    mDestroyed = true;
    execPendingActions();
    dispatchStateChange(Fragment.INITIALIZING);
    mHost = null;
    mContainer = null;
    mParent = null;
    if (mOnBackPressedDispatcher != null) {
        // mOnBackPressedDispatcher can hold a reference to the host
        // so we need to null it out to prevent memory leaks
        mOnBackPressedCallback.remove();
        mOnBackPressedDispatcher = null;
    }
}
```

它调用了 dispatchStateXXX 的方法来改变所有被管理 Fragment 的状态。实际上变更的是 FragmentManagerImpl 的状态，然后引导 Fragment 进行状态变更。

```
private void dispatchStateChange(int nextState) {
    try {
        mExecutingActions = true;
        moveToState(nextState, false);
    } finally {
        mExecutingActions = false;
    }
    execPendingActions();
}
```

接着它调用 moveToState（这部分的代码看不懂，菜是原罪）

```
void moveToState(int newState, boolean always)
```

第一个参数是 FragmentManagerImpl 要变更的新状态，第二个参数如果是 true ，新状态无论是什么样，即使和当前一样，也要把自己和管理的 Fragment 都刷新一遍。如果是 false，只有不相等的时候才刷新。

最终会进入 moveToState 五个参数的方法，在这个里面对 Fragment 进行状态迁移，调用 Fragment 的 生命周期方法。

```
void moveToState(Fragment f, int newState, int transit, int transitionStyle,
                 boolean keepActive)
{
	...
	else if (f.mState > newState) {
        switch (f.mState) {
        	...
            case Fragment.CREATED:
                if (newState < Fragment.CREATED) {
                    if (mDestroyed) {
                        // The fragment's containing activity is
                        // being destroyed, but this fragment is
                        // currently animating away.  Stop the
                        // animation right now -- it is not needed,
                        // and we can't wait any more on destroying
                        // the fragment.
                        ...
                    }
                    if (f.getAnimatingAway() != null || f.getAnimator() != null) {
                        // We are waiting for the fragment's view to finish
                        // animating away.  Just make a note of the state
                        // the fragment now should move to once the animation
                        // is done.
                        f.setStateAfterAnimating(newState);
                        newState = Fragment.CREATED;
                    } else {
                        if (DEBUG) Log.v(TAG, "movefrom CREATED: " + f);
                        boolean beingRemoved = f.mRemoving && !f.isInBackStack();
                        if (beingRemoved || mNonConfig.shouldDestroy(f)) {
                            ...
                            f.performDestroy();
                            dispatchOnFragmentDestroyed(f, false);
                        } else {
                            f.mState = Fragment.INITIALIZING;
                        }

                        f.performDetach();
                        dispatchOnFragmentDetached(f, false);
                        ...
                    }
                }
            
        }
    }
}
```

这部分的描述和图片引用了开头所给链接的文章内容：

*fragment的 state取值，为前面提到的七中状态，其中最低值是 INITIALIZING状态，代表 fragment刚创建，还未被 add， 最高状态值是 RESUMED,代表 fragment处于前台。 所以 moveToState内部分两条线，状态跃升，和状态降低，里面各有一个 switch判断，switch里每个 case都没有 break，这意味着，状态可以持续变迁，比如从INITIALIZING，一直跃升到 RESUMED，将每个 case都走一遍，每次 case语句内，都会改变 state的值。*

![img](https://upload-images.jianshu.io/upload_images/1008428-c0809de4a37a0d74.jpg?imageMogr2/auto-orient/strip|imageView2/2/w/1040/format/webp)





由于水平有限，moveToState 里面的具体细节不懂，所以就不往下分析了。

这仅仅只是 FragmentActivity 来控制 Fragment，还缺少 Fragment 单方面对 FragmentActivity 的访问。

在 FragmentActivity 的 onCreate 方法中调用了 FragmentController.attachHost，将 FragmentController 当中持有的 FragmentHostCallback 对象传递给了 FragmentManagerImpl，使 FragmentManagerImpl 也持有了 FragmentHostCallback 对象，并分发给由这个 FragmentManagerImpl 管理的 Fragment 持有从 FragmentActivity 传递过来的 FragmentHostCallback 对象。

```
/*FragmentActivity*/
mFragments.attachHost(null /*parent*/);

/*FragmentController*/
public void attachHost(@Nullable Fragment parent) {
    mHost.mFragmentManager.attachController(
            mHost, mHost /*container*/, parent);
}

/*FragmentManagerImpl*/
public void attachController(@NonNull FragmentHostCallback host,
        @NonNull FragmentContainer container, @Nullable final Fragment parent) {
    ...
    mHost = host;
    ...
}
```

然后 Fragment 当中有 FragmentHostCallback 的引用，它通过 FragmentManagerImpl 的 onCreateView 和 moveToState 获得

```
void moveToState(Fragment f, int newState, int transit, int transitionStyle,
                 boolean keepActive) {
    ...
    if (f.mState <= newState) {
        ...
        switch (f.mState) {
            case Fragment.INITIALIZING:
                if (newState > Fragment.INITIALIZING) {
                    ...
                    f.mHost = mHost;
                    ...
                }
        }
        ...
    }
    ...
}

public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
    if (fragment == null) {
        ...
        fragment.mHost = mHost;
        ...
    } else if (fragment.mInLayout) {
        ...
    } else {
        ...
        fragment.mHost = mHost;
        ...
    }
}    
```

onCreateView 方法主要用于定义在 xml 中的 fragment。这样 Fragment 就可以通过使用 FragmentHostCallback 提供获取 FragmentActivity 实例的方法来获取 FragmentActivity 来使用，并且它本身也封装了一些方法供 Fragment 使用。

以上是 FragmentActivity 与 Fragment 之间的联系。

因为需要根据情况展示界面，所以不得不对 Fragment 进行一些操作，因此一个东西油然而生：FragmentTransaction。

```.java
API for performing a set of Fragment operations.
用于执行一组 Fragment 操作的API。
```

换句话说，就是平时写代码的时候使用的 add、show、replace 等操作。

它使用了一个可变长度的数组来存储一次事务提交的操作：

```.java
ArrayList<Op> mOps = new ArrayList<>()
```

Op 就是一次操作的参数储存的容器。

```
static final int OP_NULL = 0;
static final int OP_ADD = 1;
static final int OP_REPLACE = 2;
static final int OP_REMOVE = 3;
static final int OP_HIDE = 4;
static final int OP_SHOW = 5;
static final int OP_DETACH = 6;
static final int OP_ATTACH = 7;
static final int OP_SET_PRIMARY_NAV = 8;
static final int OP_UNSET_PRIMARY_NAV = 9;
static final int OP_SET_MAX_LIFECYCLE = 10;

static final class Op {
    int mCmd;
    Fragment mFragment;
    int mEnterAnim;
    int mExitAnim;
    int mPopEnterAnim;
    int mPopExitAnim;
    Lifecycle.State mOldMaxState;
    Lifecycle.State mCurrentMaxState;

    Op() {
    }

    Op(int cmd, Fragment fragment) {
        this.mCmd = cmd;
        this.mFragment = fragment;
        this.mOldMaxState = Lifecycle.State.RESUMED;
        this.mCurrentMaxState = Lifecycle.State.RESUMED;
    }

    Op(int cmd, @NonNull Fragment fragment, Lifecycle.State state) {
        this.mCmd = cmd;
        this.mFragment = fragment;
        this.mOldMaxState = fragment.mMaxState;
        this.mCurrentMaxState = state;
    }
}
```

存放了操作的命令 id、操作的 Fragment 引用、动画以及生命周期状态。

add、show、replace 等操作使用的是第二个构造函数，add，replace 调用 doAddOp 方法添加到数组中，其他操作是直接添加的。

```
void doAddOp(int containerViewId, Fragment fragment, @Nullable String tag, int opcmd) {
    ...
    addOp(new Op(opcmd, fragment));
}
```

添加到返回栈的操作它仅仅只是把 mAddToBackStack 置为 true，传入的 name 算是一个标识，用于返回栈弹出的判断。mAllowAddToBackStack 可以通过 disallowAddToBackStack 方法置为 false 使得不能进行添加到返回栈的操作。

```
@NonNull
public FragmentTransaction addToBackStack(@Nullable String name) {
    if (!mAllowAddToBackStack) {
        throw new IllegalStateException(
                "This FragmentTransaction is not allowed to be added to the back stack.");
    }
    mAddToBackStack = true;
    mName = name;
    return this;
}
```

不过它只还是一个抽象类，BackStackRecord 类继承了它，并且它还持有了一个 FragmentManagerImpl 对象，因为它只是操作的记录者，由 FragmentManagerImpl 来进行操作。

之后使用 BackStackRecord.commit() 提交事务，就开始运作了。

```
@Override
public int commit() {
    return commitInternal(false);
}

@Override
public int commitAllowingStateLoss() {
    return commitInternal(true);
}

int commitInternal(boolean allowStateLoss) {
    if (mCommitted) throw new IllegalStateException("commit already called");
    if (FragmentManagerImpl.DEBUG) {
        Log.v(TAG, "Commit: " + this);
        LogWriter logw = new LogWriter(TAG);
        PrintWriter pw = new PrintWriter(logw);
        dump("  ", pw);
        pw.close();
    }
    mCommitted = true;
    if (mAddToBackStack) {
        mIndex = mManager.allocBackStackIndex(this);
    } else {
        mIndex = -1;
    }
    mManager.enqueueAction(this, allowStateLoss);
    return mIndex;
}
```

如果有添加到返回栈的操作，FragmentManagerImpl 会返回一个下标（下标这一部分不懂就不再讲解）过来，然后调用 enqueueAction。

```
public void enqueueAction(OpGenerator action, boolean allowStateLoss) {
     if (!allowStateLoss) {
        checkStateLoss();
     }
     synchronized (this) {
        if (mDestroyed || mHost == null) {
            if (allowStateLoss) {
                // This FragmentManager isn't attached, so drop the entire transaction.
                return;
            }
            throw new IllegalStateException("Activity has been destroyed");
        }
        if (mPendingActions == null) {
            mPendingActions = new ArrayList<>();
        }
        mPendingActions.add(action);
        scheduleCommit();
    }
}
```

这里使用了一个变量来存储等待处理的操作：

```
ArrayList<OpGenerator> mPendingActions;
```

接下来又调用了 scheduleCommit，用一个 Runnable 在 FragmentActivity 获取的 handler 中运行了 execPendingActions。

```
void scheduleCommit() {
    synchronized (this) {
        boolean postponeReady =
                mPostponedTransactions != null && !mPostponedTransactions.isEmpty();
        boolean pendingReady = mPendingActions != null && mPendingActions.size() == 1;
        if (postponeReady || pendingReady) {
            mHost.getHandler().removeCallbacks(mExecCommit);
            mHost.getHandler().post(mExecCommit);
            updateOnBackPressedCallbackEnabled();
        }
    }
}

Runnable mExecCommit = new Runnable() {
    @Override
    public void run() {
        execPendingActions();
    }
};

public boolean execPendingActions() {
    ensureExecReady(true);
    boolean didSomething = false;
    while (generateOpsForPendingActions(mTmpRecords, mTmpIsPop)) {
        mExecutingActions = true;
        try {
            removeRedundantOperationsAndExecute(mTmpRecords, mTmpIsPop);
        } finally {
            cleanupExec();
        }
        didSomething = true;
    }
    updateOnBackPressedCallbackEnabled();
    doPendingDeferredStart();
    burpActive();

    return didSomething;
}
```

mTmpRecords 存储了 BackStackRecord 提交的变量，mTmpIsPop 是记录与之对应的BackStackRecord 是否是出栈操作的变量，根据注释所述，是用于除去冗余操作的。

```
// Temporary vars for removing redundant operations in BackStackRecords:
ArrayList<BackStackRecord> mTmpRecords;
ArrayList<Boolean> mTmpIsPop;
```

然后调用 generateOpsForPendingActions 对 mPendingActions 当中的成员调用自身的 generateOps 生成操作。

```
private boolean generateOpsForPendingActions(ArrayList<BackStackRecord> records,
                                                 ArrayList<Boolean> isPop) {
    boolean didSomething = false;
    synchronized (this) {
        if (mPendingActions == null || mPendingActions.size() == 0) {
            return false;
        }
        final int numActions = mPendingActions.size();
        for (int i = 0; i < numActions; i++) {
            didSomething |= mPendingActions.get(i).generateOps(records, isPop);
        }
        mPendingActions.clear();
        mHost.getHandler().removeCallbacks(mExecCommit);
    }
    return didSomething;
}
```

generateOps 是一个接口的方法，在 BackStackRecord 和 PopBackStackState 类都实现了它。

```
/*FragmentManagerImpl.OpGenerator*/
/**
 * An add or pop transaction to be scheduled for the UI thread.
 */
interface OpGenerator {
    /**
     * Generate transactions to add to {@code records} and whether or not the transaction      * is an add or pop to {@code isRecordPop}.
     *
     * records and isRecordPop must be added equally so that each transaction in records
     * matches the boolean for whether or not it is a pop in isRecordPop.
     *
     * @param records A list to add transactions to.
     * @param isRecordPop A list to add whether or not the transactions added to records      *                    is a pop transaction.
     * @return true if something was added or false otherwise.
     */
    boolean generateOps(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop);
    }

/*FragmentManagerImpl.PopBackStackState*/
/**
 * A pop operation OpGenerator. This will be run on the UI thread and will generate the
 * transactions that will be popped if anything can be popped.
 */
private class PopBackStackState implements OpGenerator {
    ...

    @Override
    public boolean generateOps(ArrayList<BackStackRecord> records,
                               ArrayList<Boolean> isRecordPop) {
        if (mPrimaryNav != null // We have a primary nav fragment
                && mId < 0 // No valid id (since they're local)
                && mName == null) { // no name to pop to (since they're local)
            final FragmentManager childManager = mPrimaryNav.getChildFragmentManager();
            if (childManager.popBackStackImmediate()) {
                // We didn't add any operations for this FragmentManager even though
                // a child did do work.
                return false;
            }
        }
        return popBackStackState(records, isRecordPop, mName, mId, mFlags);
    }
}

/*BackStackRecord*/
public boolean generateOps(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop) {
    if (FragmentManagerImpl.DEBUG) {
        Log.v(TAG, "Run: " + this);
    }
    records.add(this);
    isRecordPop.add(false);
    if (mAddToBackStack) {
        mManager.addBackStackState(this);
    }
    return true;
}
```

PopBackStackState.generateOps 是对所提交的 BackStackRecord 进行弹出的操作。调用 popBackStack 的时候就相当于提交一次弹出的操作。

```
@Override
public void popBackStack(final int id, final int flags) {
    if (id < 0) {
        throw new IllegalArgumentException("Bad id: " + id);
    }
    enqueueAction(new PopBackStackState(null, id, flags), false);
}
```

popBackStackState 方法把要弹出的 BackStackRecord 进行了如下操作：

1. 如果 name 、id 没有设置，且没有设置 POP_BACK_STACK_INCLUSIVE 位，那么就把返回栈最后一个 BackStackRecord 移除，并添加到要执行操作序列 records 中去，isRecordPop 置为 true。
2. 如果有一个参数及以上被设置过，那么将从栈顶开始进行匹配，直到有一个参数相等为止。之后对匹配下标之后的 BackStackRecord 进行和情况 1 一样的操作；如果设置了 POP_BACK_STACK_INCLUSIVE 位，那么从匹配的下标开始再接着找，直到任何一个参数都不相等的时候停止，从栈顶到当前匹配的下标（包括自身）都进行和情况 1 一样的操作。
3. 没有参数被设置过但设置了 POP_BACK_STACK_INCLUSIVE 位，那么返回栈内的所有 BackStackRecord 都会进行和情况 1 一样的操作。

```
ArrayList<BackStackRecord> mBackStack;			\\返回栈

@SuppressWarnings("unused")
boolean popBackStackState(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, String name, int id, int flags) {
    if (mBackStack == null) {
        return false;
    }
    if (name == null && id < 0 && (flags & POP_BACK_STACK_INCLUSIVE) == 0) {
        int last = mBackStack.size() - 1;
        if (last < 0) {
            return false;
        }
        records.add(mBackStack.remove(last));
        isRecordPop.add(true);
    } else {
        int index = -1;
        if (name != null || id >= 0) {
            // If a name or ID is specified, look for that place in
            // the stack.
            index = mBackStack.size()-1;
            while (index >= 0) {
                BackStackRecord bss = mBackStack.get(index);
                if (name != null && name.equals(bss.getName())) {
                    break;
                }
                if (id >= 0 && id == bss.mIndex) {
                    break;
                }
                index--;
            }
            if (index < 0) {
                return false;
            }
            if ((flags&POP_BACK_STACK_INCLUSIVE) != 0) {
                index--;
                // Consume all following entries that match.
                while (index >= 0) {
                    BackStackRecord bss = mBackStack.get(index);
                    if ((name != null && name.equals(bss.getName()))
                            || (id >= 0 && id == bss.mIndex)) {
                        index--;
                        continue;
                    }
                    break;
                }
            }
        }
        if (index == mBackStack.size()-1) {
            return false;
        }
        for (int i = mBackStack.size() - 1; i > index; i--) {
            records.add(mBackStack.remove(i));
            isRecordPop.add(true);
        }
    }
    return true;
}
```

BackStackRecord.generateOps 进行添加操作了，并且如果添加到返回栈的话它会去调用 FragmentManagerImpl 的 addBackStackState 方法。

```
void addBackStackState(BackStackRecord state) {
    if (mBackStack == null) {
        mBackStack = new ArrayList<BackStackRecord>();
    }
    mBackStack.add(state);
}
```

回到 execPendingActions 方法，它调用 removeRedundantOperationsAndExecute 方法进行操作。个人能力有限，其他的不会，我就只讲有个比较重要的方法 executeOps 中的 BackStackRecord.executeOps，BackStackRecord.executePopOps：

```
/*FragmentManagerImpl*/
private static void executeOps(ArrayList<BackStackRecord> records,
                         ArrayList<Boolean> isRecordPop, int startIndex, int endIndex) {
    for (int i = startIndex; i < endIndex; i++) {
        final BackStackRecord record = records.get(i);
        final boolean isPop = isRecordPop.get(i);
        if (isPop) {
            record.bumpBackStackNesting(-1);
            // Only execute the add operations at the end of
            // all transactions.
            boolean moveToState = i == (endIndex - 1);
            record.executePopOps(moveToState);
        } else {
            record.bumpBackStackNesting(1);
            record.executeOps();
        }
    }
}

/*BackStackRecord*/
void executeOps() {
    final int numOps = mOps.size();
    for (int opNum = 0; opNum < numOps; opNum++) {
        final Op op = mOps.get(opNum);
        final Fragment f = op.mFragment;
        if (f != null) {
            f.setNextTransition(mTransition, mTransitionStyle);
        }
        switch (op.mCmd) {
            case OP_ADD:
                f.setNextAnim(op.mEnterAnim);
                mManager.addFragment(f, false);
                break;
            case OP_REMOVE:
                f.setNextAnim(op.mExitAnim);
                mManager.removeFragment(f);
                break;
            case OP_HIDE:
                f.setNextAnim(op.mExitAnim);
                mManager.hideFragment(f);
                break;
            case OP_SHOW:
                f.setNextAnim(op.mEnterAnim);
                mManager.showFragment(f);
                break;
            case OP_DETACH:
                f.setNextAnim(op.mExitAnim);
                mManager.detachFragment(f);
                break;
            case OP_ATTACH:
                f.setNextAnim(op.mEnterAnim);
                mManager.attachFragment(f);
                break;
            case OP_SET_PRIMARY_NAV:
                mManager.setPrimaryNavigationFragment(f);
                break;
            case OP_UNSET_PRIMARY_NAV:
                mManager.setPrimaryNavigationFragment(null);
                break;
            case OP_SET_MAX_LIFECYCLE:
                mManager.setMaxLifecycle(f, op.mCurrentMaxState);
                break;
            default:
                throw new IllegalArgumentException("Unknown cmd: " + op.mCmd);
        }
        if (!mReorderingAllowed && op.mCmd != OP_ADD && f != null) {
            mManager.moveFragmentToExpectedState(f);
        }
    }
    if (!mReorderingAllowed) {
        // Added fragments are added at the end to comply with prior behavior.
        mManager.moveToState(mManager.mCurState, true);
    }
}
    
/*BackStackRecord*/
void executePopOps(boolean moveToState) {
    for (int opNum = mOps.size() - 1; opNum >= 0; opNum--) {
        final Op op = mOps.get(opNum);
        Fragment f = op.mFragment;
        if (f != null) {
            f.setNextTransition(FragmentManagerImpl.reverseTransit(mTransition),
                    mTransitionStyle);
        }
        switch (op.mCmd) {
            case OP_ADD:
                f.setNextAnim(op.mPopExitAnim);
                mManager.removeFragment(f);
                break;
            case OP_REMOVE:
                f.setNextAnim(op.mPopEnterAnim);
                mManager.addFragment(f, false);
                break;
            case OP_HIDE:
                f.setNextAnim(op.mPopEnterAnim);
                mManager.showFragment(f);
                break;
            case OP_SHOW:
                f.setNextAnim(op.mPopExitAnim);
                mManager.hideFragment(f);
                break;
            case OP_DETACH:
                f.setNextAnim(op.mPopEnterAnim);
                mManager.attachFragment(f);
                break;
            case OP_ATTACH:
                f.setNextAnim(op.mPopExitAnim);
                mManager.detachFragment(f);
                break;
            case OP_SET_PRIMARY_NAV:
                mManager.setPrimaryNavigationFragment(null);
                break;
            case OP_UNSET_PRIMARY_NAV:
                mManager.setPrimaryNavigationFragment(f);
                break;
            case OP_SET_MAX_LIFECYCLE:
                mManager.setMaxLifecycle(f, op.mOldMaxState);
                break;
            default:
                throw new IllegalArgumentException("Unknown cmd: " + op.mCmd);
        }
        if (!mReorderingAllowed && op.mCmd != OP_REMOVE && f != null) {
            mManager.moveFragmentToExpectedState(f);
        }
    }
    if (!mReorderingAllowed && moveToState) {
        mManager.moveToState(mManager.mCurState, true);
    }
}
```

不过也不用多说什么了，之前所提交的操作都在这里消费了，executeOps 方法中我们提交了什么操作就通过FragmentManagerImpl 对 Fragment 执行什么操作，executePopOps 则是反过来，因为作为弹出操作肯定是按原路返回恢复到之前的状态。

这样事务的添加和返回栈的操作就到这里了。下面就是 Fragment 和 FragmentManagerImpl 的 数据保存和恢复。在 FragmentManagerImpl.enqueueAction 中 allowStateLoss 参数为 true 那就认为允许数据丢失，为 false 认为不允许丢失，即如果调用过 onSaveInstanceState 方法或 onStop 方法在 FragmentManagerImpl 就会抛出异常。

```
private void checkStateLoss() {
    if (isStateSaved()) {
        throw new IllegalStateException(
                "Can not perform this action after onSaveInstanceState");
    }
}

@Override
public boolean isStateSaved() {
    // See saveAllState() for the explanation of this.  We do this for
    // all platform versions, to keep our behavior more consistent between
    // them.
    return mStateSaved || mStopped;
}
```

这里的 state 指的是 FragmentManagerImpl 的一系列信息，它通过 saveAllState 来进行存储操作。

```
Parcelable saveAllState() {
	...
	execPendingActions();
    mStateSaved = true;
    if (mActive.isEmpty()) {
        return null;
    }
    int size = mActive.size();
    ArrayList<FragmentState> active = new ArrayList<>(size);
    boolean haveFragments = false;
    for (Fragment f : mActive.values()) {
        if (f != null) {
    		...
    		haveFragments = true;
    		FragmentState fs = new FragmentState(f);
	        active.add(fs);
 	        if (f.mState > Fragment.INITIALIZING && fs.mSavedFragmentState == null) {
                fs.mSavedFragmentState = saveFragmentBasicState(f);
                if (f.mTargetWho != null) {
                    Fragment target = mActive.get(f.mTargetWho);
                    if (target == null) {
                        throwException(new IllegalStateException(
                                "Failure saving state: " + f
                                        + " has target not in fragment manager: "
                                        + f.mTargetWho));
                    }
                    if (fs.mSavedFragmentState == null) {
                        fs.mSavedFragmentState = new Bundle();
                    }
                    putFragment(fs.mSavedFragmentState,
                            FragmentManagerImpl.TARGET_STATE_TAG, target);
                    if (f.mTargetRequestCode != 0) {
                        fs.mSavedFragmentState.putInt(
                                FragmentManagerImpl.TARGET_REQUEST_CODE_STATE_TAG,
                                f.mTargetRequestCode);
                    }
                }
            } else {
              fs.mSavedFragmentState = f.mSavedFragmentState;
            }
        }
    }
    if (!haveFragments) {
    	if (DEBUG) Log.v(TAG, "saveAllState: no fragments!");
        return null;
    }
    ArrayList<String> added = null;
    BackStackState[] backStack = null;
    // Build list of currently added fragments.
    size = mAdded.size();
    if (size > 0) {
        added = new ArrayList<>(size);
        for (Fragment f : mAdded) {
            added.add(f.mWho);
            if (f.mFragmentManager != this) {
                throwException(new IllegalStateException(
                        "Failure saving state: active " + f
                                + " was removed from the FragmentManager"));
            }
            if (DEBUG) {
                Log.v(TAG, "saveAllState: adding fragment (" + f.mWho
                        + "): " + f);
            }
        }
    }

    // Now save back stack.
    if (mBackStack != null) {
        size = mBackStack.size();
        if (size > 0) {
            backStack = new BackStackState[size];
            for (int i = 0; i < size; i++) {
                backStack[i] = new BackStackState(mBackStack.get(i));
                if (DEBUG) Log.v(TAG, "saveAllState: adding back stack #" + i
                       + ": " + mBackStack.get(i));
            }
        }
    }
    FragmentManagerState fms = new FragmentManagerState();
    fms.mActive = active;
    fms.mAdded = added;
    fms.mBackStack = backStack;
    if (mPrimaryNav != null) {
        fms.mPrimaryNavActiveWho = mPrimaryNav.mWho;
    }
    fms.mNextFragmentIndex = mNextFragmentIndex;
    return fms;
}        
```

Fragment （FragmentState）信息，返回栈（BackStackState）信息，FragmentManager （FragmentManagerState）自己的一些信息都被保存下来。

```
/*FragmentController*/
@Nullable
public Parcelable saveAllState() {
    return mHost.mFragmentManager.saveAllState();
}

/*FragmentActivity*/
@Override
protected void onSaveInstanceState(@NonNull Bundle outState) {
    ...
    Parcelable p = mFragments.saveAllState();
    if (p != null) {
        outState.putParcelable(FRAGMENTS_TAG, p);
    }
    ...
}    
```

然后 FragmentActivity 通过调用 restoreSaveState 来进行数据的恢复：

```
/*FragmentActivity*/
@SuppressWarnings("deprecation")
@Override
protected void onCreate(@Nullable Bundle savedInstanceState) {
    mFragments.attachHost(null /*parent*/);
    if (savedInstanceState != null) {
        Parcelable p = savedInstanceState.getParcelable(FRAGMENTS_TAG);
        mFragments.restoreSaveState(p);
        ...
    }
    ...
}

/*FragmentController*/
public void restoreSaveState(@Nullable Parcelable state) {
    if (!(mHost instanceof ViewModelStoreOwner)) {
        throw new IllegalStateException("Your FragmentHostCallback must implement "
                + "ViewModelStoreOwner to call restoreSaveState(). Call restoreAllState()                 + " if you're still using retainNestedNonConfig().");
    }
    mHost.mFragmentManager.restoreSaveState(state);
}

//放一下 ViewModelStoreOwner 源码
/**
 * A scope that owns {@link ViewModelStore}.
 * A responsibility of an implementation of this interface is to retain owned
 * ViewModelStore during the configuration changes and call {@link 
 * ViewModelStore#clear()}, when this scope is going to be destroyed.
 */
@SuppressWarnings("WeakerAccess")
public interface ViewModelStoreOwner {
    /**
     * Returns owned {@link ViewModelStore}
     *
     * @return a {@code ViewModelStore}
     */
    @NonNull
    ViewModelStore getViewModelStore();
}

/*FragmentManagerImpl*/
void restoreSaveState(Parcelable state) {
    if (state == null) return;
    FragmentManagerState fms = (FragmentManagerState)state;
    if (fms.mActive == null) return;
    ...
    mActive.clear();
    for (FragmentState fs : fms.mActive) {
        ...
    }
    mAdded.clear();
    if (fms.mAdded != null) {
        for (String who : fms.mAdded) {
            ...
        }
    }
    if (fms.mBackStack != null) {
        ...
    }
    ...
}
```

注释：

① 我仔细看了一下 FragmentActivity 的父类 Activity，里面有类似 FragmentActivity 持有 FragmentController 的操作，不过好像被弃用了。

② FragmentHostCallback 还实现了 FragmentContainer 接口，提供了回调到 Fragment 的容器（Google 的注释是这么说的。里面有三个方法，其中一个弃用了，一个是 View onFindViewById(@IdRes int id) ，用来找 Fragment 里面的视图，boolean onHasView() 来检查是否持有视图）。

③ Fragment 中也有自己的 FragmentManagerImpl，这样就实现了 Fragment 的嵌套。