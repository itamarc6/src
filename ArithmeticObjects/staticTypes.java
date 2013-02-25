package ArithmeticObjects;

import ArithUtils.BigNumber;
import MathObjects.Field;

public class staticTypes {

	public static BTField<BigNumber> getNumberField(final BigNumber p)
	{
		
		return new BTField<BigNumber>(
				new Field<BigNumber>(UsfulOppStructs.getMultOp(p, p),
						UsfulOppStructs.getAddOp(p), p), null, null);//TODO
	}
	
	public static BTRing<BigNumber> getNumberRing(final BigNumber p)
	{
		return new BTRing<BigNumber>(
				new Field<BigNumber>(UsfulOppStructs.getMultOp(p),
						UsfulOppStructs.getAddOp(p), p), null, null);//TODO
	}
}
