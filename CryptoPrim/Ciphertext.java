package CryptoPrim;

import java.io.IOException;

import Utils.ByteTree;
import Utils.TypeConvertionUtils;
import ArithmeticObjects.Element;
import ArithmeticUtils.Array;

public class Ciphertext {
	
	private Array<Element> elmArr1;
	private Array<Element> elmArr2;
	
	public Ciphertext(Array<Element> a, Array<Element> b) {
		this.elmArr1 = a;
		this.elmArr2 = b;
	}
	
	public Ciphertext(){
		this.elmArr1 = new Array<Element>();
		this.elmArr2 = new Array<Element>();
	}

	public ByteTree getByteTreeRep() throws IOException{
		ByteTree bt = ByteTree.generateEmptyNode();
		bt.addMultChildren(elmArr1.getByteTreeRep(),elmArr2.getByteTreeRep());
		return bt;
	}
	
	public static Ciphertext getCiphertext(ByteTree bt) {
		Array<Element> a = ArithmeticUtils.Conversions.BTToElementArray(bt.getTheNChild(0));
		Array<Element> b = ArithmeticUtils.Conversions.BTToElementArray(bt.getTheNChild(1));
		return new Ciphertext(a,b);
	}

	public int getWidth() {
		return TypeConvertionUtils.bigNumberToInt(this.elmArr1.getLength());
	}

	public Array<Element> getA() {
		return this.elmArr1;
	}
	
	public Array<Element> getB() {
		return this.elmArr2;
	}

	public void add(Element a, Element b) {
		this.elmArr1.add(a);
		this.elmArr2.add(b);
	}

	public static Array<Ciphertext> getCiphertextArray(ByteTree bt) {
		Array<Ciphertext> ret = new Array<Ciphertext>();
		for(int i=0; i<bt.getAmountOfDirectChildren(); i++){
			ret.add(Ciphertext.getCiphertext(bt.getTheNChild(i)));
		}
		return ret;
	}
	
	
	
}
