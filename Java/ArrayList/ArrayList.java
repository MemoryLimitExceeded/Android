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
	public int size()
	{
		return mSize;
	}
	/**
	 * ����������С
	 */
	public int capacity()
	{
		return mCapacity;
	}
	/**
	 * ���·���ռ䣬���鸴��ʵ�ֿռ�ķ��䣬������·���Ŀռ�С�ڵ���ʵ��ʹ�õĿռ��򲻱�
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
	public boolean isEmpty()
	{
		return mSize==0;
	}
	/**
	 * ���Ƴ�һ����ȫһ���� ArrayList ʵ��
	 */
	public ArrayList<?> clone()
	{
		ArrayList<?> clonearray = new ArrayList<>(mCapacity);
		clonearray.mElements = mElements.clone();
		return clonearray;
	}
	/**
	 * check �±��Ƿ�Խ��
	 */
	private void rangeChecker(int index)
	{
		if(index >= mSize || index < 0)
		{
			throw new ArrayIndexOutOfBoundsException("Խ�����");
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
		Integer aInteger = test.get(1);
	}
}