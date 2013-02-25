package CryptoPrim;

import java.security.*;

/**
 * The HashFunction class is a wrapping class represents a
 * cryptographic hash function of the SHA-2 family.
 * 
 * @author Itamar Carmel
 */
public class HashFunction {
	private MessageDigest 	md; 			// java.security.MessageDigest field
	private int 			outLenInBits; 	// outlen(H) in bits
	
	/**
	 * Constructs a hash function (H) according to the given algorithm.
	 * 
	 * @param algorithm - String which represents a SHA-2 hash algorithm (256,384,512).
	 * @throws NoSuchAlgorithmException - case given algorithm is not supported
	 */
	public HashFunction(String algorithm){
		try{
			this.md = MessageDigest.getInstance(algorithm);
			this.outLenInBits = md.getDigestLength() * 8;
			if ((this.outLenInBits != 256) && (this.outLenInBits != 384) && (this.outLenInBits != 512))
				throw new NoSuchAlgorithmException(); // algorithm not supported
		}
		catch (NoSuchAlgorithmException e){ // algorithm unknown
            System.err.println("ERROR: Hash Function algorithm " + algorithm + " is not supported or unknown.");
		}
	}
	
	/**
	 * Produces a message digest from this hash function. 
	 * 
	 * @param d - input to the hash function of byte array
	 * @return H(d)
	 */
	public byte[] produceDigest(byte[] d){
		this.md.reset(); // reset digest
		return this.md.digest(d); // calculate digest
	}
	
	/**
	 * Getter for output length of digest in bits.
	 * 
	 * @return outlen(H)
	 */
	public int getOutLen(){
		return this.outLenInBits;
	}
	
	/**
	 * @return H's string representation
	 */
	public String toString(){
		return "HashFunction(" + md.getAlgorithm() + ")";
	}
	
}
