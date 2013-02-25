package ArithmeticObjects;

import ArithUtils.*;
import FiatShamirProofs.Proof;
import Utils.ByteTree;

/**
 * @author Shir Peleg
 * This class represents a ModularGroup which extends Group of BigNumbers. 
 */

public class ModularGroup extends ConvertableGroup<BigNumber> {

	private BTElement generator;
	private BigNumber order;
	private BigNumber fieldChar;
	private static int certainty = (int)Math.pow(10,6);
	
	/**
	 * @param p- the order of the containing group.
	 * @param q- the order of the group.
	 * @param g- the generator of the group.
	 */
	public ModularGroup(BigNumber p, BigNumber q, BigNumber g) throws IllegalArgumentException
	{
		super(UsfulOppStructs.getMultOp(p, q), q, UsfulOppStructs.getMGtoBT(), UsfulOppStructs.getMGfromBT());//TODO
		if(!p.isProbablyPrime(certainty)||!q.isProbablyPrime(certainty))
			throw new IllegalArgumentException("Error: ModularGroup order, and the order of the containig group must be primes!");
		this.generator=new BTElement(g);
		this.order=q;
		this.fieldChar=p;
	}
	public ModularGroup(ByteTree bt) throws IllegalArgumentException
	{
		this (bt.getTheNChild(2).getTheNChild(0).getBigNum(),
			   bt.getTheNChild(2).getTheNChild(1).getBigNum(),
		       bt.getTheNChild(2).getTheNChild(2).getBigNum());
	}
	
	public BTElement getGenerator() {
		return this.generator;
	}
	/**
	 * @return a random Array of ModularGroup.Elements.
	 */
	@Override
	public Array<BTElement> randomArray(int N, byte [] vector, int nr) {
		Array<BTElement> res=new Array<BTElement>();
		BigNumber exp=this.fieldChar.substract(BigNumber.ONE).divide(order);
		int np=fieldChar.bitLength();
		BigNumber [] numbers=Proof.deriveRandomBigNumbers(vector, nr+np, N, exp, this.fieldChar);
		for (int i = 0; i < numbers.length; i++) {
			BigNumber temp=numbers[i].modulo(BigNumber.TWO.pow(nr+np));
			BigNumber hi=temp.powMod(exp, fieldChar);
			res.add(new BTElement(hi));
		}
		return res;
	}
	
	public BTElement getElement(ByteTree k) {
		return getElement(k.getBigNum());
	}
	
	public ByteTree getByteTreeRep()
	{
		ByteTree bt = ByteTree.generateEmptyNode();
		ByteTree node = ByteTree.generateEmptyNode();
		bt.addChild(ByteTree.generateNewLeaf("verificatum.arithm.ModPGroup"));
		node.addChild(ByteTree.generateNewLeaf(this.fieldChar));
		node.addChild(ByteTree.generateNewLeaf(this.order));
		node.addChild(ByteTree.generateNewLeaf(this.generator.getValue()));
		bt.addChild(node);
		return bt;
	}
}