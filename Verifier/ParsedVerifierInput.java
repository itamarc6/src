package Verifier;

import java.io.IOException;

/**
 * A class which parses the command line input of the verifier.
 * Checks validity of parameters and
 * contains getters to each one of them.
 * 
 * @author Itamar Carmel
 */
public class ParsedVerifierInput {
	
	private String protInfoPath = null; // path to protInfo.xml
	private String directoryPath = null; // path to directory
	private String type = null; // type of verification
	private String auxsid = null; // auxiliary session id
	private int width = -1; // width of ciphertexts and plaintexts
	private boolean posc = false; // proof of shuffle commitment
	private boolean ccpos = false; // commitment-consistent PoS
	private boolean dec = false; // correct decryption proof
	
	private boolean vFlag = false; // verbose - logging
	private IOException e = new IOException ("Failed parsing verifier input.");
	
	/**
	 * A constructor which gets a string array
	 * (command line arguments), parses them, checks validity
	 * and sets them to fields.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public ParsedVerifierInput(String[] args) throws Exception{
		if (args.length < 3){ // illegal format
			throw e;
		}
		this.protInfoPath = args[1];
		this.directoryPath = args[2];
		
		if (args[0].equals("-mix")){
			// default values
			this.type = "mixing";
			this.auxsid = "default";
			this.width = -1;
			this.posc = true;
			this.ccpos = true;
			this.dec = true;
			// optional values
			for (int i = 3; i < args.length; i++){
				if (args[i].equals("-auxsid")){
					if (!correctFormatAuxsid(args[i+1]))
						throw e;
					this.auxsid = args[++i];
				} else if (args[i].equals("-width")){
					this.width = Integer.parseInt(args[++i]);
				} else if (args[i].equals("-nopos")){
					this.posc = false;
					this.ccpos = false;
				} else if (args[i].equals("-noposc")){
					this.posc = false;
				} else if (args[i].equals("-noccpos")){
					this.ccpos = false;
				} else if (args[i].equals("-nodec")){
					this.dec = false;
				} else if (args[i].equals("-v")){
					this.vFlag = true;
				} else throw e;
			}
			
		} else if (args[0].equals("-shuffle")){
			// default values
			this.type = "shuffling";
			this.auxsid = "default";
			this.width = -1;
			this.posc = true;
			this.ccpos = true;
			this.dec = false;
			// optional values
			for (int i = 3; i < args.length; i++){
				if (args[i].equals("-auxsid")){
					if (!correctFormatAuxsid(args[i+1]))
						throw e;
					this.auxsid = args[++i];
				} else if (args[i].equals("-width")){
					this.width = Integer.parseInt(args[++i]);
				} else if (args[i].equals("-nopos")){
					this.posc = false;
					this.ccpos = false;
				} else if (args[i].equals("-noposc")){
					this.posc = false;
				} else if (args[i].equals("-noccpos")){
					this.ccpos = false;
				} else if (args[i].equals("-v")){
					this.vFlag = true;
				} else throw e;
			}

		} else if (args[0].equals("-decrypt")){
			// default values
			this.type = "decryption";
			this.auxsid = "default";
			this.width = -1;
			this.posc = false;
			this.ccpos = false;
			this.dec = true;
			// optional values
			for (int i = 3; i < args.length; i++){
				if (args[i].equals("-auxsid")){
					if (!correctFormatAuxsid(args[i+1]))
						throw e;
					this.auxsid = args[++i];
				} else if (args[i].equals("-width")){
					this.width = Integer.parseInt(args[++i]);
				} else if (args[i].equals("-v")){
					this.vFlag = true;
				} else throw e;
			}
		} else throw e; // invalid type
	}

	/**
	 * Checks validity of auxsid string argument.
	 * 
	 * @param auxsid
	 * @return true - correct form; false - otherwise
	 */
	private boolean correctFormatAuxsid(String auxsid) {
		for (char c : auxsid.toCharArray()){
			if (!(
					((c >= 48) && (c <= 57)) || // 0-9
					((c >= 65) && (c <= 90)) || // A-Z
					((c >= 97) && (c <= 122)) || // a-z
					(c == 95))) // underscore
				return false;
		}
		return true;
	}

	/**
	 * Getter for protInfo.xml path.
	 * @return
	 */
	public String getProtInfoPath() {
		return this.protInfoPath;
	}

	/**
	 * Getter for directory path.
	 * 
	 * @return
	 */
	public String getDirectoryPath() {
		return this.directoryPath;
	}

	/**
	 * Getter for type of verification.
	 * 
	 * @return
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Getter for auxiliary session id.
	 * 
	 * @return
	 */
	public String getAuxsid() {
		return this.auxsid;
	}

	/**
	 * Getter for width.
	 * 
	 * @return
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Getter for ccpos value.
	 * 
	 * @return
	 */
	public boolean getCcpos() {
		return this.ccpos;
	}

	/**
	 * Getter for posc value.
	 * 
	 * @return
	 */
	public boolean getPosc() {
		return this.posc;
	}

	/**
	 * Getter for dec value.
	 * 
	 * @return
	 */
	public boolean getDec() {
		return this.dec;
	}

	/**
	 * Getter for verbose value.
	 * 
	 * @return
	 */
	public boolean isV() {
		return this.vFlag;
	}

	/**
	 * A string representation of the parsed verifier inputs.
	 */
	public String toString(){
		String str = "PARSED VERIFIER INPUT";
		str += "\n--------------------------------";
		str += "\n(1) protInfo - \t\t" + this.protInfoPath;
		str += "\n(2) directory - \t" + this.directoryPath;
		str += "\n(3) type - \t\t" + this.type;
		str += "\n(4) auxsid - \t\t" + this.auxsid;
		str += "\n(5) width - \t\t" + this.width;
		str += "\n(6) posc - \t\t" + this.posc;
		str += "\n(7) ccpos - \t\t" + this.ccpos;
		str += "\n(8) dec - \t\t" + this.dec;
		str += "\n(9) verbose - \t\t" + this.vFlag;
		str += "\n--------------------------------";
		return str;
	}
}
