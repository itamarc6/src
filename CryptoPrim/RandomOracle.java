package CryptoPrim;

import java.nio.ByteBuffer;

/**
 * The RandomOracle class represents a random oracle which allows
 * us to derive any number of random bits. will be in use on the
 * verification of Fiat-Shamir proofs.
 * 
 * @author Itamar Carmel
 */
public class RandomOracle {
	private HashFunction 	h;				// H
	private PRG 			prg;			// PRG
	private int 			outLenInBits;	// n_out
	
	/**
	 * Constructs a RandomOracle (RO) from the given HashFunction
	 * and the output length (n_out).
	 * 
	 * @param h - H
	 * @param outLenInBits - n_out
	 */
	public RandomOracle(HashFunction h, int outLenInBits){
		this.h = h;
		this.outLenInBits = outLenInBits;
		this.prg = new PRG(this.h); // Constructs PRG(H)
	}
	
	/**
	 * Produces pseudo-random bytes in response to a given query.
	 * 
	 * @param query - d
	 * @return response to the query at length of n_out
	 */
	public byte[] produceMapping(byte[] query){
		int queryLen = query.length;
		byte[] hashInput = new byte[queryLen + 4];
		byte[] outLenByteArr = ByteBuffer.allocate(4).putInt(this.outLenInBits).array();
		System.arraycopy(outLenByteArr, 0, hashInput, 0, 4);
		System.arraycopy(query, 0, hashInput, 4, queryLen);
		byte[] seed = this.h.produceDigest(hashInput); // Compute 's'
		this.prg.setSeed(seed);
		byte[] result = this.prg.produceVector((this.outLenInBits + 7) / 8); // Compute 'a'
		if ((this.outLenInBits % 8) != 0){ // Case [n_out mod 8 != 0]
			int prefixLenInBits = 8 - (this.outLenInBits % 8);
			int prefix = 0xFF;
			prefix >>= prefixLenInBits;
			result[0] = (byte) (prefix & result[0]); // Set first bits of 'a' to zero
		}
		return result;
	}

	/**
	 * @return RO's string representation
	 */
	public String toString(){
		return "RandomOracle(" + this.h + "," + this.outLenInBits + ")";
	}

}
