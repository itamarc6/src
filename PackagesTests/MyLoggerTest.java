package PackagesTests;

import java.io.IOException;

import Verifier.MyLogger;

public class MyLoggerTest {
	
	private static String path = "C:\\TESTS\\OTHER";

	public static void main(String[] args) throws IOException{
		
		MyLogger myLogger;
		myLogger = new MyLogger(path ,true);

		myLogger.write("A simple massage.");
		myLogger.write("And another one.");
		myLogger.setLevel(2);
		myLogger.writePlusTime("Somebody got the time please?");
		myLogger.resetLevel();
		myLogger.writePlusTime("Ohh here it is!!");
		myLogger.writePlusTime("Let's build a pyramid!!");
		for (int i=0; i<5; i++){
			myLogger.incrementLevel();
			for (int j=0; j<2; j++)
				myLogger.writePlusTime("BRICK");
		}
		for (int i=0; i<4; i++){
			myLogger.decrementLevel();
			for (int j=0; j<2; j++)
				myLogger.writePlusTime("BRICK");
		}
		
		myLogger.writeHeader("How about a header right here??");
		myLogger.write("Sounds good to me, bro.");
		myLogger.writeError("Wow what was that??");
		myLogger.write("Going away now. see ya!");
		myLogger.close();

		myLogger = new MyLogger(path ,false);
		
		myLogger.write("Can't see this...");
		myLogger.write("And this..");
		myLogger.writeHeader("Maybe if i write it like this??");
		myLogger.writePlusTime("TIMEEEE PLEASEEEE");
		myLogger.writeError("F*CK IT!!");
		myLogger.close();
		
	}

}
