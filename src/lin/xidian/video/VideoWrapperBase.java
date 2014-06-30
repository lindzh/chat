package lin.xidian.video;

import java.net.DatagramSocket;

import lin.xidian.core.Reciver;
import lin.xidian.core.Sender;
import lin.xidian.core.Wrapper;

public abstract class VideoWrapperBase implements Wrapper
{
	private String selfSendIp;
	private int selfSendPort;
	private String destReciveIp;
	private int destRecivePort;
	
	private String selfReciveIp;
	private int selfRecivePort;
	private String destSendIp;
	private int destSendPort;
	
	private String destIP;
	
	

	public String getSelfSendIp()
	{
		return selfSendIp;
	}

	public void setSelfSendIp(String selfSendIp)
	{
		this.selfSendIp = selfSendIp;
	}

	public int getSelfSendPort()
	{
		return selfSendPort;
	}

	public void setSelfSendPort(int selfSendPort)
	{
		this.selfSendPort = selfSendPort;
	}

	public String getDestReciveIp()
	{
		return destReciveIp;
	}

	public void setDestReciveIp(String destReciveIp)
	{
		this.destReciveIp = destReciveIp;
	}

	public int getDestRecivePort()
	{
		return destRecivePort;
	}

	public void setDestRecivePort(int destRecivePort)
	{
		this.destRecivePort = destRecivePort;
	}

	

	public String getSelfReciveIp()
	{
		return selfReciveIp;
	}

	public void setSelfReciveIp(String selfReciveIp)
	{
		this.selfReciveIp = selfReciveIp;
	}

	public int getSelfRecivePort()
	{
		return selfRecivePort;
	}

	public void setSelfRecivePort(int selfRecivePort)
	{
		this.selfRecivePort = selfRecivePort;
	}

	public String getDestSendIp()
	{
		return destSendIp;
	}

	public void setDestSendIp(String destSendIp)
	{
		this.destSendIp = destSendIp;
	}

	public int getDestSendPort()
	{
		return destSendPort;
	}

	public void setDestSendPort(int destSendPort)
	{
		this.destSendPort = destSendPort;
	}

	public DatagramSocket getDatagramSocket()
	{
		return null;
	}

	public String getDestIp()
	{
		return destIP;
	}

	public int getDestPort()
	{
		return 0;
	}

	public void setDestIp(String ip)
	{
		destIP = ip;
	}

	public void setDestPort(int port)
	{
		
	}

	public void startTick()
	{
		
	}

	public void setReciver(Reciver reciver)
	{
		
	}

	public void setSender(Sender sender)
	{
		
	}
	
	public abstract void addVideoEventListener(VideoEventListener listener,String type);
}
