package CryptoPrim;

import Utils.ByteTree;
import Utils.TypeConvertionUtils;
import ArithmeticObjects.Element;
import ArithmeticUtils.Array;

public class Plaintext {
	
	private Array<Element> elmArr;
	
	public Plaintext(Array<Element> elmArr){
		this.elmArr = elmArr;
	}

	public Plaintext() {
		this.elmArr = new Array<Element>();
	}

	public Element get(int i){
		return this.elmArr.get(i);
	}
	
	public void add(Element elm){
		this.elmArr.add(elm);
	}
	
	public int getWidth(){
		return TypeConvertionUtils.bigNumberToInt(this.elmArr.getLength());
	}
	
	public ByteTree getByteTreeRep(){
		return this.elmArr.getByteTreeRep();
	}
	
	public static Plaintext getPlaintext(ByteTree bt){
		return new Plaintext(ArithmeticUtils.Conversions.BTToElementArray(bt));
	}
	
	public static Plaintext generateFixedPlaintext(Element elm, int width) {
		Plaintext ret = new Plaintext();
		for (int i=0; i<width; i++)
			ret.add(elm);
		return ret;
	}

	public Array<Element> getArr() {
		return this.elmArr;
	}

	public static Array<Plaintext> getPlaintextArray(ByteTree bt) {
		Array<Plaintext> ret = new Array<Plaintext>();
		for(int i=0; i<bt.getAmountOfDirectChildren(); i++){
			ret.add(Plaintext.getPlaintext(bt.getTheNChild(i)));
		}
		return ret;
	}
}
