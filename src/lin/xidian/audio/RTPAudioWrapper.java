package lin.xidian.audio;

/**
 * set init start stop
 * @author lindia
 *
 */
public class RTPAudioWrapper extends RTPAudioWrapperBase
{
	private int selfSendPort;
	private int selfRecivePort;
	private String selfSendIp;
	private String selfReciveIp;
	
	private int destSendPort;
	private int destRecivePort;
	private String destSendIp;
	private String destReciveIp;
	
	
	private long destId;
	private String destName;
	
	private RTPAudioReciverBase reciver;
	private RTPAudioSenderBase sender;
	
	private boolean initted = false;
	
	@Override
	public String getDestReciveIp()
	{
		return destReciveIp;
	}

	@Override
	public int getDestRecivePort()
	{
		return destRecivePort;
	}

	@Override
	public String getDestSendIp()
	{
		return destSendIp;
	}

	@Override
	public int getDestSendPort()
	{
		return destSendPort;
	}

	@Override
	public String getSelfReciveIp()
	{
		return selfReciveIp;
	}

	@Override
	public int getSelfRecivePort()
	{
		return selfRecivePort;
	}

	@Override
	public String getSelfSendIp()
	{
		return selfSendIp;
	}

	@Override
	public int getSelfSendPort()
	{
		return selfSendPort;
	}

	@Override
	public void setDestReciveIp(String ip)
	{
		destReciveIp = ip;		
	}

	@Override
	public void setDestRecivePort(int port)
	{
		destRecivePort = port;		
	}

	@Override
	public void setDestSendIp(String destSendIp)
	{
		this.destSendIp = destSendIp;		
	}

	@Override
	public void setDestSendPort(int destSendPort)
	{
		this.destSendPort = destSendPort;
	}

	@Override
	public void setSelfReciveIp(String ip)
	{
		this.selfReciveIp = ip;
	}

	@Override
	public void setSelfRecivePort(int port)
	{
		this.selfRecivePort = port;
	}

	@Override
	public void setSelfSendIp(String ip)
	{
		this.selfSendIp = ip;
	}

	@Override
	public void setSelfSendPort(int port)
	{
		this.selfSendPort = port;
	}

	public long getDestId()
	{
		return destId;
	}

	public String getDestName()
	{
		return destName;
	}

	public void init()
	{
		sender = new RTPAudioSender();
		reciver = new RTPAudioReciver();
		sender.setWrapper(this);
		reciver.setWrapper(this);
		sender.init();
		reciver.init();
		initted = true;
	}

	public void setDestId(long id)
	{
		this.destId = id;
	}

	public void setDestName(String name)
	{
		this.destName = name;
	}

	public void start()
	{
		sender.start();
		reciver.start();
	}

	public void stop()
	{
		sender.stop();
		reciver.stop();
	}

	@Override
	public void addAudioEventListener(AudioEventListener listener, String type)
	{
		if(initted)
		{
			if(type.equals("send"))
			{
				sender.addAudioEventListener(listener);
			}
			if(type.equals("recive"))
			{
				reciver.addAudioEventListener(listener);
			}
		}
	}
}
