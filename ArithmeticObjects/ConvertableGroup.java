package ArithmeticObjects;

import java.util.InputMismatchException;
import ArithUtils.Array;
import ArithUtils.BigNumber;
import ArithUtils.Func.P1;
import MathObjects.OperationStruct;
import Utils.ByteTree;

public abstract class ConvertableGroup<T> extends BTGroup<T> {

	public ConvertableGroup(OperationStruct<T> multOpp, BigNumber order,
			P1<ByteTree, T> toBT, P1<T, ByteTree> fromBT) {
		super(multOpp, order, toBT, fromBT);
	}
	public abstract Array<BTElement> randomArray(int N, byte [] vector, int nr) throws InputMismatchException;
	
	public abstract ByteTree getByteTreeRep();
	
	public abstract BTElement getGenerator();
}