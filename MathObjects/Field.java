package MathObjects;

import ArithUtils.BigNumber;

public class Field<T> extends Ring<T> {

	protected Field(ArithmeticSet<T> ArithObj, BigNumber order) {
		super(ArithObj, order);
	}
	public Field(OperationStruct<T> addOpp, OperationStruct<T> multOpp, BigNumber order)
	{
		super(addOpp, multOpp, order);
	}
	/**
	 * @param a
	 * @return new Element opp2^-1(a)
	 */
	@Override
	public Element inverse(Element a) throws UnsupportedOperationException, IllegalArgumentException
	{
		if (a.getParent() != this.getArithObj())
			throw new IllegalArgumentException("Two elements must be members of the same ring");
		if(ElementIsEqual(a, new Element(this.getArithObj().getUnit(0))))
			throw new IllegalArgumentException("Division by zero");
		if (getArithObj().getInverse(0)==null)
			throw new UnsupportedOperationException("This ArithObject Does not support Inverse for this opperation\n");
		return new Element(getArithObj().getInverse(1).invoke(a.getValue()));
	}

}
