package ArithUtils;
import java.math.BigInteger;


/**
 * @author shirpele
 *
 */

public class BigNumber {
	
	BigInteger value;
	public static BigNumber MONE = new BigNumber("-1");
	public static BigNumber ZERO = new BigNumber("0");
	public static BigNumber ONE = new BigNumber("1");
	public static BigNumber TWO = new BigNumber("2");
	public static BigNumber THREE = new BigNumber("3");
	/**
	 * @param value
	 */
	public BigNumber(String value){
		this.value=new BigInteger(value);
	}
	public BigNumber(String value, int radix){
		this.value=new BigInteger(value, radix);
	}
	public BigNumber(int value){
		this.value=new BigInteger(String.valueOf(value));
	}
	public BigNumber(byte[] array){
		this.value=new BigInteger(array);
	}
	public BigNumber(BigInteger value){
		this.value=value;
	}
	private BigInteger getValue(){
		return this.value;
	}
	public BigNumber add(BigNumber other){
		return new BigNumber(value.add(other.getValue()));
	}
	
	public BigNumber substract(BigNumber other){
		return new BigNumber(value.subtract(other.getValue()));
	}
	public BigNumber multiply(BigNumber other){
		return new BigNumber(value.multiply(other.getValue()));
	}
	public BigNumber divide(BigNumber other){
		return new BigNumber(value.divide(other.getValue()));
	}
	public BigNumber pow(int exponent){
		return new BigNumber(value.pow(exponent));
	}
	public BigNumber powMod(BigNumber exponent, BigNumber mod){
		return this.pow(exponent).modulo(mod);
	}
	public BigNumber pow(BigNumber exponent) throws IllegalArgumentException{
		char c;
		BigNumber result = ONE;
		String binary=exponent.toBinaryString();
		if(binary.length()>32)
			throw new IllegalArgumentException("You cannot raise a bigNumber to a non int power");
		while(binary.length() > 0)
		{
			c = binary.charAt(0);
			binary=binary.substring(1); 
			result=result.multiply(result);
			if(c == '1')
				result = result.multiply(this);
		}
		return result;
	}
	public int compareTo(BigNumber other){
		return value.compareTo(other.getValue());
	}
	public String toBinaryString(){
		return value.toString(2);
	}
	public BigNumber modulo(BigNumber other)
	{
		return new BigNumber(this.value.mod(other.getValue()));
	}
	public Boolean isProbablyPrime(int certainty)
	{
		return this.value.isProbablePrime(certainty);
	}
	public int bitLength() {
		return value.bitLength();
	}
	public String toString(){
		return value.toString();
	}
	public boolean equals(Object other)
	{
		
		return (this.compareTo((BigNumber)other)==0);
	}
	
	 public byte[] toByteArray()
	{
		return (this.value.toByteArray());
	}
	 
	 /**
		 * @return S s.t this.field.charecther= 2^S*Q where Q is odd.
		 */
		public BigNumber getMaxTwoExp()
		{
			BigNumber res=BigNumber.ZERO;
			int i=0;
			StringBuffer binRepBuff=new StringBuffer(this.toBinaryString());
			String binRep=binRepBuff.reverse().toString();
			while((binRep.charAt(i)=='0') && (i<=binRep.length()))
			{
				res=res.add(BigNumber.ONE);
				i++;
			}
			return res;
		}
		

}


