package ArithmeticObjects;

import ArithUtils.BigNumber;
import ArithUtils.Func;
import MathObjects.Field;
import MathObjects.OperationStruct;
import Utils.ByteTree;
/**
 * @author Shir Peleg
 * This class represents a general field, of a given characteristics. 
 */
public class BTField<T> extends BTRing<T> {

	protected Field<T> inner;
	public BTField(OperationStruct<T> addOpp, OperationStruct<T> multOpp, BigNumber order, Func.P1<ByteTree, T> toBT, Func.P1<T, ByteTree> fromBT)
	{
		this(new Field<T>(addOpp, multOpp, order), toBT, fromBT);
	}
	public BTField(Field<T> inner, Func.P1<ByteTree, T> toBT, Func.P1<T, ByteTree> fromBT)
	{
		super(inner, toBT, fromBT);
		this.inner = inner;
	}

	/**
	 * @param a
	 * @return new Element opp2^-1(a)
	 */
	@Override
	public BTElement inverse(BTElement a) throws UnsupportedOperationException, IllegalArgumentException
	{
		return new BTElement(this.inner.inverse(a.getElement()));
	}

	public BTElement getGenerator() {
		return this.getMultUnit();
	}
}
