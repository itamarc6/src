package CryptoPrim;

import java.nio.ByteBuffer;

/**
 * The PRG class represents a pseudo random generator
 * which expends short challenge string into long pseudo-random vector.
 * 
 * @author Itamar Carmel
 */
public class PRG {
	private HashFunction 	h;					// H
	private int 			seedLenInBits;		// seedlen(PRG)
	private int 			seedLenInBytes;		// seedlen(PRG) / 8
	private int 			i;					// counter for current hash input
	private byte[] 			seed;				// seed for PRG
	private byte[] 			hashOutput;
	private byte[] 			hashInput;
	private int 			curIndex;
	
	/**
	 * Constructs a PRG (PRG) from the given HashFunction.
	 * 
	 * @param h - hash function (H) for PRG 
	 */
	public PRG(HashFunction h){
		this.h = h;
		this.seedLenInBits = this.h.getOutLen();
		this.seedLenInBytes = this.seedLenInBits / 8;
		this.hashOutput = new byte[this.seedLenInBytes];
		this.hashInput = new byte[this.seedLenInBytes + 4];
		this.seed = null;
	}
	
	/**
	 * Sets a seed to this PRG. enables to derive more random vectors
	 * from the same seed on different sets of operations.
	 * 
	 * @param seed
	 */
	public void setSeed(byte[] seed){
		if (seed.length != this.seedLenInBytes){ // Case seed length do not fit
			System.err.println("ERROR: Seed length (in bits) should be: " + this.seedLenInBits);
			return;
		}
		this.seed = seed.clone();
		this.i = 0; // Resets counter
		this.curIndex = this.seedLenInBytes;
		System.arraycopy(this.seed, 0, this.hashInput, 0, this.seedLenInBytes);
	}
	
	/**
	 * Resets counter of PRG to zero by seeding PRG with the same seed.
	 */
	public void resetCounter() {
		setSeed(this.seed);
	}
	
	/**
	 * Produces a pseudo-random vector according to a 
	 * given length in bytes.
	 * 
	 * @param vecLenInBytes - length of wanted pseudo-random vector
	 * @return vector of pseudo-random bytes
	 */
	public byte[] produceVector(int vecLenInBytes){
		if (this.seed == null){ // Case PRG not seeded
			System.err.println("ERROR: PRG is not seeded yet.");
			return null;
		}
		byte[] vector = new byte[vecLenInBytes];
		int bytesLeft = vecLenInBytes;
		int curBytes;
		
		while (bytesLeft > 0){
			if (this.curIndex == this.seedLenInBytes){ // Case latest hash digest is at the end
				updateHashValue();
			}
			curBytes = Math.min(bytesLeft, this.seedLenInBytes - this.curIndex); // Bytes we can copy on this loop
			System.arraycopy(this.hashOutput, this.curIndex, vector, vecLenInBytes - bytesLeft, curBytes);
			bytesLeft -= curBytes;
			this.curIndex += curBytes;
		}
		
		return vector;
	}
	
	/**
	 * Updates input and output of H according to
	 * current index (i) and index of output array.
	 */
	private void updateHashValue(){
		byte[] counterByteArr = ByteBuffer.allocate(4).putInt(this.i).array();
		System.arraycopy(counterByteArr, 0, this.hashInput, this.seedLenInBytes, 4);
		this.hashOutput = this.h.produceDigest(hashInput);
		if (this.i == Integer.MAX_VALUE) // Resets 'i' - unlikely
			this.i = 0;
		else
			this.i++; // Increment 'i' for the next hash calculation
		this.curIndex = 0;
		
	}

	/**
	 * Getter for the length of seed (in bits) needed for PRG.
	 * 
	 * @return seedlen(PRG)
	 */
	public int getSeedLen(){
		return seedLenInBits;
	}
	
	/**
	 * @return PRG's string representation.
	 */
	public String toString(){
		return "PRG(" + this.h + ")";
	}

	/**
	 * Query of PRG's seed status.
	 * 
	 * @return true - seeded; false - not seeded.
	 */
	public boolean isSeeded() {
		return (this.seed == null);
	}
}
