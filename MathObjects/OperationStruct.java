package MathObjects;

import Utils.Pair;
import ArithUtils.BigNumber;
import ArithUtils.Func;

public class OperationStruct<T> 
{

	private final T unit;
	private final Action<T> action;
	private Power<T> power;
	private Inverse<T> inverse;

	public OperationStruct(T unit, Action<T> action, Power<T> power, Inverse<T> inverse) {
		this.unit = unit;
		this.action = action;
		this.power = power;
		this.inverse = inverse;
	}
	public OperationStruct(T unit, Action<T> action, Power<T> power) {
		this(unit, action, power, null);
	}
	public OperationStruct(T unit, Action<T> action, Inverse<T> inverse) {
		this(unit, action, null, inverse);
		this.power = getDefoltPower(action);
	}
	public OperationStruct(T unit, Action<T> action) {
		this(unit, action, null, null);
		this.power = getDefoltPower(action);
	}

	public Power<T> getDefoltPower(Action<T> action2) 
	{
		Power<T> power = new Power<T>() {
			@Override
			public T invoke(T p1, BigNumber p2) {
				char c;
				T result = unit;
				String binary=p2.toBinaryString();
				while(binary.length() > 0)
				{
					c = binary.charAt(0);
					binary=binary.substring(1); 
					result=action.invoke(result, result);
					if(c == '1')
						result = action.invoke(p1,result);
				}
				return result;
			}
		};
		return power;
	}
	public T getUnit()
	{
		return unit;
	}
	public Action<T> getAction()
	{
		return action;
	}
	public Power<T> getPower()
	{
		return power;
	}
	public Inverse<T> getInverse() 
	{
		return inverse;
	}
	public void setPower(Power<T> power)
	{
		this.power = power;
	}
	public void setInverse(Inverse<T> inverse)
	{
		this.inverse = inverse;
	}

	protected <S> OperationStruct<Pair<T,S>> genCartProd (final OperationStruct<S> other)
	{
		Pair<T,S> unit = new Pair<T, S>(this.getUnit(), other.getUnit());
		Action<Pair<T,S>> action = new Action<Pair<T,S>>(){
			@Override
			public Pair<T,S> invoke(Pair<T,S> p1, Pair<T,S> p2)	{
				T v1 = OperationStruct.this.getAction().invoke(p1.getT1(), p2.getT1());
				S v2 = other.getAction().invoke(p1.getT2(), p2.getT2());;
				return new Pair<T, S>(v1, v2);
			}
		};
		Power<Pair<T,S>> power = new Power<Pair<T,S>>(){
			@Override
			public Pair<T,S> invoke(Pair<T,S> p1, BigNumber p2)	{
				T v1 = OperationStruct.this.getPower().invoke(p1.getT1(), p2);
				S v2 = other.getPower().invoke(p1.getT2(), p2);
				return new Pair<T, S>(v1, v2);
			}
		};
		Inverse<Pair<T,S>> inverse = new Inverse<Pair<T,S>>(){
			@Override
			public Pair<T,S> invoke(Pair<T,S> p1)	{
				T v1 = OperationStruct.this.getInverse().invoke(p1.getT1());
				S v2 = other.getInverse().invoke(p1.getT2());
				return new Pair<T, S>(v1, v2);
			}
		};

		return new OperationStruct<Pair<T,S>>(unit, action, power, inverse);
	}
	/** 
	 * @Rename of the Func class, to have meaningful names to the context.
	 */
	public static abstract class Inverse<T> extends Func.P1<T, T> { };
	public static abstract class Power<T> extends Func.P2<T, T, BigNumber> { };
	public static abstract class Action<T> extends Func.P2<T, T, T> { };
}
