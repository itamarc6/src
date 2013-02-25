package MathObjects;

import Utils.Pair;
import ArithUtils.BigNumber;

public class Ring<T> extends Group<T> {

	protected Ring(ArithmeticSet<T> ArithObj, BigNumber order) {
		super(ArithObj, order);
	}
	public Ring(OperationStruct<T> addOpp, OperationStruct<T> multOpp, BigNumber order) {
		super(addOpp, order);
		super.getArithObj().addOpp(multOpp);
	}
	/**
	 * @param a
	 * @param b
	 * @return new Element add(a,b)
	 */
	public Element getAddUnit()
	{
		return super.getMultUnit();
	}
	
	public Element add(Element a, Element b) throws IllegalArgumentException
	{
		return super.mult(a, b);
	}
	/**
	 * @param a
	 * @param k>=0
	 * @return new Element opp(a,a)^k
	 */
	public Element mult(Element a, BigNumber k) throws IllegalArgumentException
	{
		return super.pow(a, k);
	}
	/**
	 * @param a
	 * @return new Element opp^-1(a)
	 */
	public Element addInverse(Element a) throws IllegalArgumentException
	{
		return super.inverse(a);
	}
	/**
	 * @param a
	 * @param b
	 * @return new Element opp(a,opp^-1(b))
	 */
	public Element subtract(Element a, Element b) throws IllegalArgumentException
	{
		return super.rightAdj(a, b);		
	}
	@Override
	public Element getMultUnit()
	{
		return new Element(getArithObj().getUnit(1));
	}
	
	/**
	 * @param a
	 * @param b
	 * @return new Element opp2(a,b)
	 */
	@Override
	public Element mult(Element a, Element b) throws IllegalArgumentException
	{
		if (a.getParent() != b.getParent() || a.getParent() != this.getArithObj())
			throw new IllegalArgumentException("Two elements must be members of the same ring");
		return new Element(getArithObj().getAction(1).invoke(a.getValue(), b.getValue()));
	}
	/**
	 * @param a
	 * @param k>=0
	 * @return new Element opp2(a,a)^k
	 */
	@Override
	public Element pow(Element a, BigNumber k) throws IllegalArgumentException
	{
		if (a.getParent() != this.getArithObj())
			throw new IllegalArgumentException("Two elements must be members of the same ring");
		if (k.compareTo(BigNumber.ZERO) < 0)
			throw new IllegalArgumentException("The exponent must be a non-negative integer");
		return new Element(getArithObj().getPower(1).invoke(a.getValue(), k));
	}
	/**
	 * @param a
	 * @param b
	 * @return new Element opp2(a,opp2^-1(b))
	 */
	@Override
	public Element inverse(Element a) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException("This ArithObject Does not support Inverse for this opperation\n");
	}
	@Override
	public Element rightAdj(Element a, Element b) throws IllegalArgumentException
	{
		return mult(a,inverse(b));		
	}
	public <S> Ring<Pair<T, S>> genCarProd(final Ring<S> other){
		BigNumber order = this.getOrder().multiply(other.getOrder());
		return new Ring<Pair<T,S>>(this.getArithObj().genCartProd(other.getArithObj()), order);
	}
}
