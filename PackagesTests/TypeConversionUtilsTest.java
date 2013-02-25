package PackagesTests;

import Utils.TypeConvertionUtils;

public class TypeConversionUtilsTest {
	public static void main(String [ ] args){
		int maxValueInt = Integer.MAX_VALUE;
		int minValueInt = Integer.MIN_VALUE;
		int zeroInt = 0;
		int oneInt = 1;
		System.out.println(maxValueInt + " is - " + TypeConvertionUtils.byteArrToHexString(TypeConvertionUtils.intToByteArr(maxValueInt)));
		System.out.println(minValueInt + " is - " + TypeConvertionUtils.byteArrToHexString(TypeConvertionUtils.intToByteArr(minValueInt)));
		System.out.println(zeroInt + " is - " + TypeConvertionUtils.byteArrToHexString(TypeConvertionUtils.intToByteArr(zeroInt)));
		System.out.println(oneInt + " is - " + TypeConvertionUtils.byteArrToHexString(TypeConvertionUtils.intToByteArr(oneInt)));
		System.out.println();
		byte[] maxValueArr = TypeConvertionUtils.hexStringToByteArr("7fffffff");
		byte[] n = TypeConvertionUtils.hexStringToByteArr("0000000d");
		System.out.println(TypeConvertionUtils.byteArrToInt(maxValueArr));
		System.out.println(TypeConvertionUtils.byteArrToInt(n));
		System.out.println();
		String str1 = "Bla Bla %^% what 123 )()(   ***";
		byte[] str1ByteArr = TypeConvertionUtils.stringToByteArr(str1);
		System.out.println(TypeConvertionUtils.byteArrToString(str1ByteArr));
	}
}
