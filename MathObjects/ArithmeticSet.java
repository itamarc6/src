package MathObjects;

import java.util.ArrayList;
import MathObjects.OperationStruct.*;
import Utils.Pair;


/**
 * @author Shir Peleg
 * This class represents a general, generic Arithmetic Set, as Defined to be a list of actions 
 * opp:TxT->T, their units, inverse, and powers (if defined).
 */

public class ArithmeticSet<T> 
{
	protected ArrayList<OperationStruct<T>> opps = new ArrayList<OperationStruct<T>>();

	protected ArithmeticSet(OperationStruct<T> opp1)
	{
		this.opps.add(opp1);
	}
	protected ArithmeticSet(ArrayList<OperationStruct<T>> opps)
	{
		this.opps = opps;
	}
	protected void addOpp(OperationStruct<T> opp)
	{
		this.opps.add(opp);
	}
	/** 
	 * @return the number of actions of the Arithmetic Set.
	 */
	protected int getActionNumber() {
		return opps.size();
	}

	/** 
	 * @return the unit value of the i'th action.
	 */
	protected final T getUnit(int id) throws UnsupportedOperationException
	{
		return this.opps.get(id).getUnit();
	}

	/** 
	 * @return the i'th action.
	 */
	protected final Action<T> getAction(int id) throws UnsupportedOperationException
	{
		return this.opps.get(id).getAction();
	}

	/** 
	 * @return the Power action of the i'th action.
	 */
	protected final Power<T> getPower(int id) throws UnsupportedOperationException
	{
		return this.opps.get(id).getPower();
	}

	/** 
	 * @return the inverse action of the i'th action.
	 */
	protected final Inverse<T> getInverse(int id) throws UnsupportedOperationException
	{
		return this.opps.get(id).getInverse();
	}
	/** 
	 * @return Sets the inverse action of the i-th action.
	 */
	protected void setInverse(int i, Inverse<T> inverse)
	{
		this.opps.get(i).setInverse(inverse);
	}

	/** 
	 * @return An arithmetic sets that operates on the Cartesian product of the given arithmetic sets.
	 */
	protected <S> ArithmeticSet<Pair<T,S>> genCartProd (final ArithmeticSet<S> other)
	{

		int exeptedActions = Math.min(this.getActionNumber(), other.getActionNumber());
		ArrayList<OperationStruct<Pair<T,S>>> res = new ArrayList<OperationStruct<Pair<T,S>>>();
		for(int i = 0; i < exeptedActions; i++)
			res.add(this.opps.get(i).genCartProd(other.opps.get(i)));
		return new ArithmeticSet<Pair<T,S>>(res);
	}
}