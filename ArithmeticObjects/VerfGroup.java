package ArithmeticObjects;

import java.util.InputMismatchException;

import Utils.ByteTree;
import ArithUtils.*;
import ArithmeticObjects.BTGroup.BTElement;

public class VerfGroup{

	private ConvertableGroup<?> group;
	private VElement generator;

	public VerfGroup(String name) {
		this.group=new ElipticCurve(name);
		this.generator = new VElement(group.getGenerator());
	}

	public VerfGroup(BigNumber p, BigNumber q, BigNumber g) {
		this.group=new ModularGroup(p,q,g);
		this.generator = new VElement(((ModularGroup)group).getGenerator());
	}
	public VElement mult(VElement a, VElement b) throws IllegalArgumentException
	{
		return new VElement(group.mult(a.getValue(), b.getValue()));
	}
	/**
	 * @param a
	 * @param k>=0
	 * @return new Element opp(a,a)^k
	 */
	public VElement pow(VElement a, BigNumber k) throws IllegalArgumentException
	{
		return new VElement(group.pow(a.getValue(), k));
	}
	/**
	 * @param a
	 * @return new Element opp^-1(a)
	 */
	public VElement inverse(VElement a) throws IllegalArgumentException
	{
		return new VElement(group.inverse(a.getValue()));
	}
	/**
	 * @param a
	 * @param b
	 * @return new Element opp(a,opp^-1(b))
	 */
	public VElement rightAdj(VElement a, VElement b) throws IllegalArgumentException
	{
		return new VElement(group.rightAdj(a.getValue(), b.getValue()));
	}

	public boolean ElementIsEqual(VElement a, VElement b)
	{
		return group.ElementIsEqual(a.getValue(), b.getValue());
	}
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	public BigNumber getOrder() {
		return group.getOrder();
	}

	public VElement getElement(ByteTree bt) {
		return new VElement(group.getElement(bt));
	}
	public Array<VElement> getArrayOfElementsFromBT(ByteTree bt) {
		Array<VElement> arr=new Array<VElement>();
		Array<ConvertableGroup<?>.BTElement> temp=group.getElemArr(bt);
		for (ConvertableGroup<?>.BTElement element : temp) {
			VElement n=new VElement(element);
			arr.add(n);
		}
		return arr;
	}
	public ByteTree toByteTree(VElement element) {
		return element.toByteTree();
	}
	public ByteTree getByteTreeRep() {
		return group.getByteTreeRep();
	}
	public VElement getGenerator() {
		return this.generator;
	}
	public Array<VElement> randomArray(int N, byte[] vector, int nr) throws InputMismatchException {
		Array<VElement> arr=new Array<VElement>();
		Array<ConvertableGroup<?>.BTElement> temp=group.randomArray(N, vector, nr);
		for (ConvertableGroup<?>.BTElement element : temp) {
			VElement n=new VElement(element);
			arr.add(n);
		}
		return arr;
	}
	public class VElement{
		private ConvertableGroup<?>.BTElement elem;

		private VElement(ConvertableGroup<?>.BTElement elem) {
			this.elem =  elem;
		}
		public ConvertableGroup<?>.BTElement getValue()
		{
			return this.elem;
		}
		public ByteTree toByteTree()
		{
			return elem.getByteTree();
		}
	}
}
