package Verifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ArithmeticObjects.*;
import ArithmeticUtils.Array;
import CryptoPrim.*;
import FiatShamirProofs.ProofCCPOAS;
import FiatShamirProofs.ProofOAS;
import FiatShamirProofs.ProofOASOC;
import FiatShamirProofs.ProofOCD;
import FiatShamirProofs.ProofUtils;
import Utils.*;

public class Main {
	
	private static ProtocolInfo protInfo = null;
	private static ParsedVerifierInput verifierInput = null;
	private static ProofDirectoryParam proofDir = null;
	private static boolean vFlag = false;	
	
	private static String COMPAT_OPT = "-compat";
	private static int ERROR = -1;
	private static int SUCCESS = 0;
	private static BufferedWriter logWriter;
	
	public static int main(String[] args) {
		
		/////////////////////////////////
		// Step 1: Protocol parameters //
		/////////////////////////////////
		
		if (args[0].equals(COMPAT_OPT)){
			if (args.length > 1){
				printUsage();
				exit(ERROR);
			}
			printVersions();
			exit(SUCCESS);
		}
		
		try {
			verifierInput = new ParsedVerifierInput(args);
		} catch (Exception e1) {
			printUsage();
			exit(ERROR);
		}
		
		
		try {
			logWriter = new BufferedWriter(new FileWriter(
					new File(verifierInput.getDirectoryPath() + "log.txt"), true));
		} catch (IOException e1) {
			logAndError("Log file initiation failed. Program will not exit.");
		}
		vFlag = verifierInput.isV();
		log("*** VERIFIER INITIATED ***");
		log("** STEP 1: Protocol parameters **");
		
		protInfo = new ProtocolInfo(verifierInput.getProtInfoPath());
		if (protInfo == null){
			logAndError("Loading of protocol information file failed.");
			exit(ERROR);
		}
		log("Protocol parameters readed successfully.");

		//////////////////////////////
		// Step 2: Proof parameters //
		//////////////////////////////
		log("** STEP 2: Proof parameters **");
		
		try {
			proofDir = new ProofDirectoryParam(verifierInput.getDirectoryPath());
		} catch (Exception e1) {
			logAndError("Loading of proof directory failed.");
			exit(ERROR);
		}
		log("Proof parameters readed successfully.");
		if (!verifyProofParam()){
			logAndError("Verifying of proof parameters failed.");
			exit(ERROR);
		}
		log("Proof parameters verified.");
		
		///////////////////////////////////////
		// Step 3: Deriving sets and objects //
		///////////////////////////////////////
		log("** STEP 3: Deriving sets and objects **");
		
		Group Gq = null;
		Field Zq = null;
		PlaintextGroup Mw = null;
		CiphertextGroup Cw = null;
		RandomizerGroup Rw = null;
		HashFunction H = null;
		PRG prg = null;
		
		Gq = protInfo.getGroup();
		Zq = new Field(Gq.getGroupOrder());
		Mw = new PlaintextGroup(Gq,proofDir.getWidth());
		Cw = new CiphertextGroup(Gq,proofDir.getWidth());
		Rw = new RandomizerGroup(Zq,proofDir.getWidth());
		H = new HashFunction(protInfo.getHashfunction());
		prg = new PRG(new HashFunction(protInfo.getPRG()));
		
		if (
				(Gq == null) || (Zq == null) || (Mw == null) ||
				(Cw == null) || (Rw == null) || (H == null) ||
				(prg == null)){
			logAndError("Deriving and defining sets and objects failed.");
			exit(ERROR);
		}
		log("Sets and objects derived successfully.");
		
		//////////////////////////////////////////////
		// Step 4: Compute prefix to Random Oracles //
		//////////////////////////////////////////////
		log("** STEP 4: Compute prefix to Random Oracles **");
		
		ByteTree bt = ByteTree.generateEmptyNode();
		bt.addMultChildren(
				ByteTree.generateNewLeaf(proofDir.getVersion()),
				ByteTree.generateNewLeaf(protInfo.getSid() + "." + proofDir.getAuxsid()),
				ByteTree.generateNewLeaf(ByteBuffer.allocate(4).putInt(proofDir.getWidth()).array()),
				ByteTree.generateNewLeaf(ByteBuffer.allocate(4).putInt(protInfo.getNe()).array()),
				ByteTree.generateNewLeaf(ByteBuffer.allocate(4).putInt(protInfo.getNr()).array()),
				ByteTree.generateNewLeaf(ByteBuffer.allocate(4).putInt(protInfo.getNv()).array()),
				ByteTree.generateNewLeaf(protInfo.getGroupStr()),
				ByteTree.generateNewLeaf(protInfo.getPRG()),
				ByteTree.generateNewLeaf(protInfo.getHashfunction()));
		byte[] rho = H.produceDigest(bt.getByteRepOfTree());
		log("Prefix to Random Oracles computed successfully.");
		
		///////////////////////
		// Step 5: Read keys //
		///////////////////////
		log("** STEP 5: Read keys **");
		
		// 5.1. Joint public key
		log("* Read joint public key *");
		PubKey pk = proofDir.getFullPublicKey();
		if (pk == null){
			logAndError("Reading of public key from proof directory failed.");
			exit(ERROR);
		}
		log("Public key loaded successfully.");
		
		// 5.2. Public keys
		log("* Read public keys *");
		Array<Element> y = new Array<Element>();
		Element yTemp;
		for (int l = 0; l < protInfo.getLambda(); l++){
			yTemp = proofDir.getPartialPublicKey(l);
			if (yTemp == null){
				logAndError("Attempt to read the " + l + " partial public key failed.");
				exit(ERROR);
			}
			y.add(yTemp);
		}
		log(protInfo.getLambda() + " partial public keys read successfuly.");
		
		Element yPI = y.get(0);
		for (int l = 1; l < protInfo.getLambda(); l++)
			yPI = Gq.multiply(yPI,y.get(l));
		if (!Gq.ElementIsEqual(pk.getY(),yPI)){
			logAndError("Partial public keys are inconsistent.");
			exit(ERROR);
		}
		log("Public keys consistency verified.");
		
		// 5.3. Secret keys
		log("* Read secret keys *");
		Array<SecKey> x = new Array<SecKey>();
		SecKey sk = null;
		for (int l = 0; l < protInfo.getLambda(); l++){
			try{
				sk = proofDir.getSecretKey(l);
			} catch (Exception e){
				logAndError((l+1) + "'th secret key is not in the correct format.");
				exit(ERROR);
			}
			if (sk == null){
				x.add(null);
				continue;
			}
			if (!Gq.ElementIsEqual(y.get(l),Gq.pow(pk.getG(),sk.getX().getValue()))){
				logAndError((l+1) + "'th secret key is not consistent with his parallel public key.");
				exit(ERROR);
			}
			x.add(sk);
		}
		log(protInfo.getLambda() + " secret keys read and verified successfuly.");
		
		////////////////////////
		// Step 6: Read lists //
		////////////////////////
		log("** STEP 6: Read lists of ciphertexts and plaintexts **");
		
		// 6.a. Read input ciphertexts
		log("* Read input ciphertexts *");
		Array<Ciphertext> L0 = proofDir.getInputCiphertexts();
		if (L0 == null){
			logAndError("Loading input ciphertexts failed.");
			exit(ERROR);
		}
		log("Input ciphertexts loaded successfully.");
		int N = TypeConvertionUtils.bigNumberToInt(L0.getLength());
		log("N (number of ciphertexts) is set to be - " + N);
		
		// 6.b. Read shuffled ciphertexts
		Array<Ciphertext> Llambda = null;
		if (verifierInput.getType().equals("mixing")){
			log("* Read shuffled ciphertexts *");
			Llambda = proofDir.getCiphertexts(protInfo.getLambda());
		}
		else if (verifierInput.getType().equals("shuffling")){
			log("* Read shuffled ciphertexts *");
			Llambda = proofDir.getShuffledCiphertexts();
		}
		// 6.c. Read plaintexts
		Array<Plaintext> m = new Array<Plaintext>();
		if ((verifierInput.getType().equals("mixing")) ||
				(verifierInput.getType().equals("decryption"))){
			log("* Read plaintexts *");
			m = proofDir.getPlaintexts();
		}
		log("Shuffled ciphertexts / plaintexts loaded successfully.");	
		
		////////////////////////////////////////////
		// Step 7: Verify relations between lists //
		////////////////////////////////////////////
		
		log("** STEP 7: Verify relations between lists **");
		log("Initiating seed and challenge Random Oracles.");
		RandomOracle ROSeed = new RandomOracle(H,prg.getSeedLen());
		RandomOracle ROChallenge = new RandomOracle(H,protInfo.getNv());
		ByteTree tau;
		ByteTree sigma;
		
		// Compute h - derive random elements of Gq
		log("Deriving random elements in Gq.");
		byte[] seed = ProofUtils.computeSeed(ROSeed,rho,null);
		prg.setSeed(seed);
		Array<Element> h = Gq.randomArray(N,prg,protInfo.getNr());
		
		// 7.a. Verify shuffling
		if (
				((verifierInput.getType().equals("mixing")) ||
				(verifierInput.getType().equals("shuffling"))) &&
				(verifierInput.getPosc() || verifierInput.getCcpos())){
			log("* Verify shuffling *");
			log("Attempt to read 'maxciph' from proof directory.");
			int N0 = proofDir.getMaxciph();
			Array<Element> ul = null;
			Array<Ciphertext> Lpre = null;
			Array<Ciphertext> Lcur = null;
			
			// 7.a.I. In case 'maxciph' file does not exist
			if (N0 < 0){
				log("'maxciph' does not exists.");
				Lpre = L0;
				ProofOAS pOAS;
				
				log("Commencing verification of shuffling for each one of the " + protInfo.getLambda() + "'th mix-server.");
				for (int l = 0; l < protInfo.getLambda(); l++){
					// 7.a.I.1. Array of ciphertexts
					if (l < protInfo.getLambda() - 1){
						Lcur = proofDir.getCiphertexts(l);
						if (Lcur == null){
							logAndError("Attempt to read the " + l + "'th ciphertexts failed.");
							exit(ERROR);
						}
					} else
						Lcur = Llambda;
					
					// 7.a.I.2. Verify proof of shuffle
					tau = proofDir.getPoSCommitment(l);
					sigma = proofDir.getPoSReply(l);
					ul = proofDir.getPermutationCommitment(l);
					pOAS = new ProofOAS(
							ROSeed,ROChallenge,rho,N,protInfo.getNe(),
							protInfo.getNr(),protInfo.getNv(),prg,Gq,
							Rw,Cw,pk,Lpre,Lcur,ul,tau,sigma,h);
					if (
							(ul == null) || (tau == null) ||
							(sigma == null) || (!pOAS.verifyProof())){
						for (int i = 0; i < N; i++){
							if (!Cw.ciphertextIsEqual(Lcur.get(i),Lpre.get(i))){
								logAndError("Attempt to verify the " + l + "'th mix-server proof of shuffle failed.");
								exit(ERROR);
							}
						}
					}
					
					// 7.a.I.3. Accept proof
					
					Lpre = Lcur;
				}
				log("All " + protInfo.getLambda() + " mix-servers' shuffling verified.");
			}
			// 7.a.II. In case 'maxciph' file exists
			else{
				log("'maxciph' exists.");
				if (N0 < N){
					logAndError("Current amount of ciphers (N) is larger then the maximum(N0).");
					exit(ERROR);
				}
				Lpre = L0;
				ProofOASOC pOASOC;
				ProofCCPOAS pCCPOAS;
				ArrayOfBooleans tl;
				ArrayOfBooleans tDefault = ArrayOfBooleans.setDefaultArray(N0,N);
				Array<Element> uTemp;
				
				log("Commencing verification of shuffle of commitments and commitment-consistent proofs" +
						" for each one of the " + protInfo.getLambda() + "'th mix-server.");
				for (int l = 0; l < protInfo.getLambda(); l++){
					// 7.a.II.1. Verify proof of SOC
					if (verifierInput.getPosc()){
						ul = proofDir.getPermutationCommitment(l);
						tau = proofDir.getPoSCCommitment(l);
						sigma = proofDir.getPoSCReply(l);
						pOASOC = new ProofOASOC(
								ROSeed,ROChallenge,rho,N0,protInfo.getNe(),
								protInfo.getNr(),protInfo.getNv(),prg,Gq,
								ul,tau,sigma,h);
						if (
									(!pOASOC.verifyProof()) || (tau == null) ||
									(sigma == null) || (ul == null)){
								ul = h; 
						}	
					}
					
					// 7.a.II.2. Potential early abort
					if (!verifierInput.getCcpos()){
						continue;
					}
					
					// 7.a.II.3. Shrink permutation commitment
					tl = proofDir.getKeepList(l);
					if (tl == null){
						tl = tDefault;
					}
					uTemp = new Array<Element>();
					for (int i = 0; i < N; i++){
						if (tl.get(i))
							uTemp.add(ul.get(i));
					}
					ul = uTemp;
					
					// 7.a.II.4. Array of ciphertexts
					if (l < protInfo.getLambda() - 1){
						Lcur = proofDir.getCiphertexts(l);
						if (Lcur == null){
							logAndError("Reading of ciphertexts failed.");
							exit(ERROR);
						}
					} else
						Lcur = Llambda;
					
					// 7.a.II.5. Verify commitment-consistent proof of shuffle

					tau = proofDir.getCCPoSCommitment(l);
					sigma = proofDir.getCCPoSReply(l);
					pCCPOAS = new ProofCCPOAS(
							ROSeed,ROChallenge,rho,N,protInfo.getNe(),
							protInfo.getNr(),protInfo.getNv(),prg,Gq,
							ul,Rw,Cw,Lpre,Lcur,pk,tau,sigma,h);
					if (
							(sigma == null) || (tau == null) ||
							(!pCCPOAS.verifyProof())){
						for (int i = 0; i < N; i++){
							if (!Cw.ciphertextIsEqual(Lcur.get(i),Lpre.get(i))){
								logAndError("Attempt to verify the " + l + "'th mix-server " +
										"commitment-consistent proof of shuffle failed.");
								exit(ERROR);
							}
						}
					}
					// 7.a.II.6. Accept proof
					
					Lpre = Lcur;
				}
				log("All " + protInfo.getLambda() + " mix-servers' " +
						"shuffle of commitments and commitment-consistent " +
						"proof of shuffle verified.");
			}
			
		}

		// 7.b. Verify decryption
		Array<Ciphertext> L = new Array<Ciphertext>();
		
		if (verifierInput.getDec()){
			log("* Verify decryption *");
			if (verifierInput.getType().equals("mixing"))
				L = Llambda;
			else if (verifierInput.getType().equals("decryption"))
				L = L0;
			else{
				logAndError("Verifer inputs do not match none of " +
						"the proofs.");
				exit(ERROR);
			}
			
			// 7.b.1. Read proofs
			Array<Array<Plaintext>> f = new Array<Array<Plaintext>>();
			tau = ByteTree.generateEmptyNode();
			sigma = ByteTree.generateEmptyNode();
			Array<Plaintext> tempF;
			ByteTree tempTau;
			ByteTree tempSigma;
			for (int l = 0; l < protInfo.getLambda(); l++){
				tempF = proofDir.getDecryptionFactors(l);
				tempTau = proofDir.getDecrFactCommitment(l);
				tempSigma = proofDir.getDecrFactReply(l);
				if ((tempF == null) || (tempTau == null) || (tempSigma == null)){
					logAndError("Reading of " + (l+1) + "'th decryption parameters failed.");
					exit(ERROR);
				}
				f.add(tempF);
				tau.addChild(tempTau);
				sigma.addChild(tempSigma);
			}
			
			// 7.b.2. Verify combined proof
			ProofOCD pOCD = new ProofOCD(
					ROSeed,ROChallenge,rho,N,protInfo.getNe(),
					protInfo.getNr(),protInfo.getNv(),prg,Gq,
					y,Cw,Mw,L,f,tau,sigma,protInfo.getLambda());
			if (!pOCD.verifyProof(0)){
				// 7.b.3. Verify individual proofs
				Plaintext pdecL;
				for (int l = 0; l < protInfo.getLambda(); l++){
					
					if (!pOCD.verifyProof(l)){
						if (x.get(l) == null){
							logAndError("Decryption proof for " + l +
									"'th mix-server failed.");
							exit(ERROR);
						}
						for (int i = 0; i < N; i++){
							pdecL = ProofUtils.PDec(x.get(l),L.get(i),Mw,Zq);
							if (!Mw.plaintextIsEqual(f.get(l).get(i),pdecL)){
								logAndError("Decryption proof for " + l +
										"'th mix-server failed.");
								exit(ERROR);
							}
						}
					}
				}		
			}
			
			// 7.b.4. Verify plaintext
			Plaintext tdecL;
			Array<Plaintext> PIf = f.get(0);
			Plaintext tempPT;
			for (int i = 0; i < N; i++){
				tempPT = f.get(0).get(i);
				for (int l = 1; l < protInfo.getLambda(); l++){
					tempPT = Mw.multiply(tempPT,f.get(l).get(i));
				}
				PIf.add(tempPT);
			}
			
			for (int i = 0; i < N; i++){
				tdecL = ProofUtils.TDec(L.get(i),PIf.get(i),Mw);
				if (!Mw.plaintextIsEqual(m.get(i),tdecL)){
					logAndError("Plaintext " + i + " verafication failed.");
					exit(ERROR);
				}
			}
			
			// 7.b.5. Accept proof
			
			log("Verification of decryption was successfull.");
		}
		
		// 7.c. Accept Proof
		
		try {logWriter.close();}
		catch (IOException e) {}
		return SUCCESS;
	}

	private static void exit(int status) {
		log("*** VERIFIER TERMINATED - STATUS: " + status + " **");
		System.exit(status);
	}

	private static void logAndError(String msg) {
		System.err.println("ERROR - " + msg);
		log("ERROR - " + msg);
	}

	private static void log(String msg) {
		if (!vFlag) return;
		
		try {
			logWriter.write(getCurTime() + ":\t" + msg);
			logWriter.newLine();
		} catch (IOException e) {
			System.err.println();
			exit(ERROR);
		}
	}

	private static String getCurTime() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss:SSS");
	    return sdf.format(cal.getTime());
	}

	private static boolean verifyProofParam() {
		log("Verifying proof parameters.");
		if (!proofDir.getVersion().equals(protInfo.getVersion())){
			logAndError("Version verification failed.");
			return false;
		}
		if (!proofDir.getType().equals(verifierInput.getType())){
			logAndError("Type verification failed.");
			return false;
		}
		if (!proofDir.getAuxsid().equals(verifierInput.getAuxsid())){
			logAndError("Auxsid verification failed.");
			return false;
		}
		if ((verifierInput.getWidth() == -1) &&
				(proofDir.getWidth() != protInfo.getWidth())){
			logAndError("Width verification failed.");
			return false;
		}
		if ((verifierInput.getWidth() != -1) &&
				(proofDir.getWidth() != verifierInput.getWidth())){
			logAndError("Width verification failed.");
			return false;
		}
		return true;
	}

	private static void printUsage() {
		System.out.println("Correct verifier usage:");
		System.out.println("MIXING -\tverifier -mix <protInfo.xml_path> <directory_path>");
		System.out.println("\t[-auxsid <auxsid>] [-width <width>] [-nopos] [-noposc] [-noccpos] [-nodec]");
		System.out.println("SHUFFLE -\tverifier -shuffle <protInfo.xml_path> <directory_path>");
		System.out.println("\t[-auxsid <auxsid>] [-width <width>] [-nopos] [-noposc] [-noccpos]");
		System.out.println("DECRYPTION -\tverifier -decrypt <protInfo.xml_path> <directory_path>");
		System.out.println("\t[-auxsid <auxsid>] [-width <width>]");
	}

	private static void printVersions() {
		// line seperated list of versions
		
	}


}