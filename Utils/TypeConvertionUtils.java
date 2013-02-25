package Utils;

import java.nio.ByteBuffer;
import javax.xml.bind.DatatypeConverter;
import ArithmeticUtils.BigNumber;

public class TypeConvertionUtils {
	
	public static byte[] stringToByteArr(String str){
		return str.getBytes();
	}
	
	public static String byteArrToString(byte[] byteArr){
		return new String(byteArr);
	}
	
	public static byte[] intToByteArr(int i){
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(i);
		return bb.array();
	}
	
	public static int byteArrToInt(byte[] byteArr){
		if (byteArr.length != 4){
			System.err.println("ERROR: Illegal int representation - byte array should contain exactly 4 bytes.");
			return 0;
		}
		ByteBuffer bb = ByteBuffer.wrap(byteArr);
		return bb.getInt();
	}
	
	public static byte[] hexStringToByteArr(String str){
		if (str.length() % 2 != 0){
			System.err.println("ERROR: Illegal byte representation - hex string should have an even length.");
			return null;
		}
		return DatatypeConverter.parseHexBinary(str);
	}
	
	public static String byteArrToHexString(byte[] byteArr){
		StringBuilder sb = new StringBuilder();
		for(byte b: byteArr){
			sb.append(String.format("%02x", b&0xff));
		}
		return sb.toString();
	}

	public static int bigNumberToInt(BigNumber bn){
		return Integer.parseInt(bn.toString());
	}
	
}
