package CryptoPrim;

import ArithmeticObjects.FieldElement;
import Utils.ByteTree;

public class SecKey {

	private FieldElement x;

	public SecKey(FieldElement x){
		this.x = x;
	}
	
	public static SecKey getSecKey(ByteTree bt) {
		return new SecKey(ArithmeticUtils.Conversions.BTToFieldElement(bt));
	}

	public FieldElement getX() {
		return this.x;
	}
	
	public ByteTree getByteTreeRep(){
		return ArithmeticUtils.Conversions.FieldElementToBT(this.x);
	}

}
