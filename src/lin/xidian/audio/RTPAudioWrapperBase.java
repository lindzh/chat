package lin.xidian.audio;

import java.net.DatagramSocket;

import lin.xidian.core.Reciver;
import lin.xidian.core.Sender;
import lin.xidian.core.Wrapper;

public abstract class RTPAudioWrapperBase implements Wrapper
{
	public abstract void addAudioEventListener(AudioEventListener listener,String type);
	
	public abstract void setSelfSendPort(int port);
	public abstract void setSelfSendIp(String ip);
	public abstract int getSelfSendPort();
	public abstract String getSelfSendIp();
	public abstract void setDestReciveIp(String ip);
	public abstract void setDestRecivePort(int port);
	public abstract String getDestReciveIp();
	public abstract int getDestRecivePort();
	
	public abstract String getDestSendIp();
	public abstract int getDestSendPort();
	public abstract void setDestSendIp(String destSendIp);
	public abstract void setDestSendPort(int destSendPort);
	public abstract void setSelfReciveIp(String ip);
	public abstract void setSelfRecivePort(int port);
	public abstract String getSelfReciveIp();
	public abstract int getSelfRecivePort();
	
	public void startTick()
	{
		
	}
	public DatagramSocket getDatagramSocket()
	{
		return null;
	}
	public String getDestIp()
	{
		return null;
	}
	public int getDestPort()
	{
		return 0;
	}
	public void setDestIp(String ip)
	{
		
	}
	public void setDestPort(int port)
	{
		
	}
	public void setReciver(Reciver reciver)
	{
		
	}
	public void setSender(Sender sender)
	{
		
	}
	
	
}
