package ArithUtils;

import java.util.Iterator;

public class Array<T> implements Iterable<T> 
{
	private BigNumber length;
	

	private Node list, last;
	
	public Array() 
	{
		this.last = this.list = new Node();
		length=BigNumber.ZERO;
	}
	public void add(T value) 
	{
		this.last = this.last.next = new Node(value);
		length=length.add(BigNumber.ONE);
	}
	
	public T get(BigNumber i) throws IndexOutOfBoundsException
	{
		Node cur = this.list;
		while(cur.next != null && (i = i.substract(BigNumber.ONE)).compareTo(BigNumber.ZERO) >= 0)
			cur=cur.next;
		if(cur.next == null)
			throw new IndexOutOfBoundsException("Error: There are too few elements in your Array");
		return cur.next.value;
	}
	
	public BigNumber getLength() {
		return length;
	}
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private Node start=list;
			@Override
			public boolean hasNext() {
				return this.start.next != null;
			}

			@Override
			public T next() {
				return (this.start=this.start.next).value;
			}

			@Override
			public void remove() {
				next();
				
			}
			
		};
	}
		
	private class Node
	{
		private T value;
		private Node next;
		
		private Node()
		{
			this.next=null;
		}
		private Node(T value)
		{
			this.value=value;
			this.next=null;
		}
	}

	public static<T> Array<T> getArray(T typeExample)
	{
		return new Array<T>();
	}
}

