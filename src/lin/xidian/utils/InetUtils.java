package lin.xidian.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetUtils
{
	public static String getLocalIp()
	{
		String ip = "127.0.0.1";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}
	
	public static boolean isIPAddress(String ip)
	{
		String [] str = ip.split("\\.");
		if(str.length!=4)
		{
			return false;
		}
		else
		{
			for(int i=0;i<4;i++)
			{
				try
				{
					Integer t = Integer.parseInt(str[i]);
					if(t>0&&t<255)
					{
						continue;
					}
				}catch(Exception e)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean canUse(String port)
	{
		try
		{
			Integer it = Integer.parseInt(port);
			if(it>1024&&it<65536)
			{
				return true;
			}
		}catch(Exception e)
		{
			return false;
		}
		return false;
	}
}
