package lin.xidian.utils;

import java.net.DatagramSocket;
import java.net.SocketException;

public class DatagramSocketFactory
{
	private int basePort = 6010;
	private int managerStep = 10;
	private int otherStep = 1;
	
	private static DatagramSocketFactory factory = new DatagramSocketFactory();
	private DatagramSocketFactory()
	{
		
	}
	
	public static DatagramSocketFactory getInstance()
	{
		if(factory == null)
		{
			factory = new DatagramSocketFactory();
		}
		return factory;
	}
	
	public DatagramSocket createDatagramSocket(String type)
	{
		boolean ok = false;
		DatagramSocket socket = null;
		while(!ok)
		{
			try {
				if(type.equals("manager"))
				{
					socket = new DatagramSocket(basePort);
				}
				else
				{
					socket = new DatagramSocket(basePort+otherStep);
					otherStep++;
				}
				ok = true;
			} catch (SocketException e) {
				e.printStackTrace();
				if(type.equals("manager"))
				{
					basePort+=managerStep;
				}
				else
				{
					otherStep++;
				}
			}
		}
		return socket;
		
	}
	
	

}
