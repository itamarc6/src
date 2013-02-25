package Verifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A Logger class which provides writing methods to a
 * given path of a log file.
 * 
 * @author Itamar Carmel
 */
public class MyLogger {
	
	private BufferedWriter logWriter; // writes to the log file
	private boolean verbose; // determine whether logging is allowed
	private int level; // determine level of writing for current line
	private static SimpleDateFormat sdf; // determine format of date or time written to log file
	
	/**
	 * Initiate a MyLogger instance by a path to log file
	 * and verbose value, which determines the logging state.
	 * 
	 * @param path
	 * @param verbose
	 * @throws IOException
	 */
	public MyLogger(String path, boolean verbose) throws IOException{
		this.verbose = verbose;
		if (!this.verbose)
			return;
		logWriter = new BufferedWriter(new FileWriter(
				new File(path + "\\log.txt"), true));
		resetLevel();
		sdf = new SimpleDateFormat("dd/MM/yy H:mm:ss:SSS");
		writePlusTime("New log file has created.");
		write("================================================================");
		write("");
		sdf = new SimpleDateFormat("H:mm:ss:SSS");
	}

	public void resetLevel() {this.level = 0;}
	public void incrementLevel() {this.level++;}
	public void decrementLevel() {if (this.level > 0) this.level--;}
	public void setLevel(int level) {this.level = level;}
	
	/**
	 * Writes a simple massage and a newline
	 * to the log file.
	 * 
	 * @param msg
	 */
	public void write(String msg){
		if (!this.verbose)
			return;
		try {
			logWriter.write(msg);
			logWriter.newLine();
		} catch (IOException e) {}
	}

	/**
	 * Writes a massage with the prefix of the
	 * current time.
	 * 
	 * @param msg
	 */
	public void writePlusTime(String msg){
		String prefix = "";
        for (int i=0; i<this.level; i++)
            prefix += "| ";
		this.write(getCurTime() + "\t" + prefix + msg);
	}

	/**
	 * Writes a fixed-size header.
	 * 
	 * @param msg
	 */
	public void writeHeader(String msg){
        String header = "============ ";
        header += (msg + " ");
        for (int i=0; i<50-msg.length(); i++)
                header += '=';
        this.write(header);
	}
	
	/**
	 * Writes an error to log file (plus time).
	 * In addition print out an error massage.
	 * 
	 * @param msg
	 */
	public void writeError(String msg){
		System.err.println("ERROR: " + msg);
		this.writePlusTime("ERROR: " + msg);
	}
	
	/**
	 * Closes the buffered writer.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException{
		if (!this.verbose)
			return;
		this.logWriter.close();
	}
	
	/**
	 * Private method which gets the current time
	 * in string representation.
	 * 
	 * @return string representation of the current time
	 */
	private static String getCurTime() {
	    Calendar cal = Calendar.getInstance();
	    return sdf.format(cal.getTime());
	}
}
