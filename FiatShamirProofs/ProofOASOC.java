package FiatShamirProofs;

import ArithmeticObjects.*;
import ArithmeticUtils.*;
import CryptoPrim.PRG;
import CryptoPrim.RandomOracle;
import Utils.*;

/**
 * The ProofOASOC class represents a non-interactive ZKP of
 * Fiat-Shamir's Proof of a Shuffle of Commitments.
 * Allows the mix-server to show in a pre-computation phase that
 * it knows how to open a commitment to a permutation.
 * Has similar functionality to ProofOASOC, so the last inherits from it.
 * 
 * @author Itamar Carmel
 */
public class ProofOASOC extends Proof{
	
	protected Array<Element> u; // permutation commitment
	
	// Fields common to ProofOASOC and ProofOAS
	protected BigNumber[] e; // requested at ProofOAS
	protected Array<FieldElement> kE; // requested at ProofOAS
	protected byte[] seed; // if instance of ProofOAS - computed at ProofOAS
	protected BigNumber v; // requested at ProofOAS
	protected Array<Element> h;
	
	/**
	 * Constructs a Proof of a Shuffle of Commitments by it's parameters.
	 * 
	 * @param iop - inputs of proof
	 */
	public ProofOASOC(RandomOracle ROSeed, RandomOracle ROChallenge,
			byte[] rho, int N0, int ne, int nr, int nv, PRG prg, Group Gq,
			Array<Element> u, ByteTree tau, ByteTree sigma, Array<Element> h) {
		super(ROSeed, ROChallenge, rho, N0, ne, nr, nv, prg, Gq, tau, sigma);
		this.u = u;
		this.h = h;
		this.e = null;
		this.kE = null;
		this.seed = null;
		this.v = null;
	}

	public boolean verifyProof(){

		////////////
		// STEP 1 //
		////////////
		
		// Interpret Objects
		Element g = Gq.getGenerator();
		BigNumber q = Gq.getGroupOrder();
		Field Zq = new Field(q);
		
		Element APrime = Gq.getElement(tau.getTheNChild(1));
		Element CPrime = Gq.getElement(tau.getTheNChild(3));
		Element DPrime = Gq.getElement(tau.getTheNChild(4));
		Array<Element> B = ArithmeticUtils.Conversions.BTToElementArray(tau.getTheNChild(0));
		Array<Element> BPrime = ArithmeticUtils.Conversions.BTToElementArray(tau.getTheNChild(2));
		FieldElement kA = Zq.getElement(sigma.getTheNChild(0));
		FieldElement kC = Zq.getElement(sigma.getTheNChild(2));
		FieldElement kD = Zq.getElement(sigma.getTheNChild(3));
		Array<FieldElement> kB = Zq.getElementsFromArr(sigma.getTheNChild(1));
		kE = Zq.getElementsFromArr(sigma.getTheNChild(4)); // Sets kE field

		////////////
		// STEP 2 //
		////////////
		
		ByteTree bt;
		
		// Compute seed
		if (seed == null){ // Case seed is not computed - not an instance of ProofOAS
			bt = ByteTree.generateEmptyNode();
			bt.addMultChildren(g.getByteTreeRep(),h.getByteTreeRep(),u.getByteTreeRep());
			seed = computeSeed(bt);
		}
		
		////////////
		// STEP 3 //
		////////////
		
		// Compute A
		e = deriveRandomBigNumbers(seed); // Sets e field
		Element A = Gq.pow(u.get(0),e[0]);
		for (int i=1; i<N; i++)
			A = Gq.multiply(A,Gq.pow(u.get(i),e[i]));
		
		////////////
		// STEP 4 //
		////////////
		
		// Compute v (challenge)
		bt = ByteTree.generateEmptyNode();
		bt.addMultChildren(ByteTree.generateNewLeaf(seed),tau);
		v = computeChallenge(bt); // Sets v field
		
		////////////
		// STEP 5 //
		////////////
		
		// Compute C
		Element CDividend = u.get(0);
		for (int i=1; i<N; i++)
			CDividend = Gq.multiply(CDividend,u.get(i));
		Element CDivisor = h.get(0);
		for (int i=1; i<N; i++)
			CDivisor = Gq.multiply(CDivisor,h.get(i));
		Element C = Gq.oppRightAdj(CDividend,CDivisor);
		
		// Compute D
		Element DDividend = B.get(N - 1);
		BigNumber DExp = e[0];
		for (int i=1; i<N; i++)
			DExp = DExp.multiply(e[i]);
		Element DDivisor = Gq.pow(h.get(0),DExp);
		Element D = Gq.oppRightAdj(DDividend,DDivisor);
		
		// Verification
		Element left;
		Element right;
		
		// Verify #1 equation [A^v*A' = g^ka*PI(h[i]^ke[i])]
		left = Gq.multiply(Gq.pow(A,v),APrime);
		Element hPI = Gq.pow(h.get(0),kE.get(0).getValue());
		for (int i=1; i<N; i++)
			hPI = Gq.multiply(hPI,Gq.pow(h.get(i),kE.get(i).getValue()));
		right = Gq.multiply(Gq.pow(g,kA.getValue()),hPI);
		if (!Gq.ElementIsEqual(left,right))
			return false;
		
        // Verify #2 equation [B[i]^v*B'[i] = g^kb[i]*B[i-1]^ke[i] for i = 0,..,N-1] 
		left = Gq.multiply(Gq.pow(B.get(0),v),BPrime.get(0));
		// Uses h[0] instead of B[-1]
		right = Gq.multiply(Gq.pow(g,kB.get(0).getValue()),Gq.pow(h.get(0),kE.get(0).getValue()));
		if (!Gq.ElementIsEqual(left,right))
			return false;
		for (int i=1; i<N; i++){
			left = Gq.multiply(Gq.pow(B.get(i),v),BPrime.get(i));
			right = Gq.multiply(Gq.pow(g,kB.get(i).getValue()),Gq.pow(B.get(i-1),kE.get(i).getValue()));
			if (!Gq.ElementIsEqual(left,right))
				return false;
		}
				
        // Verify #3 equation [C^v*C' = g^kc]
		left = Gq.multiply(Gq.pow(C,v),CPrime);
		right = Gq.pow(g,kC.getValue());
		if (!Gq.ElementIsEqual(left,right))
			return false;
		
        // Verify #4 equation [D^v*D' = g^kd]
		left = Gq.multiply(Gq.pow(D,v),DPrime);
		right = Gq.pow(g,kD.getValue());
		if (!Gq.ElementIsEqual(left,right))
			return false;
		
		// Case all above equations verified - Proof verified
		return true;
	}
}
