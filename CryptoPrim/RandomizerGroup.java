package CryptoPrim;

import ArithmeticObjects.Field;

public class RandomizerGroup {

	private Field Zq;
	private int width;
	
	public RandomizerGroup(Field Zq, int width) {
		this.Zq = Zq;
		this.width = width;
	}

	public Randomizer addInverse(Randomizer rm) {
		Randomizer ret = new Randomizer();
		for (int i=0; i < this.width; i++){
			ret.add(Zq.addInverse(rm.get(i)));
		}
		return ret;
	}

}
