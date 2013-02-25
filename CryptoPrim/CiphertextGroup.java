package CryptoPrim;

import ArithmeticObjects.Group;
import ArithmeticUtils.BigNumber;

public class CiphertextGroup {
	
	private Group Gq;
	private int width;
	
	public CiphertextGroup(Group Gq, int width) {
		this.Gq = Gq;
		this.width = width;
	}
	
	public boolean ciphertextIsEqual(Ciphertext ct1, Ciphertext ct2) {
		if (ct1.getWidth() != ct2.getWidth())
			return false;
		for (int i=0; i < this.width; i++){
			if(
					(!Gq.ElementIsEqual(ct1.getA().get(i),ct2.getA().get(i))) ||
					(!Gq.ElementIsEqual(ct1.getB().get(i),ct2.getB().get(i))))	
				return false;
		}
		return true;
	}

	public Ciphertext pow(Ciphertext ct, BigNumber e) {
		Ciphertext ret = new Ciphertext();
		for (int i=0; i < this.width; i++)
			ret.add(
					Gq.pow(ct.getA().get(i),e),
					Gq.pow(ct.getB().get(i),e));
		return ret;
	}

	public Ciphertext multiply(Ciphertext ct1, Ciphertext ct2) {
		Ciphertext ret = new Ciphertext();
		for (int i=0; i < this.width; i++)
			ret.add(
					Gq.multiply(ct1.getA().get(i),ct2.getA().get(i)),
					Gq.multiply(ct1.getB().get(i),ct2.getB().get(i)));
		return ret;
	}

	public int getWidth() {
		return this.width;
	}


	
}
