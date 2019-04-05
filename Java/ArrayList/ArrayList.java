import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

/**
 * @author shuafen/MLE
 * @version 1.0 edit
 */

public class ArrayList<E>
extends AbstractList<E>
//implements List<E>, RandomAccess, Cloneable, Serializable
{
	/**
	 * 序列化版本号
	 */
	private static final long serialVersionUID = -2993544531683317990L;
	/**
	 * 默认容量
	 */
	private static final int DEFAULT_CAPACITY = 10;
	private Object[] mElements;
	private int mSize;
	private int mCapacity;
	/**
	 * ArrayList 构造方法
	 * 1 ：无参构造默认容量为 10
	 * 2 ：有参构造容量自定
	 */
	public ArrayList()
	{
		//super();		隐式调用
		this(DEFAULT_CAPACITY);
	}
	public ArrayList(int capacity)
	{
		//super();		隐式调用
		mElements = new Object[capacity];
		mCapacity = capacity;
		mSize = 0;
	}
	/**
	 * 返回元素个数
	 */
	public int size()
	{
		return mSize;
	}
	/**
	 * 返回容量大小
	 */
	public int capacity()
	{
		return mCapacity;
	}
	/**
	 * 重新分配空间，数组复制实现空间的分配，如果重新分配的空间小于等于实际使用的空间则不变
	 */
	public void reCapacity(int capacity)
	{
		if(capacity > mSize)
		{
			Object[] elements = new Object[capacity];
			for(int i = 0; mSize > i; i++)
			{
				elements[i] = mElements[i];
			}
			mElements = elements;
			mCapacity = capacity;
		}
	}
	/**
	 * 添加元素，如果容量不够则容量扩增到 (原始容量 * 3) / 2 + 1
	 */
	@Override
	public boolean add(E obj)
	{
		if(mSize == mCapacity)
		{
			reCapacity((mCapacity * 3) / 2 + 1);
		}
		mElements[mSize++] = obj;
		return true;
	}
	/**
	 * 清空 ArrayList
	 */
	public void clear()
	{
		while(mSize != 0)
		{
			mSize--;
			mElements[mSize] = null;
		}
	}
	/**
	 * ArrayList 是与否为空
	 */
	public boolean isEmpty()
	{
		return mSize==0;
	}
	/**
	 * 复制出一个完全一样的 ArrayList 实例
	 */
	public ArrayList<?> clone()
	{
		ArrayList<?> clonearray = new ArrayList<>(mCapacity);
		clonearray.mElements = mElements.clone();
		return clonearray;
	}
	/**
	 * check 下标是否越界
	 */
	private void rangeChecker(int index)
	{
		if(index >= mSize || index < 0)
		{
			throw new ArrayIndexOutOfBoundsException("越界访问");
		}
	}
	/**
	 * 随机访问，取出下标为 index 的元素
	 * @param index
	 * @return
	 */
	@Override
	public E get(int index)
	{
		rangeChecker(index);
		return (E) mElements[index];
	}
	/**
	 * 测试
	 */
	public static void main(String[] args)
	{
		ArrayList<Integer> test = new ArrayList<>();
		Integer aInteger = test.get(1);
	}
}