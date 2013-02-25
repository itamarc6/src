package Verifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import ArithmeticObjects.*;
import ArithmeticUtils.*;
import CryptoPrim.Ciphertext;
import CryptoPrim.Plaintext;
import CryptoPrim.PubKey;
import CryptoPrim.SecKey;
import Utils.ByteTree;
import Utils.TypeConvertionUtils;

/**
 * A class which gets a path to the main directory
 * that holds a 'proofs' directory, which will
 * become handy in the verification of the non-interactive
 * proofs segment.
 * 
 * @author Itamar Carmel
 */
public class ProofDirectoryParam {
	
	private String mainDirectoryPath; // a path to the main directory
	private String proofsDirectoryPath; // a path to the proofs directory (main + "\\proofs")

	/**
	 * A constructor which gets a path to main directory
	 * and verifies that they are indeed directories, and
	 * not files. The constructor sets both paths as fields
	 * for further use.
	 * 
	 * @param directoryPath
	 * @throws IOException
	 */
	public ProofDirectoryParam(String directoryPath) throws IOException{
		this.mainDirectoryPath = directoryPath;
		File dir = new File(this.mainDirectoryPath);
		if (!dir.isDirectory())
			throw new IOException();
		this.proofsDirectoryPath = this.mainDirectoryPath + "\\proofs";
		dir = new File(this.proofsDirectoryPath);
		if (!dir.isDirectory())
			throw new IOException();
	}

	/**
	 * Getter for a string representation of verificatum's version.
	 * Abstract notation - version; Point - 1.
	 * 
	 * @return version
	 */
	public String getVersion() {
		return asciiFileToString(this.mainDirectoryPath + "\\version");
	}

	/**
	 * Getter for a string representation of verification's type.
	 * Abstract notation - type; Point - 2.
	 * 
	 * @return type
	 */
	public String getType() {
		return asciiFileToString(this.mainDirectoryPath + "\\type");
	}
	
	/**
	 * Getter for a string representation of verification's auxsid.
	 * Abstract notation - auxsid; Point - 3.
	 * 
	 * @return auxsid
	 */
	public String getAuxsid() {
		return asciiFileToString(this.mainDirectoryPath + "\\auxsid");
	}

	/**
	 * Getter for an int representation of verification's
	 * ciphertexts and plaintexts width.
	 * Abstract notation - w; Point - 4.
	 * 
	 * @return w
	 */
	public int getWidth() {
		String w = asciiFileToString(this.mainDirectoryPath + "\\width");
		if (w == null)
			return -1;
		return Integer.parseInt(w);
	}

	/**
	 * Getter for a PubKey representation of the joint public key.
	 * Abstract notation - pk; Point - 5.
	 * 
	 * @return pk
	 */
	public PubKey getFullPublicKey() {
		ByteTree bt = ByteTree.loadFromFile(this.mainDirectoryPath + "\\FullPublicKey");
		if (bt == null) return null;
		return PubKey.getPubKey(bt);
	}

	/**
	 * Getter for an array of input ciphertexts.
	 * Abstract notation - L0; Point - 6.
	 * 
	 * @return L0
	 */
	public Array<Ciphertext> getInputCiphertexts() {
		ByteTree bt = ByteTree.loadFromFile(this.mainDirectoryPath + "\\Ciphertexts");
		if (bt == null) return null;
		return Ciphertext.getCiphertextArray(bt);
	}

	/**
	 * Getter for an array of output plaintexts.
	 * Abstract notation - m; Point - 7a.
	 * 
	 * @return m
	 */
	public Array<Plaintext> getPlaintexts() {
		ByteTree bt = ByteTree.loadFromFile(this.mainDirectoryPath + "\\Plaintexts");
		if (bt == null) return null;
		return Plaintext.getPlaintextArray(bt);
	}

	/**
	 * Getter for an array of shuffled ciphertexts.
	 * Abstract notation - Llambda; Point - 7b.
	 * 
	 * @return Llambda
	 */
	public Array<Ciphertext> getShuffledCiphertexts() {
		ByteTree bt = ByteTree.loadFromFile(this.mainDirectoryPath + "\\ShuffledCiphertexts");
		if (bt == null) return null;
		return Ciphertext.getCiphertextArray(bt);
	}

	/**
	 * Getter for a partial public key,
	 * according to an index which represents a mix-server.
	 * Abstract notation - yl; Point - 8.
	 * 
	 * @return yl
	 */
	public Element getPartialPublicKey(int l) {
		ByteTree bt = ByteTree.loadFromFile(this.proofsDirectoryPath + "\\PublicKey",++l);
		if (bt == null) return null;
		return ArithmeticUtils.Conversions.BTToElement(bt);
	}

	/**
	 * Getter for a SecKey,
	 * according to an index which represents a mix-server.
	 * Abstract notation - xl; Point - 9.
	 * 
	 * @return xl
	 */
	public SecKey getSecretKey(int l) {
		ByteTree bt = ByteTree.loadFromFile(this.proofsDirectoryPath + "\\SecretKey",++l);
		if (bt == null) return null;
		return SecKey.getSecKey(bt);
	}

	/**
	 * Getter for an array of ciphertexts,
	 * according to an index which represents a mix-server.
	 * Abstract notation - Ll; Point - 10.
	 * 
	 * @return Ll
	 */
	public Array<Ciphertext> getCiphertexts(int l) {
		ByteTree bt = ByteTree.loadFromFile(this.proofsDirectoryPath + "\\Ciphertexts",++l);
		if (bt == null) return null;
		return Ciphertext.getCiphertextArray(bt);
	}

	/**
	 * Getter for an array of elements (Pedersen commitment),
	 * according to an index which represents a mix-server.
	 * Abstract notation - ul; Point - 11.
	 * 
	 * @return ul
	 */
	public Array<Element> getPermutationCommitment(int l) {
		ByteTree bt = ByteTree.loadFromFile(this.proofsDirectoryPath + "\\PermutationCommitment",++l);
		if (bt == null) return null;
		return ArithmeticUtils.Conversions.BTToElementArray(bt);
	}

	/**
	 * Getter for a ByteTree representation of PoS commitment,
	 * according to an index which represents a mix-server.
	 * Abstract notation - tauPoS[l]; Point - 12.
	 * 
	 * @return tauPoS[l]
	 */
	public ByteTree getPoSCommitment(int l) {
		return ByteTree.loadFromFile(this.proofsDirectoryPath + "\\PoSCommitment",++l);
	}

	/**
	 * Getter for a ByteTree representation of PoS reply,
	 * according to an index which represents a mix-server.
	 * Abstract notation - sigmaPoS[l]; Point - 13.
	 * 
	 * @return sigmaPoS[l]
	 */
	public ByteTree getPoSReply(int l) {
		return ByteTree.loadFromFile(this.proofsDirectoryPath + "\\PoSReply",++l);
	}

	/**
	 * Getter for an int representation of maxciph
	 * (max chiphers).
	 * Abstract notation - maxciph; Point - 14.
	 * 
	 * @return maxciph
	 */
	public int getMaxciph() {
		String w = asciiFileToString(this.proofsDirectoryPath + "\\maxciph");
		if (w == null)
			return -1;
		return Integer.parseInt(w);
	}

	/**
	 * Getter for a ByteTree representation of PoSC commitment,
	 * according to an index which represents a mix-server.
	 * Abstract notation - tauPoSC[l]; Point - 15.
	 * 
	 * @return tauPoSC[l]
	 */
	public ByteTree getPoSCCommitment(int l) {
		return ByteTree.loadFromFile(this.proofsDirectoryPath + "\\PoSCCommitment" ,++l);
	}

	/**
	 * Getter for a ByteTree representation of PoSC reply,
	 * according to an index which represents a mix-server.
	 * Abstract notation - sigmaPoSC[l]; Point - 16.
	 * 
	 * @return sigmaPoSC[l]
	 */
	public ByteTree getPoSCReply(int l) {
		return ByteTree.loadFromFile(this.proofsDirectoryPath + "\\PoSCReply",++l);
	}

	/**
	 * Getter for an array of booleans (keep list),
	 * according to an index which represents a mix-server.
	 * Abstract notation - tl; Point - 17.
	 * 
	 * @return tl
	 */
	public ArrayOfBooleans getKeepList(int l) {
		ByteTree bt = ByteTree.loadFromFile(this.proofsDirectoryPath + "\\KeepList",++l);
		if (bt == null) return null;
		return ArithmeticUtils.Conversions.BTToArrayOfBooleans(bt);
	}

	/**
	 * Getter for a ByteTree representation of CCPoS commitment,
	 * according to an index which represents a mix-server.
	 * Abstract notation - tauCCPoS[l]; Point - 18.
	 * 
	 * @return tauCCPoS[l]
	 */
	public ByteTree getCCPoSCommitment(int l) {
		return ByteTree.loadFromFile(this.proofsDirectoryPath + "\\CCPoSCommitment",++l);
	}

	/**
	 * Getter for a ByteTree representation of CCPoS reply,
	 * according to an index which represents a mix-server.
	 * Abstract notation - sigmaCCPoS[l]; Point - 19.
	 * 
	 * @return sigmaCCPoS[l]
	 */
	public ByteTree getCCPoSReply(int l) {
		return ByteTree.loadFromFile(this.proofsDirectoryPath + "\\CCPoSReply",++l);
	}

	/**
	 * Getter for an array of plaintexts (decryption factors),
	 * according to an index which represents a mix-server.
	 * Abstract notation - fl; Point - 20.
	 * 
	 * @return fl
	 */
	public Array<Plaintext> getDecryptionFactors(int l) {
		ByteTree bt = ByteTree.loadFromFile(this.proofsDirectoryPath + "\\DecryptionFactors",++l);
		if (bt == null) return null;
		return Plaintext.getPlaintextArray(bt);
	}

	/**
	 * Getter for a ByteTree representation of DecrFact commitment,
	 * according to an index which represents a mix-server.
	 * Abstract notation - tauDec[l]; Point - 21.
	 * 
	 * @return tauDec[l]
	 */
	public ByteTree getDecrFactCommitment(int l) {
		return ByteTree.loadFromFile(this.proofsDirectoryPath + "\\DecrFactCommitment",++l);
	}

	/**
	 * Getter for a ByteTree representation of DecrFact reply,
	 * according to an index which represents a mix-server.
	 * Abstract notation - sigmaDec[l]; Point - 22.
	 * 
	 * @return sigmaDec[l]
	 */
	public ByteTree getDecrFactReply(int l) {
		return ByteTree.loadFromFile(this.proofsDirectoryPath + "\\DecrFactReply",++l);
	}
	
	/**
	 * A private method which returns the string which holds
	 * the file, according to the given path to it.
	 * 
	 * @param path (to a string file)
	 * @return the string within the file
	 */
	private String asciiFileToString(String path) {
		try{
			FileInputStream fis = new FileInputStream(path);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			String str = TypeConvertionUtils.byteArrToString(b);
			fis.close();
			return str;
		} catch (Exception e){
			return null;
		}
	}
	
}
