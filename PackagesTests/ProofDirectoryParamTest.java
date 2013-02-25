package PackagesTests;

import Verifier.ProofDirectoryParam;

public class ProofDirectoryParamTest {
	
	private static int lambda = 2;
	private static String directory = "C:\\TESTS\\test1\\default";

	public static void main(String[] args){
		
		ProofDirectoryParam pdp;
		try {
			pdp = new ProofDirectoryParam(directory );
		} catch (Exception e) {
			System.err.println("Loading proof directory failed.");
			return;
		}
		
		System.out.println("Version: " + pdp.getVersion());
		System.out.println("Type: " + pdp.getType());
		System.out.println("Auxsid: " + pdp.getAuxsid());
		System.out.println("Width: " + pdp.getWidth());
		System.out.println("Maxciph: " + pdp.getMaxciph());
		System.out.println("* LOADING BYTE TREE FILES *");
		System.out.println("FullPublicKey.bt: " + (pdp.getFullPublicKey() != null));
		System.out.println("Ciphertexts.bt: " + (pdp.getInputCiphertexts()!= null));
		System.out.println("Plaintexts.bt: " + (pdp.getPlaintexts() != null));
		System.out.println("ShuffledCiphertexts.bt: " + (pdp.getShuffledCiphertexts() != null));
		for (int l=0; l<lambda; l++){
			System.out.println((l+1) + "'th Mix-Server:");
			System.out.println("PublicKey"+(l+1)+".bt: " + (pdp.getPartialPublicKey(l) != null));
			System.out.println("SecretKey"+(l+1)+".bt: " + (pdp.getSecretKey(l) != null));
			System.out.println("Ciphertexts"+(l+1)+".bt: " + (pdp.getCiphertexts(l) != null));
			System.out.println("PermutationCommitment"+(l+1)+".bt: " + (pdp.getPermutationCommitment(l) != null));
			System.out.println("PoSCommitment"+(l+1)+".bt: " + (pdp.getPoSCommitment(l)!= null));
			System.out.println("PoSReply"+(l+1)+".bt: " + (pdp.getPoSReply(l) != null));
			System.out.println("PoSCCommitment"+(l+1)+".bt: " + (pdp.getPoSCCommitment(l) != null));
			System.out.println("PoSCReply"+(l+1)+".bt: " + (pdp.getPoSCReply(l) != null));
			System.out.println("KeepList"+(l+1)+".bt: " + (pdp.getKeepList(l) != null));
			System.out.println("CCPoSCommitment"+(l+1)+".bt: " + (pdp.getCCPoSCommitment(l) != null));
			System.out.println("CCPoSReply"+(l+1)+".bt: " + (pdp.getCCPoSReply(l) != null));
			System.out.println("DecryptionFactors"+(l+1)+".bt: " + (pdp.getDecryptionFactors(l) != null));
			System.out.println("DecrFactCommitment"+(l+1)+".bt: " + (pdp.getDecrFactCommitment(l) != null));
			System.out.println("DecrFactReply"+(l+1)+".bt: " + (pdp.getDecrFactReply(l) != null));
			
		}
		
		
	}
}
