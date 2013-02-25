package CryptoPrim;

import ArithmeticObjects.FieldElement;
import ArithmeticUtils.Array;
import Utils.ByteTree;

public class Randomizer {

	private Array<FieldElement> elmArr;
	
	public Randomizer(){
		this.elmArr = new Array<FieldElement>();
	}
	
	public Randomizer(Array<FieldElement> elmArr){
		this.elmArr = elmArr;
	}
	
	public static Randomizer getRandomizer(ByteTree bt) {
		return new Randomizer(ArithmeticUtils.Conversions.BTToFieldElementArray(bt));
	}

	public ByteTree getByteTreeRep(){
		return this.elmArr.getByteTreeRep();
	}

	public FieldElement get(int i) {
		return this.elmArr.get(i);
	}

	public void add(FieldElement elm) {
		this.elmArr.add(elm);
	}
	
}
