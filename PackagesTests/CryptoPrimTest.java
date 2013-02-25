package PackagesTests;

import CryptoPrim.HashFunction;
import CryptoPrim.PRG;
import CryptoPrim.RandomOracle;
import Utils.TypeConvertionUtils;

/**
 * A test class for CryptoPrim package and all of it's classes and methods.
 * The test is as described in Appendix A - 
 * Test Vectors for Cryptographic Primitives
 * 
 * @author Itamar Carmel
 */
public class CryptoPrimTest {
	
	public static void main(String [ ] args){
		HashFunction h256 = new HashFunction("SHA-256");
		HashFunction h384 = new HashFunction("SHA-384");
		HashFunction h512 = new HashFunction("SHA-512");
		String s32 = "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f";
		String s48 = "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f";
		String s64 = "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f";
		byte[] seed32 = TypeConvertionUtils.hexStringToByteArr(s32);
		byte[] seed48 = TypeConvertionUtils.hexStringToByteArr(s48);
		byte[] seed64 = TypeConvertionUtils.hexStringToByteArr(s64);	
		PRG prg256 = new PRG(h256);
		PRG prg384 = new PRG(h384);
		PRG prg512 = new PRG(h512);
		prg256.setSeed(seed32);
		prg384.setSeed(seed48);
		prg512.setSeed(seed64);
		RandomOracle RO256a = new RandomOracle(h256, 65);
		RandomOracle RO256b = new RandomOracle(h256, 261);
		RandomOracle RO384a = new RandomOracle(h384, 93);
		RandomOracle RO384b = new RandomOracle(h384, 411);
		RandomOracle RO512a = new RandomOracle(h512, 111);
		RandomOracle RO512b = new RandomOracle(h512, 579);
		System.out.println("Test Vectors for Cryptographic Primitives");
		System.out.println("=========================================");
		System.out.println();
		System.out.println(prg256);
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg256.produceVector(32)));
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg256.produceVector(32)));
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg256.produceVector(32)));
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg256.produceVector(32)));
		System.out.println();
		System.out.println(prg384);
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg384.produceVector(32)));
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg384.produceVector(32)));
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg384.produceVector(32)));
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg384.produceVector(32)));
		System.out.println();
		System.out.println(prg512);
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg512.produceVector(32)));
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg512.produceVector(32)));
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg512.produceVector(32)));
		System.out.println(TypeConvertionUtils.byteArrToHexString(prg512.produceVector(32)));
		System.out.println();
		System.out.println(RO256a);
		System.out.println(TypeConvertionUtils.byteArrToHexString(RO256a.produceMapping(seed32)));
		System.out.println();
		System.out.println(RO256b);
		System.out.println(TypeConvertionUtils.byteArrToHexString(RO256b.produceMapping(seed32)));
		System.out.println();
		System.out.println(RO384a);
		System.out.println(TypeConvertionUtils.byteArrToHexString(RO384a.produceMapping(seed32)));
		System.out.println();
		System.out.println(RO384b);
		System.out.println(TypeConvertionUtils.byteArrToHexString(RO384b.produceMapping(seed32)));
		System.out.println();
		System.out.println(RO512a);
		System.out.println(TypeConvertionUtils.byteArrToHexString(RO512a.produceMapping(seed32)));
		System.out.println();
		System.out.println(RO512b);
		System.out.println(TypeConvertionUtils.byteArrToHexString(RO512b.produceMapping(seed32)));
		System.out.println();
	}
}
