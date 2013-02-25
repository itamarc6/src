package ArithUtils;

public class Func
{
	public static abstract class P0<TRes> 
	{
		public abstract TRes invoke();
	}
	public static abstract class P1<TRes, TP1> 
	{
		public abstract TRes invoke(TP1 p1);
	}
	public static abstract class P2<TRes, TP1, TP2>
	{
		public abstract TRes invoke(TP1 p1, TP2 p2);
	}
	public static abstract class P3<TRes, TP1, TP2, TP3> 
	{
		public abstract TRes invoke(TP1 p1, TP2 p2, TP3 p3);
	}
}