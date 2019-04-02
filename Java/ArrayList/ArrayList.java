import java.io.Serializable;
import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

import javax.swing.table.TableStringConverter;

/**
 * @author shuafen/MLE
 * @version 1.0
 */

public class ArrayList<E>
//extends AbstractList<E>
//implements List<E>, RandomAccess, Cloneable, Serializable
{
	/**
	 * Description: 序列化版本号
	 */
	private static final long serialVersionUID = -2993544531683317990L;
	private Object[] mElements;
	private int mSize;
	private int mCapacity;
	/**
	 * Description：ArrayList 构造方法
	 * 1 ：无参构造默认容量为 10
	 * 2 ：有参构造容量自定
	 */
	public ArrayList()
	{
		//super();		隐式调用
		this(10);
	}
	public ArrayList(int capacity)
	{
		//super();		隐式调用
		mElements = new Object[capacity];
		mCapacity = capacity;
		mSize = 0;
	}
	/**
	 * Description：返回已包含 Object 的个数
	 */
	public int size()
	{
		return mSize;
	}
	/**
	 * Description：返回容量大小
	 */
	public int capacity()
	{
		return mCapacity;
	}
	/**
	 * 
	 */
	public void recapacity(int capacity)
	{
		if(capacity > mCapacity)
		{
			Object[] elements = new Object[capacity];
			for(int i = 0; mSize > i; i++)
			{
				elements[i] = mElements[i];
			}
			mElements = elements;
		}
	}
	/**
	 * Description：添加元素
	 */
	public void add(Object obj)
	{
		mElements[mSize++] = obj;
	}
	/**
	 * Description: 清空 ArrayList
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
	 * Description: 测试
	 */
	public static void main(String[] args)
	{
		ArrayList<Integer> test = new ArrayList<>();
		System.out.println(test.size());
		ArrayList<String> test1 = new ArrayList<>(0);
		System.out.println(test1.size());
	}
}
