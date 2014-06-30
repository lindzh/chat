package lin.xidian.video;

import lin.xidian.core.FinishListener;
import lin.xidian.core.SendEventListener;
import lin.xidian.core.Sender;
import lin.xidian.core.Wrapper;

public abstract class VideoSenderBase implements Sender
{
	public abstract void start();
	
	public abstract void addVideoEventListener(VideoEventListener listener);
	
	public void addFinishListener(FinishListener listener)
	{
		
	}

	public void addSendEventListener(SendEventListener listener)
	{
		
	}

	public void send(String type, byte[] data)
	{
		
	}

	public void startTick()
	{
		
	}
	
	
	
}
