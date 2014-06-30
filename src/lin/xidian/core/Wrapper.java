package lin.xidian.core;

import java.net.DatagramSocket;

public interface Wrapper
{
	public void setDestIp(String ip);
	public void setDestPort(int port);
	public void setDestId(long id);
	public void setDestName(String name);
	public String getDestName();
	public String getDestIp();
	public int getDestPort();
	public long getDestId();
	public DatagramSocket getDatagramSocket();
	
	public void setReciver(Reciver reciver);
	public void setSender(Sender sender);
	public void init();
	public void start();
	public void stop();
	public void startTick();
}
