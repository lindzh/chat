package lin.xidian.utils;

public class IDGenerator
{
	private static IDGenerator instance = new IDGenerator();
	
	private IDGenerator()
	{
		
	}
	
	public static IDGenerator getInstance()
	{
		return instance;
	}
		
	public static long generateId()
	{
		return (long)Math.random();
	}
}
