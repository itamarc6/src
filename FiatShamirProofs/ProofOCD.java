package FiatShamirProofs;

import Utils.ByteTree;
import ArithmeticObjects.*;
import ArithmeticUtils.*;
import CryptoPrim.*;

/**
 * The ProofOCD class represents a non-interactive ZKP of
 * Fiat-Shamir's Proof of Decryption.
 * Verifies that at the end of the mixing the parties jointly
 * decrypt the re-encrypted and permuted list of ciphertexts.
 * 
 * @author Itamar Carmel
 */
public class ProofOCD extends Proof{
	
	// Fields which will be initiated at the constructor
	private byte[] seed;
	private BigNumber v;
	private Ciphertext A;
	private Element g;
	private BigNumber[] e;
	
	
	private Array<Element> y;
	private CiphertextGroup Cw;
	private PlaintextGroup Mw;
	private Array<Ciphertext> w;
	private Array<Array<Plaintext>> f;
	private int lambda;

	/**
	 * Constructs a Proof of Decryption by it's parameters.
	 * Computes field that will be needed in the verification.
	 * 
	 * @param iop - inputs of proof
	 */
	public ProofOCD(RandomOracle ROSeed, RandomOracle ROChallenge, byte[] rho,
			int N, int ne, int nr, int nv, PRG prg, Group Gq, Array<Element> y,
			CiphertextGroup Cw, PlaintextGroup Mw,
			Array<Ciphertext> w, Array<Array<Plaintext>> f, ByteTree tau,
			ByteTree sigma, int lambda) {
		super(ROSeed, ROChallenge, rho, N, ne, nr, nv, prg, Gq, sigma, tau);
		this.y = y;
		this.Cw = Cw;
		this.Mw = Mw;
		this.w = w;
		this.f = f;
		this.lambda = lambda;
		computeSeedChallengeAndA();
	}

	/**
	 * Computes fields that are mutual to all 'j' parameters.
	 */
	private void computeSeedChallengeAndA() {
		
		////////////
		// STEP 2 //
		////////////
		
		// Compute seed
		g = Gq.getGenerator();
        ByteTree leftNode = ByteTree.generateEmptyNode();
        leftNode.addMultChildren(g.getByteTreeRep(),w.getByteTreeRep());
        ByteTree rightNode = ByteTree.generateEmptyNode();
        rightNode.addMultChildren(y.getByteTreeRep(),f.getByteTreeRep());
        ByteTree bt = ByteTree.generateEmptyNode();
        bt.addMultChildren(leftNode, rightNode);
        seed = computeSeed(bt);
        
		////////////
		// STEP 3 //
		////////////
		
		// Compute A
		e = deriveRandomBigNumbers(seed);
		Ciphertext Api = Cw.pow(w.get(0),e[0]);
		for (int i=1; i<N; i++)
			Api = Cw.multiply(Api,Cw.pow(w.get(i),e[i]));
		A = new Ciphertext(Api.getA(),
				Plaintext.generateFixedPlaintext(Gq.getIdentity(),Cw.getWidth()).getArr());
		
		////////////
		// STEP 4 //
		////////////
		
		// Compute v (challenge)
		bt = ByteTree.generateEmptyNode();
		bt.addMultChildren(ByteTree.generateNewLeaf(seed),tau);
		v = computeChallenge(bt);
	}

	/**
	 * Verifies the Proof of Decryption for a given j.
	 * j represents the index of an array of decryption factors 'f'.
	 * @param j - index of proof to verify
	 * @return true - proof verified; false - else
	 */
	public boolean verifyProof(int j) {
		
		////////////
		// STEP 1 //
		////////////
		
		// Interpret Objects
		ByteTree tauJ = tau.getTheNChild(j);
		ByteTree sigmaJ = sigma.getTheNChild(j);
		BigNumber q = Gq.getGroupOrder();
		Field Zq = new Field(q);
		
		Element yjPrime = Gq.getElement(tauJ.getTheNChild(0));
		Plaintext BjPrime = Plaintext.getPlaintext(tauJ.getTheNChild(1));
		FieldElement kjX = Zq.getElement(sigmaJ);
        
		////////////
		// STEP 5 //
		////////////
		
		// Verification
		Element left1;
		Element right1;
		Plaintext left2;
		Plaintext right2;
		
		if (j == 0)
		{
			// Compute B
			Plaintext f0 = f.get(0).get(0);
			for (int l=1; l<lambda; l++){
				f0 = Mw.multiply(f0,f.get(l).get(0));
			}
			Plaintext B = Mw.pow(f0,e[0]);
			Plaintext fi;
			for(int i=1; i<N; i++){
				fi = f.get(0).get(i);
				for (int l=1; l<lambda; l++){
					fi = Mw.multiply(fi,f.get(l).get(i));
				}
				B = Mw.multiply(B,Mw.pow(fi,e[i]));
			}
			
			// Verify #1 equation
			// [(PI(y[l]))^v*PI(y'[l]) = g^SIGMA(kx[l])]
			Element leftA = y.get(0);
			for (int l=1; l<lambda; l++){
				leftA = Gq.multiply(leftA,y.get(l));
			}
			leftA = Gq.pow(leftA,v);
			Element leftB = Gq.getElement(tau.getTheNChild(0).getTheNChild(0));
			for (int l=1; l<lambda; l++){
				leftB = Gq.multiply(leftB,Gq.getElement(tau.getTheNChild(l).getTheNChild(0)));
			}
			left1 = Gq.multiply(leftA,leftB);
			
			FieldElement kXSum = Zq.getElement(sigma.getTheNChild(0));
			for (int l=1; l<lambda; l++){
				kXSum = Zq.add(kXSum,Zq.getElement(sigma.getTheNChild(1)));
			}
			right1 = Gq.pow(g,kXSum.getValue());
			if (!Gq.ElementIsEqual(left1,right1))
				return false;
			
			// Verify #2 equation
			// [B^v*PI(B'[l]) = PDec_SIGMA(kx[l])(A)]
			Plaintext leftPI = Plaintext.getPlaintext(tau.getTheNChild(0).getTheNChild(1));
			for (int l=1; l<lambda; l++){
				leftPI = Mw.multiply(leftPI,Plaintext.getPlaintext(tau.getTheNChild(l).getTheNChild(1)));
			}
			left2 = Mw.multiply(Mw.pow(B,v),leftPI);
			right2 = ProofUtils.PDec(new SecKey(kXSum),A,Mw,Zq);
			if (!Mw.plaintextIsEqual(left2,right2))
				return false;
		}
		else // j > 0
		{
			// Compute Bj
			Plaintext Bj = Mw.pow(f.get(j).get(0),e[0]);
			for(int i=1; i<N; i++)
				Bj = Mw.multiply(Bj,Mw.pow(f.get(j).get(i),e[i]));
			
			// Verify #1 equation [y[j]^v*y'[j] = g^(kx[j])]
			left1 = Gq.multiply(Gq.pow(y.get(j),v),yjPrime);
			right1 = Gq.pow(g,kjX.getValue());
			if (!Gq.ElementIsEqual(left1,right1))
				return false;
			
			// Verify #2 equation [B[j]^v*B'[j] = PDec_kx[j](A)]
			left2 = Mw.multiply(Mw.pow(Bj,v),BjPrime);
			right2 = ProofUtils.PDec(new SecKey(kjX),A,Mw,Zq);
			if (!Mw.plaintextIsEqual(left2,right2))
				return false;
		}
		
		// Case all above equations verified - Proof verified
		return true;
	}
}
