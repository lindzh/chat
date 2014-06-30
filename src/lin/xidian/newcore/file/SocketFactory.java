package lin.xidian.newcore.file;

import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class SocketFactory
{
	public static Map<DatagramSocket,StableFileSocket> map = new HashMap<DatagramSocket,StableFileSocket>();
	
	public static void add(DatagramSocket gram,StableFileSocket socket)
	{
		map.put(gram, socket);
	}
	
	public static StableFileSocket get(DatagramSocket gram)
	{
		return map.get(gram);
	}	
	
	public static void close(DatagramSocket gram)
	{
		map.get(gram).close();
	}
}
