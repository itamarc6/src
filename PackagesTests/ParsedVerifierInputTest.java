package PackagesTests;

import Verifier.ParsedVerifierInput;

public class ParsedVerifierInputTest {
	
	public static void main(String[] args){
		
		ParsedVerifierInput pvi;
		String[][] argsArr = new String[9][];
		
		// mixing + no decryption
		argsArr[0] =
				"-mix C:\\protInfo.xml C:\\directory -nodec".split("\\s+");
		// mixing + auxsid (error - no string attached)
		argsArr[1] =
				"-mix C:\\protInfo.xml C:\\directory -auxsid".split("\\s+");
		// mixing + auxsid + verbose
		argsArr[2] =
				"-mix C:\\protInfo.xml C:\\directory -auxsid AUXSID -v".split("\\s+");
		// shuffle + nopos
		argsArr[3] =
				"-shuffle C:\\protInfo.xml C:\\directory -nopos".split("\\s+");
		// shuffle + width (error - wrong int format)
		argsArr[4] =
				"-shuffle C:\\protInfo.xml C:\\directory -width 20t".split("\\s+");
		// shuffle + width + verbose
		argsArr[5] =
				"-shuffle C:\\protInfo.xml C:\\directory -width 20 -v".split("\\s+");
		// decrypt + auxsid (error - wrong auxsid format)
		argsArr[6] =
				"-decrypt C:\\protInfo.xml C:\\directory -auxsid AaB_6y_G$".split("\\s+");
		// decrypt + auxsid
		argsArr[7] =
				"-decrypt C:\\protInfo.xml C:\\directory -auxsid AaB_6y_G".split("\\s+");
		// decrypt + nodec (error - invalid option)
		argsArr[8] =
				"-decrypt C:\\protInfo.xml C:\\directory -nodec".split("\\s+");
		
		for (int i=0; i<argsArr.length; i++){
			try {
				pvi = new ParsedVerifierInput(argsArr[i]);
				System.out.println((i+1) + "'th input parsed successfully.");
				System.out.println(pvi);
				System.out.println();
			} catch (Exception e) {
				System.err.println("Parsing failed at the " + (i+1) + "'th input.");
			}
		}
	}

}
