package FiatShamirProofs;

import Utils.*;
import ArithmeticObjects.Group;
import ArithmeticUtils.*;
import CryptoPrim.PRG;
import CryptoPrim.RandomOracle;

/**
 * The Proof class represents a non-interactive ZKP of
 * Fiat-Shamir's proofs. Holds some of the shared functionalities
 * of all FS proofs, which will be inherited by them.
 * 
 * @author Itamar Carmel
 */
public class Proof {
	
	protected RandomOracle ROSeed; // generates seeds
	protected RandomOracle ROChallenge; // generates challenges
	protected byte[] rho; // prefix to random oracles
	protected int N; // size of the arrays
	protected int ne; // length of batching vectors in bits
	protected int nr; // acceptable "statistical error"
	protected int nv; // challenge length in bits
	protected PRG prg; // PRG used to derive batching vectors
	protected Group Gq; // Group of prime order with standard generator
	protected ByteTree tau; // commitment of the Fiat-Shamir proof
	protected ByteTree sigma; // reply to the Fiat-Shamir proof
	
	/**
	 * Constructs a proof by it's parameters.
	 * 
	 * @param iop - inputs of proof
	 */
	public Proof(
			RandomOracle ROSeed, RandomOracle ROChallenge, byte[] rho,
			int N, int ne, int nr, int nv, PRG prg, Group Gq,
			ByteTree tau, ByteTree sigma){
		this.ROSeed = ROSeed;
		this.ROChallenge = ROChallenge;
		this.rho = rho;
		this.N = N;
		this.ne = ne;
		this.nr = nr;
		this.nv = nv;
		this.prg = prg;
		this.Gq = Gq;
	}
	
	/**
	 * Computes a seed by concatenating rho and a given ByteTree
	 * and generating a mapping from the seed's RO.
	 * 
	 * @param bt
	 * @return seed
	 */
	protected byte[] computeSeed(ByteTree bt){
		return ProofUtils.computeSeed(this.ROSeed,this.rho,bt);
	}
	
	/**
	 * Computes a challenge by concatenating rho and a given ByteTree
	 * and generating a mapping from the challenge's RO.
	 * the challenge will be converted to BigNumber representation
	 * and will be returned.
	 * 
	 * @param BTNode
	 * @return v
	 */
	protected BigNumber computeChallenge(ByteTree BTNode){
		byte[] query = ProofUtils.concatByteArrays(this.rho,BTNode.getByteRepOfTree());
		byte[] challenge = this.ROChallenge.produceMapping(query);
		BigNumber v = new BigNumber(challenge); // Construct BigNumber from challenge
		v = v.modulo((BigNumber.TWO.pow(this.nv))); // Adapt BigNumber to boundaries 
		return v;
	}
	
	/**
	 * Derive random BigNumber array from a given seed.
	 * Calls a static method with appropriate parameters.
	 * 
	 * @param seed
	 * @return array of random BigNumbers
	 */
	protected BigNumber[] deriveRandomBigNumbers(byte[] seed){
		this.prg.setSeed(seed);
		return ProofUtils.deriveRandomBigNumbers(this.prg,this.ne,this.N,null,null);
	}
	

}
