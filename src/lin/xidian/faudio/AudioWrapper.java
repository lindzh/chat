package lin.xidian.faudio;

import java.net.DatagramSocket;

import lin.xidian.core.Reciver;
import lin.xidian.core.Sender;
import lin.xidian.core.Wrapper;
import lin.xidian.utils.DatagramSocketFactory;

//set init start stop
public class AudioWrapper implements Wrapper
{
	private DatagramSocket socket;
	private String destIp = "";
	private long destId = 0;
	private String destName = "";
	private int destPort = 0;
	
	private Reciver reciver;
	private Sender sender;
	
	public AudioWrapper()
	{
		socket = DatagramSocketFactory.getInstance().createDatagramSocket("audio");
	}
	
	public DatagramSocket getDatagramSocket()
	{
		return socket;
	}

	public long getDestId()
	{
		return destId;
	}

	public String getDestIp()
	{
		return destIp;
	}

	public String getDestName()
	{
		return destName;
	}

	public int getDestPort()
	{
		return destPort;
	}

	public void init()
	{
		reciver = new AudioReciver();
		reciver.setWrapper(this);
		sender = new  AudioSender();
		sender.setWrapper(this);
	}

	public void setDestId(long id)
	{
		destId = id;
	}

	public void setDestIp(String ip)
	{
		destIp = ip;
	}

	public void setDestName(String name)
	{
		destName = name;
	}

	public void setDestPort(int port)
	{
		destPort = port;
	}

	public void setReciver(Reciver reciver)
	{
		this.reciver = reciver;
	}

	public void setSender(Sender sender)
	{
		this.sender = sender;
	}

	public void start()
	{
		sender.init();
		reciver.init();
	}

	public void startTick()
	{
		
	}

	public void stop()
	{
		sender.stop();
		reciver.stop();
	}

}
