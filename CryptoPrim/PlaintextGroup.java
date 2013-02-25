package CryptoPrim;

import ArithmeticObjects.Group;
import ArithmeticUtils.BigNumber;

public class PlaintextGroup {
	
	private Group Gq;
	private int width;

	public PlaintextGroup(Group Gq, int width) {
		this.Gq = Gq;
		this.width = width;
	}

	public Plaintext multiply(Plaintext pt1, Plaintext pt2) {
		Plaintext ret = new Plaintext();
		for (int i = 0; i < this.width; i++){
			ret.add(Gq.multiply(pt1.get(i),pt2.get(i)));
		}
		return ret;
	}

	public boolean plaintextIsEqual(Plaintext pt1, Plaintext pt2) {
		if (pt1.getWidth() != pt2.getWidth())
			return false;
		for (int i=0; i < this.width; i++){
			if(!Gq.ElementIsEqual(pt1.get(i),pt2.get(i)))	
				return false;
		}
		return true;
	}

	public Plaintext pow(Plaintext pt, BigNumber e) {
		Plaintext ret = new Plaintext();
		for (int i=0; i < this.width; i++){
			ret.add(Gq.pow(pt.get(i),e));
		}
		return ret;
	}

	public Group getGq() {
		return this.Gq;
	}

	public Plaintext pow(Plaintext pt, Randomizer rm) {
		Plaintext ret = new Plaintext();
		for (int i=0; i < this.width; i++){
			ret.add(Gq.pow(pt.get(i),rm.get(i).getValue()));
		}
		return ret;
	}

}
