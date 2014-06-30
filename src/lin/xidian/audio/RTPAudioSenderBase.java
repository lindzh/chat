package lin.xidian.audio;

import lin.xidian.core.FinishListener;
import lin.xidian.core.SendEventListener;
import lin.xidian.core.Sender;
import lin.xidian.core.Wrapper;

public abstract class RTPAudioSenderBase implements Sender
{

	public abstract void start();
	
	public abstract void addAudioEventListener(AudioEventListener listener);
	
	
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
