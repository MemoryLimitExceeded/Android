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
implements List<E>, RandomAccess, Cloneable, Serializable
{
	/**
	 * ���л��汾��
	 */
	private static final long serialVersionUID = -2993544531683317990L;
	/**
	 * Ĭ������
	 */
	private static final int DEFAULT_CAPACITY = 10;
	private Object[] mElements;
	private int mSize;
	private int mCapacity;
	/**
	 * ArrayList ���췽��
	 * 1 ���޲ι���Ĭ������Ϊ 10
	 * 2 ���вι��������Զ�
	 */
	public ArrayList()
	{
		//super();		��ʽ����
		this(DEFAULT_CAPACITY);
	}
	public ArrayList(int capacity)
	{
		//super();		��ʽ����
		mElements = new Object[capacity];
		mCapacity = capacity;
		mSize = 0;
	}
	/**
	 * ����Ԫ�ظ���
	 */
	@Override
	public int size()
	{
		return mSize;
	}
	/**
	 * ����������С
	 * @return
	 */
	public int capacity()
	{
		return mCapacity;
	}
	/**
	 * ���·���ռ䣬���鸴��ʵ�ֿռ�ķ��䣬������·���Ŀռ�С�ڵ���ʵ��ʹ�õĿռ��򲻱�
	 * @param capacity
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
	 * ���Ԫ�أ������������������������ (ԭʼ���� * 3) / 2 + 1
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
	 * ��� ArrayList
	 */
	@Override
	public void clear()
	{
		while(mSize != 0)
		{
			mSize--;
			mElements[mSize] = null;
		}
	}
	/**
	 * ArrayList �����Ϊ��
	 */
	@Override
	public boolean isEmpty()
	{
		return mSize == 0;
	}
	/**
	 * ��ǰ��������ҵ���һ����Ԫ�� o һ����Ԫ�ز����������ڵ��±�
	 */
	@Override
	public int indexOf(Object o)
	{
		if(o == null)
		{
			for(int i = 0; mSize > i; i++)
			{
				if(o == null)
				{
					return i;
				}
			}
		}
		else
		{
			for(int i = 0; mSize > i; i++)
			{
				if(o.equals(mElements[i]))
				{
					return i;
				}
			}
		}
		return -1;
	}
	/**
	 * �� IndexOf ���ƣ��Ӻ���ǰ�����ҵ���һ����Ԫ�� o һ����Ԫ�ز����������ڵ��±�
	 */
	@Override
	public int lastIndexOf(Object o)
	{
		if(o == null)
		{
			for(int i = mSize - 1; i >= 0; i--)
			{
				if(o == null)
				{
					return i;
				}
			}
		}
		else
		{
			for(int i = mSize - 1; i >= 0; i--)
			{
				if(o.equals(mElements[i]))
				{
					return i;
				}
			}
		}
		return -1;
	}
	/**
	 * �Ƿ���� o Ԫ��
	 */
	@Override
	public boolean contains(Object o)
	{
		return indexOf(o) != -1;
	}
	/**
	 * ���Ƴ�һ����ȫһ���� ArrayList ʵ��
	 */
	@Override
	public ArrayList<E> clone()
	{
		ArrayList<E> clonearray = new ArrayList<>(mCapacity);
		
		//clonearray.mElements = mElements.clone();
		return clonearray;
	}
	/**
	 * check �±��Ƿ�Խ��
	 * @param index
	 */
	private void rangeChecker(int index)
	{
		if(index >= mSize || index < 0)
		{
			throw new ArrayIndexOutOfBoundsException("Խ�����"+" size:"+mSize+" index:"+index);
		}
	}
	/**
	 * ������ʣ�ȡ���±�Ϊ index ��Ԫ��
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
	 * ����
	 */
	public static void main(String[] args)
	{
		ArrayList<Integer> test = new ArrayList<>();
		test.add(1);
		ArrayList<Integer> test1 = test.clone();
		System.out.println(test.get(0));
		System.out.println(test1.get(0));
	}
}