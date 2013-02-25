package CryptoPrim;

import ArithmeticObjects.Element;
import Utils.ByteTree;

public class PubKey {

	private Element g;
	private Element y;
	
	public PubKey(Element g, Element y) {
		this.g = g;
		this.y = y;
	}
	
	public ByteTree getByteTreeRep(){
		ByteTree ret = ByteTree.generateEmptyNode();
		return ret.addMultChildren(
				ArithmeticUtils.Conversions.ElementToByteTree(this.g),
				ArithmeticUtils.Conversions.ElementToByteTree(this.y));
	}
	
	public static PubKey getPubKey(ByteTree bt){
		return new PubKey(
				ArithmeticUtils.Conversions.BTToElement(bt.getTheNChild(0)),
				ArithmeticUtils.Conversions.BTToElement(bt.getTheNChild(1)));
	}

	public Element getY() {
		return this.y;
	}

	public Element getG() {
		return this.g;
	}
	
}
