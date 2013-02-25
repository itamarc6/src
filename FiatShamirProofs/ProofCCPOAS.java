package FiatShamirProofs;

import Utils.ByteTree;
import ArithmeticObjects.*;
import ArithmeticUtils.*;
import CryptoPrim.Ciphertext;
import CryptoPrim.CiphertextGroup;
import CryptoPrim.PRG;
import CryptoPrim.Plaintext;
import CryptoPrim.PlaintextGroup;
import CryptoPrim.PubKey;
import CryptoPrim.RandomOracle;
import CryptoPrim.Randomizer;
import CryptoPrim.RandomizerGroup;

/**
 * The ProofCCPOAS class represents a non-interactive ZKP of
 * Fiat-Shamir's Commitment-Consistent Proof of a Shuffle.
 * Used to verify the proof of a shuffle for the given permutation
 * commitment.
 * 
 * @author Itamar Carmel
 */
public class ProofCCPOAS extends Proof{
	
	private RandomizerGroup Rw;
	private CiphertextGroup Cw;
	private PubKey pk;
	private Array<Ciphertext> w;
	private Array<Ciphertext> wPrime;
	private Array<Element> h;
	private Array<Element> u;
	
	/**
	 * Constructs a Commitment-Consistent Proof of a Shuffle by it's parameters.
	 * @param h 
	 * 
	 * @param iop - inputs of proof
	 */
	public ProofCCPOAS(RandomOracle ROSeed, RandomOracle ROChallenge,
			byte[] rho, int N, int ne, int nr, int nv, PRG prg, Group Gq,
			Array<Element> u, RandomizerGroup Rw, CiphertextGroup Cw,
			Array<Ciphertext> w, Array<Ciphertext> wPrime, PubKey pk,
			ByteTree tau, ByteTree sigma, Array<Element> h) {
		super(ROSeed, ROChallenge, rho, N, ne, nr, nv, prg, Gq, sigma, tau);
		this.Rw = Rw;
		this.Cw = Cw;
		this.pk = pk;
		this.w = w;
		this.wPrime = wPrime;
		this.h = h;
		this.u = u;
	}

	/**
	 * Verifies the Commitment-Consistent Proof of a Shuffle.
	 * 
	 * @return true - proof verified; false - else
	 */
	public boolean verifyProof() {

		////////////
		// STEP 1 //
		////////////
		
		// Interpret Objects
		Element g = Gq.getGenerator();
		BigNumber q = Gq.getGroupOrder();
		Field Zq = new Field(q);
		PlaintextGroup Mw = new PlaintextGroup(Gq,Cw.getWidth());
		
		Element APrime = Gq.getElement(tau.getTheNChild(0));
		Ciphertext BPrime = Ciphertext.getCiphertext(tau.getTheNChild(1));
		FieldElement kA = Zq.getElement(sigma.getTheNChild(0));
		Randomizer kB = Randomizer.getRandomizer(sigma.getTheNChild(1));
		Array<FieldElement> kE = Zq.getElementsFromArr(sigma.getTheNChild(2));
		
		////////////
		// STEP 2 //
		////////////
		
		// Compute seed
		ByteTree bt = ByteTree.generateEmptyNode();
		bt.addMultChildren(g.getByteTreeRep(),h.getByteTreeRep(),u.getByteTreeRep(),
				pk.getByteTreeRep(),w.getByteTreeRep(),wPrime.getByteTreeRep());
		byte[] seed = computeSeed(bt);
		
		////////////
		// STEP 3 //
		////////////
		
		// Compute A
		BigNumber[] e = deriveRandomBigNumbers(seed);
		Element A = Gq.pow(u.get(0),e[0]);
		for (int i=1; i<N; i++)
			A = Gq.multiply(A,Gq.pow(u.get(i),e[i]));
		
		////////////
		// STEP 4 //
		////////////
		
		// Compute v (challenge)
		bt = ByteTree.generateEmptyNode();
		bt.addMultChildren(ByteTree.generateNewLeaf(seed),tau);
		BigNumber v = computeChallenge(bt);
		
		////////////
		// STEP 5 //
		////////////
		
		// Compute B
		Ciphertext B = Cw.pow(w.get(0),e[0]);
		for (int i=1; i<N; i++)
			B = Cw.multiply(B,Cw.pow(w.get(i),e[i]));
		
		// Verification
		Element left1;
		Element right1;
		Ciphertext left2;
		Ciphertext right2;
		
		// Verify #1 equation [A^v*A' = g^ka*PI(h[i]^ke[i])]
		left1 = Gq.multiply(Gq.pow(A,v),APrime);
		Element hPI = Gq.pow(h.get(0),kE.get(0).getValue());
		for (int i=1; i<N; i++)
			hPI = Gq.multiply(hPI,Gq.pow(h.get(i),kE.get(i).getValue()));
		right1 = Gq.multiply(Gq.pow(g,kA.getValue()),hPI);
		if (!Gq.ElementIsEqual(left1,right1))
			return false;
		
		// Verify #2 equation [B^v*B' = Enc_pk(1,-kb)*PI(w'[i]^ke[i])]
		left2 = Cw.multiply(Cw.pow(B,v),BPrime);
		Ciphertext wPrimePI = Cw.pow(wPrime.get(0),kE.get(0).getValue());
		for (int i=1; i<N; i++)
			wPrimePI = Cw.multiply(wPrimePI,Cw.pow(wPrime.get(i),kE.get(i).getValue()));
		Plaintext id = Plaintext.generateFixedPlaintext(Gq.getIdentity(),Cw.getWidth());
		Ciphertext enc = ProofUtils.Enc(id,Rw.addInverse(kB),pk,Mw);
		right2 = Cw.multiply(enc,wPrimePI);
		if (!Cw.ciphertextIsEqual(left2,right2))
			return false;

		// Case all above equations verified - Proof verified
		return true;
	}
}
