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
 * The ProofOAS class represents a non-interactive ZKP of
 * Fiat-Shamir's Proof of a Shuffle.
 * Used by a mix-server to prove it re-encrypted and permuted its
 * input ciphertexts.
 * Has similar functionality to ProofOASOC, so it inherits from the last.
 * 
 * @author Itamar Carmel
 */
public class ProofOAS extends ProofOASOC{
	
	private Array<Element> u;
	private RandomizerGroup Rw;
	private CiphertextGroup Cw;
	private PubKey pk;
	private Array<Ciphertext> w;
	private Array<Ciphertext> wPrime;

	/**
	 * Constructs a Proof of a Shuffle by it's parameters.
	 * 
	 * @param iop - inputs of proof
	 */
	public ProofOAS(RandomOracle ROSeed, RandomOracle ROChallenge, byte[] rho,
			int N, int ne, int nr, int nv, PRG prg, Group Gq,
			RandomizerGroup Rw, CiphertextGroup Cw, PubKey pk,
			Array<Ciphertext> w, Array<Ciphertext> wPrime, Array<Element> u,
			ByteTree tau, ByteTree sigma, Array<Element> h) {
		super(ROSeed, ROChallenge, rho, N, ne, nr, nv, prg, Gq, u, tau, sigma, h);
		this.u = u;
		this.Rw = Rw;
		this.Cw = Cw;
		this.pk = pk;
		this.w = w;
		this.wPrime = wPrime;
		this.h = h;
	}

	/**
	 * Verifies the Proof of a Shuffle.
	 * 
	 * @return true - proof verified; false - else
	 */
	public boolean verifyProof(){
		
		////////////
		// STEP 1 //
		////////////
		
		// Interpret Objects
		PlaintextGroup Mw = new PlaintextGroup(Gq,Cw.getWidth());
		Ciphertext FPrime = Ciphertext.getCiphertext(tau.getTheNChild(5));
		Randomizer kF = Randomizer.getRandomizer(sigma.getTheNChild(5));
		
		////////////
		// STEP 2 //
		////////////
		
		// Compute seed
		Element g = Gq.getGenerator();
		ByteTree bt = ByteTree.generateEmptyNode();
		bt.addMultChildren(g.getByteTreeRep(),h.getByteTreeRep(),u.getByteTreeRep(),
				pk.getByteTreeRep(),w.getByteTreeRep(),wPrime.getByteTreeRep());
		seed = computeSeed(bt); // Sets seed field at the super class
		
		// Verify #1 - #4 equations
		
		// Check validity of #1 - #4 equations in super class - the same as this proof
		boolean verified = super.verifyProof();
		if (!verified)
			return false;
		
		////////////
		// STEP 3 //
		////////////
		
		// Compute F
		Ciphertext F = Cw.pow(w.get(0),e[0]);
		for (int i=1; i<N; i++)
			F = Cw.multiply(F,Cw.pow(w.get(i),e[i]));
		
		////////////
		// STEP 4 //
		////////////
		
		// Challenge will be computed on super's verification method
		
		////////////
		// STEP 5 //
		////////////
		
		// Verification
		Ciphertext left;
		Ciphertext right;
		
		// Verify #5 equation [F^v*F' = Enc_pk(1,-kf)*PI(w'[i]^ke[i])]
		left = Cw.multiply(Cw.pow(F,v),FPrime);
		Ciphertext wPrimePI = Cw.pow(wPrime.get(0),kE.get(0).getValue());
		for (int i=1; i<N; i++)
			wPrimePI = Cw.multiply(wPrimePI,Cw.pow(wPrime.get(i),kE.get(i).getValue()));
		Plaintext id = Plaintext.generateFixedPlaintext(Gq.getIdentity(),Cw.getWidth());
		Ciphertext enc =
			ProofUtils.Enc(id,Rw.addInverse(kF),pk,Mw);
		right = Cw.multiply(enc,wPrimePI);
		if (!Cw.ciphertextIsEqual(left,right))
			return false;
		
		// Case all above equations verified - Proof verified
		return true;
		
	}
}
