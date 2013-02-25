package ArithmeticObjects;

import java.util.InputMismatchException;
import ArithUtils.*;
import FiatShamirProofs.Proof;
import Utils.ByteTree;
import Utils.Pair;

/**
 * @author Shir Peleg
 * This class represents an Elliptic Curve which extends Group of pairs of field elements. 
 */

public class ElipticCurve extends ConvertableGroup<Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>> 
{
	private String name;
	private BTField<BigNumber> field;
	private BTField<BigNumber>.BTElement ACoaf;
	private BTField<BigNumber>.BTElement BCoaf;
	private BTElement generator;

	public ElipticCurve(String name)
	{
		super(UsfulOppStructs.getECMultOp(name), new EllipticCurveParam(name).groupOrder, null, null);
		EllipticCurveParam params = EllipticCurveParam.get(name);
		this.field = staticTypes.getNumberField(params.fieldChar);
		this.ACoaf = field.getElement(params.aCoaf);
		this.BCoaf = field.getElement(params.bCoaf);
		BTField<BigNumber>.BTElement xCoef = field.getElement(params.genXCoaf);
		BTField<BigNumber>.BTElement yCoef = field.getElement(params.genYCoaf);
		Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> value = new Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>(xCoef, yCoef);
		this.generator = new BTElement(value);
	}
	public ElipticCurve(ByteTree bt)
	{
		this(bt.getTheNChild(1).getString());
	}

//	private static ArithmeticSet<Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>> genrateArithObj(final String name)
//	{
//		final ArithmeticSet<Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>> res = new ArithmeticSet<Pair<BTField<BigNumber>.BTElement,BTField<BigNumber>.BTElement>>(1);
//		EllipticCurveParam params = EllipticCurveParam.get(name);
//		final BTField<BigNumber> fl = staticTypes.getNumberField(params.fieldChar);
//		final BTField<BigNumber>.BTElement Xco = fl.getElement(params.aCoaf);
//		final Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> unit = new Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> (fl.getElement(BigNumber.MONE),fl.getElement(BigNumber.MONE));
//		ArithmeticSet<Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>>.Inverse inverse = res.new Inverse() {
//
//			@Override
//			public Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> invoke(Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> p1) {
//				BTField<BigNumber>.BTElement Yi=fl.addInverse(p1.getT2());
//				return new Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>(p1.getT1(), Yi);
//			}
//		};
//		ArithmeticSet<Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>>.Action action = res.new Action()
//		{
//			@Override
//			public Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> invoke(Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> p1, Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement> p2) {
//				BTField<BigNumber>.BTElement Xp1 = p1.getT1();
//				BTField<BigNumber>.BTElement Xp2 = p2.getT1();
//				BTField<BigNumber>.BTElement Yp1 = p1.getT2();
//				BTField<BigNumber>.BTElement Yp2 = p2.getT2();
//
//				if(fl.ElementIsEqual(Xp1,unit.getT1()) && fl.ElementIsEqual(Yp1,unit.getT2()))
//					return p2;
//				if(fl.ElementIsEqual(Xp2,unit.getT1()) && fl.ElementIsEqual(Yp2,unit.getT2()))
//					return p1;
//
//				if(!fl.ElementIsEqual(Xp1, Xp2))
//				{
//					BTField<BigNumber>.BTElement S=fl.rightAdj(fl.subtract(Yp1, Yp2), fl.subtract(Xp1, Xp1));
//					BTField<BigNumber>.BTElement Xr=fl.subtract(fl.subtract(fl.pow(S, BigNumber.TWO), Xp1), Xp2);
//					BTField<BigNumber>.BTElement Yr=fl.subtract(Yp1, fl.mult(S, fl.subtract(Xr, Xp1)));
//					return new Pair<BTField<BigNumber>.BTElement,BTField<BigNumber>.BTElement>(Xr, Yr);
//				}
//				if(fl.ElementIsEqual(Yp1, fl.addInverse(Yp2)))
//					return unit;
//					
//				BTField<BigNumber>.BTElement S = fl.rightAdj(fl.subtract(fl.mult(fl.mult(Xp1, Xp1),new BigNumber("3")),Xco) , fl.add(Yp1, Yp1));
//				BTField<BigNumber>.BTElement Xr=fl.subtract(fl.pow(S, BigNumber.TWO), fl.mult(Xp1, BigNumber.TWO));
//				BTField<BigNumber>.BTElement Yr=fl.subtract(Yp1, fl.mult(S, fl.subtract(Xr, Xp1)));
//				return new Pair<BTField<BigNumber>.BTElement,BTField<BigNumber>.BTElement>(Xr, fl.addInverse(Yr));	
//			}
//		};
//
//		res.addAction(unit, action, inverse);
//		return res;
//	}


	public BTElement getElement(BigNumber t1, BigNumber t2) {
		return super.getElement(new Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>(field.getElement(t1), field.getElement(t2)));
	}

	public BTElement getElement(ByteTree k) {
		return getElement(k.getTheNChild(0).getBigNum(), k.getTheNChild(1).getBigNum());
	}
	/** 
	 * @return A Random Array of  ElipticCurve.Element.
	 */
	@Override
	public Array<BTElement> randomArray(int N, byte [] vector, int nr) throws InputMismatchException{
		Array<BTElement> res=new Array<BTElement>();
		BigNumber exp=BigNumber.ONE;
		BigNumber FChar=field.getOrder();
		int np=FChar.bitLength();
		BigNumber [] numbers=Proof.deriveRandomBigNumbers(vector, nr+np, N, exp,FChar);
		BigNumber bigN=new BigNumber(N);
		int i=0;
		BigNumber maxPow2=field.getOrder().getMaxTwoExp();
		BigNumber res2=field.getOrder().divide(maxPow2);
		BigNumber minZ=getMinimalNQR(field.getOrder());
		while((res.getLength().compareTo(bigN)<0) && (i<numbers.length)) {
			BigNumber temp=numbers[i].modulo(BigNumber.TWO.pow(nr+np));
			BigNumber ziValue=temp.modulo(FChar);
			BTField<BigNumber>.BTElement zi=field.getElement(ziValue);
			if(isQuadraticResedue(getFuncRes(zi)))
			{
				BTField<BigNumber>.BTElement yi=field.getElement(getMinimalRoot(getFuncRes(zi), maxPow2, res2, minZ));
				res.add(new BTElement(new Pair<BTField<BigNumber>.BTElement, BTField<BigNumber>.BTElement>(zi, yi)));
			}
			i++;
		}
		if(res.getLength().compareTo(new BigNumber(N))!=0)
			throw new InputMismatchException("Not Enought Numbers, to generate wanted size array of ElupticCureve.Element");
		return res;
	}
	/**
	 * @return the minimal element of the field Z-groupOrder, which is not a quadratic residue.
	 */
	private BigNumber getMinimalNQR(BigNumber groupOrder) {
		BigNumber i=BigNumber.TWO;
		while(i.compareTo(field.getOrder())<=0)
		{
			BTField<BigNumber>.BTElement elem=field.getElement(i);
			if(!isQuadraticResedue(elem))
				return elem.getValue();
		}
		return null;
	}
	/**
	 * @return the minimal root of Fzi in this.field.
	 */
	private BigNumber getMinimalRoot(BTField<BigNumber>.BTElement Fzi, BigNumber maxPow2, BigNumber resQ, BigNumber minZ) {
		BigNumber res;
		BigNumber[] UnitArr=new BigNumber[10];
		for (int i=0;i<10;i++) {
			UnitArr[i]=new BigNumber(i);
		}
		if (field.getOrder().modulo(UnitArr[4]).compareTo(UnitArr[3])==0)
		{
			res = field.pow(Fzi, field.getOrder().divide(UnitArr[4]).add(UnitArr[1])).getValue();
		}
		else
		{		
			BigNumber M=maxPow2;

			BTField<BigNumber>.BTElement c=field.pow(field.getElement(minZ), resQ);
			BTField<BigNumber>.BTElement R=field.pow(Fzi, (resQ.add(BigNumber.ONE)).divide(BigNumber.TWO));
			BTField<BigNumber>.BTElement t=field.pow(Fzi, resQ);
			while(!field.ElementIsEqual(t, field.getGenerator()))
			{
				BigNumber i=BigNumber.ZERO;
				BTField<BigNumber>.BTElement temp=t;
				while((!field.ElementIsEqual(temp, field.getGenerator())) && (i.compareTo(M)<0)){
					temp=field.pow(temp, BigNumber.TWO);
					i=i.add(BigNumber.ONE);
				}
				BigNumber exp=M.substract(i.add(BigNumber.ONE));	
				BTField<BigNumber>.BTElement b=field.pow(c, BigNumber.TWO.pow(exp));
				R=field.mult(R, b);
				t=field.mult(t, field.pow(b, BigNumber.TWO));
				c=field.pow(b, BigNumber.TWO);
				M=i;
			}
			res=R.getValue();
		}
		if(res.compareTo(field.getOrder().substract(res))>=0)
		{
			return field.getOrder().substract(res); 
		}
		return res;
	}

	public BigNumber[] getCoaf()
	{
		BigNumber[] arr = {ACoaf.getValue(),BCoaf.getValue()};
		return arr;
	}
	public BTElement getGenerator() {
		return this.generator;
	}
	/**
	 * @return true iff elem is Quadratic residue in this.field
	 */
	private boolean isQuadraticResedue(BTField<BigNumber>.BTElement elem)
	{
		BigNumber exp=(field.getOrder().substract(BigNumber.ONE)).divide(BigNumber.TWO);
		return ((elem.getValue().pow(exp)).compareTo(BigNumber.ONE)==0);
	}
	/**
	 * @return F(z)- the curve res over zi.
	 */
	private BTField<BigNumber>.BTElement getFuncRes(BTField<BigNumber>.BTElement elem)
	{
		return field.add(field.add((field.pow(elem,new BigNumber("3"))),field.mult(elem, ACoaf)),BCoaf);
	}
	
	public ByteTree getElemByteTreeRep(BTElement element){
		ByteTree leaf=ByteTree.generateEmptyNode();
		ByteTree leaf1=ByteTree.generateNewLeaf(element.getValue().getT1().getValue().toByteArray());
		ByteTree leaf2=ByteTree.generateNewLeaf(element.getValue().getT2().getValue().toByteArray());
		leaf.addMultChildren(leaf1, leaf2);
		return leaf;
	}
	@Override
	public ByteTree getByteTreeRep(){
		ByteTree bt = ByteTree.generateEmptyNode();
		bt.addChild(ByteTree.generateNewLeaf("verificatum.arithm.ECqPGroup"));
		bt.addChild(ByteTree.generateNewLeaf(this.name));
		return bt;
	}


}
