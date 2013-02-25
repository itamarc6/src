package MathObjects;

import Utils.Pair;
import ArithUtils.*;

/**
 * @author Shir Peleg
 * This class represents a general, generic Group- contains an arithmetic set of 1 operation. 
 */

public class Group<T> 
{
	private final ArithmeticSet<T> ArithObj;
	protected final BigNumber order; 
	protected Group(ArithmeticSet<T> ArithObj, BigNumber order)
	{
		this.ArithObj = ArithObj;
		this.order = order;
	}
	public Group(OperationStruct<T> multOpp, BigNumber order)
	{
		this(new ArithmeticSet<T>(multOpp), order);
	}
	
	public Element getMultUnit()
	{
		return new Element(ArithObj.getUnit(0));
	}
	/**
	 * @param a
	 * @param b
	 * @return new Element opp(a,b)
	 */
	public Element mult(Element a, Element b) throws IllegalArgumentException
	{
		if (a.getParent() != b.getParent() || a.getParent() != this.ArithObj)
			throw new IllegalArgumentException("Two Elements must be members of the same group");
		return new Element(ArithObj.getAction(0).invoke(a.getValue(), b.getValue()));
	}
	/**
	 * @param a
	 * @param k>=0
	 * @return new Element opp(a,a)^k
	 */
	public Element pow(Element a, BigNumber k) throws IllegalArgumentException
	{
		if (a.getParent() != this.ArithObj)
			throw new IllegalArgumentException("Two Elements must be members of the same group");
		if (k.compareTo(BigNumber.ZERO)<0)
			throw new IllegalArgumentException("The exponent must be a non-negative integer");
		return new Element(ArithObj.getPower(0).invoke(a.getValue(), k));
	}
	/**
	 * @param a
	 * @return new Element opp^-1(a)
	 */
	public Element inverse(Element a) throws IllegalArgumentException
	{
		if (a.getParent() != this.ArithObj)
			throw new IllegalArgumentException("Two Elements must be members of the same group");
		return new Element(ArithObj.getInverse(0).invoke(a.getValue()));
	}
	/**
	 * @param a
	 * @param b
	 * @return new Element opp(a,opp^-1(b))
	 */
	public Element rightAdj(Element a, Element b) throws IllegalArgumentException
	{
		return mult(a,inverse(b));		
	}
	
	public boolean ElementIsEqual(Element a, Element b)
	{
		if(a.getParent() != b.getParent())
			return false;
		return a.getValue().equals(b.getValue());
	}
	/**
	 * @return A group element containing the value.
	 */
	public Element getElement(T value)
	{
		return new Element(value);	
	}
	
	/**
	 * @return The group order, i.e the number of elements in it. null if infinity.
	 */
	public BigNumber getOrder()
	{
		return this.order;
	}
	/** 
	 * @return A Cartesian product of two certain groups.
	 */
	public <S> Group<Pair<T, S>> genCartProd(final Group<S> other)
	{
		BigNumber order = this.getOrder().multiply(other.getOrder());
		return new Group<Pair<T,S>>(this.ArithObj.genCartProd(other.ArithObj), order);
	}
	public ArithmeticSet<T> getArithObj() {
		return ArithObj;
	}
	/** 
	 * @class representing a Group Element.
	 */
	public class Element{
		
		private T value;
		
		public Element(T value) 
		{
			this.value = value;
		}
		public T getValue() 
		{
			return this.value;
		}
		public ArithmeticSet<T> getParent() {
			return getArithObj();
		}
	};
}