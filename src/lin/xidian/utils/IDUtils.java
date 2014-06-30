package lin.xidian.utils;

import java.util.Random;

public class IDUtils
{
	private static final IDUtils utils = new IDUtils();
	private long id= 0;
	
	private IDUtils()
	{
		Random random = new Random();
		id = random.nextLong();
		if(id<0)
		{
			id = -id;
		}
	}
	
	public static IDUtils getInstance()
	{
		return utils;
	}
	
	public long getID()
	{
		
		id++;
		return id;
	}
}
