package ArithmeticObjects;

import ArithUtils.BigNumber;
import ArithUtils.Func;
import MathObjects.OperationStruct;
import MathObjects.OperationStruct.*;
import Utils.ByteTree;
import Utils.Pair;

public class UsfulOppStructs {
	public static OperationStruct<BigNumber> getMultOp(final BigNumber p, final BigNumber q) {
		OperationStruct<BigNumber> res = getMultOp(p);
		res.setInverse(new Inverse<BigNumber>() {
			@Override
			public BigNumber invoke(BigNumber p1) {
				return p1.powMod(q.substract(BigNumber.TWO), p);
			}
		});
		return res;
	}
	public static OperationStruct<BigNumber> getMultOp(final BigNumber p) {
		BigNumber unit = BigNumber.ONE;
		Action<BigNumber> action = new Action<BigNumber>() {

			@Override
			public BigNumber invoke(BigNumber p1, BigNumber p2) {
				return (p1.multiply(p2)).modulo(p);
			}
		};
		return new OperationStruct<BigNumber>(unit, action);
	}
	public static OperationStruct<BigNumber> getAddOp(final BigNumber p) {
		BigNumber unit = BigNumber.ZERO;
		Inverse<BigNumber> inverse = new Inverse<BigNumber>() {
			@Override
			public BigNumber invoke(BigNumber p1) {
				return p.substract(p1);
			}
		};
		Action<BigNumber> action = new Action<BigNumber>() {
			@Override
			public BigNumber invoke(BigNumber p1, BigNumber p2) {
				return (p1.add(p2)).modulo(p);
			}
		};
		return new OperationStruct<BigNumber>(unit, action ,inverse);
	}
	
	public static OperationStruct<Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>> getECMultOp(final String name)
	{
		EllipticCurveParam params = EllipticCurveParam.get(name);
		final BTField<BigNumber> fl = staticTypes.getNumberField(params.fieldChar);
		final BTField<BigNumber>.BTElement Xco = fl.getElement(params.aCoaf);
		final Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> unit = new Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> (fl.getElement(BigNumber.MONE),fl.getElement(BigNumber.MONE));
		Inverse<Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>> inverse = new Inverse<Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>>() {
			@Override
			public Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> invoke(Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> p1) {
				BTField<BigNumber>.BTElement Yi=fl.addInverse(p1.getT2());
				return new Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>(p1.getT1(), Yi);
			}
		};
		Action<Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>> action =new Action<Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>>()
		{
			@Override
			public Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> invoke(Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> p1, Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> p2) {
				BTField<BigNumber>.BTElement Xp1 = p1.getT1();
				BTField<BigNumber>.BTElement Xp2 = p2.getT1();
				BTField<BigNumber>.BTElement Yp1 = p1.getT2();
				BTField<BigNumber>.BTElement Yp2 = p2.getT2();

				if(fl.ElementIsEqual(Xp1,unit.getT1()) && fl.ElementIsEqual(Yp1,unit.getT2()))
					return p2;
				if(fl.ElementIsEqual(Xp2,unit.getT1()) && fl.ElementIsEqual(Yp2,unit.getT2()))
					return p1;

				if(!fl.ElementIsEqual(Xp1, Xp2))
				{
					BTField<BigNumber>.BTElement S=fl.rightAdj(fl.subtract(Yp1, Yp2), fl.subtract(Xp1, Xp1));
					BTField<BigNumber>.BTElement Xr=fl.subtract(fl.subtract(fl.pow(S, BigNumber.TWO), Xp1), Xp2);
					BTField<BigNumber>.BTElement Yr=fl.subtract(Yp1, fl.mult(S, fl.subtract(Xr, Xp1)));
					return new Pair<BTField<BigNumber>.BTElement,BTField<BigNumber>.BTElement>(Xr, Yr);
				}
				if(fl.ElementIsEqual(Yp1, fl.addInverse(Yp2)))
					return unit;
					
				BTField<BigNumber>.BTElement S = fl.rightAdj(fl.subtract(fl.mult(fl.mult(Xp1, Xp1),new BigNumber("3")),Xco) , fl.add(Yp1, Yp1));
				BTField<BigNumber>.BTElement Xr=fl.subtract(fl.pow(S, BigNumber.TWO), fl.mult(Xp1, BigNumber.TWO));
				BTField<BigNumber>.BTElement Yr=fl.subtract(Yp1, fl.mult(S, fl.subtract(Xr, Xp1)));
				return new Pair<BTField<BigNumber>.BTElement,BTField<BigNumber>.BTElement>(Xr, fl.addInverse(Yr));	
			}
		};

		return new OperationStruct<Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>>(unit, action ,inverse);
		
	}
	public static Func.P1<ByteTree, BigNumber> getMGtoBT()
	{
		return new Func.P1<ByteTree, BigNumber>() {
			@Override
			public ByteTree invoke(BigNumber p1) {
				ByteTree leaf=ByteTree.generateNewLeaf(p1.toByteArray());
				return leaf;
			}
		};
	}
	
	public static Func.P1<BigNumber, ByteTree> getMGfromBT()
	{
		return new Func.P1<BigNumber, ByteTree>() {
			@Override
			public BigNumber invoke(ByteTree bt) {
				return bt.getBigNum();
			}
		};
	}
	
}
