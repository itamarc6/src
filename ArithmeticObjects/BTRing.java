package ArithmeticObjects;

import ArithUtils.BigNumber;
import ArithUtils.Func;
import MathObjects.OperationStruct;
import MathObjects.Ring;
import Utils.ByteTree;
import Utils.Pair;

public class BTRing<T> extends BTGroup<T> {

	protected Ring<T> inner;
	public BTRing(OperationStruct<T> addOpp, OperationStruct<T> multOpp, BigNumber order, Func.P1<ByteTree, T> toBT, Func.P1<T, ByteTree> fromBT)
	{
		this(new Ring<T>(addOpp, multOpp, order), toBT, fromBT);
	}
	public BTRing(Ring<T> inner, Func.P1<ByteTree, T> toBT, Func.P1<T, ByteTree> fromBT)
	{
		super(inner, toBT, fromBT);
		this.inner = inner;
	}

	public BTElement getAddUnit()
	{
		return super.getMultUnit();
	}
	/**
	 * @param a
	 * @param b
	 * @return new BTBTElement add(a,b)
	 */
	public BTElement add(BTElement a, BTElement b) throws IllegalArgumentException
	{
		return super.mult(a, b);
	}
	/**
	 * @param a
	 * @param k>=0
	 * @return new BTBTElement opp(a,a)^k
	 */
	public BTElement mult(BTElement a, BigNumber k) throws IllegalArgumentException
	{
		return super.pow(a, k);
	}
	/**
	 * @param a
	 * @return new BTElement opp^-1(a)
	 */
	public BTElement addInverse(BTElement a) throws IllegalArgumentException
	{
		return super.inverse(a);
	}
	/**
	 * @param a
	 * @param b
	 * @return new BTElement opp(a,opp^-1(b))
	 */
	public BTElement subtract(BTElement a, BTElement b) throws IllegalArgumentException
	{
		return super.rightAdj(a, b);		
	}
	@Override
	public BTElement getMultUnit()
	{
		return new BTElement(this.inner.getAddUnit());
	}
	/**
	 * @param a
	 * @param b
	 * @return new BTElement opp2(a,b)
	 */
	@Override
	public BTElement mult(BTElement a, BTElement b) throws IllegalArgumentException
	{
		return new BTElement(this.inner.mult(a.getElement(), b.getElement()));
	}
	/**
	 * @param a
	 * @param k>=0
	 * @return new BTElement opp2(a,a)^k
	 */
	@Override
	public BTElement pow(BTElement a, BigNumber k) throws IllegalArgumentException
	{
		return new BTElement(this.inner.mult(a.getElement(), k));
	}
	/**
	 * @param a
	 * @param b
	 * @return new BTElement opp2(a,opp2^-1(b))
	 */
	@Override
	public BTElement inverse(BTElement a) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("This ArithObject Does not support Inverse for this opperation\n");
	}
	@Override
	public BTElement rightAdj(BTElement a, BTElement b) throws IllegalArgumentException
	{
		return mult(a,inverse(b));		
	}
	public <S> BTRing<Pair<T, S>> genCartProd(final BTRing<S> other){
		return new BTRing<Pair<T,S>>(this.inner.genCarProd(other.inner), super.genCartToBT(other), super.genCartFromBT(other)); 	
	}
}

