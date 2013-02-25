package FiatShamirProofs;

import ArithmeticObjects.*;
import ArithmeticUtils.*;
import CryptoPrim.Ciphertext;
import CryptoPrim.PRG;
import CryptoPrim.Plaintext;
import CryptoPrim.PlaintextGroup;
import CryptoPrim.PubKey;
import CryptoPrim.RandomOracle;
import CryptoPrim.Randomizer;
import CryptoPrim.SecKey;
import Utils.ByteTree;

/**
 * The ProofUtils class supplies utilities (as static methods)
 * to all the proofs in Fiat-Shamir Proofs package.
 * 
 * @author Itamar Carmel
 */
public class ProofUtils {
	
	/**
	 * 
	 * @param m
	 * @param s
	 * @param pk
	 * @return
	 */
	public static Ciphertext Enc(Plaintext m, Randomizer s, PubKey pk, PlaintextGroup Mw) {
		Plaintext g = Plaintext.generateFixedPlaintext(pk.getG(),m.getWidth());
		g = Mw.pow(g,s);
		Plaintext y = Plaintext.generateFixedPlaintext(pk.getY(),m.getWidth());
		y = Mw.multiply(Mw.pow(y,s),m);
		return new Ciphertext(g.getArr(),y.getArr());
	}

	/**
	 * 
	 * @param sk
	 * @param ct
	 * @param Mw
	 * @param Zq
	 * @return
	 */
	public static Plaintext PDec(SecKey sk, Ciphertext ct, PlaintextGroup Mw, Field Zq) {
		Plaintext tmp = new Plaintext(ct.getA());
		return Mw.pow(tmp,Zq.addInverse(sk.getX()).getValue());
	}

	/**
	 * 
	 * @param ct
	 * @param pt
	 * @param Mw
	 * @return
	 */
	public static Plaintext TDec(Ciphertext ct, Plaintext pt, PlaintextGroup Mw) {
		Plaintext tmp = new Plaintext(ct.getA());
		return Mw.multiply(pt,tmp);
	}

	/**
	 * Derive random BigNumber array from a given vector.
	 * 
	 * @param prg - a seeded PRG
	 * @param lenInBits - length of each BigNumber
	 * @param N - size of array
	 * @param exp - if not null, calculate 'power' by exp
	 * @param mod - if not null, calculate 'mod' by mod
	 * @return array of random BigNumbers
	 */
	public static BigNumber[] deriveRandomBigNumbers(PRG prg, int lenInBits, int N, BigNumber exp, BigNumber mod){
		if (!prg.isSeeded())
			return null;
		int lenInBytes = (int) Math.ceil((double)lenInBits / 8);
		byte[] vector = prg.produceVector(lenInBytes * N);
		BigNumber[] ret = new BigNumber[N];
		byte[] byteArrTemp = new byte[lenInBytes];
		int curIndex = 0;
		for(BigNumber bn : ret){
			System.arraycopy(vector, curIndex, byteArrTemp, 0, lenInBytes);
			curIndex += lenInBytes;
			byteArrTemp[0] &=  (0xFF >> (8 - (lenInBits % 8))); // non-negative number
			bn = new BigNumber(byteArrTemp);
			bn = bn.modulo((BigNumber.TWO.pow(lenInBits)));
			if (exp != null){
				bn = bn.pow(exp);
				bn = bn.modulo(mod);
			}
		}
		return ret;
	}

	public static byte[] computeSeed(RandomOracle ROSeed, byte[] rho,
			ByteTree bt) {
		if (bt == null)
			bt = ByteTree.generateNewLeaf("generators");
		byte[] query = concatByteArrays(rho,bt.getByteRepOfTree());
		return ROSeed.produceMapping(query);
	}
	
	/**
	 * Concatenation of two byte arrays.
	 * 
	 * @param byteArr1
	 * @param byteArr2
	 * @return result = concat(byteArr1,byteArr2)
	 */
	public static byte[] concatByteArrays(byte[] byteArr1, byte[] byteArr2) {
		byte[] result = new byte[byteArr1.length + byteArr2.length];
		System.arraycopy(byteArr1, 0, result, 0, byteArr1.length);
		System.arraycopy(byteArr2, 0, result, byteArr1.length, byteArr2.length);
		return result;
	}
	
}
