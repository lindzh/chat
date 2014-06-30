package lin.xidian.video;

import lin.xidian.utils.Constant;
import lin.xidian.utils.InetUtils;

/**
 * set init start stop
 * @author lindia
 *
 */
public class VideoWrapper extends VideoWrapperBase
{
	private long destId = 0;
	private String destName = "";
	
	private VideoReciverBase reciver;
	private VideoSenderBase sender;
	
	private boolean initted = false;
	
	public void init()
	{
		sender = new VideoSender();
		reciver = new VideoReciver();
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
		sender = null;
		reciver = null;
	}

	public long getDestId()
	{
		return destId;
	}

	public String getDestName()
	{
		return destName;
	}

	@Override
	public void addVideoEventListener(VideoEventListener listener, String type)
	{
		if(initted)
		{
			if(type.equals("send"))
			{
				sender.addVideoEventListener(listener);
			}
			if(type.equals("recive"))
			{
				reciver.addVideoEventListener(listener);
			}
		}
	}

}
