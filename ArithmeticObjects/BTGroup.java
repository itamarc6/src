package ArithmeticObjects;

import ArithUtils.Array;
import ArithUtils.BigNumber;
import ArithUtils.Func;
import MathObjects.ArithmeticSet;
import MathObjects.Group;
import MathObjects.OperationStruct;
import Utils.ByteTree;
import Utils.Pair;


public class BTGroup<T> 
{
	protected final Func.P1<ByteTree, T> toBT;
	protected final Func.P1<T, ByteTree> fromBT;
	protected Group<T> inner;

	public BTGroup(OperationStruct<T> multOpp, BigNumber order,  Func.P1<ByteTree, T> toBT, Func.P1<T, ByteTree> fromBT)
	{
		this(new Group<T>(multOpp, order), toBT, fromBT);
	}

	public BTGroup(Group<T> inner, Func.P1<ByteTree, T> toBT, Func.P1<T, ByteTree> fromBT)
	{
		this.inner = inner;
		this.toBT = toBT;
		this.fromBT = fromBT;
	}
	public BTElement getMultUnit()
	{
		return new BTElement(this.inner.getMultUnit());
	}
	/**
	 * @param btElement
	 * @param btElement2
	 * @return new Element opp(a,b)
	 */
	public BTElement mult(BTElement btElement, BTElement btElement2) throws IllegalArgumentException
	{
		return new BTElement(this.inner.mult(btElement.getElement(), btElement2.getElement()));
	}
	/**
	 * @param a
	 * @param k>=0
	 * @return new Element opp(a,a)^k
	 */
	public BTElement pow(BTElement a, BigNumber k) throws IllegalArgumentException
	{
		return new BTElement(this.inner.pow(a.getElement(), k));
	}
	/**
	 * @param a
	 * @return new Element opp^-1(a)
	 */
	public BTElement inverse(BTElement a) throws IllegalArgumentException
	{
		return new BTElement(this.inner.inverse(a.getElement()));
	}
	/**
	 * @param a
	 * @param b
	 * @return new Element opp(a,opp^-1(b))
	 */
	public BTElement rightAdj(BTElement a, BTElement b) throws IllegalArgumentException
	{
		return new BTElement(this.inner.rightAdj(a.getElement(), b.getElement()));
	}

	public boolean ElementIsEqual(BTElement a, BTElement b)
	{
		return this.inner.ElementIsEqual(a.getElement(), b.getElement());
	}
	/**
	 * @return A group element containing the value.
	 */
	public BTElement getElement(T value)
	{
		return new BTElement(this.inner.getElement(value));	
	}

	/**
	 * @return The group order, i.e the number of elements in it. null if infinity.
	 */
	public BigNumber getOrder()
	{
		return this.inner.getOrder();
	}

	protected <S> Func.P1<ByteTree,Pair<T, S>> genCartToBT(final BTGroup<S> other)
	{
		return new Func.P1<ByteTree, Pair<T, S>>() {
			@Override
			public ByteTree invoke(Pair<T, S> p1) {			
				ByteTree res = ByteTree.generateEmptyNode();
				res.addChild(BTGroup.this.toBT.invoke(p1.getT1()));
				res.addChild(other.toBT.invoke(p1.getT2()));
				return res;
			}
		};
	}

	protected <S> Func.P1<Pair<T, S>, ByteTree> genCartFromBT(final BTGroup<S> other)
	{
		return new Func.P1<Pair<T, S>, ByteTree>() {
			@Override
			public Pair<T, S> invoke(ByteTree bt) {
				T t = BTGroup.this.fromBT.invoke(bt.getTheNChild(0));
				S s = other.fromBT.invoke(bt.getTheNChild(1));
				return new Pair<T, S> (t, s);
			}
		};
	}
	/** 
	 * @return A Cartesian product of two certain groups.
	 */
	public <S> BTGroup<Pair<T, S>> genCartProd(final BTGroup<S> other)
	{
		Func.P1<ByteTree, Pair<T, S>> toBT = this.genCartToBT(other);
		Func.P1<Pair<T, S>, ByteTree> fromBT = this.genCartFromBT(other);
		return new BTGroup<Pair<T, S>>(this.inner.genCartProd(other.inner),toBT, fromBT);
	}

	/**
	 * @return An Array of group elements contained in the ByteTree.
	 */

	public BTElement getElement(ByteTree bytetree)
	{ 
		return new BTElement(this.fromBT.invoke(bytetree));
	}
	public ByteTree EtoByteTree(BTElement element)
	{ 
		return this.toBT.invoke(element.getValue());
	}

	public Array<BTElement> getElemArr(ByteTree bt) {
		Array<BTElement> res = new Array<BTElement>();
		for(ByteTree bytetree : bt.getDirectChildren())
			res.add(this.getElement(bytetree));
		return res;
	}

	/**
	 * @return An Array of group elements contained in the ByteTree.
	 */
	public ByteTree toByteTree(Array<BTElement> elements)
	{
		ByteTree res = ByteTree.generateEmptyNode();
		for(BTElement element : elements)
			res.addChild(EtoByteTree(element));
		return res;
	}

	/**
	 * @return A group element containing the value in the ByteTree.
	 */
	//public Element getElement(ByteTree bt);
	/**
	 * @return A ByteTree representation of the element.
	 */
	//public ByteTree getElemByteTreeRep(Element element);
	//public ByteTree getByteTreeRep();



	/** 
	 * @class representing a Group Element.
	 */
	public class BTElement{

		private Group<T>.Element elem;

		public BTElement(Group<T>.Element elem) 
		{
			this.elem = elem;
		}
		public BTElement(T value) 
		{
			this.elem = inner.getElement(value);
		}
		public Group<T>.Element getElement() 
		{
			return this.elem;
		}
		public T getValue() 
		{
			return this.elem.getValue();
		}
		public ArithmeticSet<T> getParent() {
			return BTGroup.this.inner.getArithObj();
		}
		public ByteTree getByteTree(){
			return BTGroup.this.EtoByteTree(this);
		}
	};
}